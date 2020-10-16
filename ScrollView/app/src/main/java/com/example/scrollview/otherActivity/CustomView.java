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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.scrollview.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CustomView extends View {    //笔画列表
    List<Path> listStrokes = new ArrayList<Path>();
    List<Integer> colors=new ArrayList<Integer>();
    List<Integer> sizes=new ArrayList<Integer>();
    Path pathStroke;
    Bitmap memBMP;
    Paint memPaint;
    Canvas memCanvas;
    boolean mBooleanOnTouch = false;   //上一个点
    float oldx;
    float oldy;
    int size=5;
    int color=Color.RED;
    String path="";//图片路径


    public CustomView(Context context, AttributeSet attrs) {

        super(context, attrs);
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
                }
                break;
        }       //本View已经处理完了Touch事件，不需要向上传递
        return true;
    }

    void drawStrokes() {
        if (memCanvas == null) {           //缓冲位图
            memBMP = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            memCanvas = new Canvas(); //缓冲画布
            memCanvas.setBitmap(memBMP); //为画布设置位图，图形实际保存在位图中
            memPaint = new Paint(); //画笔
            memPaint.setAntiAlias(true); //抗锯齿
            memPaint.setColor(color); //画笔颜色
            memPaint.setStyle(Paint.Style.STROKE); //设置填充类型
            memPaint.setStrokeWidth(size); //设置画笔宽度
            memCanvas.drawColor(Color.WHITE);
            drawold();
            Log.v("memCanvas","null");
        }

        for (int i=0;i<listStrokes.size();i++) {   //每次都会重新画一次
            memPaint.setColor(colors.get(i));
            memPaint.setStrokeWidth(sizes.get(i));
            memCanvas.drawPath(listStrokes.get(i), memPaint);

        }
        invalidate(); //刷新屏幕
    }

    @Override

    protected void onDraw(Canvas canvas) {  //初始化结束会调用，每次绘制也会调用
        Paint paint = new Paint();
         Log.v("on","onDraw");
        if (memBMP != null){
            canvas.drawBitmap(memBMP, 0, 0, paint);
        }
        else{
            if(!path.equals("")){
                FileInputStream fs = null;
                try {
                    fs = new FileInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(fs);
                    canvas.drawBitmap(bitmap, new Matrix(), paint);  //利用每次重新画，画好后就保存了
                    Log.v("123","123");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void backListStrokes(List<Path> listStrokes) {
        this.listStrokes = listStrokes;
        colors.remove(colors.size()-1);
        sizes.remove(sizes.size()-1);
        memCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清空画布
        drawold();

    }
    public void goListStrokes(int color,int size,List<Path> listStrokes){  //恢复原来的颜色大小
        this.listStrokes = listStrokes;
        this.colors.add(color);
        this.sizes.add(size);
        memCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清空画布
        drawold();
    }
    public List<Integer> getColors() {
        return colors;
    }

    public List<Integer> getSizes() {
        return sizes;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public void setSizes(List<Integer> sizes) {
        this.sizes = sizes;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Path> getListStrokes() {
        return listStrokes;
    }

    public Bitmap getMemBMP(){
        return memBMP;
    }
    public void drawold(){  //每次画是都重画，每次都重新加载一次
        if(!path.equals("")){
            FileInputStream fs = null;
            try {
                fs = new FileInputStream(path);
                Bitmap bitmap = BitmapFactory.decodeStream(fs);
                memCanvas.drawBitmap(bitmap, new Matrix(), memPaint);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}

