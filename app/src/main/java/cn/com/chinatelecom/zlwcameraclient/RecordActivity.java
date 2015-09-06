package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;
import java.util.Timer;

/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class RecordActivity extends Activity {

    private DeviceListFragment deviceList;
    private static Boolean isQuit = false;
    Timer timer = new Timer();

    public static void actionStart(Context context){
        Intent intent = new Intent(context,RecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setOverflowShowAlways();
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
            case R.id.record_video:
                Intent intent = new Intent(RecordActivity.this, RecordActivity.class);
                startActivity(intent);
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
