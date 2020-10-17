package com.example.scrollview.db.javabean;

public class infor {
    String i_id;
    String mypath;
    int mytype;
    String l_id;

    public infor(String i_id, String mypath, int mytype, String l_id) {
        this.i_id = i_id;
        this.mypath = mypath;
        this.mytype = mytype;
        this.l_id = l_id;
    }

    public String getI_id() {
        return i_id;
    }

    public void setI_id(String i_id) {
        this.i_id = i_id;
    }

    public String getMypath() {
        return mypath;
    }

    public void setMypath(String mypath) {
        this.mypath = mypath;
    }

    public int getMytype() {
        return mytype;
    }

    public void setMytype(int mytype) {
        this.mytype = mytype;
    }

    public String getL_id() {
        return l_id;
    }

    public void setL_id(String l_id) {
        this.l_id = l_id;
    }
}
