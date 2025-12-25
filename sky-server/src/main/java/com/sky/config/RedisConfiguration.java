package com.sky.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;

@Configuration
@Slf4j
public class RedisConfiguration {

    /**
     * 创建支持 JSON 序列化的 RedisTemplate
     * 效果等同于 StringRedisTemplate + JSON
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建 RedisTemplate 对象...");

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 1. 创建 ObjectMapper 并注册 JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册 Java 8 时间模块（支持 LocalDateTime）
        objectMapper.registerModule(new JavaTimeModule());
        // 设置时间格式（可选）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 2. 创建 JSON 序列化器（使用配置好的 ObjectMapper）
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // 3. 创建 String 序列化器（Key 使用）
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // 4. 设置 Key 的序列化
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setHashKeySerializer(keySerializer);

        // 5. 设置 Value 的序列化
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);

        // 6. 必须执行这个，使配置生效
        redisTemplate.afterPropertiesSet();

        log.info("RedisTemplate 对象创建成功！支持 LocalDateTime 等类型");
        return redisTemplate;
    }
}
