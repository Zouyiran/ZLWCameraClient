package com.ctcc.zlwcamera;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ctcc.zlwcamera.tools.Config;

import java.math.BigInteger;

/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class LoginSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText serverAddress;
    private EditText serverPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_setting);

        Button saveButton = (Button) findViewById(R.id.login_setting_save_button);
        serverAddress = (EditText) findViewById(R.id.login_settings_address);
        serverPort = (EditText) findViewById(R.id.login_settings_port);
        serverAddress.setText(Config.address);
        serverPort.setText(Config.port);
        saveButton.setOnClickListener(this);
        Applications.getInstance().addActivity(this);
    }

    @Override
    public void onClick(View v) {
        BigInteger portInt;
        boolean cancel = false;
        View focusView = null;
        String address = serverAddress.getText().toString();
        String port = serverPort.getText().toString();
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
            SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("address", address).apply();
            editor.putString("port", port).apply();
            Toast.makeText(LoginSettingActivity.this, getResources().getString(R.string.setting_save_success), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        LoginActivity.actionStart(this);
        finish();
    }
}
