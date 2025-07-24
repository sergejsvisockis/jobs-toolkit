# Jobs Toolkit Enterprise

The version which is currently available publicly the Open Source is a core Job API engine which is supposed to be very
tiny by nature and provide the core async job functionality. In other words this is a rough diamond - root architecture.

In a nutshell just a JDBC RDBMS implementation.

Examples in the `examples` module contain both free and enterprise versions. Examples that demonstrate premium features
simply won't compile, since artifact are located in the private artifactory.
Access to an artifactory is being granted upon the subscription being in place and license key granted.

Examples that are available only upon the request and subscription are the following ones:

* example-mongo
* example-redis-lock
* example-zookeeper-lock

Overall enterprise version contains the following additional features:
* MongoDB support (for both job metadata storage and locking)
* Zookeeper distributed lock
* Redis distributed lock
* Dead Letter Queue
* Retry mechanism (in case the job has failed)
* AWS DynamoDB support (both job metadata storage and locking)
* AWS S3 support (for job metadata storage)
* Azure Cosmos DB support (both job metadata storage and locking)
* GCP (both job metadata storage and locking)