package cn.com.chinatelecom.zlwcameraclient;

/**
 * Created by Zouyiran on 2014/11/24.
 *
 */

public class Config {
    static public String server = "222.197.180.143";//113.240.243.180
    static public String port = "8087";
    static public int videoQuality = 50;
    static public String loginAPI = "/api?method=login";
    static public String getDeviceAPI = "/api?method=getDevices";
    static public String getRecordAPI = "/api?method=getRecords";
    static public String getPhoneStatusAPI = "/api?method=getPhoneStatus";
    static public String registerAPI = "/api?method=registerPhone";
    static public String getPhoneEnableAPI = "/api?method=checkPhoneEnable";
    static public String startRecordAPI = "/api?method=startPhone";
    static public String stopRecordAPI = "/api?method=stopPhone";
}
