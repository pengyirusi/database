package cn.weiyupeng;

import redis.clients.jedis.Jedis;

/**
 * Create by weiyupeng on 2021/8/29 20:40
 */
public class TestPing {
    public static void main(String[] args) {
        // 1. new Jedis 对象
        Jedis jedis = new Jedis("192.168.132.129", 6379);

        System.out.println(jedis.ping());
        jedis.set("name", "weiyupeng");
        System.out.println(jedis.get("name"));

        for (String key : jedis.keys("*")) {
            System.out.println(key);
        }

        jedis.select(3);
        jedis.flushAll();
    }
}
