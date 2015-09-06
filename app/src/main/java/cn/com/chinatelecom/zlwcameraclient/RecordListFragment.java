package cn.com.chinatelecom.zlwcameraclient;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import java.util.*;

/**
 * Created by Zouyiran on 2014/11/20.
 *
 */

public class RecordListFragment extends Fragment{
    private View rootView;
    private ActionBar actionBar;
    private LinearLayout recordList;
    private LinearLayout loading;
    private String result = "";
    private Map<String, Map<String, String>> recordMap = new HashMap<String, Map<String, String>>();
    private int START_GETTING_RECORDS = 0;
    private int GETTING_RECORDS_SUCCESS = 1;
    private int GETTING_RECORDS_FAIL = 2;
    private PullToRefreshScrollView mPullRefreshScrollView;
    private int page = 1;
    private int num = 10;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_record_list, container, false);
        recordList = (LinearLayout)rootView.findViewById(R.id.recordlist);
        loading = (LinearLayout) rootView.findViewById(R.id.record_loading);
        requestRecords(0, num);
        actionBar = getActivity().getActionBar();
        actionBar.setTitle(getResources().getString(R.string.recordlist_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.record);

        mPullRefreshScrollView = (PullToRefreshScrollView) rootView.findViewById(R.id.refreshscrollview);
        mPullRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setPullLabel(getResources().getString(R.string.recordlist_pulltoload));
        mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setRefreshingLabel(getResources().getString(R.string.recordlist_startloading));
        mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setReleaseLabel(getResources().getString(R.string.recordlist_releasetoload));
        mPullRefreshScrollView.setOnRefreshListener(refreshListener);

        return rootView;
    }
    private PullToRefreshBase.OnRefreshListener refreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            requestRecords(page * num, num);
            page++;
        }
    };
    private void requestRecords(int start, int num) {
        final int record_start = start;
        final int record_num = num;
        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = START_GETTING_RECORDS;
                handler.sendMessage(msg);
                String param = String.format("deviceid=%s&start=%d&num=%d", Globals.NOW_DEVICE.get("id"), record_start, record_num);
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
                mPullRefreshScrollView.onRefreshComplete();
                try {
                    List records;
                    records = Functions.readJson(result);
                    for (int i = 0; i < records.size(); i++) {
                        Map<String, String> record = (Map)records.get(i);
                        View recordView = createRecordView(record);
                        recordMap.put(record.get("id"), record);
                        recordList.addView(recordView);

                        int height = (int)(1 * getResources().getDisplayMetrics().density);
                        LinearLayout.LayoutParams lineP = new LinearLayout.LayoutParams(
                                LayoutParams.FILL_PARENT, height);
                        //int margin = (int)(10 * getResources().getDisplayMetrics().density);
                        //lineP.bottomMargin = margin;
                        //lineP.topMargin = margin;


                        LinearLayout line = new LinearLayout(getActivity());
                        line.setBackground(getResources().getDrawable(R.color.huise));
                        line.setLayoutParams(lineP);//设置布局参数
                        recordList.addView(line);

                    }
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private View createRecordView(Map<String, String> record) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        LinearLayout view = new LinearLayout(getActivity());
        view.setLayoutParams(lp);//设置布局参数
        view.setOrientation(LinearLayout.HORIZONTAL);// 设置子View的Linearlayout// 为垂直方向布局
        int paddingValueInPx = (int)(5 * getResources().getDisplayMetrics().density);
        view.setPadding(paddingValueInPx, paddingValueInPx, paddingValueInPx, paddingValueInPx);
        //定义ImageView的属性
        LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                (float)1.0);
        imageLp.gravity = Gravity.CENTER_VERTICAL;
        ImageView typeImage = new ImageView(getActivity());
        typeImage.setLayoutParams(imageLp);
        typeImage.setImageResource(R.drawable.record);


        //定义文字Layout
        int heightInPx = (int)(60 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                heightInPx,
                (float)7.0
        );
        LinearLayout textLineLayout = new LinearLayout(getActivity());
        textLineLayout.setGravity(Gravity.CENTER_VERTICAL);
        textLineLayout.setLayoutParams(textLp);
        textLineLayout.setOrientation(LinearLayout.VERTICAL);
        int textPaddingPx = (int)(15 * getResources().getDisplayMetrics().density);
        textLineLayout.setPadding(textPaddingPx, 0, textPaddingPx, 0);

        //定义起始时间Layout
        LayoutParams startLp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        TextView start = new TextView(getActivity());
        start.setLayoutParams(startLp);
        start.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        start.setText(getResources().getString(R.string.recordlist_starttime) + record.get("start"));

        //定义结束时间  Layout
        LayoutParams endLp = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        TextView end = new TextView(getActivity());
        end.setLayoutParams(endLp);
        end.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        end.setText(getResources().getString(R.string.recordlist_endtime) + record.get("end"));

        //定义持续时间Layout
        LayoutParams durLp = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        TextView duration = new TextView(getActivity());
        duration.setLayoutParams(durLp);
        duration.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        duration.setText(getResources().getString(R.string.recordlist_duration) + record.get("duration"));


        textLineLayout.addView(start);
        textLineLayout.addView(end);
        textLineLayout.addView(duration);

        //定义ImageView的属性
        LinearLayout.LayoutParams buttonLp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                (float)2.0);
        buttonLp.gravity = Gravity.CENTER_VERTICAL;
        Button playButton = new Button(getActivity());
        playButton.setLayoutParams(buttonLp);
        playButton.setBackground(getResources().getDrawable(R.drawable.button_background));
        playButton.setText(getString(R.string.recordlist_play_button));
        playButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        playButton.setTextColor(getResources().getColor(R.color.white));
        playButton.setOnClickListener(recordClickListener);
        playButton.setId(Integer.parseInt(record.get("id")));

        view.addView(typeImage);//将TextView 添加到子View 中
        view.addView(textLineLayout);//将TextView 添加到子View 中
        view.addView(playButton);

        return view;
    }

    private View.OnClickListener recordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String url = recordMap.get(String.valueOf(v.getId())).get("url");
            String ratio = Globals.NOW_DEVICE.get("ratio");
            Intent intent = new Intent();
            intent.setClass(getActivity(), PlayerActivity.class);

            if (intent != null) {
                intent.putExtra("path", url);
                intent.putExtra("ratio", ratio);
                intent.putExtra("type", "record");
                startActivity(intent);
            }

        }
    };
}  
