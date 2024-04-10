package com.lottery.marketplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHostName;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private Optional<String> redisPassword;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() throws NoSuchAlgorithmException {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisHostName, redisPort);

        redisPassword.ifPresent(configuration::setPassword);

        // SSL parameters
        SSLParameters sslParameters = new SSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");

        // Build Jedis client configuration
        JedisClientConfiguration jedisClientConfiguration =
                JedisClientConfiguration.builder()
                        .useSsl()
                        .sslParameters(sslParameters)
                        .sslSocketFactory(SSLContext.getDefault().getSocketFactory())
                        .and()
                        .usePooling()
                        .build();

        return new JedisConnectionFactory(configuration, jedisClientConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory fact) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(fact);
        return template;
    }
}