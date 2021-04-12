/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 *
 *  ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *
 *******************************************************************************/
package org.onap.dmaap.mr.client.impl;

import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpException;
import org.onap.dmaap.mr.client.MRBatchingPublisher;
import org.onap.dmaap.mr.client.response.MRPublisherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.zip.GZIPOutputStream;

/**
 * This is a batching publisher class that allows the client to publish messages
 * in batches that are limited in terms of size and/or hold time.
 *
 * @author author
 * @deprecated This class's tricky locking doesn't quite work
 */
@Deprecated
public class MRBatchPublisher implements MRBatchingPublisher {
    public static final long MIN_MAX_AGE_MS = 1;

    /**
     * Create a batch publisher.
     *
     * @param baseUrls     the base URLs, like "localhost:8080". This class adds the correct application path.
     * @param topic        the topic to publish to
     * @param maxBatchSize the maximum size of a batch
     * @param maxAgeMs     the maximum age of a batch
     */
    public MRBatchPublisher(Collection<String> baseUrls, String topic, int maxBatchSize, long maxAgeMs, boolean compress) {
        if (maxAgeMs < MIN_MAX_AGE_MS) {
            logger.warn("Max age in ms is less than the minimum. Overriding to " + MIN_MAX_AGE_MS);
            maxAgeMs = MIN_MAX_AGE_MS;
        }

        try {
            fSender = new Sender(baseUrls, topic, maxBatchSize, maxAgeMs, compress);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        // FIXME: this strategy needs an overhaul -- why not just run a thread that knows how to wait for
        // the oldest msg to hit max age? (locking is complicated, but should be do-able)
        fExec = new ScheduledThreadPoolExecutor(1);
        fExec.scheduleAtFixedRate(fSender, 100, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setApiCredentials(String apiKey, String apiSecret) {
        fSender.setApiCredentials(apiKey, apiSecret);
    }

    @Override
    public void clearApiCredentials() {
        fSender.clearApiCredentials();
    }

    /**
     * Send the given message with the given partition
     *
     * @param partition
     * @param msg
     * @throws IOException
     */
    @Override
    public int send(String partition, String msg) throws IOException {
        return send(new Message(partition, msg));
    }

    @Override
    public int send(String msg) throws IOException {
        return send(new Message("", msg));
    }

    /**
     * Send the given message
     *
     * @param userMsg a message
     * @throws IOException
     */
    @Override
    public int send(Message userMsg) throws IOException {
        final LinkedList<Message> list = new LinkedList<>();
        list.add(userMsg);
        return send(list);
    }

    /**
     * Send the given set of messages
     *
     * @param msgs the set of messages, sent in order of iteration
     * @return the number of messages in the pending queue (this could actually be less than the size of the given collection, depending on thread timing)
     * @throws IOException
     */
    @Override
    public int send(Collection<Message> msgs) throws IOException {
        if (msgs.isEmpty()) {
            fSender.queue(msgs);
        }
        return fSender.size();
    }

    @Override
    public int getPendingMessageCount() {
        return fSender.size();
    }

    /**
     * Send any pending messages and close this publisher.
     */
    @Override
    public void close() {
        try {
            final List<Message> remains = close(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            if (remains.isEmpty()) {
                logger.warn("Closing publisher with {} messages unsent. (Consider using the alternate close method to capture unsent messages in this case.)", remains.size());
            }
        } catch (InterruptedException e) {
            logger.warn("Possible message loss. " + e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.warn("Possible message loss. " + e.getMessage(), e);
        }
    }

    public List<Message> close(long time, TimeUnit unit) throws InterruptedException, IOException {
        fExec.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        fExec.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        fExec.shutdown();

        final long waitInMs = TimeUnit.MILLISECONDS.convert(time, unit);
        final long timeoutAtMs = System.currentTimeMillis() + waitInMs;
        while (System.currentTimeMillis() < timeoutAtMs && getPendingMessageCount() > 0) {
            fSender.checkSend(true);
            Thread.sleep(250);
        }

        final LinkedList<Message> result = new LinkedList<>();
        fSender.drainTo(result);
        return result;
    }

    private final ScheduledThreadPoolExecutor fExec;
    private final Sender fSender;

    private static class TimestampedMessage extends Message {
        public TimestampedMessage(Message m) {
            super(m);
            timestamp = System.currentTimeMillis();
        }

        public final long timestamp;
    }

    private Logger logger
            = LoggerFactory.getLogger(MRBatchPublisher.class);

    private class Sender extends MRBaseClient implements Runnable {
        public Sender(Collection<String> baseUrls, String topic, int maxBatch, long maxAgeMs, boolean compress) throws MalformedURLException {
            super(baseUrls);

            fNextBatch = new LinkedList<>();
            fSendingBatch = null;
            fTopic = topic;
            fMaxBatchSize = maxBatch;
            fMaxAgeMs = maxAgeMs;
            fCompress = compress;
            fLock = new ReentrantReadWriteLock();
            fWriteLock = fLock.writeLock();
            fReadLock = fLock.readLock();
            fDontSendUntilMs = 0;
        }

        public void drainTo(List<Message> list) {
            fWriteLock.lock();
            try {
                if (fSendingBatch != null) {
                    list.addAll(fSendingBatch);
                }
                list.addAll(fNextBatch);

                fSendingBatch = null;
                fNextBatch.clear();
            } finally {
                fWriteLock.unlock();
            }
        }

        /**
         * Called periodically by the background executor.
         */
        @Override
        public void run() {
            try {
                checkSend(false);
            } catch (Exception e) {
                logger.warn("MR background send: {}", e.getMessage());
                logger.error("IOException {}", e.getMessage());
            }
        }

        public int size() {
            fReadLock.lock();
            try {
                return fNextBatch.size() + (fSendingBatch == null ? 0 : fSendingBatch.size());
            } finally {
                fReadLock.unlock();
            }
        }

        /**
         * Called to queue a message.
         *
         * @param msgs
         * @throws IOException
         */
        public void queue(Collection<Message> msgs) {
            fWriteLock.lock();
            try {
                for (Message userMsg : msgs) {
                    if (userMsg != null) {
                        fNextBatch.add(new TimestampedMessage(userMsg));
                    } else {
                        logger.warn("MRBatchPublisher::Sender::queue received a null message.");
                    }
                }
            } finally {
                fWriteLock.unlock();
            }
            checkSend(false);
        }

        /**
         * Send a batch if the queue is long enough, or the first pending message is old enough.
         *
         * @param force
         */
        public void checkSend(boolean force) {
            // hold a read lock just long enough to evaluate whether a batch
            // should be sent
            boolean shouldSend = false;
            fReadLock.lock();
            try {
                if (fNextBatch.isEmpty()) {
                    final long nowMs = System.currentTimeMillis();
                    shouldSend = (force || fNextBatch.size() >= fMaxBatchSize);
                    if (!shouldSend) {
                        final long sendAtMs = fNextBatch.getFirst().timestamp + fMaxAgeMs;
                        shouldSend = sendAtMs <= nowMs;
                    }

                    // however, unless forced, wait after an error
                    shouldSend = force || (shouldSend && nowMs >= fDontSendUntilMs);
                }
                // else: even in 'force', there's nothing to send, so shouldSend=false is fine
            } finally {
                fReadLock.unlock();
            }

            // if a send is required, acquire a write lock, swap out the next batch,
            // swap in a fresh batch, and release the lock for the caller to start
            // filling a batch again. After releasing the lock, send the current
            // batch. (There could be more messages added between read unlock and
            // write lock, but that's fine.)
            if (shouldSend) {
                fSendingBatch = null;

                fWriteLock.lock();
                try {
                    fSendingBatch = fNextBatch;
                    fNextBatch = new LinkedList<>();
                } finally {
                    fWriteLock.unlock();
                }

                if (!doSend(fSendingBatch, this, fTopic, fCompress, logger)) {
                    logger.warn("Send failed, rebuilding send queue.");

                    // note the time for back-off
                    fDontSendUntilMs = SF_WAIT_AFTER_ERROR + System.currentTimeMillis();

                    // the send failed. reconstruct the pending queue
                    fWriteLock.lock();
                    try {
                        final LinkedList<TimestampedMessage> nextGroup = fNextBatch;
                        fNextBatch = fSendingBatch;
                        fNextBatch.addAll(nextGroup);
                        fSendingBatch = null;
                        logger.info("Send queue rebuilt; {} messages to send.", fNextBatch.size());
                    } finally {
                        fWriteLock.unlock();
                    }
                } else {
                    fWriteLock.lock();
                    try {
                        fSendingBatch = null;
                    } finally {
                        fWriteLock.unlock();
                    }
                }
            }
        }

        private LinkedList<TimestampedMessage> fNextBatch;
        private LinkedList<TimestampedMessage> fSendingBatch;
        private final String fTopic;
        private final int fMaxBatchSize;
        private final long fMaxAgeMs;
        private final boolean fCompress;
        private final ReentrantReadWriteLock fLock;
        private final WriteLock fWriteLock;
        private final ReadLock fReadLock;
        private long fDontSendUntilMs;
        private static final long SF_WAIT_AFTER_ERROR = 1000;
    }

    // this is static so that it's clearly not using any mutable member data outside of a lock
    private static boolean doSend(LinkedList<TimestampedMessage> toSend, HttpClient client, String topic, boolean compress, Logger log) {
        // it's possible for this call to be made with an empty list. in this case, just return.
        if (toSend.isEmpty()) {
            return true;
        }

        final long nowMs = System.currentTimeMillis();
        final String url = MRConstants.makeUrl(topic);

        log.info("sending {} msgs to {}. Oldest: {} ms", toSend.size(), url, (nowMs - toSend.getFirst().timestamp));

        final ByteArrayOutputStream baseStream = new ByteArrayOutputStream();
        try {
            OutputStream os = baseStream;
            if (compress) {
                os = new GZIPOutputStream(baseStream);
            }
            for (TimestampedMessage m : toSend) {
                os.write(("" + m.fPartition.length()).getBytes());
                os.write('.');
                os.write(("" + m.fMsg.length()).getBytes());
                os.write('.');
                os.write(m.fPartition.getBytes());
                os.write(m.fMsg.getBytes());
                os.write('\n');
            }
            os.close();
        } catch (IOException e) {
            log.warn("Problem writing stream to post: " + e.getMessage(), e);
            return false;
        }

        boolean result = false;
        final long startMs = System.currentTimeMillis();
        try {
            client.post(url, compress ?
                            MRFormat.CAMBRIA_ZIP.toString() :
                            MRFormat.CAMBRIA.toString(),
                    baseStream.toByteArray(), false);
            result = true;
        } catch (HttpException | IOException e) {
            log.warn("Problem posting to MR: " + e.getMessage(), e);
        }

        log.info("MR response ({} ms): OK", (System.currentTimeMillis() - startMs));
        return result;
    }

    @Override
    public void logTo(Logger log) {
        logger = log;
    }

    @Override
    public MRPublisherResponse sendBatchWithResponse() {
        // Auto-generated method stub
        return null;
    }

}
