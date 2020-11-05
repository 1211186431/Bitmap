package com.example.scrollview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scrollview.db.javabean.MyList;

import java.util.ArrayList;

/**
 * 列表界面适配器
 */
public class ListAdapter extends BaseAdapter {

    private ArrayList<MyList> mData;
    private Context mContext;

    public ListAdapter(ArrayList<MyList> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


   //显示标题，修改时间，星标
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(!mData.isEmpty()){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list,parent,false);
            MyList list=mData.get(position);
            TextView title=convertView.findViewById(R.id.textViewWord);
            TextView myText=convertView.findViewById(R.id.mytext);
            TextView id=convertView.findViewById(R.id.textId);
            TextView mytime=convertView.findViewById(R.id.mytime);
            ImageView imageView=convertView.findViewById(R.id.starP);
            Log.v("_id",list.getL_id());
            id.setText(list.getL_id());
            if(list.getMyText().length()>12){
                String s=list.getMyText().substring(0,12);
                title.setText(s+"...");
            }
            else{
                if(list.getMyText().equals(""))
                    title.setText("无标题");
                else
                    title.setText(list.getMyText());
            }

            myText.setText(list.getMyText());
            if(list.getStar()>0)
                imageView.setImageResource(android.R.drawable.star_big_on);
            else
                imageView.setImageBitmap(null);
            mytime.setText(list.getMytime());
        }
        return convertView;
    }
}