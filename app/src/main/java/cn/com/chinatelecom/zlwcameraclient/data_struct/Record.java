package cn.com.chinatelecom.zlwcameraclient.data_struct;

/**
 * Created by Zouyiran on 2015/9/7.
 */
public class Record {
//    id->6871
//    duration->01:00:02.93
//    start->2015-08-08 06:59:54
//    url->http://222.197.180.143:80/videos/240/2015-08-08-06-59-54.mp4
//    end->2015-08-08 07:59:57
//    deviceid->240

    private String id;
    private String start;
    private String end;
    private String duration;
    private String url;//×ÊÔ´µØÖ·

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return id;
    }

    public void setStart(String start){
        this.start = start;
    }
    public String getStart(){
        return start;
    }

    public void setEnd(String end){
        this.end = end;
    }
    public String getEnd(){
        return end;
    }

    public void setDuration(String duration){
        this.duration = duration;
    }
    public String getDuration(){
        return duration;
    }

    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return url;
    }
}
