package cn.com.chinatelecom.zlwcameraclient;

import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zouyiran on 2014/11/25.
 *
 */
public class Functions {
    public static List<Map<String, String>> readJson(String jsonStr) throws Exception{
        List result = new ArrayList<Map<String, String>>();
        JsonReader reader = new JsonReader(new StringReader(jsonStr));
        reader.beginArray();
        while (reader.hasNext()) {
            Map oneresult = new HashMap();
            reader.beginObject();
            while(reader.hasNext()){
                String key = reader.nextName();
                String value = reader.nextString();
                oneresult.put(key, value);
            }
            result.add(oneresult);
            reader.endObject();
        }
        reader.endArray();
        return result;
    }
    public static String getID() {
        String result;
        try {
            result = Globals.telemanager.getDeviceId();
        } catch (Exception ignored) {
            result = null;
        }

        if (result == null) {
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class, String.class);
                result = (String)(get.invoke(c, "ro.serialno", "unknown"));
            } catch (Exception ignored) {
                result = "unknown";
            }
        }
        if (result.equals("unknown")) {
            try {
                result = android.provider.Settings.Secure.getString(Globals.resolver, android.provider.Settings.Secure.ANDROID_ID);
            } catch (Exception ignored) {
                result = "unknown";
            }
        }
        return result;
    }
}

