package cn.com.chinatelecom.zlwcameraclient;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.com.chinatelecom.zlwcameraclient.tools.Config;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */

public class RecordListFragment extends Fragment{
    private int START_GETTING_RECORDS = 0;
    private int GETTING_RECORDS_SUCCESS = 1;
    private int GETTING_RECORDS_FAIL = 2;
    private int page = 1;
    private int num = 10;

    private View rootView;
    private LinearLayout loading;
    private PullToRefreshListView recordListView;
    private List<Record> recordList =  new ArrayList<Record>();
    private RecordAdapter adapter;
    private String result = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_record_list, container, false);
        loading = (LinearLayout) rootView.findViewById(R.id.record_loading);
        recordListView = (PullToRefreshListView) rootView.findViewById(R.id.record_list_view);
        setActionbar();
        setPullToRefreshMode();
        initRecords(0, num);
        return rootView;
    }

    private void setActionbar(){
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//显示返回图标
            actionBar.setDisplayShowHomeEnabled(false);//显示app图标
            actionBar.setTitle(getActivity().getResources().getString(R.string.record_list));
        }
    }

    private void setPullToRefreshMode(){
        recordListView.setOnRefreshListener(refreshListener);
        recordListView.setOnItemClickListener(itemListener);
        recordListView.setMode(PullToRefreshBase.Mode.BOTH);
        recordListView.setScrollingWhileRefreshingEnabled(false);
        ILoadingLayout labels = recordListView.getLoadingLayoutProxy(false, true);
        labels.setPullLabel(getResources().getString(R.string.recordlist_pulltoload));
        labels.setRefreshingLabel(getResources().getString(R.string.recordlist_startloading));
        labels.setReleaseLabel(getResources().getString(R.string.recordlist_releasetoload));

    }

    private void initRecords(int start, int num) {
        final int record_start = start;
        final int record_num = num;
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = START_GETTING_RECORDS;
                handler.sendMessage(msg);
                String param = String.format("deviceid=%s&start=%d&num=%d", Globals.NOW_DEVICE.getId(), record_start, record_num);
                try {
                    String api = "http://" + Config.server + ":" + Config.port + Config.getRecordAPI;
                    result = HttpRequest.sendPost(api, param);
                    if (result.equals("")) {
                        Message fail = new Message();
                        fail.what = GETTING_RECORDS_FAIL;
                        handler.sendMessage(fail);
                    }
                    else {
                        Message success = new Message();
                        success.what = GETTING_RECORDS_SUCCESS;
                        handler.sendMessage(success);
                    }
                } catch (Exception e){
                    Message fail = new Message();
                    fail.what = GETTING_RECORDS_FAIL;
                    handler.sendMessage(fail);
                }
            }
        };
        new Thread(requestThread).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == START_GETTING_RECORDS) {
                loading.setVisibility(View.VISIBLE);
            }
            else if (msg.what == GETTING_RECORDS_SUCCESS) {
                loading.setVisibility(View.GONE);
                try {
                    List<Map<String,String>> records = Functions.readJson(result);
                    getRecordInfo(records);
                    adapter = new RecordAdapter(getActivity(),R.layout.record_item,recordList);
                    recordListView.setAdapter(adapter);
                } catch (Exception e ) {
                    ProgressBar loadingImage = (ProgressBar) rootView.findViewById(R.id.record_loading_bar);
                    loadingImage.setVisibility(View.GONE);
                    TextView loadingText = (TextView) rootView.findViewById(R.id.record_loading_text);
                    loadingText.setText(getResources().getString(R.string.recordlist_error));                }
            }
            else {
                ProgressBar loadingImage = (ProgressBar) rootView.findViewById(R.id.record_loading_bar);
                loadingImage.setVisibility(View.GONE);
                TextView loadingText = (TextView) rootView.findViewById(R.id.record_loading_text);
                loadingText.setText(getResources().getString(R.string.recordlist_fail));
            }
        }
    };

    private void getRecordInfo(List<Map<String,String>> records){
        for(int i=0;i<records.size();i++){
            Map<String, String> recordMap = records.get(i);
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
            Globals.NOW_RECORD = recordList.get(position);
            String url = Globals.NOW_RECORD.getUrl();
            String ratio = Globals.NOW_DEVICE.getRatio();
            Intent intent = new Intent();
            intent.setClass(getActivity(), PlayerActivity.class);
            intent.putExtra("path", url);
            intent.putExtra("ratio", ratio);
            intent.putExtra("type", "record");
            startActivity(intent);
        }
    };


    private PullToRefreshBase.OnRefreshListener2<ListView> refreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView){
            new UpdateTask(recordListView,adapter,recordList).execute();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            new GetDataTask(recordListView,adapter,recordList).execute();
        }
    };


    private class UpdateTask extends AsyncTask<Void,Void,Void> {

        private PullToRefreshListView mPullToRefreshListView;
        private RecordAdapter mEssayAdapter;
        private List<Record> mRecordList;

        public UpdateTask(PullToRefreshListView pullRefreshListView, RecordAdapter recordAdapter,List<Record> recordList){
            mPullToRefreshListView = pullRefreshListView;
            mEssayAdapter = recordAdapter;
            mRecordList = recordList;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params){
            String param = String.format("deviceid=%s&start=%d&num=%d", Globals.NOW_DEVICE.getId(), 0, num);
            try {
                String api = "http://" + Config.server + ":" + Config.port + Config.getRecordAPI;
                result = HttpRequest.sendPost(api, param);
                if (result.length() != 0) {
                    try{
                        List<Map<String,String>> records = Functions.readJson(result);
                        if(recordList.size() != 0){
                            recordList.clear();
                        }
                        getRecordInfo(records);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            mEssayAdapter.notifyDataSetChanged();
            mPullToRefreshListView.onRefreshComplete();
        }
    }


    private class GetDataTask extends AsyncTask<Void,Void,Void> {

        private PullToRefreshListView mPullToRefreshListView;
        private RecordAdapter mEssayAdapter;
        private List<Record> mEssayList;

        public GetDataTask(PullToRefreshListView pullRefreshListView, RecordAdapter essayAdapter,List<Record> essayList){
            mPullToRefreshListView = pullRefreshListView;
            mEssayAdapter = essayAdapter;
            mEssayList = essayList;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                refreshRequest(page * num, num);
                page++;
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        void refreshRequest(int start, int num){
            String param = String.format("deviceid=%s&start=%d&num=%d", Globals.NOW_DEVICE.getId(), start, num);
            try{
                String api = "http://" + Config.server + ":" + Config.port + Config.getRecordAPI;
                result = HttpRequest.sendPost(api, param);
                if(result.length() != 0){
                    try{
                        List<Map<String,String>> records = Functions.readJson(result);
                        getRecordInfo(records);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            mEssayAdapter.notifyDataSetChanged();
            mPullToRefreshListView.onRefreshComplete();
        }
    }
}
