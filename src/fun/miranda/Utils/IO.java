package fun.miranda.Utils;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class IO {
    public static String serialize(Object object) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(stream);
            data.writeObject(object);
            data.close();
            return Base64.getEncoder().encodeToString(stream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Object deserialize(String data) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream inputStream = new BukkitObjectInputStream(stream);
            Object object = inputStream.readObject();
            inputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
