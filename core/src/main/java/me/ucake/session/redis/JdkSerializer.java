package me.ucake.session.redis;


import java.io.*;

/**
 * Created by alexqdjay on 2017/9/3.
 */
public class JdkSerializer implements Serializer {
    @Override
    public byte[] encode(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        return bos.toByteArray();
    }

    @Override
    public Object decode(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
}
