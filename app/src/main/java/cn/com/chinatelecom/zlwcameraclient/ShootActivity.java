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

public class ShootActivity extends Activity{

    private ShootFragment shootFragment;


    public static void actionStart(Context context){
        Intent intent = new Intent(context,ShootActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoot);
        setFragment();
    }

    private void setFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(shootFragment == null){
            shootFragment = new ShootFragment();
            transaction.add(R.id.framelayout_shoot,shootFragment,"shootFragment");
        }else{
            transaction.show(shootFragment);
        }
        transaction.commit();
    }
}
