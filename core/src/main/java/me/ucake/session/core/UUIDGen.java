package me.ucake.session.core;

import java.util.UUID;

/**
 * Created by alexqdjay on 2017/9/3.
 */
public class UUIDGen {
    private UUIDGen() {
    }

    public static String gen() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
