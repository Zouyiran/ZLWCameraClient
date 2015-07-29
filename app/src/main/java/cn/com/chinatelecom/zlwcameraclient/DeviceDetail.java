package cn.com.chinatelecom.zlwcameraclient;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Zouyiran on 2014/11/22.
 *
 */

public class DeviceDetail extends Fragment {
    private View rootView;
    private ActionBar actionBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_device_detail, null);
        actionBar = getActivity().getActionBar();
        actionBar.setTitle(Globals.NOW_DEVICE.get("name"));
        actionBar.setDisplayHomeAsUpEnabled(true);
        initDeviceInfo();
        return rootView;
    }

    private void initDeviceInfo() {
        ImageView deviceIcon = (ImageView)rootView.findViewById(R.id.device_icon);
        if (Globals.NOW_DEVICE.get("type").equals(getResources().getString(R.string.detail_hdcamera))) {
            deviceIcon.setImageResource(R.drawable.hd);
            actionBar.setIcon(R.drawable.hd);
        }
        else if (Globals.NOW_DEVICE.get("type").equals(getResources().getString(R.string.detail_sdcamera))) {
            deviceIcon.setImageResource(R.drawable.sd);
            actionBar.setIcon(R.drawable.sd);
        }
        else if (Globals.NOW_DEVICE.get("type").equals(getResources().getString(R.string.detail_phone))) {
            deviceIcon.setImageResource(R.drawable.phone);
            actionBar.setIcon(R.drawable.phone);
        }
        else {
            deviceIcon.setImageResource(R.drawable.unknown);
            actionBar.setIcon(R.drawable.unknown);
        }

        ((TextView)rootView.findViewById(R.id.device_name)).setText(Globals.NOW_DEVICE.get("name"));
        ((TextView)rootView.findViewById(R.id.device_type)).setText(Globals.NOW_DEVICE.get("type"));
        ((TextView)rootView.findViewById(R.id.device_addr)).setText(Globals.NOW_DEVICE.get("province") +
                Globals.NOW_DEVICE.get("city") +
                Globals.NOW_DEVICE.get("county") +
                Globals.NOW_DEVICE.get("street"));

        ((TextView)rootView.findViewById(R.id.device_live_status)).setText(Globals.NOW_DEVICE.get("live"));
        ((TextView)rootView.findViewById(R.id.device_record_status)).setText(Globals.NOW_DEVICE.get("record"));

        ((Button)rootView.findViewById(R.id.view_live_button)).setOnClickListener(playListener);
        ((Button)rootView.findViewById(R.id.view_record_button)).setOnClickListener(recordListener);

        if (!Globals.NOW_DEVICE.get("live").equals(getResources().getString(R.string.detail_camera_live_ready))) {
            if (!Globals.NOW_DEVICE.get("live").equals(getResources().getString(R.string.detail_phone_live_ready))) {
                ((Button) rootView.findViewById(R.id.view_live_button)).setEnabled(false);
            }
        }
    }
    private View.OnClickListener playListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String rtmp = Globals.NOW_DEVICE.get("url");
            String ratio = Globals.NOW_DEVICE.get("ratio");
            Intent intent = new Intent();
            intent.setClass(getActivity(), PlayerActivity.class);

            if (intent != null) {
                intent.putExtra("path", rtmp);
                intent.putExtra("ratio", ratio);
                intent.putExtra("type", "live");
                startActivity(intent);
            }
        }
    };

    private View.OnClickListener recordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecordListContainer recordList = new RecordListContainer();
            //因为是第二层子Fragment
            ((BaseContainer)getParentFragment().getParentFragment()).replaceFragment(recordList, true);
        }
    };
}