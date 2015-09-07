package cn.com.chinatelecom.zlwcameraclient;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.chinatelecom.zlwcameraclient.tools.Config;

import java.util.*;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */


public class DeviceListFragment extends Fragment{
    private int START_GETTING_DEVICELIST = 0;
    private int GETTING_DEVICELIST_SUCCESS = 1;
    private int GETTING_DEVICELIST_FAIL = 2;
    private LinearLayout deviceList;
    private LinearLayout loading;
    private String result = "";
    private Map<String, Map<String, String>> deviceMap = new HashMap<String, Map<String, String>>();
    private View rootView;
    private DeviceDetailFragment deviceDetail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_device_list, container, false);
        deviceList = (LinearLayout) rootView.findViewById(R.id.devicelist);
        loading = (LinearLayout) rootView.findViewById(R.id.device_loading);
        requestDevices();
        setActionbar();
//        ActionBar actionBar = getActivity().getActionBar();
//        actionBar.setTitle(getResources().getString(R.string.devicelist_title));
//        actionBar.setIcon(R.drawable.play);
//        actionBar.setDisplayHomeAsUpEnabled(false);
        return rootView;
    }

    private void setActionbar(){
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);//显示返回图标
            actionBar.setDisplayShowHomeEnabled(false);//显示app图标
            actionBar.setTitle(getActivity().getResources().getString(R.string.device_list));
        }
    }
    private void requestDevices() {
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = START_GETTING_DEVICELIST;
                handler.sendMessage(msg);
                String param = String.format("username=%s", Globals.username);
                try {
                    String api = "http://" + Config.server + ":" + Config.port + Config.getDeviceAPI;
                    result = HttpRequest.sendPost(api, param);
                    if (result.equals("")) {
                        Message fail = new Message();
                        fail.what = GETTING_DEVICELIST_FAIL;
                        handler.sendMessage(fail);
                    }
                    else {
                        Message success = new Message();
                        success.what = GETTING_DEVICELIST_SUCCESS;
                        handler.sendMessage(success);
                    }

                } catch (Exception e){
                    Message fail = new Message();
                    fail.what = GETTING_DEVICELIST_FAIL;
                    handler.sendMessage(fail);
                }
            }
        };
        new Thread(requestThread).start();
    }
    private Handler handler = new Handler() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == START_GETTING_DEVICELIST) {
                loading.setVisibility(View.VISIBLE);
            }
            else if (msg.what == GETTING_DEVICELIST_SUCCESS) {
                loading.setVisibility(View.GONE);
                try {
                    List devices;
                    devices = Functions.readJson(result);
                    for (int i = 0; i < devices.size(); i++) {
                        Map<String, String> device = (Map)devices.get(i);
                        View deviceView = createDeviceView(device);
                        deviceMap.put(String.valueOf(deviceView.getId()), device);
                        deviceList.addView(deviceView);

                        int height = (int)(1 * getResources().getDisplayMetrics().density);
                        LinearLayout.LayoutParams lineP = new LinearLayout.LayoutParams(
                                LayoutParams.FILL_PARENT, height);
                        //int margin = (int)(2 * getResources().getDisplayMetrics().density);
                        //lineP.bottomMargin = margin;
                        //lineP.topMargin = margin;


                        LinearLayout line = new LinearLayout(getActivity());
                        line.setBackground(getResources().getDrawable(R.color.huise));
                        line.setLayoutParams(lineP);//设置布局参数
                        deviceList.addView(line);
                    }
                } catch (Exception e ) {
                    ProgressBar loadingImage = (ProgressBar) rootView.findViewById(R.id.device_loadiing_bar);
                    loadingImage.setVisibility(View.GONE);
                    TextView loadingText = (TextView) rootView.findViewById(R.id.device_loading_text);
                    loadingText.setText(getResources().getString(R.string.devicelist_error));
                }
            }
            else {
                ProgressBar loadingImage = (ProgressBar) rootView.findViewById(R.id.device_loadiing_bar);
                loadingImage.setVisibility(View.GONE);
                TextView loadingText = (TextView) rootView.findViewById(R.id.device_loading_text);
                loadingText.setText(getResources().getString(R.string.devicelist_fail));
            }
        }
    };
    private View createDeviceView(Map<String, String> device) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        LinearLayout view = new LinearLayout(getActivity());
        view.setLayoutParams(lp);//设置布局参数
        view.setOrientation(LinearLayout.HORIZONTAL);// 设置子View的Linearlayout// 为垂直方向布局
        int paddingValueInPx = (int)(5 * getResources().getDisplayMetrics().density);
        view.setPadding(paddingValueInPx, paddingValueInPx, paddingValueInPx, paddingValueInPx);
        view.setClickable(true);
        view.setBackground(getResources().getDrawable(R.drawable.linear_background));
        //定义ImageView的属性
        LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        imageLp.gravity = Gravity.CENTER_VERTICAL;
        ImageView typeImage = new ImageView(getActivity());
        typeImage.setLayoutParams(imageLp);
        if (device.get("type").equals(getResources().getString(R.string.detail_hdcamera))) {
            typeImage.setImageResource(R.drawable.hd);
        }
        else if (device.get("type").equals(getResources().getString(R.string.detail_sdcamera))) {
            typeImage.setImageResource(R.drawable.sd);
        }
        else if (device.get("type").equals(getResources().getString(R.string.detail_phone))) {
            typeImage.setImageResource(R.drawable.phone);
        }
        else {
            typeImage.setImageResource(R.drawable.unknown);
        }


        //定义文字Layout
        int heightInPx = (int)(60 * getResources().getDisplayMetrics().density);
        LayoutParams textLp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                heightInPx
        );
        LinearLayout textLineLayout = new LinearLayout(getActivity());
        textLineLayout.setLayoutParams(textLp);
        textLineLayout.setGravity(Gravity.CENTER_VERTICAL);
        textLineLayout.setOrientation(LinearLayout.VERTICAL);
        int textPaddingPx = (int)(15 * getResources().getDisplayMetrics().density);
        textLineLayout.setPadding(textPaddingPx, 0, textPaddingPx, 0);

        //定义设备名Layout
        LayoutParams nameLp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        TextView name = new TextView(getActivity());
        name.setLayoutParams(nameLp);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        name.setText(device.get("name"));

        //定义设备地址 Layout
        LayoutParams addrLp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        TextView deviceAddr = new TextView(getActivity());
        deviceAddr.setLayoutParams(addrLp);
        deviceAddr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        deviceAddr.setText(device.get("province") + device.get("city") + device.get("county") + device.get("street"));


        textLineLayout.addView(name);
        textLineLayout.addView(deviceAddr);

        view.addView(typeImage);//将TextView 添加到子View 中
        view.addView(textLineLayout);//将TextView 添加到子View 中

        view.setOnClickListener(deviceClickListener);
        view.setId(Integer.parseInt(device.get("id")));
        return view;
    }

    private View.OnClickListener deviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Globals.NOW_DEVICE = deviceMap.get(String.valueOf(v.getId()));
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            deviceDetail = new DeviceDetailFragment();
            transaction.replace(R.id.framelayout_device, deviceDetail,"deviceDetailFragment");
            transaction.addToBackStack(null);
            transaction.commit();


            /*
            String rtmp = deviceMap.get(String.valueOf(v.getId())).get("url");
            Intent intent = new Intent();
            intent.setClass(getActivity(), PlayerActivity.class);

            if (intent != null) {
                intent.putExtra("path", rtmp);
                startActivity(intent);
            }
            */
        }
    };
}  
