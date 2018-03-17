package me.ucake.session.config.boot;

import me.ucake.session.FlushMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by alexqdjay on 2018/3/17.
 */
@ConfigurationProperties(prefix = "simplesession")
public class SimpleSessionProperties {

    private FlushMode flushMode = FlushMode.LAZY;
    private String strategy = "cookie"; // cookie or header

    public FlushMode getFlushMode() {
        return flushMode;
    }

    public void setFlushMode(FlushMode flushMode) {
        this.flushMode = flushMode;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}
