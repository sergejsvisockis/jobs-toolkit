package io.github.sergejsvisockis.jobs.demo;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import io.github.sergejsvisockis.jobs.aws.lockrepository.DynamoDBLockRepository;
import io.github.sergejsvisockis.jobs.aws.repository.DynamoDBJobRepository;
import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.EU_NORTH_1)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    @Bean
    public JobRepository jobRepository(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDBJobRepository(amazonDynamoDB);
    }

    @Bean
    public LockRepository lockRepository(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDBLockRepository(amazonDynamoDB);
    }

}
