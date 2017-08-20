package me.ucake.session.samples.springmvc;

import me.ucake.session.web.SimpleSessionFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Created by alexqdjay on 2017/8/20.
 */
@SpringBootApplication
public class AppMainRun {

    public static void main(String[] args) {
        SpringApplication.run(AppMainRun.class);
    }


    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new SimpleSessionFilter());
        filterRegistrationBean.setOrder(Integer.MIN_VALUE);
        return filterRegistrationBean;
    }

}
