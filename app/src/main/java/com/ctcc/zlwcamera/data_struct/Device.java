package com.ctcc.zlwcamera.data_struct;

/**
 * Created by Zouyiran on 2015/9/7.
 */
public class Device {

    private String id;
    private String name;
    private String type;
    private String url;
    private String rtsp;

    private String keep;
    private String live;
    private String record;
    private String ratio;
    private String priority;

    private String serverAddr;
    private String serverPort;

    private String province;
    private String city;
    private String county;
    private String street;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setProvince(String province){
        this.province = province;
    }
    public String getProvince(){
        return province;
    }

    public void setCity(String city){
        this.city = city;
    }
    public String getCity(){
        return city;
    }

    public void setCounty(String county){
        this.county = county;
    }
    public String getCounty(){
        return county;
    }

    public void setStreet(String street){
        this.street = street;
    }
    public String getStreet(){
        return street;
    }

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }

    public void setLive(String live){
        this.live = live;
    }
    public String getLive(){
        return live;
    }

    public void setRecord(String record){
        this.record = record;
    }
    public String getRecord(){
        return record;
    }

    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return url;
    }

    public void setRatio(String ratio){
        this.ratio = ratio;
    }
    public String getRatio(){
        return ratio;
    }
}
