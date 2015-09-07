package cn.com.chinatelecom.zlwcameraclient;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.chinatelecom.zlwcameraclient.tools.Config;

import java.util.Map;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */


public class ShootFragment extends Fragment {
    private View view;
    private int NOT_REGISTER = 0;
    private int REGISTERED = 1;
    private int DIABLED = 2;
    private int ENABLED = 3;
    private int WRONG = 4;
    private int DEVICE_GETTED = 5;
    private int START_RECORD = 6;
    private ProgressBar progressBar;
    private TextView loadingText;
    private TextView statusText;
    private Button recordButton;
    private Map<String, String> device;
    private int port;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_shoot, container, false);
//        ActionBar actionBar = getActivity().getActionBar();
//        actionBar.setTitle(getResources().getString(R.string.record_title));
//        actionBar.setIcon(R.drawable.camera);
        progressBar = (ProgressBar) view.findViewById(R.id.camera_loading);
        loadingText = (TextView) view.findViewById(R.id.camera_loading_text);
        statusText = (TextView) view.findViewById(R.id.camera_status_text);
        recordButton = (Button) view.findViewById(R.id.recordButton);

        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.GONE);
        recordButton.setVisibility(View.GONE);

        recordButton.setOnClickListener(recordListener);
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

    private View.OnClickListener recordListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            requestDevices();
        }
    };
    private void checkIfRegister() {
        final String serial = Functions.getID();
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String param = String.format("serial=%s", serial);
                String api = "http://" + Config.server + ":" + Config.port + Config.getPhoneStatusAPI;
                String result = HttpRequest.sendPost(api, param);
                if (result.equals("0")) {
                    Message statusmsg = new Message();
                    statusmsg.what = NOT_REGISTER;
                    handler.sendMessage(statusmsg);
                }
                else {
                    Message statusmsg = new Message();
                    statusmsg.what = REGISTERED;
                    try {
                        Globals.deviceID = Integer.valueOf(result);
                    } catch (Exception e) {
                        Message wrongmsg = new Message();
                        wrongmsg.what = WRONG;
                        handler.sendMessage(wrongmsg);
                    }

                    handler.sendMessage(statusmsg);
                }
            }
        };
        new Thread(requestThread).start();
    }

    private void requestDevices() {
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String param = String.format("deviceid=%s", Globals.deviceID);
                try {
                    String api = "http://" + Config.server + ":" + Config.port + Config.getDeviceAPI;
                    String result = HttpRequest.sendPost(api, param);
                    device = Functions.readJson(result).get(0);
                    Message statusmsg = new Message();
                    statusmsg.what = DEVICE_GETTED;
                    handler.sendMessage(statusmsg);
                } catch (Exception e){
                    Message statusmsg = new Message();
                    statusmsg.what = WRONG;
                    handler.sendMessage(statusmsg);
                }
            }
        };
        new Thread(requestThread).start();
    }

    private void startRecord() {
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String param = String.format("deviceid=%s", Globals.deviceID);
                try {
                    String api = "http://" + device.get("server_addr") + ":" + device.get("server_port") + Config.startRecordAPI;
                    String result = HttpRequest.sendPost(api, param);
                    port = Integer.valueOf(result);

                    Message statusmsg = new Message();
                    statusmsg.what = START_RECORD;
                    handler.sendMessage(statusmsg);
                } catch (Exception e){
                    Message statusmsg = new Message();
                    statusmsg.what = WRONG;
                    handler.sendMessage(statusmsg);
                }
            }
        };
        new Thread(requestThread).start();
    }


    private void checkIfEnabled() {
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String param = String.format("deviceid=%d", Globals.deviceID);
                String api = "http://" + Config.server + ":" + Config.port + Config.getPhoneEnableAPI;
                String result = HttpRequest.sendPost(api, param);
                if (result.equals("0")) {
                    Message statusmsg = new Message();
                    statusmsg.what = DIABLED;
                    handler.sendMessage(statusmsg);
                }
                else if (result.equals("1")){
                    Message statusmsg = new Message();
                    statusmsg.what = ENABLED;
                    handler.sendMessage(statusmsg);
                }
                else {
                    Message statusmsg = new Message();
                    statusmsg.what = WRONG;
                    handler.sendMessage(statusmsg);
                }
            }
        };
        new Thread(requestThread).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == NOT_REGISTER) {
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                statusText.setVisibility(View.VISIBLE);
                recordButton.setVisibility(View.GONE);
                statusText.setText(getResources().getString(R.string.record_not_register));
            }
            else if (msg.what == REGISTERED){
                checkIfEnabled();
            }
            else if (msg.what == DIABLED) {
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                statusText.setVisibility(View.VISIBLE);
                recordButton.setVisibility(View.GONE);
                statusText.setText(getResources().getString(R.string.record_disabled));
            }
            else if (msg.what == ENABLED) {
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                recordButton.setVisibility(View.VISIBLE);
            }
            else if (msg.what == DEVICE_GETTED) {
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                recordButton.setVisibility(View.VISIBLE);
                startRecord();
            }
            else if (msg.what == START_RECORD) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), CameraActivity.class);
                intent.putExtra("server", device.get("server_addr"));
                intent.putExtra("port", port);
                intent.putExtra("manage_port", device.get("server_port"));
                startActivity(intent);
            }
            else {
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                statusText.setVisibility(View.VISIBLE);
                recordButton.setVisibility(View.GONE);
                statusText.setText(getResources().getString(R.string.record_error));
            }
        }
    };

}
