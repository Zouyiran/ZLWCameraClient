package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

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
        Applications.getInstance().addActivity(this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this,upIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
