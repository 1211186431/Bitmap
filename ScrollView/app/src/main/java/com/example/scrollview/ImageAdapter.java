package com.example.scrollview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class ImageAdapter extends BaseAdapter {

    private ArrayList<Image> mData;
    private Context mContext;

    public ImageAdapter( ArrayList<Image> mData, Context mContext) {
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



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (mData.get(position).getType()){
            case 1:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item,parent,false);
                ImageView image = (ImageView) convertView.findViewById(R.id.image);
                TextView id = (TextView) convertView.findViewById(R.id.GUID);
                image.setImageBitmap(mData.get(position).getBitmap());
                id.setText(mData.get(position).getId());
                break;
            case 2:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item2,parent,false);
                TextView id2 = (TextView) convertView.findViewById(R.id.GUID);
                id2.setText(mData.get(position).getId());
                break;
            case 3:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item3,parent,false);
                TextView id3 = (TextView) convertView.findViewById(R.id.GUID);
                id3.setText(mData.get(position).getId());
                break;
            default:
                break;

        }
        return convertView;
    }
}