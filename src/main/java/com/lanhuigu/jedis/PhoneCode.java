package com.lanhuigu.jedis;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * @author: HeChengyao
 * @date: 2021/9/2 14:38
 */
public class PhoneCode {
    private static final String HOST_NAME = "192.168.10.102";
    private static final int PORT = 6379;
    private static final String PASS_WORD = "666666";
    private static final String VERIFYCODE = "verifyCode";
    private static final String COUNT = ":count";
    private static final String CODE = ":code";
    private static final Logger logger = LoggerFactory.getLogger(PhoneCode.class);
    private static Jedis jedis;
    private static String phone;

    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            String code = getRandonCode();
            logger.info(">>>>>> code = {}", code);
            phone = "18713527619";
            verifyCode(phone, code);
            getRedisCode(phone, code);
        }

        // logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        // String code = getRandonCode();
        // logger.info(">>>>>> code = {}", code);
        // phone = "18713527619";
        // verifyCode(phone, code);
        //
        //  getRedisCode(phone, code);
    }

    public static void getRedisCode(String phone, String code) {
        // 从 redis 获取验证码
        login();
        // 验证码 key
        String codeKey = VERIFYCODE + phone + CODE;
        String redisCode = jedis.get(codeKey);
        // 判断
        if (StringUtils.equals(code, redisCode)) {
            logger.info(">>>>>> 成功");
        } else {
            logger.info(">>>>>> 失败");
            close();
        }

    }

    // 2. 每个手机只能发送三次，验证码放到 redis 中，设置过期时间
    public static void verifyCode(String phone, String code) {
        login();
        String countKey = VERIFYCODE + phone + COUNT;
        String codeKey = VERIFYCODE + phone + CODE;
        String count = jedis.get(countKey);
        if (null == count) {
            // 没有发送次数，第一次发送
            // 设置发送次数为1
            jedis.setex(countKey, 60 * 60 * 24, "1");
            logger.info("没有发送次数，今天第1次发送");
            setRedisCode(code, codeKey);
        } else if (Integer.parseInt(count) <= 2) {
            jedis.incr(countKey);
            logger.info("今天第{}次发送", Integer.parseInt(count) + 1);
            setRedisCode(code, codeKey);
        } else if (Integer.parseInt(count) > 2) {
            logger.info("今天发送次数已经超过三次");
            close();
        }
    }

    private static void setRedisCode(String code, String codeKey) {
        // 发送验证码，放到 redis 里面
        logger.info("发送验证码，放到 redis 里面");
        // String code = getRandonCode();
        // logger.info(">>>>>> code = {}", code);
        jedis.setex(codeKey, 60 * 2, code);
    }

    public static void login() {
        jedis = new Jedis(HOST_NAME, PORT);
        jedis.auth(PASS_WORD);
        logger.info(">>>>>> jedis : login()");
    }

    public static void close() {
        logger.info(">>>>>> jedis.close()");
        jedis.close();
    }

    // 1. 生成6位数字验证码
    public static String getRandonCode() {
        String randomCode = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int rand = random.nextInt(10);
            randomCode += rand;
        }
        return randomCode;
    }

}
