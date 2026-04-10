package com.example.demo.config;

import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableCaching
public class CachingConfig {

  //    @Bean
  //    public CacheManager cacheManager() {
  //        return new ConcurrentMapCacheManager("myCache");
  //    }
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

    // downloadCache
    RedisCacheConfiguration downloadCacheConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    RedisSerializer.byteArray()));

    // zipCache
    RedisCacheConfiguration zipCacheConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    RedisSerializer.byteArray()));

    // default (JSON)
    RedisCacheConfiguration defaultConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()));

    // 🔥 CACHE pentru imagini procesate
    RedisCacheConfiguration imageCacheConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)) // mai mare pentru imagini
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    RedisSerializer.byteArray()));

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(defaultConfig)
        .withCacheConfiguration("downloadCache", downloadCacheConfig)
        .withCacheConfiguration("zipCache", zipCacheConfig)
        .withCacheConfiguration("imageCache", imageCacheConfig)
        .build();
  }
}
