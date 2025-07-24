package io.github.sergejsvisockis.jobs.redislock;

import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.redis.lockrepository.RedisLockRepository;
import io.github.sergejsvisockis.jobs.repository.JdbcJobRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.sql.DataSource;
import java.time.Duration;

@Configuration
public class Config {

    @Bean
    public JobRepository jobRepository(DataSource dataSource) {
        return new JdbcJobRepository(dataSource);
    }

    @Bean
    public JedisPool jedisPool(@Value("${redis.host}") String host,
                               @Value("${redis.port}") Integer port) {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setJmxEnabled(false);
        poolConfig.setMinEvictableIdleDuration(Duration.ofSeconds(60));
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return new JedisPool(poolConfig, host, port);
    }

    @Bean
    public LockRepository redisLockRepository(JedisPool jedisPool) {
        return new RedisLockRepository(jedisPool);
    }

}
