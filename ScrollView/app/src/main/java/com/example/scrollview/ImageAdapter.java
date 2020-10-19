package com.example.scrollview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
            case 4:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item,parent,false);
                ImageView image = (ImageView) convertView.findViewById(R.id.image);
                TextView id = (TextView) convertView.findViewById(R.id.GUID);
                String path=mData.get(position).getPath();
                FileInputStream fs = null;
                File f = new File(path);
                // 加载本地图片
                Glide.with(convertView.getContext()).load(f).placeholder(R.drawable.ic_launcher_background).into(image);//教材499 https://www.jianshu.com/p/791ee473a89b
                id.setText(mData.get(position).getId());
//                try {
//                    fs = new FileInputStream(f);
//                    Bitmap bitmap = BitmapFactory.decodeStream(fs);
//                    image.setImageBitmap(bitmap);  //用存的bitmap显示，不好更新

//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                break;
            case 2:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item2,parent,false);
                TextView id2 = (TextView) convertView.findViewById(R.id.GUID);
                TextView n=convertView.findViewById(R.id.name);
                id2.setText(mData.get(position).getId());
                String path2=mData.get(position).getPath();
                String[] p=path2.split("/");
                String name=p[p.length-1];
                n.setText(name);
                Log.v("p",p[p.length-1]);
                break;
            case 3:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item3,parent,false);
                TextView id3 = (TextView) convertView.findViewById(R.id.GUID);
                TextView n2=convertView.findViewById(R.id.name);
                id3.setText(mData.get(position).getId());
                String path22=mData.get(position).getPath();
                String[] p2=path22.split("/");
                String name2=p2[p2.length-1];
                n2.setText(name2);
                break;

            default:
                break;

        }
        return convertView;
    }
}