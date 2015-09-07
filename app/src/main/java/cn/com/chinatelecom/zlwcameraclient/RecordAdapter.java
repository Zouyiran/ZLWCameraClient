package cn.com.chinatelecom.zlwcameraclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Zouyiran on 2015/9/7.
 */
public class RecordAdapter extends ArrayAdapter<Record> {
    private int subLayoutId;

    public RecordAdapter(Context context, int subLayoutId, List<Record> recordList){
        super(context,subLayoutId,recordList);
        this.subLayoutId = subLayoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        class ViewHolder{
            TextView recordStart;
            TextView recordEnd;
            TextView recordDuration;
        }
        Record record = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view =  LayoutInflater.from(getContext()).inflate(subLayoutId,null);
            viewHolder = new ViewHolder();
            viewHolder.recordStart = (TextView) view.findViewById(R.id.record_start);
            viewHolder.recordEnd = (TextView) view.findViewById(R.id.record_end);
            viewHolder.recordDuration = (TextView) view.findViewById(R.id.record_duration);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();//return Object
        }
        viewHolder.recordStart.setText(record.getStart());
        viewHolder.recordEnd.setText(record.getEnd());
        viewHolder.recordDuration.setText(record.getDuration());
        return view;
    }
}
