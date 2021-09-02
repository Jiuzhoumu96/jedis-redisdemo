package com.lanhuigu.jedis;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

/**
 * @author: HeChengyao
 * @date: 2021/9/2 10:02
 */
public class JedisDemo {

    private static final String HOST_NAME = "192.168.10.102";
    private static final int PORT = 6379;
    private static final String PASS_WORD = "666666";
    private static final Logger logger = LoggerFactory.getLogger(JedisDemo.class);
    private static Jedis jedis;

    public static void main(String[] args) {
        // 创建 Jedis 对象
        login();
        // 测试
        String value = jedis.ping();
        logger.info(">>>>>> jedis.ping() = {}", value);
        System.out.println(value);
    }

    // 操作 hash
    @Test
    public void demo5() {
        // 创建 Jedis 对象
        login();
        jedis.zadd("china",100d,"shanghai");
        Set<String> zset_china = jedis.zrange("china", 0, -1);
        logger.info(">>>>>> zset_chaina = {}", zset_china);
    }

    // 操作 hash
    @Test
    public void demo4() {
        // 创建 Jedis 对象
        login();
        jedis.hset("users","age","20");
        String hget_age = jedis.hget("users", "age");
        logger.info(">>>>>> hget_age = {}", hget_age);
    }

    // 操作 set
    @Test
    public void demo3() {
        // 创建 Jedis 对象
        login();
        jedis.sadd("snames","lucy");
        jedis.sadd("snames","eric");
        jedis.sadd("snames","nike");
        jedis.sadd("snames","anna");
        Set<String> snames = jedis.smembers("snames");
        logger.info(">>>>>> snames = {}", snames);
    }

    // 操作 list
    @Test
    public void demo2() {
        // 创建 Jedis 对象
        login();
        jedis.lpush("k9", "mary", "luna", "rose");
        List<String> values = jedis.lrange("k9", 0, -1);
        logger.info(">>>>>> values = {}", values);
    }

    // 操作 key String
    @Test
    public void demo1() {
        // 创建 Jedis 对象
        login();
        // 判断是否存在
        Boolean exists_k2 = jedis.exists("k2");
        logger.info(">>>>>> exists_k2 : {}", exists_k2);
        // 多参数，判断是否存在
        Long multiExists = jedis.exists("k3", "jack");
        logger.info(">>>>>> multiExists : {}", multiExists);
        // 查看过期时间，-1为永不过期
        Long ttl_k1 = jedis.ttl("k1");
        // 设置过期时间
        // jedis.expire("k1", 3600L);
        logger.info(">>>>>> ttl_k1 = {}", ttl_k1);
        // 设置多个key、value
        jedis.mset("k4", "v4", "k5", "v5", "k6", "v6", "k7", "v7", "k8", "v8");
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            logger.info(">>>>>> key = {}", key);
            String value = jedis.get(key);
            logger.info(">>>>>> key {} 的 value 为 {}", key, value);
        }
    }

    public static void login() {
        jedis = new Jedis(HOST_NAME, PORT);
        jedis.auth(PASS_WORD);
    }

}
