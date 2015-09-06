package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;


public class SplashActivity extends Activity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000;
    private ImageView appImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appImage = (ImageView) findViewById(R.id.app_image);
        appImage.setImageResource(R.drawable.logo);

        new Handler().postDelayed(new Runnable(){

            @Override
            public void run(){
                LoginActivity.actionStart(SplashActivity.this);
                finish();
            }
        },SPLASH_DISPLAY_LENGTH);

    }

}
