package com.example.scrollview.otherActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scrollview.MainActivity;
import com.example.scrollview.R;

public class VideoActivity extends AppCompatActivity {
    Boolean prepare =false;
    int istouch = 1;
    SeekBar seekBar;
    String Path="";
    String n="";
    VideoView mVideoView;
    Boolean isStopThread = false;
    //处理进度条更新
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //更新进度
                    int position = mVideoView.getCurrentPosition();

                    int time = mVideoView.getDuration();
                    int max = seekBar.getMax();
                    if (istouch == 1) {

                        seekBar.setProgress(position * max / time);

                        double n = ((double) position) / 1000;
                        String n2 = String.format("%.2f", n);
                        String time2 = String.format("%.2f", ((double) time) / 1000);
                        TextView t = (TextView) findViewById(R.id.time2);
                        t.setText(n2 + "s / " + time2 + "s");
                    }
                    break;
                default:
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Intent intent=getIntent();
        Path=intent.getStringExtra("path3");
        n=intent.getStringExtra("n");
        Button back=findViewById(R.id.back);
        Button delete=findViewById(R.id.delete);
       seekBar = (SeekBar) findViewById(R.id.seekbar);
        mVideoView=findViewById(R.id.video);
        mVideoView.setVideoPath(Path);//设置视频文件
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //视频加载完成,准备好播放视频的回调
               prepare=true;
                mVideoView.start();
                try {
                    //后台线程发送消息进行更新进度条
                    final int milliseconds = 200;
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
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //视频播放完成后的回调
               mVideoView.resume();
            }
        });
        final Button play=findViewById(R.id.video_play);
        final Button replay=findViewById(R.id.video_Replay);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(mVideoView.isPlaying()){
                        mVideoView.pause();
                        play.setText("Play");
                    }
                    else {
                        mVideoView.start();
                        play.setText("Pause");
                    }
            }
        });
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prepare)
                    mVideoView.resume();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                //;
                double dest = seekBar.getProgress();
                double time = mVideoView.getDuration();
                double max = seekBar.getMax();
                double n = (time * dest / max) / 1000;
                String n2 = String.format("%.2f", n);
                String time2 = String.format("%.2f", time / 1000);
                TextView t = (TextView) findViewById(R.id.time2);
                t.setText(n2 + "s / " + time2 + "s");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "开始滑动！");
                istouch = -1;     //不让线程走
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { //改到这里
                int dest = seekBar.getProgress();
                int time = mVideoView.getDuration();
                int max = seekBar.getMax();
                if (mVideoView.isPlaying()) {
                    mVideoView.seekTo(time * dest / max);
                }
                Log.e("------------", "停止滑动！");
                istouch = 1;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.stopPlayback();
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.stopPlayback();

                Intent intent =
                        new Intent(VideoActivity.this, MainActivity.class);
                intent.putExtra("delete_n", n);
                setResult(111, intent);
                Log.v("delete", n);
                finish();
            }
        });
    }
    @Override

    protected void onDestroy() {

        super.onDestroy();
        isStopThread = true;  //利用变量控制线程结束 ，
        //https://blog.csdn.net/liulanzaijia/article/details/85780831?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase
    }
}