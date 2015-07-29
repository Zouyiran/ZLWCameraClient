package cn.com.chinatelecom.zlwcameraclient;

import android.content.ContentResolver;
import android.telephony.TelephonyManager;
import java.util.Map;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */
public class Globals {
    static public String username;
    static public Map<String, String> NOW_DEVICE;
    static public int deviceID;
    static TelephonyManager telemanager;
    static ContentResolver resolver;
}
