package com.example.scrollview.otherActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class WriteView extends View {    //笔画列表
    List<Path> listStrokes = new ArrayList<Path>();
    List<Integer> colors = new ArrayList<Integer>();
    List<Integer> sizes = new ArrayList<Integer>();
    Path pathStroke;
    Bitmap memBMP;
    Paint memPaint;
    Canvas memCanvas;
    boolean mBooleanOnTouch = false;   //上一个点
    float oldx;
    float oldy;
    int size = 5;
    int color = Color.BLACK;
    String path = "";//图片路径

    long endTime=0;
    boolean isStopThread=false;
    boolean n;
    Bitmap saveBMP;
    Canvas saveCanvas;
    Stack<Bitmap> list=new Stack<>();
    int my_x;
    int old_x=0;
    int my_y;
    int old_y=0;
    int x1 = 0;
    int b=0;
    Bitmap oldBitmap;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(memBMP!=null){
                        if(endTime!=0&&System.currentTimeMillis()-endTime>300&&n&&b!=0){
                            Log.v("myTag","s");
                            Bitmap bitmap1 = resizeImage(memBMP, 300, 300);
                            list.push(bitmap1);
                            listStrokes.clear();
                            Paint p = new Paint();
                            //清屏
                            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            memCanvas.drawPaint(p);
                            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                            invalidate();
                            b--;
                        }

                    }


            }

        }
    };


    public WriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.v("myTag", "1");
        try {
            //后台线程发送消息进行更新进度条
            final int milliseconds = 300;
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        if(isStopThread)
                            break;
                        mHandler.sendEmptyMessage(0);
                        try {
                            sleep(milliseconds);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override

    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();        //每一次落下-抬起之间经过的点为一个笔画
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://落下
                pathStroke = new Path();
                pathStroke.moveTo(x, y);
                oldx = x;
                oldy = y;
                mBooleanOnTouch = true;
                listStrokes.add(pathStroke);
                colors.add(color);
                sizes.add(size);
                n=false;
                b=0;
                break;
            case MotionEvent.ACTION_MOVE://移动
                // Add a quadratic bezier from the last point, approaching control point (x1,y1), and ending at (x2,y2).
                // 在Path结尾添加二次Bezier曲线
                if (mBooleanOnTouch) {
                    pathStroke.quadTo(oldx, oldy, x, y);
                    oldx = x;
                    oldy = y;
                    drawStrokes();
                }
                break;
            case MotionEvent.ACTION_UP://抬起
                if (mBooleanOnTouch) {
                    pathStroke.quadTo(oldx, oldy, x, y);
                    drawStrokes();
                    mBooleanOnTouch = false;//一个笔画已经画完
                    x1 += 20;
                    n=true;
                    b=1;
                    endTime=System.currentTimeMillis();
                }
                break;
        }       //本View已经处理完了Touch事件，不需要向上传递

        return true;
    }

    void drawStrokes() {
        for (int i = 0; i < listStrokes.size(); i++) {   //每次都会重新画一次
            memPaint.setColor(colors.get(i));
            memPaint.setStrokeWidth(sizes.get(i));
            memCanvas.drawPath(listStrokes.get(i), memPaint);
        }
        invalidate(); //刷新屏幕
    }

    @Override

    protected void onDraw(Canvas canvas) {  //初始化结束会调用，每次绘制也会调用
        Paint paint = new Paint();
        if(oldBitmap!=null){     //从下面放上面了 原来第一画不会加载
            canvas.drawBitmap(oldBitmap, 0,0, paint);
            saveCanvas.drawBitmap(oldBitmap, 0,0, paint);
        }
        if (memBMP != null &&saveBMP!=null) {
            if(!list.isEmpty()){
                my_x=old_x;
                my_y=old_y;
                for(int i=0;i<list.size();i++){
                    canvas.drawBitmap(list.get(i),my_x, my_y, paint);
                    saveCanvas.drawBitmap(list.get(i),my_x, my_y, paint);
                    Log.v("tag",getWidth()+" 1 "+getHeight());
                    if(my_x<getWidth()-300){
                        my_x+=getWidth()*0.1;
                    }
                    else {
                        my_x=0;
                        my_y+=getHeight()*0.1;
                    }
                }
            }
            canvas.drawBitmap(memBMP, 0, 0, paint);
        } else {
            if (!path.equals("")) {
                FileInputStream fs = null;
                try {
                    fs = new FileInputStream(path);
                    oldBitmap = BitmapFactory.decodeStream(fs);
                    canvas.drawBitmap(oldBitmap, 0,0, paint);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        //在这里缓冲，放原来位置如果不画直接保存，位图为空，会闪退
        if (memCanvas == null && saveCanvas==null) {           //缓冲位图
            memBMP = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            memCanvas = new Canvas(); //缓冲画布
            memCanvas.setBitmap(memBMP); //为画布设置位图，图形实际保存在位图中
            memPaint = new Paint(); //画笔
            memPaint.setAntiAlias(true); //抗锯齿
            memPaint.setColor(color); //画笔颜色
            memPaint.setStyle(Paint.Style.STROKE); //设置填充类型
            memPaint.setStrokeWidth(size); //设置画笔宽度
            Log.v("memCanvas", "null");
            saveBMP = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            saveCanvas = new Canvas(); //缓冲画布
            saveCanvas.setBitmap(saveBMP); //为画布设置位图，图形实际保存在位图中
            saveCanvas.drawColor(Color.WHITE);
        }
    }




    public void setPath(String path) {
        this.path = path;
    }
    public Bitmap getSaveBMP() {
        return saveBMP;
    }

    public int getMy_x() {
        return my_x;
    }

    public void setMy_x(int my_x) {
        this.old_x = my_x;
    }

    public int getMy_y() {
        return my_y;
    }

    public void setMy_y(int my_y) {
        this.old_y = my_y;
    }

    // 缩放
    public static Bitmap resizeImage(Bitmap bitmap, int width, int height) {
        int originWidth = bitmap.getWidth();
        int originHeight = bitmap.getHeight();
        float scaleWidth = ((float) width) / originWidth;
        float scaleHeight = ((float) height) / originHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(0.2f ,0.2f);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, originWidth,
                originHeight, matrix, true);
        return resizedBitmap;
    }

    public void back(){
        if(!list.empty()){
            list.pop();
            invalidate();
        }
        else
            Toast.makeText(getContext(),"不能退了",Toast.LENGTH_LONG).show();
    }

    public boolean isStopThread() {
        return isStopThread;
    }

    public void setStopThread(boolean stopThread) {
        isStopThread = stopThread;
    }
}

