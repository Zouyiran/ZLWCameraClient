package cn.com.chinatelecom.zlwcameraclient;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.com.chinatelecom.zlwcameraclient.tools.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */

public class DeviceListFragment extends Fragment{

    private int START_GETTING_DEVICELIST = 0;
    private int GETTING_DEVICELIST_SUCCESS = 1;
    private int GETTING_DEVICELIST_FAIL = 2;

    private View rootView;
    private LinearLayout loading;
    private ListView deviceListView;
    private List<Device> deviceList;
    private String result = "";

    private DeviceDetailFragment deviceDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_device_list, container, false);
        loading = (LinearLayout) rootView.findViewById(R.id.device_loading);
        deviceListView = (ListView) rootView.findViewById(R.id.device_list_view);
        setActionbar();
        requestDevices();
        return rootView;
    }

    private void setActionbar(){
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);//显示返回图标
            actionBar.setDisplayShowHomeEnabled(false);//显示app图标
            actionBar.setTitle(getActivity().getResources().getString(R.string.device_list));
        }
    }
    private void requestDevices() {
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = START_GETTING_DEVICELIST;
                handler.sendMessage(msg);
                String param = String.format("username=%s", Globals.username);
                try {
                    String api = "http://" + Config.server + ":" + Config.port + Config.getDeviceAPI;
                    result = HttpRequest.sendPost(api, param);
                    if (result.equals("")) {
                        Message fail = new Message();
                        fail.what = GETTING_DEVICELIST_FAIL;
                        handler.sendMessage(fail);
                    }
                    else {
                        Message success = new Message();
                        success.what = GETTING_DEVICELIST_SUCCESS;
                        handler.sendMessage(success);
                    }

                } catch (Exception e){
                    Message fail = new Message();
                    fail.what = GETTING_DEVICELIST_FAIL;
                    handler.sendMessage(fail);
                }
            }
        };
        new Thread(requestThread).start();
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == START_GETTING_DEVICELIST) {
                loading.setVisibility(View.VISIBLE);
            }
            else if (msg.what == GETTING_DEVICELIST_SUCCESS) {
                loading.setVisibility(View.GONE);
                try {
                    List<Map<String,String>> devices = Functions.readJson(result);
                    getDeviceInfo(devices);
                    DeviceAdapter adapter = new DeviceAdapter(getActivity(),R.layout.device_item,deviceList);
                    deviceListView.setAdapter(adapter);
                    deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Globals.NOW_DEVICE = deviceList.get(position);
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            deviceDetail = new DeviceDetailFragment();
                            transaction.replace(R.id.framelayout_device, deviceDetail,"deviceDetailFragment");
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                } catch (Exception e ) {
                    ProgressBar loadingImage = (ProgressBar) rootView.findViewById(R.id.device_loadiing_bar);
                    loadingImage.setVisibility(View.GONE);
                    TextView loadingText = (TextView) rootView.findViewById(R.id.device_loading_text);
                    loadingText.setText(getResources().getString(R.string.devicelist_error));
                }
            }
            else {
                ProgressBar loadingImage = (ProgressBar) rootView.findViewById(R.id.device_loadiing_bar);
                loadingImage.setVisibility(View.GONE);
                TextView loadingText = (TextView) rootView.findViewById(R.id.device_loading_text);
                loadingText.setText(getResources().getString(R.string.devicelist_fail));
            }
        }
    };

    private void getDeviceInfo(List<Map<String, String>> devices){
        deviceList = new ArrayList<Device>();
        for (int i = 0; i < devices.size(); i++) {
            Map<String, String> deviceMap = devices.get(i);
            Device device = new Device();
            device.setName(deviceMap.get("name"));
            device.setType(deviceMap.get("type"));
            device.setProvince(deviceMap.get("province"));
            device.setCity(deviceMap.get("city"));
            device.setCounty(deviceMap.get("county"));
            device.setStreet(deviceMap.get("street"));
            device.setId(deviceMap.get("id"));
            device.setLive(deviceMap.get("live"));
            device.setRecord(deviceMap.get("record"));
            device.setUrl(deviceMap.get("url"));
            device.setRatio(deviceMap.get("ratio"));
            deviceList.add(device);
        }
    }
}
