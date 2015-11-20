package com.ctcc.zlwcamera;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctcc.zlwcamera.data_struct.Device;
import com.ctcc.zlwcamera.tools.Config;
import com.ctcc.zlwcamera.tools.Functions;
import com.ctcc.zlwcamera.tools.Globals;
import com.ctcc.zlwcamera.tools.HttpRequest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */

public class DeviceFragment extends Fragment {

    private static int GETTING_DEVICE_SUCCESS = 1;
    private static int GETTING_DEVICE_FAIL = 2;

    private GetDeviceTask getDeviceTask = null;

    private static View rootView;
    private static LinearLayout loading;
    private static ProgressBar loadingBar;
    private static TextView loadingText;

    private static ListView deviceListView;
    private static List<Device> deviceList;
    private static WeakReference<MainActivity> mActivity;
    private MainActivity context;

//    Called to have the fragment instantiate its user interface view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_device, container, false);
        loading = (LinearLayout) rootView.findViewById(R.id.loading);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);
        loadingText = (TextView) rootView.findViewById(R.id.loading_text);
        deviceListView = (ListView) rootView.findViewById(R.id.device_list_view);
//        deviceListView.setEmptyView(rootView.findViewById(R.id.empty_view));
        mActivity = new WeakReference<MainActivity>((MainActivity) getActivity());

        attemptGetDevice();

        return rootView;
    }

    private void attemptGetDevice(){
        if (getDeviceTask != null) {
            return;
        }
        showProgress(true);
        getDeviceTask = new GetDeviceTask();
        getDeviceTask.execute((Void) null);
    }

    private void showProgress(final boolean show) {
        loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public class GetDeviceTask extends AsyncTask<Void, Void, Integer> {

        String deviceInfo;

        GetDeviceTask() {
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                String param = String.format("username=%s", Globals.nowUsername);
                String api = "http://" + Config.address + ":" + Config.port + Config.getDeviceAPI;
                deviceInfo = HttpRequest.sendPost(api, param);
                if (deviceInfo.equals("")) {
                    return GETTING_DEVICE_FAIL;
                } else {
                    return GETTING_DEVICE_SUCCESS;
                }

            } catch (Exception e) {
                return GETTING_DEVICE_FAIL;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            getDeviceTask = null;
            if (result == GETTING_DEVICE_SUCCESS) {
                try {
                    showProgress(false);
                    parseDeviceInfo(Functions.readJson(deviceInfo));//List<Map<String,String>>
                    DeviceAdapter adapter = new DeviceAdapter(mActivity.get(),R.layout.device_item,deviceList);
                    deviceListView.setAdapter(adapter);
                } catch (Exception e ) {
                    loadingBar.setVisibility(View.GONE);
                    loadingText.setText(mActivity.get().getString(R.string.device_parse_error));
                }
            } else {
                loadingBar.setVisibility(View.GONE);
                loadingText.setText(mActivity.get().getString(R.string.device_loading_fail));
            }
        }

        @Override
        protected void onCancelled() {
            getDeviceTask = null;
            showProgress(false);
        }
    }

    private static void parseDeviceInfo(List<Map<String, String>> devices){
        deviceList = new ArrayList<Device>();
        for (int i = 0; i < devices.size(); i++) {
            Map<String, String> deviceMap = devices.get(i);
//            Set<Map.Entry<String,String>> entrySet = deviceMap.entrySet();
//            for(Map.Entry<String,String> each : entrySet){
//                LogUtil.d("DeviceFragment",each.getKey()+"->"+each.getValue());
//            }
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
