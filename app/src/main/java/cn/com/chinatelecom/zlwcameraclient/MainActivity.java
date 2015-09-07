package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.lang.reflect.Field;
import java.util.Timer;

/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class MainActivity extends Activity implements View.OnClickListener {

    private DeviceListFragment deviceList;
    private SettingFragment setting;
    private ShootFragment shoot;
    private static Boolean isQuit = false;

    Timer timer = new Timer();

    public static void actionStart(Context context){
        Intent intent = new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setOverflowShowAlways();
        setFragment();
    }

    private void setSlidingMenu(){
        SlidingMenu slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setBehindWidth(500);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.sliding_menu);
        TextView usernameView = (TextView) findViewById(R.id.username);
        TextView settingView = (TextView) findViewById(R.id.setting);
//        usernameView.setOnClickListener(this);
        settingView.setOnClickListener(this);
    }

    private void setFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(deviceList == null){
            deviceList = new DeviceListFragment();
            transaction.add(R.id.framelayout_device,deviceList,"deviceListFragment");
        }else{
            transaction.show(deviceList);
        }
        transaction.commit();
    }

    private void setOverflowShowAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
//            case R.id.username:
//                break;
            case R.id.setting:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if(setting == null){
                    setting = new SettingFragment();
                    transaction.add(R.id.framelayout_device,setting,"setting");
                }else{
                    transaction.show(setting);
                }
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
//            case android.R.id.home:
//                Intent upIntent = NavUtils.getParentActivityIntent(this);
//                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                NavUtils.navigateUpTo(this,upIntent);
//                return true;
            case R.id.shoot_video:
                ShootActivity.actionStart(MainActivity.this);
                break;
            case R.id.app_exit:
                Applications.getInstance().exit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//        boolean isPopFragment = false;
//        String currentTabTag = mTabHost.getCurrentTabTag();
//        if (currentTabTag.equals(mTextviewArray[0])) {
//            isPopFragment = ((BaseContainer)getSupportFragmentManager().findFragmentByTag(mTextviewArray[0])).popFragment();
//        } else if (currentTabTag.equals(mTextviewArray[1])) {
//            isPopFragment = ((BaseContainer)getSupportFragmentManager().findFragmentByTag(mTextviewArray[1])).popFragment();
//        } else if (currentTabTag.equals(mTextviewArray[2])) {
//            isPopFragment = ((BaseContainer) getSupportFragmentManager().findFragmentByTag(mTextviewArray[2])).popFragment();
//        }
//        if (!isPopFragment) {
//            if (isQuit == false) {
//                isQuit = true;
//                Toast.makeText(getBaseContext(), getString(R.string.exit_msg), Toast.LENGTH_SHORT).show();
//                TimerTask task;
//                task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        isQuit = false;
//                    }
//                };
////                schedule(TimerTask task, long delay)
//                timer.schedule(task, 2000);
//            } else {
//                Applications.getInstance().exit();
//            }
//        }
//    }
}
