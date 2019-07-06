package com.bytedance.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_VIDEO = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoPath(getVideoPath(R.raw.material));

        SeekBar progressBar = findViewById(R.id.progress);

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                videoView.seekTo(i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(!videoView.isPlaying()){
                    //setPlayStatus();
                    videoView.start();
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //MediaController mediaController = new MediaController(this);

        //videoView.setMediaController(mediaController);
        //mediaController.setAnchorView(videoView);

        Button buttonPause = findViewById(R.id.buttonPause);
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
            }
        });

        Button buttonPlay = findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
            }
        });

        Button buttonSelect = findViewById(R.id.buttonSelect);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectIntent = new Intent();
                selectIntent.setType("video/*");
                selectIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(selectIntent,"Select Video"), 1);

                Uri mySelectedVideo = selectIntent.getData();
                //Log.i("uri::::",mySelectedVideo.toString());
                /*Intent playIntent = new Intent(Intent.ACTION_VIEW);
                playIntent.setDataAndType(mySelectedVideo,"video/mp4");
                startActivity(playIntent);*/
            }
        });

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //int screenWidth = getResources().getDisplayMetrics().widthPixels;
        //int screenHeight = getResources().getDisplayMetrics().heightPixels;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //setSystemUiHide();
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            Log.i("orientationset", "onConfigurationChanged: landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(MainActivity.this, 400f));
            //setSystemUiVisible();

            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            Log.i("orientationset", "onConfigurationChanged: portait");
        }
    }
    public int dp2px(Context context, float dipValue) {
        float m=context.getResources().getDisplayMetrics().density ;
        return (int)(dipValue * m + 0.5f) ;
    }

    private void setVideoViewScale(int width, int height) {
        RelativeLayout rlVideo = findViewById(R.id.videoLayout);
        ViewGroup.LayoutParams params = rlVideo.getLayoutParams();
        params.width = width;
        params.height = height;
        rlVideo.setLayoutParams(params);

        VideoView videoPlayer = findViewById(R.id.videoView);
        ViewGroup.LayoutParams layoutParams = videoPlayer.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        videoPlayer.setLayoutParams(layoutParams);
    }
    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }
}
