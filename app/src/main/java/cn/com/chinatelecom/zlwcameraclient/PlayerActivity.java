package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    private float ratio;
    private VideoView videoView;
    private View loadingView;

    public static void actionStart(Context context, String url, String ratio,String type){
        Intent intent = new Intent(context,PlayerActivity.class);
        intent.putExtra("path", url);
        intent.putExtra("ratio", ratio);
        intent.putExtra("type", type);
        context.startActivity(intent);

//        Intent intent = new Intent();
//        intent.setClass(getActivity(), PlayerActivity.class);
//        intent.putExtra("path", url);
//        intent.putExtra("ratio", ratio);
//        intent.putExtra("type", "record");
//        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Applications.getInstance().addActivity(this);
        if (!LibsChecker.checkVitamioLibs(this))
            return;

        String url = getIntent().getStringExtra("path");
        String ratioStr = getIntent().getStringExtra("ratio");
        if(ratioStr.equals("4:3")){
            ratio = (float)(4.0 / 3.0);
        }else if(ratioStr.equals("16:9")){
            ratio = (float)(16.0 / 9.0);
        }else{
            ratio = (float)(4.0 / 3.0);
        }
        videoView = (VideoView) findViewById(R.id.surface_view);
        videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, ratio);
        videoView.setVideoPath(url);
        loadingView = findViewById(R.id.video_loading);
        if (getIntent().getStringExtra("type").equals("record")) {
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
        }
        videoView.requestFocus();
        videoView.setOnInfoListener(infoListener);
        videoView.start();
    }

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
}
