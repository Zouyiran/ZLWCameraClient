package cn.com.chinatelecom.zlwcameraclient;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinatelecom.zlwcameraclient.tools.Config;
import cn.com.chinatelecom.zlwcameraclient.tools.Functions;
import cn.com.chinatelecom.zlwcameraclient.tools.Globals;
import cn.com.chinatelecom.zlwcameraclient.tools.HttpRequest;

import java.lang.ref.WeakReference;

/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class SettingFragment extends Fragment {

    private EditText address;
    private EditText port;
    private EditText quality;
    private static int NOT_REGISTER = 0;
    private static int REGISTERED = 1;
    private static int REGISTER_SUCCESS = 2;
    private static int ALREADY_REGISTERED = 3;
    private static int WRONG = 4;
    private static int deviceID;
    private static TextView statusView;
    private static WeakReference<SettingActivity> mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        mActivity = new WeakReference<SettingActivity>((SettingActivity) getActivity());

        address = (EditText) rootView.findViewById(R.id.server_addr);
        port = (EditText) rootView.findViewById(R.id.server_port);
        quality = (EditText) rootView.findViewById(R.id.quality);
        statusView = (TextView) rootView.findViewById(R.id.device_status);
        Button saveButton = (Button) rootView.findViewById(R.id.save_settings_button);

        address.setText(Config.server);
        port.setText(Config.port);
        quality.setText(String.valueOf(Config.videoQuality));
        statusView.setOnClickListener(registerListener);
        saveButton.setOnClickListener(saveSettingsListener);

        getPhoneStatus();

        setActionbar();

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void setActionbar(){
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//显示返回图标
            actionBar.setDisplayShowHomeEnabled(false);//显示app图标
            actionBar.setTitle(getActivity().getResources().getString(R.string.setting));
        }
    }

//    每一次启动Activity,都要查询设备状态
    private static void getPhoneStatus() {
        statusView.setText(mActivity.get().getResources().getString(R.string.settings_loading));
//        获取手机序列号IMEI
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
                    try {
                        deviceID = Integer.valueOf(result);
                        Globals.deviceID = deviceID;
                        Message statusmsg = new Message();
                        statusmsg.what = REGISTERED;
                        mHandler.sendMessage(statusmsg);
                    } catch (Exception e){
                        Message wrongmsg = new Message();
                        wrongmsg.what = WRONG;
                        mHandler.sendMessage(wrongmsg);
                    }

                }
            }
        };
        new Thread(requestThread).start();
    }

    private static MHandler mHandler = new MHandler();

    private static class MHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    statusView.setText(mActivity.get().getResources().getString(R.string.settings_not_register));
                    statusView.setClickable(true);
                    break;
                case 1:
                    statusView.setText(String.format(mActivity.get().getResources().getString(R.string.settings_registered), deviceID));
                    statusView.setClickable(false);
                    break;
                case 2:
                    Toast.makeText(mActivity.get(), mActivity.get().getResources().getString(R.string.settings_register_success), Toast.LENGTH_SHORT).show();
                    //注册成功之后，在次向server查询
                    getPhoneStatus();
                    break;
                case 3:
                    Toast.makeText(mActivity.get(), mActivity.get().getResources().getString(R.string.settings_already_register), Toast.LENGTH_SHORT).show();
                    getPhoneStatus();
                    break;
                case 4:
                    statusView.setText(String.format(mActivity.get().getResources().getString(R.string.settings_wrong), deviceID));
                    statusView.setClickable(false);
                    break;
                default:
                    break;
            }
        }
    }

    private static View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            statusView.setText(mActivity.get().getResources().getString(R.string.settings_registering));
            final String serial = Functions.getID();
            Runnable requestThread = new Runnable(){
                @Override
                public void run() {
                    String param = String.format("serial=%s", serial);
                    String api = "http://" + Config.server + ":" + Config.port + Config.registerAPI;
                    String result = HttpRequest.sendPost(api, param);

                    if (result.equals("1")) {
                        Message statusmsg = new Message();
                        statusmsg.what = REGISTER_SUCCESS;
                        mHandler.sendMessage(statusmsg);
                    }
                    else {
                        Message statusmsg = new Message();
                        statusmsg.what = ALREADY_REGISTERED;
                        mHandler.sendMessage(statusmsg);
                    }
                }
            };
            new Thread(requestThread).start();
        }
    };
    private View.OnClickListener saveSettingsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String serverAddr = address.getText().toString().trim();
            String serverPort = port.getText().toString().trim();
            String videoQuality = quality.getText().toString();

            if (serverAddr.length() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.settings_enter_server), Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverPort.length() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.settings_enter_port), Toast.LENGTH_SHORT).show();
                return;
            }
            int port = Integer.valueOf(serverPort);
            if (port > 65535 || port <= 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.setting_port_error), Toast.LENGTH_SHORT).show();
                return;
            }
            int qualityNum = Integer.valueOf(videoQuality);
            if (qualityNum > 100 || qualityNum < 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.setting_quality_error), Toast.LENGTH_SHORT).show();
                return;
            }
            Config.server = serverAddr;
            Config.port = serverPort;
            Config.videoQuality = qualityNum;
            SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS", 0);
            settings.edit().putString("server", serverAddr).apply();
            settings.edit().putString("port", serverPort).apply();
            settings.edit().putInt("videoQuality", qualityNum).apply();
            Toast.makeText(getActivity(), getString(R.string.settings_save_success), Toast.LENGTH_SHORT).show();
        }
    };

}
