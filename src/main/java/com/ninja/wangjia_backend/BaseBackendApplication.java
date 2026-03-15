package com.ninja.wangjia_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.ninja.wangjia_backend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class BaseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseBackendApplication.class, args);
    }

}
