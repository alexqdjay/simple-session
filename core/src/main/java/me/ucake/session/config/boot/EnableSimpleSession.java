package me.ucake.session.config.boot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by alexqdjay on 2018/3/17.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SimpleSessionConfig.class})
public @interface EnableSimpleSession {
}
