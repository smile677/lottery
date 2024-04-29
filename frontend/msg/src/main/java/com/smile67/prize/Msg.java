package com.smile67.prize;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@MapperScan("com.smile67.prize.commons.db.mapper")
public class Msg {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Msg.class).run(args);
    }

}