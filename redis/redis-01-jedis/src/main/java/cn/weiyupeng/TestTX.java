package cn.weiyupeng;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * Create by weiyupeng on 2021/8/29 21:06
 */
public class TestTX {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.132.129", 6379);
        jedis.flushAll();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "weiyupeng");
        jsonObject.put("age", "25");

        // 开启事务
        Transaction multi = jedis.multi();
        String result = jsonObject.toJSONString();

        try {
            multi.mset("k1", result, "k2", result);
            // int i = 1 / 0 ; // 模拟异常

            multi.exec(); // 执行
            System.out.println("executed");
        } catch (Exception e) {
            multi.discard(); // 放弃
            System.out.println("discarded");
            e.printStackTrace();
        } finally {
            System.out.println(jedis.get("k1"));
            System.out.println(jedis.get("k2"));
            jedis.close(); // 关闭连接
            System.out.println("closed");
        }
    }
}
