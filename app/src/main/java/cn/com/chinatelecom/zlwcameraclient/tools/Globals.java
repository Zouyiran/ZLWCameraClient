package cn.com.chinatelecom.zlwcameraclient.tools;

import android.content.ContentResolver;
import android.telephony.TelephonyManager;
import cn.com.chinatelecom.zlwcameraclient.data_struct.Device;
import cn.com.chinatelecom.zlwcameraclient.data_struct.Record;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */
public class Globals {
    public static String username;
    public static Device NOW_DEVICE;
    public static Record NOW_RECORD;
    public static  int deviceID;
    public static TelephonyManager telemanager;
    public static ContentResolver resolver;
}
