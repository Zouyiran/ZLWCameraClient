package cn.com.chinatelecom.zlwcameraclient;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class Settings extends Fragment {
    private View rootView;
    private int NOT_REGISTER = 0;
    private int REGISTERED = 1;
    private int REGISTER_SUCCESS = 2;
    private int ALREADY_REGISTERED = 3;
    private int WRONG = 4;
    private int deviceID;
    private TextView statusView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle(getResources().getString(R.string.settings_title));
        actionBar.setIcon(R.drawable.settings);
        actionBar.setDisplayHomeAsUpEnabled(false);

        getPhoneStatus();

        Button saveButton = (Button) rootView.findViewById(R.id.save_settings_button);
        saveButton.setOnClickListener(saveSettingsListener);

        Button logoutButton = (Button) rootView.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(logoutListner);

        ((EditText) rootView.findViewById(R.id.server_addr)).setText(Config.server);
        ((EditText) rootView.findViewById(R.id.server_port)).setText(Config.port);
        ((EditText) rootView.findViewById(R.id.quality)).setText(String.valueOf(Config.videoQuality));
        return rootView;
    }
    private void getPhoneStatus() {
        ((TextView) rootView.findViewById(R.id.device_status)).setText(getResources().getString(R.string.settings_loading));
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
                    try {
                        deviceID = Integer.valueOf(result);
                        Globals.deviceID = deviceID;
                        Message statusmsg = new Message();
                        statusmsg.what = REGISTERED;
                        handler.sendMessage(statusmsg);
                    } catch (Exception e){
                        Message wrongmsg = new Message();
                        wrongmsg.what = WRONG;
                        handler.sendMessage(wrongmsg);
                    }

                }
            }
        };
        new Thread(requestThread).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            statusView = (TextView) rootView.findViewById(R.id.device_status);
            if (msg.what == NOT_REGISTER) {
                statusView.setText(getResources().getString(R.string.settings_not_register));
                statusView.setClickable(true);
                statusView.setOnClickListener(registerListener);
            }
            else if (msg.what == REGISTERED){
                statusView.setText(String.format(getResources().getString(R.string.settings_registered), deviceID));
                statusView.setClickable(false);
            }
            else if (msg.what == REGISTER_SUCCESS) {
                Toast.makeText(getActivity(), getResources().getString(R.string.settings_register_success), Toast.LENGTH_SHORT).show();
                getPhoneStatus();
            }
            else if (msg.what == ALREADY_REGISTERED) {
                Toast.makeText(getActivity(), getResources().getString(R.string.settings_already_register), Toast.LENGTH_SHORT).show();
                getPhoneStatus();
            }
            else if (msg.what == WRONG) {
                statusView.setText(String.format(getResources().getString(R.string.settings_wrong), deviceID));
                statusView.setClickable(false);
            }
        }
    };

    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((TextView) rootView.findViewById(R.id.device_status)).setText(getResources().getString(R.string.settings_registering));
            final String serial = Functions.getID();
            Runnable requestThread = new Runnable(){
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String param = String.format("serial=%s", serial);
                    String api = "http://" + Config.server + ":" + Config.port + Config.registerAPI;
                    String result = HttpRequest.sendPost(api, param);

                    if (result.equals("1")) {
                        Message statusmsg = new Message();
                        statusmsg.what = REGISTER_SUCCESS;
                        handler.sendMessage(statusmsg);
                    }
                    else {
                        Message statusmsg = new Message();
                        statusmsg.what = ALREADY_REGISTERED;
                        handler.sendMessage(statusmsg);
                    }
                }
            };
            new Thread(requestThread).start();
        }
    };
    private View.OnClickListener saveSettingsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String serverAddr = ((EditText) rootView.findViewById(R.id.server_addr)).getText().toString();
            String serverPort = ((EditText) rootView.findViewById(R.id.server_port)).getText().toString();
            String quality = ((EditText) rootView.findViewById(R.id.quality)).getText().toString();

            if (serverAddr == null || serverAddr.length() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.settings_enter_server), Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverPort == null || serverPort.length() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.settings_enter_port), Toast.LENGTH_SHORT).show();
                return;
            }

            int port = Integer.valueOf(serverPort);
            if (port > 65535 || port <= 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.setting_port_error), Toast.LENGTH_SHORT).show();
                return;
            }

            int video_quality = Integer.valueOf(quality);
            if (video_quality > 100 || video_quality < 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.setting_quality_error), Toast.LENGTH_SHORT).show();
                return;
            }
            Config.server = serverAddr;
            Config.port = serverPort;
            Config.videoQuality = video_quality;
            SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS", 0);
            settings.edit().putString("server", serverAddr).apply();
            settings.edit().putString("port", serverPort).apply();
            settings.edit().putInt("videoQuality", video_quality).apply();
            Toast.makeText(getActivity(), getString(R.string.settings_save_success), Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener logoutListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().finish();
        }
    };

}
