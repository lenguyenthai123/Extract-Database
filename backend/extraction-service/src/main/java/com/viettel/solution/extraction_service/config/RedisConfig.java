package com.viettel.solution.extraction_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName("localhost"); // Đặt host là 'redis'
        redisStandaloneConfiguration.setPort(6379); // Đặt port là 6379
        // Nếu bạn có username và password, bạn có thể đặt chúng ở đây
        // redisStandaloneConfiguration.setUsername("your-username");
        // redisStandaloneConfiguration.setPassword("your-password");
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig() //
                .entryTtl(Duration.ofHours(2)) // Thời gian sống của cache
                .disableCachingNullValues() // Không cache giá trị null
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // Serialize key
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())); // Serialize value

        return RedisCacheManager.builder(connectionFactory) // Tạo RedisCacheManager
                .cacheDefaults(config) // Cấu hình mặc định cho cache
                .build(); // Xây dựng RedisCacheManager
    }
}
