package cn.com.chinatelecom.zlwcameraclient;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.chinatelecom.zlwcameraclient.tools.Globals;

/**
 * Created by Zouyiran on 2014/11/22.
 *
 */

public class DeviceDetailFragment extends Fragment {
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_device_detail, null);
        setActionbar();
        initDeviceInfo();
        return rootView;
    }

    private void setActionbar(){
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//显示返回图标
            actionBar.setDisplayShowHomeEnabled(false);//显示app图标
            actionBar.setTitle(getActivity().getResources().getString(R.string.device_detail));
        }
    }

    private void initDeviceInfo() {
        ImageView deviceIcon = (ImageView)rootView.findViewById(R.id.device_icon);
        if (Globals.NOW_DEVICE.getType().equals(getResources().getString(R.string.detail_hdcamera))) {
            deviceIcon.setImageResource(R.drawable.hd);
        }
        else if (Globals.NOW_DEVICE.getType().equals(getResources().getString(R.string.detail_sdcamera))) {
            deviceIcon.setImageResource(R.drawable.sd);
        }
        else if (Globals.NOW_DEVICE.getType().equals(getResources().getString(R.string.detail_phone))) {
            deviceIcon.setImageResource(R.drawable.phone);
        }
        else {
            deviceIcon.setImageResource(R.drawable.unknown);
        }

        ((TextView)rootView.findViewById(R.id.device_name)).setText(Globals.NOW_DEVICE.getName());
        ((TextView)rootView.findViewById(R.id.device_type)).setText(Globals.NOW_DEVICE.getType());

        ((TextView)rootView.findViewById(R.id.device_addr)).setText(Globals.NOW_DEVICE.getProvince() +
                Globals.NOW_DEVICE.getCity() +
                Globals.NOW_DEVICE.getCounty() +
                Globals.NOW_DEVICE.getStreet());
        ((TextView)rootView.findViewById(R.id.device_live_status)).setText(Globals.NOW_DEVICE.getLive());
        ((TextView)rootView.findViewById(R.id.device_record_status)).setText(Globals.NOW_DEVICE.getRecord());

        ((Button)rootView.findViewById(R.id.view_live_button)).setOnClickListener(playListener);
        ((Button)rootView.findViewById(R.id.view_record_button)).setOnClickListener(recordListener);

        if (!Globals.NOW_DEVICE.getLive().equals(getResources().getString(R.string.detail_camera_live_ready))) {
            if (!Globals.NOW_DEVICE.getLive().equals(getResources().getString(R.string.detail_phone_live_ready))) {
                ((Button) rootView.findViewById(R.id.view_live_button)).setEnabled(false);
                ((Button) rootView.findViewById(R.id.view_live_button)).setBackgroundColor(getResources().getColor(R.color.huise));

            }
        }
    }

// 直播
    private View.OnClickListener playListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String rtmp = Globals.NOW_DEVICE.getUrl();
            String ratio = Globals.NOW_DEVICE.getRatio();
            PlayerActivity.actionStart(getActivity(),rtmp,ratio,"live");
        }
    };

// 历史视频
    private View.OnClickListener recordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            RecordListFragment recordList = new RecordListFragment();
            transaction.replace(R.id.framelayout_device, recordList,"recordListFragment");
            transaction.addToBackStack(null);
            transaction.commit();
        }
    };
}