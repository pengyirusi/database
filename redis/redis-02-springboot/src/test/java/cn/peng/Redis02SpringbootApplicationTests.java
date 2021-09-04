package cn.peng;

import cn.peng.pojo.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.util.Assert;

@SpringBootTest
class Redis02SpringbootApplicationTests {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Test
    public void test() throws JsonProcessingException {
        User user = new User("weiyupeng", 25);
        String jsonUser = new ObjectMapper().writeValueAsString(user);
        ValueOperations opsForValue = redisTemplate.opsForValue();
        opsForValue.set("user1", jsonUser);
        System.out.println(opsForValue.get("user1")); //{"name":"weiyupeng","age":25}
        opsForValue.set("user2", user);
        System.out.println(opsForValue.get("user2")); //User(name=weiyupeng, age=25)
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test2() {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        ZSetOperations opsForZSet = redisTemplate.opsForZSet();
        // 其他数据结构也同理，但是他们的 key 都是 Object
        // StringRedisTemplate 换成了 <String, String>

        ValueOperations<String, String> opsForValue1 = stringRedisTemplate.opsForValue();
        ZSetOperations<String, String> opsForZSet1 = stringRedisTemplate.opsForZSet();
        opsForValue1.set("name", "weiyupeng");
        Assert.isTrue("weiyupeng".equals(opsForValue1.get("name")));

        // 获取数据库连接
        RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
        redisConnection.flushDb();
    }

    @Test
    void contextLoads() {

    }

}
