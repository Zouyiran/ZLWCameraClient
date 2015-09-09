package cn.com.chinatelecom.zlwcameraclient;

import android.app.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.com.chinatelecom.zlwcameraclient.data_struct.Device;
import cn.com.chinatelecom.zlwcameraclient.tools.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */

public class DeviceListFragment extends Fragment{

    private static int START_GETTING_DEVICELIST = 0;
    private static int GETTING_DEVICELIST_SUCCESS = 1;
    private static int GETTING_DEVICELIST_FAIL = 2;

    private static View rootView;
    private static LinearLayout loading;
    private static ListView deviceListView;
    private static List<Device> deviceList;
    private static String result = "";
    private static WeakReference<MainActivity> mActivity;
    private DeviceDetailFragment deviceDetail;
    private MainActivity context;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_device_list, container, false);
        loading = (LinearLayout) rootView.findViewById(R.id.device_loading);
        deviceListView = (ListView) rootView.findViewById(R.id.device_list_view);
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
        setActionbar();
        requestDevices();
        mActivity = new WeakReference<MainActivity>(context);
        return rootView;
    }

    private void setActionbar(){
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);//显示返回图标
            actionBar.setDisplayShowHomeEnabled(false);//显示app图标
            actionBar.setTitle(getString(R.string.device_list));
            LogUtil.d("DeviceListFragment","-->setTitle");
        }
    }
    private void requestDevices() {
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = START_GETTING_DEVICELIST;
                mHandler.sendMessage(msg);
                String param = String.format("username=%s", Globals.username);
                try {
                    String api = "http://" + Config.server + ":" + Config.port + Config.getDeviceAPI;
                    result = HttpRequest.sendPost(api, param);
                    if (result.equals("")) {
                        Message fail = new Message();
                        fail.what = GETTING_DEVICELIST_FAIL;
                        mHandler.sendMessage(fail);
                    }
                    else {
                        Message success = new Message();
                        success.what = GETTING_DEVICELIST_SUCCESS;
                        mHandler.sendMessage(success);
                    }

                } catch (Exception e){
                    Message fail = new Message();
                    fail.what = GETTING_DEVICELIST_FAIL;
                    mHandler.sendMessage(fail);
                }
            }
        };
        new Thread(requestThread).start();
    }
//TODO
    private MHandler mHandler = new MHandler();
    private static class MHandler extends Handler{
//        private static WeakReference<MainActivity> mActivity;
//
//        public MHandler(MainActivity activity){
//            mActivity = new WeakReference<MainActivity>(activity);
//        }

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
                    DeviceAdapter adapter = new DeviceAdapter(mActivity.get(),R.layout.device_item,deviceList);
                    deviceListView.setAdapter(adapter);
                } catch (Exception e ) {
                    ProgressBar loadingImage = (ProgressBar) rootView.findViewById(R.id.device_loadiing_bar);
                    loadingImage.setVisibility(View.GONE);
                    TextView loadingText = (TextView) rootView.findViewById(R.id.device_loading_text);
                    loadingText.setText(mActivity.get().getString(R.string.devicelist_error));
                }
            }
            else {
                ProgressBar loadingImage = (ProgressBar) rootView.findViewById(R.id.device_loadiing_bar);
                loadingImage.setVisibility(View.GONE);
                TextView loadingText = (TextView) rootView.findViewById(R.id.device_loading_text);
                loadingText.setText(mActivity.get().getString(R.string.devicelist_fail));
            }
        }
    }

    private static void getDeviceInfo(List<Map<String, String>> devices){
        deviceList = new ArrayList<Device>();
        for (int i = 0; i < devices.size(); i++) {
            Map<String, String> deviceMap = devices.get(i);
            Set<Map.Entry<String,String>> entrySet = deviceMap.entrySet();
            for(Map.Entry<String,String> each : entrySet){
                LogUtil.d("DeviceListFragment",each.getKey()+"->"+each.getValue());
            }
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
