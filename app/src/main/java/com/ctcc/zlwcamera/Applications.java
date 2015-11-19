package com.ctcc.zlwcamera;

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

    public static Applications getInstance() {
        if(null == instance) {
            instance = new Applications();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void exit(){
        for(Activity activity:activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
