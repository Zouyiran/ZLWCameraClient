package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinatelecom.zlwcameraclient.tools.LogUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

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
        LogUtil.d("MainActivity","-->onCreate");
        setContentView(R.layout.activity_main);
        setOverflowShowAlways();
        setFragment();
        Applications.getInstance().addActivity(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int heapSize = manager.getMemoryClass();
        LogUtil.d("MainActivity","-->onStart");
        LogUtil.d("MainActivity",String.valueOf(heapSize));

    }

    @Override
    public void onResume(){
        super.onResume();
        LogUtil.d("MainActivity","-->onResume");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        LogUtil.d("MainActivity","onConfigurationChanged");
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
        fragmentManager.executePendingTransactions();

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
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.shoot_video:
                ShootActivity.actionStart(MainActivity.this);
                break;
            case R.id.setting:
                SettingActivity.actionStart(MainActivity.this);
                break;
            case R.id.logout:
                LoginActivity.actionStart(MainActivity.this);
                MainActivity.this.finish();
                break;
            case R.id.app_exit:
                Applications.getInstance().exit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        DeviceListFragment deviceListFragment = (DeviceListFragment)fragmentManager.findFragmentByTag("deviceListFragment");
        DeviceDetailFragment deviceDetailFragment = (DeviceDetailFragment) fragmentManager.findFragmentByTag("deviceDetailFragment");
        RecordListFragment  recordListFragment = (RecordListFragment) fragmentManager.findFragmentByTag("recordListFragment");
//       按照目前的写法，每次只会有一个fragment不为null
//       因为没有将transaction加入到backstack中,每次remove掉的旧fragment就会被destroy
//        如果加入进backstack中,则旧的fragment处于stop状态
        if(deviceListFragment != null && deviceListFragment.isAdded()){
            appExit();
        }else if(deviceDetailFragment != null && deviceDetailFragment.isAdded()){
//            fragmentManager.popBackStack();
            DeviceListFragment deviceListFragment1 = new DeviceListFragment();
//            transaction.remove(deviceDetailFragment);
            transaction.replace(R.id.framelayout_device, deviceListFragment1,"deviceListFragment");
            transaction.commit();
            fragmentManager.executePendingTransactions();
        }else if(recordListFragment != null && recordListFragment.isAdded()){
//            fragmentManager.popBackStack();
            DeviceDetailFragment deviceDetailFragment1 = new DeviceDetailFragment();
//            transaction.remove(recordListFragment);
            transaction.replace(R.id.framelayout_device,deviceDetailFragment1,"deviceDetailFragment");
            transaction.commit();
            fragmentManager.executePendingTransactions();
        }
    }

    private void appExit(){
        if (!isQuit) {
            isQuit = true;
            Toast.makeText(getBaseContext(), getString(R.string.exit_msg), Toast.LENGTH_SHORT).show();
            TimerTask task;
            task = new TimerTask() {
                @Override
                public void run() {
                    isQuit = false;
                }
            };
            timer.schedule(task, 2000);
        } else {
            Applications.getInstance().exit();
        }
    }
}
