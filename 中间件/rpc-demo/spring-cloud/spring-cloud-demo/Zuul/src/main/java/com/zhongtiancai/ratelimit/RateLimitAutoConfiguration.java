package com.zhongtiancai.ratelimit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.zhongtiancai.filter.EnableDebugFilter;
import com.zhongtiancai.filter.ErrorHandlerFileter;
import com.zhongtiancai.filter.RateLimitFilter;
import com.zhongtiancai.ratelimit.local.LocalRateLimiter;
import com.zhongtiancai.ratelimit.redis.RedisRateLimiter;

/**
 * Created by pktczwd on 2016/10/31.
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(name = "zuul.ratelimit.enabled", matchIfMissing = false)
public class RateLimitAutoConfiguration {

    @Bean
    public RateLimitFilter rateLimitFilter(RateLimiter rateLimiter, RateLimitProperties rateLimitProperties, RouteLocator routeLocator) {
        return new RateLimitFilter(rateLimiter, rateLimitProperties, routeLocator);
    }

    @Bean
    public EnableDebugFilter enableDebugFilter() {
        return new EnableDebugFilter();
    }

    @Bean
    public ErrorHandlerFileter errorHandlerFileter() {
        return new ErrorHandlerFileter();
    }

    @ConditionalOnMissingBean(RateLimiter.class)
    @ConditionalOnProperty(name = "zuul.ratelimit.type", havingValue = "redis")
    @ConditionalOnClass(RedisTemplate.class)
    public static class RedisRateLimiterConfigration {

        @Bean
        public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
            return new StringRedisTemplate(cf);
        }

        @Bean
        public RateLimiter redisRateLimiter(StringRedisTemplate stringRedisTemplate) {
            return new RedisRateLimiter(stringRedisTemplate);
        }
    }

    @ConditionalOnMissingBean(RateLimiter.class)
    @ConditionalOnProperty(name = "zuul.ratelimit.type", havingValue = "local", matchIfMissing = true)
    public static class LocalRateLimiterConfiguration {
        @Bean
        public RateLimiter localRateLimiter() {
            return new LocalRateLimiter();
        }
    }


}
