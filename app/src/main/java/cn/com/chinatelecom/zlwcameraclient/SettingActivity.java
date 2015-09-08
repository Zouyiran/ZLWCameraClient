package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class SettingActivity extends Activity{

    private SettingFragment settingFragment;


    public static void actionStart(Context context){
        Intent intent = new Intent(context,SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoot);
        Applications.getInstance().addActivity(this);
        setFragment();
    }

    private void setFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(settingFragment == null){
            settingFragment = new SettingFragment();
            transaction.add(R.id.framelayout_shoot,settingFragment,"settingFragment");
        }else{
            transaction.show(settingFragment);
        }
        transaction.commit();
    }
}
