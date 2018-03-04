package me.ucake.session;

/**
 * Created by alexqdjay on 2017/8/20.
 */
public interface Consts {

    String DEFAULT_CHARSET = "UTF-8";

    String SIMPLE_SESSION_PREFIX = "simple_session_";

    interface RedisFields {
        String FIELDS_PREFIX =  SIMPLE_SESSION_PREFIX + "sessions_%s";        // session主存储
        String EXPIRES_PREFIX = SIMPLE_SESSION_PREFIX + "expires_%s";         // 用于过期标识
        String EXPIRATIONS_PREFIX = SIMPLE_SESSION_PREFIX + "expirations_%s"; // 存储过期时刻与sessionId列表

        String SESSION_ATTR_PREFIX = "attr_";
    }

    interface RequestAttributes {
        String ATTR_CURRENT_SESSION = SIMPLE_SESSION_PREFIX + "current_session";
        String FIELD_CREATE_TIME_NAME =  "createTime";
        String FIELD_LAST_ACCESS_TIME_NAME =  "lastAccessTime";
        String FIELD_MAX_INACTIVE_INTERVAL_NAME =  "maxInactiveInterval";
    }

}
