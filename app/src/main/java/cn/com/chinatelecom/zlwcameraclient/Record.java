package cn.com.chinatelecom.zlwcameraclient;

/**
 * Created by Zouyiran on 2015/9/7.
 */
public class Record {

    private String id;
    private String start;
    private String end;
    private String duration;
    private String url;

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
