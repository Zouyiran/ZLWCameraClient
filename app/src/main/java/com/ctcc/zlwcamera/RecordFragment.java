package com.ctcc.zlwcamera;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ctcc.zlwcamera.data_struct.Record;
import com.ctcc.zlwcamera.tools.*;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */

public class RecordFragment extends Fragment {
    private static int GETTING_RECORDS_SUCCESS = 1;
    private static int GETTING_RECORDS_FAIL = 2;
    private static int page = 1;
    private static int num = 10;

    private GetRecordTask getRecordTask = null;


    private static View rootView;
    private static LinearLayout loading;
    private static ProgressBar loadingBar;
    private static TextView loadingText;
    private static PullToRefreshListView recordListView;
    private static List<Record> recordList;
    private static RecordAdapter adapter;
    private static WeakReference<RecordActivity> mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_record, container, false);

        loading = (LinearLayout) rootView.findViewById(R.id.loading);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);
        loadingText = (TextView) rootView.findViewById(R.id.loading_text);

        recordListView = (PullToRefreshListView) rootView.findViewById(R.id.record_list_view);

        setPullToRefreshMode();

        attemptGetRecord(0, num, true);

        mActivity = new WeakReference<RecordActivity>((RecordActivity) getActivity());

        recordList =  new ArrayList<Record>();

        return rootView;
    }

//    private void setActionbar(){
//        ActionBar actionBar = getActivity().getActionBar();
//        if(actionBar != null){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(false);
//            actionBar.setTitle(getActivity().getResources().getString(R.string.record_list));
//        }
//    }

    private void setPullToRefreshMode(){
        recordListView.setOnRefreshListener(refreshListener);
        recordListView.setOnItemClickListener(itemListener);
        recordListView.setMode(PullToRefreshBase.Mode.BOTH);
        recordListView.setScrollingWhileRefreshingEnabled(false);
        ILoadingLayout labels = recordListView.getLoadingLayoutProxy(false, true);
        labels.setPullLabel(getResources().getString(R.string.record_pull_to_load));
        labels.setRefreshingLabel(getResources().getString(R.string.record_start_loading));
        labels.setReleaseLabel(getResources().getString(R.string.record_release_to_load));
    }

    private void attemptGetRecord(int start,int num, boolean init){
        if (getRecordTask != null) {
            return;
        }
        if(init){
            showProgress(true);
        }
        getRecordTask = new GetRecordTask(start, num, init);
        getRecordTask.execute((Void) null);
    }

    private void showProgress(final boolean show) {
        loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public class GetRecordTask extends AsyncTask<Void, Void, Integer> {

        String recordInfo;
        int start;
        int num;
        boolean init;

        GetRecordTask(int start, int num,boolean init) {
            this.start = start;
            this.num = num;
            this.init = init;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                String param = String.format("deviceid=%s&start=%d&num=%d", Globals.nowDevice.getId(), start, num);
                String api = "http://" + Config.address + ":" + Config.port + Config.getRecordAPI;
                recordInfo = HttpRequest.sendPost(api, param);
                if (recordInfo.equals("")) {
                    return GETTING_RECORDS_FAIL;
                } else {
                    return GETTING_RECORDS_SUCCESS;
                }

            } catch (Exception e) {
                return GETTING_RECORDS_FAIL;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            getRecordTask = null;
            if (result == GETTING_RECORDS_SUCCESS) {
                try {
                    parseRecordInfo(Functions.readJson(recordInfo));//List<Map<String,String>>
                    if(init){
                        showProgress(false);
                        adapter = new RecordAdapter(mActivity.get(),R.layout.record_item,recordList);
                        recordListView.setAdapter(adapter);
                    }else{
                        adapter.notifyDataSetChanged();
                        recordListView.onRefreshComplete();
                    }
                } catch (Exception e ) {
                    if(!init){
                        showProgress(true);
                    }
                    loadingBar.setVisibility(View.GONE);
                    loadingText.setText(mActivity.get().getString(R.string.record_parse_error));
                }
            } else {
                if(!init){
                    showProgress(true);
                }
                loadingBar.setVisibility(View.GONE);
                loadingText.setText(mActivity.get().getString(R.string.record_loading_fail));
            }
        }

        @Override
        protected void onCancelled() {
            getRecordTask = null;
        }
    }

    private static void parseRecordInfo(List<Map<String,String>> records){
        for(int i=0;i<records.size();i++){
            Map<String, String> recordMap = records.get(i);
//            Set<Map.Entry<String,String>> entrySet = recordMap.entrySet();
//            for(Map.Entry<String,String> each : entrySet){
//                LogUtil.d("RecordFragment", Globals.nowDevice.getName()+":"+each.getKey() + "->" + each.getValue());
//            }
            Record record = new Record();
            record.setId(recordMap.get("id"));
            record.setUrl(recordMap.get("url"));
            record.setStart(recordMap.get("start"));
            record.setEnd(recordMap.get("end"));
            record.setDuration(recordMap.get("duration"));
            recordList.add(record);
        }
    }

    private  AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Globals.nowRecord = recordList.get(position-1);
            String url = Globals.nowRecord.getUrl();
            String ratio = Globals.nowDevice.getRatio();
            PlayerActivity.actionStart(getActivity(),url,ratio,"record");
        }
    };


    private PullToRefreshBase.OnRefreshListener2<ListView> refreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView){
            recordList.clear();
            attemptGetRecord(0, num, false);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            attemptGetRecord(page * num, num, false);
            page++;
        }
    };
}
