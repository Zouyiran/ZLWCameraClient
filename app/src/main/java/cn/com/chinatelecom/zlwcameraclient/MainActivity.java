package cn.com.chinatelecom.zlwcameraclient;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class MainActivity extends FragmentActivity{
    private FragmentTabHost mTabHost;

    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {DeviceListContainer.class, RecordContainer.class, SettingsContainer.class};

    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.play,R.drawable.camera,R.drawable.settings};

    //Tab选项卡的文字
    private String mTextviewArray[] = {"监控列表", "录制视频", "设置"};

    private static Boolean isQuit = false;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Applications.getInstance().addActivity(this);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView(){
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //得到fragment的个数
        int count = fragmentArray.length;

        for(int i = 0; i < count; i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }
    //处理点击ActionBar上的返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String currentTabTag = mTabHost.getCurrentTabTag();
                if (currentTabTag.equals(mTextviewArray[0])) {
                    ((BaseContainer)getSupportFragmentManager().findFragmentByTag(mTextviewArray[0])).popFragment();
                } else if (currentTabTag.equals(mTextviewArray[1])) {
                    ((BaseContainer)getSupportFragmentManager().findFragmentByTag(mTextviewArray[1])).popFragment();
                } else if (currentTabTag.equals(mTextviewArray[2])) {
                    ((BaseContainer) getSupportFragmentManager().findFragmentByTag(mTextviewArray[2])).popFragment();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //处理按下Android返回键
    @Override
    public void onBackPressed() {
        boolean isPopFragment = false;
        String currentTabTag = mTabHost.getCurrentTabTag();
        if (currentTabTag.equals(mTextviewArray[0])) {
            isPopFragment = ((BaseContainer)getSupportFragmentManager().findFragmentByTag(mTextviewArray[0])).popFragment();
        } else if (currentTabTag.equals(mTextviewArray[1])) {
            isPopFragment = ((BaseContainer)getSupportFragmentManager().findFragmentByTag(mTextviewArray[1])).popFragment();
        } else if (currentTabTag.equals(mTextviewArray[2])) {
            isPopFragment = ((BaseContainer) getSupportFragmentManager().findFragmentByTag(mTextviewArray[2])).popFragment();
        }
        if (!isPopFragment) {
            if (isQuit == false) {
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
}
