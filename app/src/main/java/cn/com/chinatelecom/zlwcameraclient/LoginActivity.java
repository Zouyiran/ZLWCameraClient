package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.*;
import android.widget.*;
import android.os.*;
import android.content.SharedPreferences;

/**
 * Created by Zouyiran on 2014/11/23.
 *
 */

public class LoginActivity extends Activity {
    private Button loginButton;
    private View loadingView;
    private EditText usernameInput;
    private EditText passwordInput;
    private CheckBox rememberUsername;
    private CheckBox rememberPassword;
    private static int START_LOGIN = 0;
    private static int LOGIN_SUCCESS = 1;
    private static int LOGIN_FAILED = 2;
    private static int LOGIN_ERROR = 3;
    private View.OnClickListener loginButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
            if (rememberUsername.isChecked()) {
                settings.edit().putString("remember_username", "yes").apply();
                settings.edit().putString("username", usernameInput.getText().toString()).apply();
            }
            else {
                settings.edit().putString("remember_username", "no").apply();
                settings.edit().putString("username", "").apply();
            }
            if (rememberPassword.isChecked()) {
                settings.edit().putString("remember_password", "yes").apply();
                settings.edit().putString("password", passwordInput.getText().toString()).apply();
            }
            else {
                settings.edit().putString("remember_password", "no").apply();
                settings.edit().putString("password", "").apply();
            }

            final String username = usernameInput.getText().toString();
            final String password = passwordInput.getText().toString();
            if (username == null || username.length() == 0) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_username_hint), Toast.LENGTH_SHORT).show();
                return;
            }
            if (password == null || password.length() == 0) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_password_hint), Toast.LENGTH_SHORT).show();
                return;
            }

            Runnable requestThread = new Runnable(){
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Message msg = new Message();
                    msg.what = START_LOGIN;
                    handler.sendMessage(msg);
                    String param = String.format("username=%s&password=%s", username, password);
                    String api = "http://" + Config.server + ":" + Config.port + Config.loginAPI;
                    String result = HttpRequest.sendPost(api, param);

                    if (result.equals("1")) {
                        Globals.username = username;
                        Message success = new Message();
                        success.what = LOGIN_SUCCESS;
                        handler.sendMessage(success);
                    }
                    else if (result.equals("0")) {
                        Message fail = new Message();
                        fail.what = LOGIN_FAILED;
                        handler.sendMessage(fail);
                    }
                    else {
                        Message fail = new Message();
                        fail.what = LOGIN_ERROR;
                        handler.sendMessage(fail);
                    }
                }
            };
            new Thread(requestThread).start();

        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == START_LOGIN) {
                loadingView = findViewById(R.id.login_loading);
                loadingView.setVisibility(View.VISIBLE);

                usernameInput.setEnabled(false);
                passwordInput.setEnabled(false);
                loginButton.setEnabled(false);
            }
            else if (msg.what == LOGIN_SUCCESS) {
                loadingView = findViewById(R.id.login_loading);
                loadingView.setVisibility(View.GONE);
                usernameInput.setEnabled(true);
                passwordInput.setEnabled(true);
                loginButton.setEnabled(true);
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                //String path = "rtmp://222.197.182.130:8085/live/camera-119";
                //intent.putExtra("path", path);
                startActivity(intent);
            }
            else if (msg.what == LOGIN_FAILED) {
                loadingView = findViewById(R.id.login_loading);
                loadingView.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_wrongpassword), Toast.LENGTH_SHORT).show();
                usernameInput.setEnabled(true);
                passwordInput.setEnabled(true);
                loginButton.setEnabled(true);
            }
            else if (msg.what == LOGIN_ERROR) {
                loadingView = findViewById(R.id.login_loading);
                loadingView.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_servererror), Toast.LENGTH_SHORT).show();
                usernameInput.setEnabled(true);
                passwordInput.setEnabled(true);
                loginButton.setEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            Globals.telemanager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception ignored) {

        }

        try{
            Globals.resolver = getContentResolver();
        } catch (Exception ignored) {

        }

        Applications.getInstance().addActivity(this);
        setContentView(R.layout.activity_login);
        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
        Config.server = settings.getString("server", getResources().getString(R.string.settings_server_default));
        Config.port = settings.getString("port", getResources().getString(R.string.settings_port_default));
        Config.videoQuality = settings.getInt("videoQuality", Integer.valueOf(getResources().getString(R.string.settings_videoquality_default)));

        rememberUsername = (CheckBox)findViewById(R.id.remeber_username);
        rememberPassword = (CheckBox)findViewById(R.id.remeber_password);
        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);

        rememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked) {
                    rememberUsername.setChecked(true);
                }
            }
        });

        rememberUsername.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(!isChecked) {
                    rememberPassword.setChecked(false);
                }
            }
        });
        String ifRememberUsername = settings.getString("remember_username", "no");// 选中状态
        String ifRememberPassword = settings.getString("remember_password", "no");// 选中状态

        if (ifRememberUsername.equals("yes")) {
            rememberUsername.setChecked(true);
            usernameInput.setText(settings.getString("username", ""));
        }
        if (ifRememberPassword.equals("yes")) {
            rememberUsername.setChecked(true);
            rememberPassword.setChecked(true);
            usernameInput.setText(settings.getString("username", ""));
            passwordInput.setText(settings.getString("password", ""));
        }

        loginButton = (Button)findViewById(R.id.login);
        loginButton.setOnClickListener(loginButtonOnClickListener);

        TextView loginSettings = (TextView)findViewById(R.id.login_setting_button);
        loginSettings.setOnClickListener(settingButtonListener);
    }

    private View.OnClickListener settingButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, LoginSettings.class);
            startActivity(intent);
        }
    };
}
