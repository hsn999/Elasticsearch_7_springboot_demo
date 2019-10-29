package com.start.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"com.start"}, exclude = DataSourceAutoConfiguration.class)
public class EsDemoService
{
    public static void main( String[] args )
    {
        SpringApplication.run(EsDemoService.class, args);
    }
}
