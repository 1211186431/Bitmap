package com.example.scrollview.db.javabean;

public class MyList {
    String l_id;
    String myText;
    int star;
    String mytime;
    String iTime;

    public MyList(String l_id, String myText, int star, String mytime,String iTime) {
        this.l_id = l_id;
        this.myText = myText;
        this.star = star;
        this.mytime = mytime;
        this.iTime=iTime;
    }

    public String getiTime() {
        return iTime;
    }

    public void setiTime(String iTime) {
        this.iTime = iTime;
    }

    public String getL_id() {
        return l_id;
    }

    public void setL_id(String l_id) {
        this.l_id = l_id;
    }

    public String getMyText() {
        return myText;
    }

    public void setMyText(String myText) {
        this.myText = myText;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getMytime() {
        return mytime;
    }

    public void setMytime(String mytime) {
        this.mytime = mytime;
    }
}
