package com.ctcc.zlwcamera;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;

import com.ctcc.zlwcamera.tools.Globals;
import com.ctcc.zlwcamera.tools.LogUtil;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Boolean isQuit = false;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = LayoutInflater.from(this).inflate(R.layout.nav_header_main,navigationView,false);
        navigationView.addHeaderView(headerLayout);
        navigationView.setNavigationItemSelectedListener(this);

        TextView usernameView = (TextView) headerLayout.findViewById(R.id.username_text);
//        if(usernameView == null){
//            LogUtil.d("MainActivity",Globals.nowUsername);
//        }else{
        usernameView.setText(Globals.nowUsername);
//        }

        setFragment(new DeviceFragment());

        Applications.getInstance().addActivity(this);
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

    private void setFragment(Fragment fragment) {
        String fragmentTag = fragment.getClass().toString();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout_main, fragment, fragmentTag);
//        LogUtil.d("MainActivity", fragment.getClass().toString());// class com.ctcc.zlwcamera.DeviceFragment
        transaction.commit();
        fragmentManager.executePendingTransactions();

        ActionBar bar = getSupportActionBar();
        if(bar != null){
            if(fragmentTag.equals(DeviceFragment.class.toString())){
                bar.setTitle(R.string.activity_title_device);
            }else if(fragmentTag.equals(ShootFragment.class.toString())){
                bar.setTitle(R.string.activity_title_shoot);
            }else if(fragmentTag.equals(SettingFragment.class.toString())){
                bar.setTitle(R.string.title_setting);
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        DeviceFragment deviceFragment = (DeviceFragment) fragmentManager.findFragmentByTag(DeviceFragment.class.toString());
        if(deviceFragment != null){
            appExit();
        }else{
            setFragment(new DeviceFragment());
        }
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.nav_main:
                setFragment( new DeviceFragment());
                break;
            case R.id.nav_shoot:
                setFragment(new ShootFragment());
                break;
            case R.id.nav_setting:
                setFragment(new SettingFragment());
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void appExit(){
        if (!isQuit) {
            isQuit = true;
            Toast.makeText(getBaseContext(), getString(R.string.app_exit_msg), Toast.LENGTH_SHORT).show();
            TimerTask task;
            task = new TimerTask() {
                @Override
                public void run() {
                    isQuit = false;
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 2000);
        } else {
            Applications.getInstance().exit();
        }
    }
}
