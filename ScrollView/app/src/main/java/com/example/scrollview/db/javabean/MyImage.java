package com.example.scrollview.db.javabean;

import com.example.scrollview.db.GUID;

public class MyImage {  //存储多媒体类
    int type;
    String id;
    String path;

    /**
     *               id自动生成
     * @param path  路径
     * @param type  类型 1是图片  2是音乐  3是视频 4是画板/手写板
     */
    public MyImage(String path, int type){
        this.type=type;//
        this.path=path;
        this.id= GUID.getGUID();
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
