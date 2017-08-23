package com.oyh.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by hang.ouyang on 2017/8/23 16:49.
 */
@SpringBootApplication
@ComponentScan("com.oyh.common")
public class SiteMain {
    public static void main(String[] args) {
        new SpringApplication(SiteMain.class).run(args);
    }
}
