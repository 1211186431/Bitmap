package com.example.scrollview;

import android.graphics.Bitmap;

public class Image {
    int type;
    String id;
    String path;
    public Image(String path,int type){
        this.type=type;//1是图片  2是音乐  3是视频 4是画板
        this.path=path;
        this.id=GUID.getGUID();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }



    public void setType(int type) {
        this.type = type;
    }


}
