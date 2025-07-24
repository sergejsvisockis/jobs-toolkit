package io.github.sergejsvisockis.jobs.demo.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.mongo.lockrepository.MongoLockRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import io.github.sergejsvisockis.jobs.mongo.repository.MongoJobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public JobRepository jobRepository(MongoDatabase mongoDatabase) {
        return new MongoJobRepository(mongoDatabase);
    }

    @Bean
    public LockRepository lockRepository(MongoDatabase mongoDatabase) {
        return new MongoLockRepository(mongoDatabase);
    }

    @Bean
    public MongoDatabase dataSource(MongoClient mongoClient) {
        return mongoClient.getDatabase("jobs_db");
    }

    @Bean
    public MongoClient mongoClient(@Value("${mongo.url}") String mongoUri) {
        return MongoClients.create(mongoUri);
    }

}
