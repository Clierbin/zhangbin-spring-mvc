package com.zhangbin.spring.demo.log;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Slf4j
public class LogTest {

    public static void main(String[] args) {
        log.trace("6666");
        log.debug("6666");
        log.info("6666");
        log.warn("6666");
        log.error("6666");
    }
}
