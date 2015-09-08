package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.com.chinatelecom.zlwcameraclient.tools.Config;

/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class LoginSettingActivity extends Activity {

    private EditText server;
    private EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_settings);
        Button saveButton = (Button) findViewById(R.id.login_save_settings_button);
        server = (EditText) findViewById(R.id.login_server_addr);
        port = (EditText) findViewById(R.id.login_server_port);
        saveButton.setOnClickListener(saveListener);
//        set default value
        server.setText(Config.server);
        port.setText(Config.port);
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String serverAddr = server.getText().toString().trim();
            String serverPort = port.getText().toString().trim();

            if (serverAddr.length() == 0) {
                Toast.makeText(LoginSettingActivity.this, getResources().getString(R.string.settings_enter_server), Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverPort.length() == 0) {
                Toast.makeText(LoginSettingActivity.this, getResources().getString(R.string.settings_enter_port), Toast.LENGTH_SHORT).show();
                return;
            }
            int port = Integer.valueOf(serverPort);
            if (port > 65535 || port <= 0) {
                Toast.makeText(LoginSettingActivity.this, getResources().getString(R.string.setting_port_error), Toast.LENGTH_SHORT).show();
                return;
            }
            Config.server = serverAddr;
            Config.port = serverPort;
            SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("server", serverAddr).apply();
            editor.putString("port", serverPort).apply();
            Toast.makeText(LoginSettingActivity.this, getResources().getString(R.string.settings_save_success), Toast.LENGTH_SHORT).show();
            finish();
        }
    };
}
