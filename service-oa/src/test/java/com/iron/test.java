package com.iron;

import com.github.xiaoymin.knife4j.core.io.ResourceUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

@SpringBootTest
public class test {

//    @Autowired
//    RedisTemplate redisTemplate;

    @Test
    public void TestRedis() throws FileNotFoundException {

//        redisTemplate.opsForValue().set("aabbbaa", "iron");
//        System.out.println(redisTemplate.opsForValue().get("aabbbaa"));
        System.out.println(ResourceUtils.getURL("classpath").getPath());
    }
}
