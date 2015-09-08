package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.*;
import cn.com.chinatelecom.zlwcameraclient.tools.Config;
import cn.com.chinatelecom.zlwcameraclient.tools.Globals;
import cn.com.chinatelecom.zlwcameraclient.tools.HttpRequest;

import java.lang.ref.WeakReference;

/**
 * Created by Zouyiran on 2014/11/23.
 *
 */

public class LoginActivity extends Activity {
    private static Button loginButton;
    private static View loadingView;
    private static EditText usernameInput;
    private static EditText passwordInput;
    private CheckBox rememberUsername;
    private CheckBox rememberPassword;
    private static int START_LOGIN = 0;
    private static int LOGIN_SUCCESS = 1;
    private static int LOGIN_FAILED = 2;
    private static int LOGIN_ERROR = 3;

    public static void actionStart(Context context){
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Applications.getInstance().addActivity(this);
        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
        //        set default value
        Config.server = settings.getString("server", Config.server);
        Config.port = settings.getString("port", Config.port);
        Config.videoQuality = settings.getInt("videoQuality", Config.videoQuality);

        rememberUsername = (CheckBox)findViewById(R.id.remeber_username);
        rememberPassword = (CheckBox)findViewById(R.id.remeber_password);
        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.login);
        loadingView = findViewById(R.id.login_loading);

        TextView loginSettings = (TextView)findViewById(R.id.login_setting_button);

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
        String ifRememberUsername = settings.getString("remember_username", "no");
        String ifRememberPassword = settings.getString("remember_password", "no");

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

        loginButton.setOnClickListener(loginButtonOnClickListener);
        loginSettings.setOnClickListener(settingButtonListener);

        try{
            Globals.telemanager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception ignored) {

        }
        try{
            Globals.resolver = getContentResolver();
        } catch (Exception ignored) {

        }
    }

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

            final String username = usernameInput.getText().toString().trim();
            final String password = passwordInput.getText().toString().trim();
            if (username.length() == 0) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_username_hint), Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() == 0) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_password_hint), Toast.LENGTH_SHORT).show();
                return;
            }
            Runnable requestThread = new Runnable(){
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = START_LOGIN;
                    mHandler.sendMessage(msg);
                    String param = String.format("username=%s&password=%s", username, password);
                    String api = "http://" + Config.server + ":" + Config.port + Config.loginAPI;
                    String result = HttpRequest.sendPost(api, param);

                    if (result.equals("1")) {
                        Globals.username = username;
                        Message success = new Message();
                        success.what = LOGIN_SUCCESS;
                        mHandler.sendMessage(success);
                    }
                    else if (result.equals("0")) {
                        Message fail = new Message();
                        fail.what = LOGIN_FAILED;
                        mHandler.sendMessage(fail);
                    }
                    else {
                        Message fail = new Message();
                        fail.what = LOGIN_ERROR;
                        mHandler.sendMessage(fail);
                    }
                }
            };
            new Thread(requestThread).start();
        }
    };

//    In Android, Handler classes should be static or leaks might occur,
//    Messages enqueued on the application thread’s MessageQueue also retain their target Handler.
//    If the Handler is an inner class, its outer class will be retained as well.
//    To avoid leaking the outer class, declare the Handler as a static nested class with a WeakReference to its outer class

    private MHandler mHandler = new MHandler(LoginActivity.this);

    private static class MHandler extends Handler {

        private WeakReference<LoginActivity> mActivity;

        public MHandler(LoginActivity activity){
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == START_LOGIN) {
                loadingView.setVisibility(View.VISIBLE);
                usernameInput.setEnabled(false);
                passwordInput.setEnabled(false);
                loginButton.setEnabled(false);
            }
            else if (msg.what == LOGIN_SUCCESS) {
                loadingView.setVisibility(View.GONE);
                usernameInput.setEnabled(true);
                passwordInput.setEnabled(true);
                loginButton.setEnabled(true);
                Toast.makeText(mActivity.get(), mActivity.get().getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                MainActivity.actionStart(mActivity.get());
                mActivity.get().finish();
            }
            else if (msg.what == LOGIN_FAILED) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(mActivity.get(), mActivity.get().getResources().getString(R.string.login_wrongpassword), Toast.LENGTH_SHORT).show();
                usernameInput.setEnabled(true);
                passwordInput.setEnabled(true);
                loginButton.setEnabled(true);
            }
            else if (msg.what == LOGIN_ERROR) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(mActivity.get(), mActivity.get().getResources().getString(R.string.login_servererror), Toast.LENGTH_SHORT).show();
                usernameInput.setEnabled(true);
                passwordInput.setEnabled(true);
                loginButton.setEnabled(true);
            }
        }
    }

    private View.OnClickListener settingButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, LoginSettingActivity.class);
            startActivity(intent);
        }
    };
}
