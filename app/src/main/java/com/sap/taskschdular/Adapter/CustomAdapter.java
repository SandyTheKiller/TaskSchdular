package com.sap.taskschdular.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sap.taskschdular.Model.DataModel;
import com.sap.taskschdular.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Sandip on 06-04-2017.
 */

public class CustomAdapter extends ArrayAdapter<DataModel> {

    private List<DataModel> dataSet=null;
    private ArrayList<DataModel> tempData=null;
    Context mContext;
    private static final int NOT_SELECTED = -1;
    private static class ViewHolder {
        TextView txtConsNo;
        TextView txtMobileNo;
        TextView txtStartTime;
        TextView txtEndTime;
        TextView txtRate;
    }

    public CustomAdapter(List<DataModel> data, Context context) {
        super(context, R.layout.item_list, data);
        this.dataSet = data;
        this.mContext=context;
        this.tempData=new ArrayList<>();
        this.tempData.addAll(data);

    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_list, parent, false);
            viewHolder.txtConsNo = (TextView) convertView.findViewById(R.id.txt_cons_no);
            viewHolder.txtMobileNo = (TextView) convertView.findViewById(R.id.txt_mobile_no);
            viewHolder.txtStartTime = (TextView) convertView.findViewById(R.id.txt_start_time);
            viewHolder.txtEndTime = (TextView) convertView.findViewById(R.id.txt_end_time);
            viewHolder.txtRate= (TextView) convertView.findViewById(R.id.txt_rate);
           // viewHolder.txtAddress = (TextView) convertView.findViewById(R.id.txt_address);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtConsNo.setText(dataModel.getConsinmentNo());
        viewHolder.txtMobileNo.setText(dataModel.getMobileNo());
        viewHolder.txtStartTime.setText(dataModel.getStartTime());
        viewHolder.txtEndTime.setText(dataModel.getEndTime());
        viewHolder.txtRate.setText(dataModel.getRate());
        return convertView;
    }
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        dataSet.clear();
        if (charText.length() == 0) {
            dataSet.addAll(tempData);
        }
        else
        {
            for (DataModel dataConsign : tempData)
            {
                if (dataConsign.getConsinmentNo().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    dataSet.add(dataConsign);
                }
                else if (dataConsign.getMobileNo().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    dataSet.add(dataConsign);
                }
            }
        }
        notifyDataSetChanged();
    }
}