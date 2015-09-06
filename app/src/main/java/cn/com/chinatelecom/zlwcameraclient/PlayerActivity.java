package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Zouyiran on 2014/11/26.
 *
 */

public class PlayerActivity extends Activity {
    private String url;
    private float ratio;
    private VideoView videoView;
    private MediaController mediaController;
    private View loadingView;

    private boolean isPlaying() {
        return videoView != null && videoView.isPlaying();
    }

    private OnInfoListener infoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (isPlaying()) {
                loadingView.setVisibility(View.GONE);
            }
            else {
                loadingView.setVisibility(View.VISIBLE);
            }
            return true;
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, ratio);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Applications.getInstance().addActivity(this);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.activity_player);
        url = getIntent().getStringExtra("path");
        String ratioStr = getIntent().getStringExtra("ratio");
        if (ratioStr.equals("4:3")) {
            ratio = (float)(4.0 / 3.0);
        }
        else if (ratioStr.equals("16:9")) {
            ratio = (float)(16.0 / 9.0);
        }
        else {
            ratio = (float)(4.0 / 3.0);
        }
        videoView = (VideoView) findViewById(R.id.surface_view);
        loadingView = findViewById(R.id.video_loading);
        videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, ratio);
        videoView.setVideoPath(url);
//        参看历史视频record
        if (getIntent().getStringExtra("type").equals("record")) {
            mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
        }

        videoView.requestFocus();
        videoView.setOnInfoListener(infoListener);
        videoView.start();
    }
}
