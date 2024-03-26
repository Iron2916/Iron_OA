package com.iron;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class test {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void TestRedis() {

//        redisTemplate.opsForValue().set("aabbbaa", "iron");
        System.out.println(redisTemplate.opsForValue().get("aabbbaa"));
    }
}
