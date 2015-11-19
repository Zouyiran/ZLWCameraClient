package com.ctcc.zlwcamera.tools;

import android.content.ContentResolver;
import android.telephony.TelephonyManager;

import com.ctcc.zlwcamera.data_struct.Device;
import com.ctcc.zlwcamera.data_struct.Record;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */
public class Globals {
    public static String nowUsername;
    public static Device nowDevice;
    public static Record nowRecord;
    public static  int deviceID;
    public static TelephonyManager telemanager;
    public static ContentResolver resolver;
}
