package com.ctcc.zlwcamera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by Zouyiran on 2015/11/17.
 */
public class RecordLayout extends RelativeLayout {

    public RecordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.record_layout, this);
    }

}
