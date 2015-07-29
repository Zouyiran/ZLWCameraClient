package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class LoginSettings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_settings);
        Button saveButton = (Button) findViewById(R.id.login_save_settings_button);
        saveButton.setOnClickListener(saveListener);
        ((EditText) findViewById(R.id.login_server_addr)).setText(Config.server);
        ((EditText) findViewById(R.id.login_server_port)).setText(Config.port);
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String serverAddr = ((EditText) findViewById(R.id.login_server_addr)).getText().toString();
            String serverPort = ((EditText) findViewById(R.id.login_server_port)).getText().toString();

            if (serverAddr == null || serverAddr.length() == 0) {
                Toast.makeText(LoginSettings.this, getResources().getString(R.string.settings_enter_server), Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverPort == null || serverPort.length() == 0) {
                Toast.makeText(LoginSettings.this, getResources().getString(R.string.settings_enter_port), Toast.LENGTH_SHORT).show();
                return;
            }

            int port = Integer.valueOf(serverPort);
            if (port > 65535 || port <= 0) {
                Toast.makeText(LoginSettings.this, getResources().getString(R.string.setting_port_error), Toast.LENGTH_SHORT).show();
                return;
            }
            Config.server = serverAddr;
            Config.port = serverPort;
            SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
            settings.edit().putString("server", serverAddr).apply();
            settings.edit().putString("port", serverPort).apply();
            Toast.makeText(LoginSettings.this, getResources().getString(R.string.settings_save_success), Toast.LENGTH_SHORT).show();
            finish();
        }
    };
}
