package cn.com.chinatelecom.zlwcameraclient;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.chinatelecom.zlwcameraclient.tools.*;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */


public class ShootFragment extends Fragment {
    private static int NOT_REGISTER = 0;
    private static int REGISTERED = 1;
    private static int DIABLED = 2;
    private static int ENABLED = 3;
    private static int WRONG = 4;
    private static int DEVICE_GETTED = 5;
    private static int START_RECORD = 6;

    private static ProgressBar progressBar;
    private static TextView loadingText;
    private static TextView statusText;
    private static Button shootButton;

    private static Map<String, String> deviceServer;
    private static int port;
    private static WeakReference<ShootActivity> mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_shoot, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.camera_loading);
        loadingText = (TextView) view.findViewById(R.id.camera_loading_text);
        statusText = (TextView) view.findViewById(R.id.camera_status_text);
        shootButton = (Button) view.findViewById(R.id.recordButton);
        mActivity = new WeakReference<ShootActivity>((ShootActivity) getActivity());

        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.GONE);
        shootButton.setVisibility(View.GONE);

        shootButton.setOnClickListener(shootListener);
        checkIfRegister();
        setActionbar();
        return view;
    }

    private void setActionbar(){
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//显示返回图标
            actionBar.setDisplayShowHomeEnabled(false);//显示app图标
            actionBar.setTitle(getActivity().getResources().getString(R.string.shoot_video));
        }
    }

    private void checkIfRegister() {
        final String serial = Functions.getID();
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                String param = String.format("serial=%s", serial);
                String api = "http://" + Config.server + ":" + Config.port + Config.getPhoneStatusAPI;
                String result = HttpRequest.sendPost(api, param);
                if (result.equals("0")) {
                    Message statusmsg = new Message();
                    statusmsg.what = NOT_REGISTER;
                    mHandler.sendMessage(statusmsg);
                }
                else {
                    Message statusmsg = new Message();
                    statusmsg.what = REGISTERED;
                    try {
                        Globals.deviceID = Integer.valueOf(result);
                    } catch (Exception e) {
                        Message wrongmsg = new Message();
                        wrongmsg.what = WRONG;
                        mHandler.sendMessage(wrongmsg);
                    }
                    mHandler.sendMessage(statusmsg);
                }
            }
        };
        new Thread(requestThread).start();
    }

    private static void checkIfEnabled() {
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                String param = String.format("deviceid=%d", Globals.deviceID);
                String api = "http://" + Config.server + ":" + Config.port + Config.getPhoneEnableAPI;
                String result = HttpRequest.sendPost(api, param);
                if (result.equals("0")) {
                    Message statusmsg = new Message();
                    statusmsg.what = DIABLED;
                    mHandler.sendMessage(statusmsg);
                }
                else if (result.equals("1")){
                    Message statusmsg = new Message();
                    statusmsg.what = ENABLED;
                    mHandler.sendMessage(statusmsg);
                }
                else {
                    Message statusmsg = new Message();
                    statusmsg.what = WRONG;
                    mHandler.sendMessage(statusmsg);
                }
            }
        };
        new Thread(requestThread).start();
    }

    private View.OnClickListener shootListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            requestDevicesServer();
        }
    };

    private void requestDevicesServer() {
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                String param = String.format("deviceid=%s", Globals.deviceID);
                try {
                    String api = "http://" + Config.server + ":" + Config.port + Config.getDeviceAPI;
                    String result = HttpRequest.sendPost(api, param);
                    deviceServer = Functions.readJson(result).get(0);
                    Message statusmsg = new Message();
                    statusmsg.what = DEVICE_GETTED;
                    mHandler.sendMessage(statusmsg);
                } catch (Exception e){
                    Message statusmsg = new Message();
                    statusmsg.what = WRONG;
                    mHandler.sendMessage(statusmsg);
                }
            }
        };
        new Thread(requestThread).start();
    }

    private static void getShootPort() {
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                String param = String.format("deviceid=%s", Globals.deviceID);
                try {
                    String api = "http://" + deviceServer.get("server_addr") + ":" + deviceServer.get("server_port") + Config.startRecordAPI;
                    String result = HttpRequest.sendPost(api, param);
                    port = Integer.valueOf(result);
                    Message statusmsg = new Message();
                    statusmsg.what = START_RECORD;
                    mHandler.sendMessage(statusmsg);
                } catch (Exception e){
                    Message statusmsg = new Message();
                    statusmsg.what = WRONG;
                    mHandler.sendMessage(statusmsg);
                }
            }
        };
        new Thread(requestThread).start();
    }

    private static MHandler mHandler = new MHandler();
    private static class MHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
//            检查设备是否注册
            if (msg.what == NOT_REGISTER) {
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                statusText.setVisibility(View.VISIBLE);
                shootButton.setVisibility(View.GONE);
                statusText.setText(mActivity.get().getResources().getString(R.string.record_not_register));
                LogUtil.d("ShootFragment-->NOT_REGISTER",mActivity.get().toString());
            }
//           如果设备已经注册，检查设备是否开启拍摄功能
            else if (msg.what == REGISTERED){
                LogUtil.d("ShootFragment-->REGISTERED",mActivity.get().toString());
                checkIfEnabled();
            }
//            设备未开启拍摄功能
            else if (msg.what == DIABLED) {
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                statusText.setVisibility(View.VISIBLE);
                shootButton.setVisibility(View.GONE);
                statusText.setText(mActivity.get().getResources().getString(R.string.record_disabled));
            }
//           设备已开启拍摄功能
//            启动record按钮 shootButton.setVisibility(View.VISIBLE)
            else if (msg.what == ENABLED) {
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                shootButton.setVisibility(View.VISIBLE);
            }
//            获取属于该设备的Address和Port
//            并获取上传视频port
            else if (msg.what == DEVICE_GETTED) {
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                shootButton.setVisibility(View.VISIBLE);
                getShootPort();
            }
            else if (msg.what == START_RECORD) {
                LogUtil.d("ShootFragment","server->"+deviceServer.get("server_addr"));
                LogUtil.d("ShootFragment","port->"+port);
                LogUtil.d("ShootFragment","managePort->"+deviceServer.get("server_port"));
                CameraActivity.actionStart(mActivity.get(),deviceServer.get("server_addr"),port,deviceServer.get("server_port"));
            }
            else {
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                statusText.setVisibility(View.VISIBLE);
                shootButton.setVisibility(View.GONE);
                statusText.setText(mActivity.get().getResources().getString(R.string.record_error));
            }
        }
    }
}
