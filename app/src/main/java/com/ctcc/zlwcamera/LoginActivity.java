package com.ctcc.zlwcamera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ctcc.zlwcamera.tools.Config;
import com.ctcc.zlwcamera.tools.Globals;
import com.ctcc.zlwcamera.tools.HttpRequest;


public class LoginActivity extends AppCompatActivity implements OnClickListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mLoginTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private CheckBox rememberUsername;
    private CheckBox rememberPassword;
    private View mProgressView;
    private Button loginButton;

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

        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);

        Config.address = settings.getString("server", Config.address);
        Config.port = settings.getString("port", Config.port);
        Config.videoQuality = settings.getInt("videoQuality", Config.videoQuality);

        //nowUsername
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        //password
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_GO) {
                    attemptLogin();//
                    return true;
                }
                return false;
            }
        });

        //rememberUsername & rememberPassword
        rememberUsername = (CheckBox)findViewById(R.id.remember_username);
        rememberUsername.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(!isChecked) {
                    rememberPassword.setChecked(false);
                }
            }
        });

        rememberPassword = (CheckBox)findViewById(R.id.remember_password);
        rememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked) {
                    rememberUsername.setChecked(true);
                }
            }
        });

        boolean ifRememberUsername = settings.getBoolean("remember_username", false);
        boolean ifRememberPassword = settings.getBoolean("remember_password", false);
        if (ifRememberUsername) {
            rememberUsername.setChecked(true);
            mUsernameView.setText(settings.getString("nowUsername", ""));
        }
        if (ifRememberPassword) {
            rememberUsername.setChecked(true);
            rememberPassword.setChecked(true);
            mUsernameView.setText(settings.getString("nowUsername", ""));
            mPasswordView.setText(settings.getString("password", ""));
        }

        // loginButton
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        //loginSettings
        TextView loginSettings = (TextView)findViewById(R.id.login_setting_button);
        loginSettings.setOnClickListener(this);

        mProgressView = findViewById(R.id.login_progress);

        try{
            Globals.telemanager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception ignored) {

        }
        try{
            Globals.resolver = getContentResolver();
        } catch (Exception ignored) {

        }
        Applications.getInstance().addActivity(this);
    }

    @Override
    public void onClick(View v){
       switch (v.getId()){
           case R.id.login_button:
               attemptLogin();
               break;
           case R.id.login_setting_button:
               Intent intent = new Intent();
               intent.setClass(LoginActivity.this, LoginSettingActivity.class);
               startActivity(intent);
               break;
           default:
               break;
       }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid nowUsername, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mLoginTask != null) {
            return;
        }

        mUsernameView.setError(null);
        mPasswordView.setError(null);

        boolean cancel = false;
        View focusView = null;

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.field_required_msg));
            focusView = mUsernameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.field_required_msg));
            focusView = mPasswordView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
            if (rememberUsername.isChecked()) {
                settings.edit().putBoolean("remember_username", true).apply();
                settings.edit().putString("nowUsername", username).apply();
            } else {
                settings.edit().putBoolean("remember_username", false).apply();
                settings.edit().putString("nowUsername", "").apply();
            }
            if (rememberPassword.isChecked()) {
                settings.edit().putBoolean("remember_password", true).apply();
                settings.edit().putString("password", password).apply();
            } else {
                settings.edit().putBoolean("remember_password", false).apply();
                settings.edit().putString("password", "").apply();
            }
            showProgress(true);
            mLoginTask = new UserLoginTask(username, password);
            mLoginTask.execute((Void) null);
        }
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mUsernameView.setEnabled(!show);
        mPasswordView.setEnabled(!show);
        rememberUsername.setEnabled(!show);
        rememberPassword.setEnabled(!show);
        loginButton.setEnabled(!show);
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                String param = String.format("username=%s&password=%s", mUsername, mPassword);
                String api = "http://" + Config.address + ":" + Config.port + Config.loginAPI;
                String result = HttpRequest.sendPost(api, param);

                if (result.equals("1")) {
                    Globals.nowUsername = mUsername;
                    return LOGIN_SUCCESS;
                } else if (result.equals("0")) {
                    return LOGIN_FAILED;
                } else {
                    return LOGIN_ERROR;
                }

            } catch (Exception e) {
                return LOGIN_ERROR;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            mLoginTask = null;
            showProgress(false);
            if (result == LOGIN_SUCCESS) {
                Toast.makeText(LoginActivity.this, LoginActivity.this.getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                MainActivity.actionStart(LoginActivity.this);
                LoginActivity.this.finish();
            } else if(result == LOGIN_FAILED) {
                Toast.makeText(LoginActivity.this, LoginActivity.this.getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                mUsernameView.requestFocus();
            } else {
                Toast.makeText(LoginActivity.this, LoginActivity.this.getResources().getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                mUsernameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mLoginTask = null;
            showProgress(false);
        }
    }
}

