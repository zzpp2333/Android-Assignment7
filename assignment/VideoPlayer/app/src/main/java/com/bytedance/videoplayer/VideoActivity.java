package com.bytedance.videoplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView.OnItemClickListener;

public class VideoActivity extends AppCompatActivity {

    private ListView mLv;
    private Button mBtn;
    private List<Uri> mVideos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        initBtn();
        initPermission();
        //initUri();
        mLv = findViewById(R.id.lv);
        mLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                playVideo(mVideos.get(i).getPath());
                Log.i("Clicked::::", mVideos.get(i).getEncodedPath());
            }
        });
    }

    private void initPermission(){
        int state = ActivityCompat.checkSelfPermission(VideoActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (state == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(VideoActivity.this, "already granted",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        //Log.i("permissiongrant","from VIdeo Activity");
        ActivityCompat.requestPermissions(VideoActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }
    private void initBtn(){
        mBtn = findViewById(R.id.showvideos);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initUri();
                mLv.setAdapter(new MyAdapter(mVideos));
                //mRv.getAdapter().notifyDataSetChanged();
                Log.i("file",String.valueOf(mVideos.size()));
                //Log.i("file",mVideos.get(0).getEncodedPath());
            }
        });
    }

    private void initUri(){
        File dcimFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File cameraFile = new File(dcimFile,"Camera");

        if(cameraFile.exists() && cameraFile.isDirectory()){
            File[] mFileList = cameraFile.listFiles();
            if(mFileList != null){
                for(int i = 0 ; i < mFileList.length ; i++){
                    if(mFileList[i].getName().contains(".mp4")){
                        mVideos.add(Uri.fromFile(mFileList[i]));
                        Log.i("file",mFileList[i].getName());
                    }
                }
            }
        }
    }

    public class MyAdapter extends BaseAdapter {
        private List<Uri> uriLists;

        public MyAdapter(List<Uri> uriLists) {
            super();
            this.uriLists = uriLists;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return uriLists.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return uriLists.get(position);
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        @Override
        public View getView(int position, View v, ViewGroup arg2) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            int width = (screenWidth < screenHeight)?screenWidth:screenHeight;
            View view = getLayoutInflater().inflate(R.layout.im_video_item, null);
            ImageView imageView = view.findViewById(R.id.imageItem);
            Bitmap bitmap = getVideoThumbnail(uriLists.get(position).getEncodedPath(),
                    width,width,MediaStore.Images.Thumbnails.MICRO_KIND);
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
            }else{
                imageView.setImageResource(R.drawable.defaultcover);
            }
            //textView.setText(listPictures.get(position).getPath());
            return view;
        }
        private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
            Bitmap bitmap = null;
            // 获取视频的缩略图
            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
//            System.out.println("w"+bitmap.getWidth());
//            System.out.println("h"+bitmap.getHeight());
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            return bitmap;
        }
    }

    //调用系统播放器   播放视频
    public void playVideo(String videoPath) {
    //public void playVideo(Uri videoUri) {

        Log.i("Clicked::",videoPath);
        Intent playIntent = new Intent(Intent.ACTION_VIEW);
        File videoFile = new File(videoPath);
        Log.i("Clicked::",String.valueOf(videoFile.exists()));
        Uri uriContent = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID +
                ".provider",videoFile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            playIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            playIntent.setDataAndType(uriContent, "video/*");
        } else {
            playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            playIntent.setDataAndType(uriContent, "video/*");
        }
        startActivity(playIntent);
    }
}
