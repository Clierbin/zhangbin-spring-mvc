package com.zhangbin.spring.demo.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
    final static Logger logger = LoggerFactory.getLogger(LogTest.class);

    public static void main(String[] args) {
        logger.trace("6666");
        logger.debug("6666");
        logger.info("6666");
        logger.warn("6666");
        logger.error("6666");
    }
}
