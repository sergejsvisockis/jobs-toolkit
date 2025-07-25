# Jobs Toolkit Enterprise

This is a short intro into the Enterprise version of the Jobs Toolkit.
The version which is currently publicly available Open Source is a core Job API Engine which is supposed to be very
tiny by nature and provide the core async job functionality.

In other words this is a rough diamond - root architecture. In a nutshell just a JDBC RDBMS implementation.

Examples in the `examples` module contain both free and Enterprise versions. Examples that demonstrate premium features
simply won't compile, since artifacts are located in the private artifactory.
Access to an artifactory is being granted upon the subscription being in place and license key granted.

Enterprise examples are the following ones:

* example-mongo
* example-redis-lock
* example-zookeeper-lock
* example-aws-dynamodb

Overall enterprise version contains the following additional features:
* MongoDB support (for both job metadata storage and locking)
* [Not available yet] Cassandra support (for both job metadata storage and locking)
* Zookeeper distributed lock
* Redis distributed lock
* [Not available yet] Hazelcast distributed lock
* [Not available yet] Dead Letter Queue
* Retry mechanism (in case the job has failed)
  * [Not available yet] Retry history is persisted into the database (depending on the implementation)
* [Not available yet] AWS DynamoDB support (both job metadata storage and locking)
* AWS S3 support (for job metadata storage)
* [Not available yet] Azure Cosmos DB support (both job metadata storage and locking)
* [Not available yet] GCP (both job metadata storage and locking)
* [Not available yet] UI to track an execution state, time and anomalies