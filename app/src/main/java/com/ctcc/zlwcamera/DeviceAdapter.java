package com.ctcc.zlwcamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.ctcc.zlwcamera.data_struct.Device;
import com.ctcc.zlwcamera.tools.Globals;
import com.ctcc.zlwcamera.tools.LogUtil;

/**
 * Created by Zouyiran on 2015/9/7.
 */
public class DeviceAdapter extends ArrayAdapter<Device> {
    private int subLayoutId;
    private MainActivity context;

    public DeviceAdapter(Context context, int subLayoutId, List<Device> deviceList){
        super(context,subLayoutId,deviceList);
        this.context = (MainActivity) context;
        this.subLayoutId = subLayoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final Device device = getItem(position);
        LogUtil.d("DeviceAdapter",String.valueOf(position));

        class ViewHolder{
            DeviceLayout mDeviceLayout;
            TextView mDeviceNameView;
            TextView mDeviceTypeView;
            ImageView mDeviceLogoView;
            TextView mDeviceAddressView;
            TextView mLiveStatusView;
            TextView mRecordStatusView;
            Button mLiveButton;
            Button mRecordButton;
        }

        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view =  LayoutInflater.from(getContext()).inflate(subLayoutId,null);
            viewHolder = new ViewHolder();
            viewHolder.mDeviceLayout = (DeviceLayout) view.findViewById(R.id.device_layout);
            viewHolder.mDeviceNameView = (TextView) view.findViewById(R.id.device_name);
            viewHolder.mDeviceTypeView = (TextView) view.findViewById(R.id.device_type);
            viewHolder.mDeviceLogoView = (ImageView) view.findViewById(R.id.device_logo);
            viewHolder.mDeviceAddressView = (TextView) view.findViewById(R.id.device_address);
            viewHolder.mLiveStatusView  = (TextView) view.findViewById(R.id.device_live_status);
            viewHolder.mRecordStatusView = (TextView) view.findViewById(R.id.device_record_status);
            viewHolder.mLiveButton = (Button) view.findViewById(R.id.live_button);
            viewHolder.mRecordButton = (Button) view.findViewById(R.id.record_button);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mDeviceNameView.setText(device.getName());
        viewHolder.mDeviceTypeView.setText(device.getType());
        if (device.getType().equals(getContext().getResources().getString(R.string.hd_camera))) {
            viewHolder.mDeviceLogoView.setImageResource(R.drawable.hd);
        } else if (device.getType().equals(getContext().getResources().getString(R.string.sd_camera))) {
            viewHolder.mDeviceLogoView.setImageResource(R.drawable.sd);
        } else if (device.getType().equals(getContext().getResources().getString(R.string.phone))) {
            viewHolder.mDeviceLogoView.setImageResource(R.drawable.phone);
        } else {
            viewHolder.mDeviceLogoView.setImageResource(R.drawable.unknown);
        }
        String address = device.getProvince()+device.getCity()+device.getCounty()+device.getStreet();
        viewHolder.mDeviceAddressView.setText(address);
        viewHolder.mLiveStatusView.setText(device.getLive());
        viewHolder.mRecordStatusView.setText(device.getRecord());

        if (!device.getLive().equals(context.getResources().getString(R.string.living)) && !device.getLive().equals(context.getResources().getString(R.string.ready))) {
           viewHolder.mLiveButton.setVisibility(View.INVISIBLE);
        }

        viewHolder.mDeviceLayout.setDeviceClickListener(new DeviceLayout.DeviceOnClickListener(){

            @Override
            public void liveClick(){
                Globals.nowDevice = device;
                String rtmp = Globals.nowDevice.getUrl();
                String ratio = Globals.nowDevice.getRatio();
                PlayerActivity.actionStart(context,rtmp,ratio,"live");
            }

            @Override
            public void recordClick(){
                Globals.nowDevice = device;
                RecordActivity.actionStart(context);
            }
        });
        return view;
    }
}
