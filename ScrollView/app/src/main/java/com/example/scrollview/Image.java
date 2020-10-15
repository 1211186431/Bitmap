package com.example.scrollview;

import android.graphics.Bitmap;

public class Image {
    Bitmap bitmap;
    int type;
    String id;
    String path;
    public Image(Bitmap bitmap,String path,int type){
      this.bitmap=bitmap;
      this.type=type;  //1是图片  2是音乐  3是视频 4是画板
      this.id=GUID.getGUID();
      this.path=path;
    }
    public Image(String path,int type){ //测试
        this.type=type;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getType() {
        return type;
    }



    public void setType(int type) {
        this.type = type;
    }


}
