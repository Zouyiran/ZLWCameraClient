package com.ctcc.zlwcamera.data_struct;

/**
 * Created by Zouyiran on 2015/9/7.
 */
public class Device {

//    url->rtmp://222.197.180.143:8085/live/camera-240
//    street->���ӿƴ���ˮ��У��
//    server_addr->222.197.180.143
//    server_port->8086
//    type->���������
//    city->�ɶ�
//    id->240
//    record->ֹͣ
//    county->ۯ��
//    ratio->4:3
//    priority->1
//    name->��B109
//    province->�Ĵ�
//    keep->7
//    live->ֱ����
//    rtsp->rtsp://222.197.180.143:50000/video0.sdp

    private String id;
    private String name;
    private String type;
    private String url;//�豸rtmp
    private String rtsp;

    //��Ƶ��������,ֱ��,¼��,�������,���ȼ�
    private String keep;
    private String live;
    private String record;
    private String ratio;
    private String priority;

    //app��������
    private String serverAddr;
    private String serverPort;

    //����豸���ڵ���λ��
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
