package com.example.scrollview.otherActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.scrollview.MainActivity;
import com.example.scrollview.R;

/**
 * 视频播放界面
 * 和音乐播放界面类似
 * VideoView实现
 */
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
        n=intent.getStringExtra("n");
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);      //此处箭头为系统的图标资源
        //设置左上角导航键的点击监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.stopPlayback();
                finish();
            }

        });
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
        final ImageButton play = findViewById(R.id.video_play);
        final ImageButton replay = findViewById(R.id.video_Replay);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    play.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mVideoView.start();
                    play.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    }
    @Override

    protected void onDestroy() {

        super.onDestroy();
        isStopThread = true;  //利用变量控制线程结束 ，
        //https://blog.csdn.net/liulanzaijia/article/details/85780831?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {
            new android.app.AlertDialog.Builder(this).setTitle("delete").setMessage("是否真的删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent=
                            new Intent(VideoActivity.this, MainActivity.class);
                    intent.putExtra("delete_n",n);
                    setResult(111,intent);
                    Log.v("delete",n);
                    finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}