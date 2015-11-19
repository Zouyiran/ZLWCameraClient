package cn.com.chinatelecom.zlwcameraclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.chinatelecom.zlwcameraclient.data_struct.Device;

import java.util.List;

/**
 * Created by Zouyiran on 2015/9/7.
 */
public class DeviceAdapter extends ArrayAdapter<Device> {
    private int subLayoutId;

    public DeviceAdapter(Context context,int subLayoutId, List<Device> deviceList){
        super(context,subLayoutId,deviceList);
        this.subLayoutId = subLayoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        class ViewHolder{
            ImageView imageView;
            TextView deviceNameView;
            TextView deviceAddrView;
        }
        Device device = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view =  LayoutInflater.from(getContext()).inflate(subLayoutId,null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.device_type_image);
            viewHolder.deviceNameView = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceAddrView = (TextView) view.findViewById(R.id.device_addr);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();//return Object
        }
        if (device.getType().equals(getContext().getResources().getString(R.string.detail_hdcamera))) {
            viewHolder.imageView.setImageResource(R.drawable.hd);
        }
        else if (device.getType().equals(getContext().getResources().getString(R.string.detail_sdcamera))) {
            viewHolder.imageView.setImageResource(R.drawable.sd);
        }
        else if (device.getType().equals(getContext().getResources().getString(R.string.detail_phone))) {
            viewHolder.imageView.setImageResource(R.drawable.phone);
        }
        else {
            viewHolder.imageView.setImageResource(R.drawable.unknown);
        }
        viewHolder.deviceNameView.setText(device.getName());
        viewHolder.deviceAddrView.setText(device.getProvince()+device.getCity()+device.getCounty()+device.getStreet());
        return view;
    }
}
