package com.oyh.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by hang.ouyang on 2017/8/19 15:42.
 */
@SpringBootApplication
@ComponentScan("com.oyh.es")
public class SiteMain {
    public static void main(String[] args) {
        new SpringApplication(SiteMain.class).run(args);
    }
}
