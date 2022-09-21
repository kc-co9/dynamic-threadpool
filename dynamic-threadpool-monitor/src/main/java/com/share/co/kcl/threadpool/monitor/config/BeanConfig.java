package com.share.co.kcl.threadpool.monitor.config;

import com.share.co.kcl.threadpool.monitor.factory.SpringDomainFactory;
import com.share.co.kcl.threadpool.monitor.sdk.CacheClient;
import com.share.co.kcl.threadpool.monitor.sdk.RedisCacheClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class BeanConfig {

    @Bean
    public CacheClient cacheClient(RedisTemplate<String, String> redisTemplate) {
        return new RedisCacheClient(redisTemplate);
    }

    @Bean
    public SpringDomainFactory springDomainFactory(ApplicationContext applicationContext) {
        return new SpringDomainFactory(applicationContext);
    }

}
