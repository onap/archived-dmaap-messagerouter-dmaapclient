MR Client
==============

MR Log4J Appender
----------------------

To configure a MR Log4J appender the following parameters are available:

(R) Topic [String] - The topic which you wish to publish your log messages to
(R) Partition [String]- The partition within the Topic that you wish you publish your log messages to
(R) Hosts [String]- A comma separated string containing the MR API server nodes you wish to connect to
(O) MaxBatchSize [Integer] - The maximum size of the batch of log messages.
		    When the batch grows larger than this, the messages are flushed to the topic/partition.
(O) MaxAgeMs [Integer] - The maximum age in milliseconds of the batch.
		When the batch age is older than this, the messages are flushed to the topic/partition.
(O) Compress [Boolean] - Indicates whether you would like your messages compressed or not.

An example can be found in ./src/main/resources/log4j.xml
