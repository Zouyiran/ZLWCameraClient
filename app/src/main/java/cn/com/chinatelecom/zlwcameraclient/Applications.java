package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */

public class Applications {
    private List<Activity> activityList = new LinkedList<Activity>();
    private static Applications instance;
    private Applications(){ }
    //单例模式中获取唯一的MyApplication实例
    public static Applications getInstance() {
        if(null == instance) {
            instance = new Applications();
        }
        return instance;
    }
    //添加Activity到容器中
    public void addActivity(Activity activity)  {
        activityList.add(activity);
    }
    //遍历所有Activity并finish
    public void exit(){
        for(Activity activity:activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
