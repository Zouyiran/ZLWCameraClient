package com.ctcc.zlwcamera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Zouyiran on 2015/11/17.
 */
public class DeviceLayout extends RelativeLayout {

    private Button liveButton;
    private Button recordButton;
    private DeviceOnClickListener mListener;

    public DeviceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.device_layout, this);
        liveButton = (Button) findViewById(R.id.live_button);
        recordButton = (Button) findViewById(R.id.record_button);
        if(liveButton != null && liveButton.getVisibility() == View.VISIBLE){
            liveButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    mListener.liveClick();
                }
            });
        }
        if(recordButton != null && recordButton.getVisibility() == View.VISIBLE){
            recordButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.recordClick();
                }
            });
        }
    }

    public void  setDeviceClickListener(DeviceOnClickListener mListener){
        this.mListener = mListener;
    }

    public interface DeviceOnClickListener{
        void liveClick();
        void recordClick();
    }
}
