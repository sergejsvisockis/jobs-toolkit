# AWS DynamoDB Example

Create DynamoDB tables:

```shell
aws dynamodb create-table \
--table-name job_definition \
--attribute-definitions AttributeName=job_name,AttributeType=S \
--key-schema AttributeName=job_name,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
```

```shell
aws dynamodb create-table \
--table-name job_execution \
--attribute-definitions AttributeName=job_name,AttributeType=S \
--key-schema AttributeName=job_name,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
```

```shell
aws dynamodb create-table \
--table-name lock_metadata \
--attribute-definitions AttributeName=job_name,AttributeType=S \
--key-schema AttributeName=job_name,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
```
