package com.assignment.assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.dao.DataAccessException;

@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void updateToCache(String map, String key, String value) {
        try {
            redisTemplate.opsForHash().put(map, key, value);
            System.out.println("Successfully updated cache");
        } catch (DataAccessException e) {
            System.out.println("Failed to update cache. Retrying...");
            throw e;
        }
    }

    public void deleteFromCache(String map, String key) {
        redisTemplate.opsForHash().delete(map,key);
    }

    public String getFromCache(String map, String key) {
        return (String)redisTemplate.opsForHash().get(map,key);
    }

}
