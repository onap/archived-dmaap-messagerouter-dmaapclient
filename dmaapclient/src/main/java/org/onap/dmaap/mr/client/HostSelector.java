/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Modifications Copyright © 2021 Orange.
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

package org.onap.dmaap.mr.client;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostSelector {
    private final TreeSet<String> baseHosts;
    private final DelayQueue<BlacklistEntry> blacklist;
    private String idealHost;
    private String currentHost;
    private static final Logger logger = LoggerFactory.getLogger(HostSelector.class);

    public HostSelector(String hostPart) {
        this(makeSet(hostPart), null);
    }

    public HostSelector(Collection<String> baseHosts) {
        this(baseHosts, null);
    }

    public HostSelector(Collection<String> baseHosts, String signature) {
        if (baseHosts.isEmpty()) {
            throw new IllegalArgumentException("At least one host must be provided.");
        }

        this.baseHosts = new TreeSet<>(baseHosts);
        this.blacklist = new DelayQueue<>();
        this.idealHost = null;

        if (signature == null) {
            return;
        }
        int index = 0;
        int value = signature.hashCode();
        if (value != 0) {
            index = Math.abs(value) % baseHosts.size();
        }
        Iterator<String> it = this.baseHosts.iterator();
        while (index-- > 0) {
            it.next();
        }
        this.idealHost = (it.next());
    }

    public String selectBaseHost() {
        if (this.currentHost == null) {
            makeSelection();
        }
        return this.currentHost;
    }

    public void reportReachabilityProblem(long blacklistUnit, TimeUnit blacklistTimeUnit) {
        if (this.currentHost == null) {
            logger.warn("Reporting reachability problem, but no host is currently selected.");
        }

        if (blacklistUnit > 0L) {
            for (BlacklistEntry be : this.blacklist) {
                if (be.getHost().equals(this.currentHost)) {
                    be.expireNow();
                }
            }

            LinkedList<Delayed> devNull = new LinkedList<>();
            this.blacklist.drainTo(devNull);

            if (this.currentHost != null) {
                this.blacklist.add(new BlacklistEntry(this.currentHost,
                        TimeUnit.MILLISECONDS.convert(blacklistUnit, blacklistTimeUnit)));
            }
        }
        this.currentHost = null;
    }

    private String makeSelection() {
        TreeSet<String> workingSet = new TreeSet<>(this.baseHosts);

        LinkedList<Delayed> devNull = new LinkedList<>();
        this.blacklist.drainTo(devNull);
        for (BlacklistEntry be : this.blacklist) {
            workingSet.remove(be.getHost());
        }

        if (workingSet.isEmpty()) {
            logger.warn("All hosts were blacklisted; reverting to full set of hosts.");
            workingSet.addAll(this.baseHosts);
            this.currentHost = null;
        }

        String selection = null;
        if ((this.currentHost != null) && (workingSet.contains(this.currentHost))) {
            selection = this.currentHost;
        } else if ((this.idealHost != null) && (workingSet.contains(this.idealHost))) {
            selection = this.idealHost;
        } else {
            int index = 0;
            int value = new SecureRandom().nextInt();
            ArrayList<String> workingArray = new ArrayList<>(workingSet);
            if (value != 0) {
                index = Math.abs(value) % workingSet.size();
            }
            selection = workingArray.get(index);
        }

        this.currentHost = selection;
        return this.currentHost;
    }

    private static Set<String> makeSet(String firstTreeElem) {
        TreeSet<String> set = new TreeSet<>();
        set.add(firstTreeElem);
        return set;
    }

    private static class BlacklistEntry implements Delayed {
        private final String host;
        private long expireAtMs;

        public BlacklistEntry(String host, long delayMs) {
            this.host = host;
            this.expireAtMs = (System.currentTimeMillis() + delayMs);
        }

        public void expireNow() {
            this.expireAtMs = 0L;
        }

        public String getHost() {
            return this.host;
        }

        public int compareTo(Delayed object) {
            Long thisDelay = getDelay(TimeUnit.MILLISECONDS);
            return thisDelay.compareTo(object.getDelay(TimeUnit.MILLISECONDS));
        }

        public long getDelay(TimeUnit unit) {
            long remainingMs = this.expireAtMs - System.currentTimeMillis();
            return unit.convert(remainingMs, TimeUnit.MILLISECONDS);
        }
    }
}
