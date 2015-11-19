package com.ctcc.zlwcamera;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.math.BigInteger;

import com.ctcc.zlwcamera.tools.Config;
import com.ctcc.zlwcamera.tools.Functions;
import com.ctcc.zlwcamera.tools.Globals;
import com.ctcc.zlwcamera.tools.HttpRequest;

/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class SettingFragment extends Fragment {

    private EditText serverAddress;
    private EditText serverPort;
    private EditText videoQuality;
    private static int NOT_REGISTER = 0;
    private static int REGISTERED = 1;
    private static int REGISTER_SUCCESS = 2;
    private static int ALREADY_REGISTERED = 3;
    private static int WRONG = 4;
    private static int deviceID;
    private static TextView statusView;
    private static WeakReference<MainActivity> mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        mActivity = new WeakReference<MainActivity>((MainActivity) getActivity());

        serverAddress = (EditText) rootView.findViewById(R.id.server_address);
        serverPort = (EditText) rootView.findViewById(R.id.server_port);
        videoQuality = (EditText) rootView.findViewById(R.id.quality);
        statusView = (TextView) rootView.findViewById(R.id.phone_status);
        Button saveButton = (Button) rootView.findViewById(R.id.save_settings_button);

        serverAddress.setText(Config.address);
        serverPort.setText(Config.port);
        videoQuality.setText(String.valueOf(Config.videoQuality));
        statusView.setOnClickListener(registerListener);
        saveButton.setOnClickListener(saveSettingsListener);

        getPhoneStatus();

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void setActionbar(){
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle(getActivity().getResources().getString(R.string.title_setting));
        }
    }

    private static void getPhoneStatus() {
        statusView.setText(mActivity.get().getResources().getString(R.string.query_loading));
        final String serial = Functions.getID();
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                String param = String.format("serial=%s", serial);
                String api = "http://" + Config.address + ":" + Config.port + Config.getPhoneStatusAPI;
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
                    statusView.setText(mActivity.get().getResources().getString(R.string.not_register));
                    statusView.setClickable(true);
                    break;
                case 1:
                    statusView.setText(String.format(mActivity.get().getResources().getString(R.string.registered), deviceID));
                    statusView.setClickable(false);
                    break;
                case 2:
                    Toast.makeText(mActivity.get(), mActivity.get().getResources().getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    getPhoneStatus();
                    break;
                case 3:
                    Toast.makeText(mActivity.get(), mActivity.get().getResources().getString(R.string.register_already), Toast.LENGTH_SHORT).show();
                    getPhoneStatus();
                    break;
                case 4:
                    statusView.setText(String.format(mActivity.get().getResources().getString(R.string.wrong), deviceID));
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
            statusView.setText(mActivity.get().getResources().getString(R.string.registering));
            final String serial = Functions.getID();
            Runnable requestThread = new Runnable(){
                @Override
                public void run() {
                    String param = String.format("serial=%s", serial);
                    String api = "http://" + Config.address + ":" + Config.port + Config.registerAPI;
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
            BigInteger portInt;
            boolean cancel = false;
            View focusView = null;
            String address = serverAddress.getText().toString();
            String port = SettingFragment.this.serverPort.getText().toString();
            String quality = SettingFragment.this.videoQuality.getText().toString();
            if (TextUtils.isEmpty(address)) {
                serverAddress.setError(getString(R.string.field_required_msg));
                focusView = serverAddress;
                cancel = true;
            } else if (TextUtils.isEmpty(port)) {
                serverPort.setError(getString(R.string.field_required_msg));
                focusView = serverPort;
                cancel = true;
            }else{
                portInt = new BigInteger(port);
                BigInteger bigBorder = new BigInteger("65535");
                BigInteger smallBorder = new BigInteger("0");
                if (portInt.compareTo(bigBorder) > 0 ||
                        portInt.compareTo(smallBorder) < 0 ||
                        portInt.compareTo(smallBorder) == 0) {
                    serverPort.setError(getString(R.string.port_error));
                    focusView = serverPort;
                    cancel = true;
                }
            }
            if (cancel) {
                focusView.requestFocus();
            }else{
                Config.address = address;
                Config.port = port;
                int qualityNum = Integer.valueOf(quality);
                Config.videoQuality = qualityNum;
                SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS", 0);
                settings.edit().putString("server", address).apply();
                settings.edit().putString("serverPort", port).apply();
                settings.edit().putInt("videoQuality", qualityNum).apply();
                Toast.makeText(getActivity(), getString(R.string.setting_save_success), Toast.LENGTH_SHORT).show();
            }
        }
    };

}
