package com.share.co.kcl.dtp.monitor.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author KIMVON
 * @since 2019/7/11
 */
@Configuration
public class RedisConfig {

    @Primary
    @Bean(name = "dtpStringRedisTemplate")
    public RedisTemplate<String, String> stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Primary
    @Bean(name = "dtpObjectRedisTemplate")
    public RedisTemplate<String, Object> objectRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        RedisSerializer<String> redisSerializer = new StringRedisSerializer();

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        template.setConnectionFactory(redisConnectionFactory);
        // key序列化方式
        template.setKeySerializer(redisSerializer);
        // value序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // value hashmap序列化
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }

    @Primary
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedissonConfig redissonConfig) {
        Config config = new Config();
        String address = "redis://" + redissonConfig.host + ":" + redissonConfig.port;
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(address);
        if (StringUtils.isNotBlank(redissonConfig.password)) {
            singleServerConfig.setPassword(redissonConfig.password);
        }
        if (StringUtils.isNotBlank(redissonConfig.database)) {
            singleServerConfig.setDatabase(Integer.parseInt(redissonConfig.database));
        }
        return Redisson.create(config);
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "spring.redis")
    public static class RedissonConfig {
        private String host;
        private Integer port;
        private String password;
        private String database;
    }

}
