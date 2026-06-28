package com.piano.management.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 仅当 spring.redis.host 配置了非空值时启用 Redis；
 * 云端未配置 Redis 时自动跳过，避免启动失败。
 */
@Configuration
@ConditionalOnProperty(name = "spring.redis.host", matchIfMissing = false)
@Import(RedisAutoConfiguration.class)
public class RedisConditionalConfig {
}
