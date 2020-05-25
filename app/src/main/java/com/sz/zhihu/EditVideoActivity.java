package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sz.zhihu.adapter.EditVideoDialogAdapter;
import com.sz.zhihu.po.VideoMedia;
import com.sz.zhihu.utils.ArrayUtils;
import com.sz.zhihu.utils.PermissionUtils;
import com.sz.zhihu.view.EditVideoDialogView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditVideoActivity extends AbstractCustomActivity {

    private RecyclerView recyclerView;
    private BottomSheetDialog dialog;
    private BottomSheetBehavior<View> behavior;
    private View view;
    private TextView close;
    private TextView getVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);
        init();
    }

    private void init() {
        showBottomDialog();
        close = findViewById(R.id.aev_close);
        getVideo = findViewById(R.id.aev_get_video);
        setListeners();
    }

    private void setListeners() {
        close.setOnClickListener(v->finish());
        getVideo.setOnClickListener(v->dialog.show());
    }

    private void showBottomDialog() {
        view = EditVideoDialogView.getView(this);
        dialog = new BottomSheetDialog(this, R.style.DialogTheme);
        dialog.setContentView(view);
        behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setPeekHeight(getPeekHeight());
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.dismiss();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        initRecyclerView(view);
        dialog.show();
    }

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.evd_recyclerview);
        if(PermissionUtils.registerPermissionWithCode(this,PermissionUtils.REQUEST_VIDEO, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            bindData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String[] permissionNotAgrees = ArrayUtils.getPermissionNotAgrees(permissions, grantResults);
        if(permissionNotAgrees.length != 0){
            Toast.makeText(this,"权限未通过",Toast.LENGTH_SHORT).show();
        }else{
            bindData();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void bindData() {
        Map<String, List<VideoMedia>> videoMedias = getVideoMedia(0,20);
        Set<String> strings = videoMedias.keySet();
        List<VideoMedia> allData = new ArrayList<>();
        strings.forEach(s -> {
            List<VideoMedia> videoMediaList = videoMedias.get(s);
            videoMediaList.forEach(allData::add);
        });
        EditVideoDialogAdapter editVideoDialogAdapter = new EditVideoDialogAdapter(this, allData);
        recyclerView.setAdapter(editVideoDialogAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
    }

    private Map<String, List<VideoMedia>> getVideoMedia(int start,int count) {
        Map<String,List<VideoMedia>> data = new HashMap<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] proj = { MediaStore.Video.Thumbnails._ID
                , MediaStore.Video.Thumbnails.DATA
                ,MediaStore.Video.Media.DURATION
                ,MediaStore.Video.Media.SIZE
                ,MediaStore.Video.Media.DISPLAY_NAME
                ,MediaStore.Video.Media.DATE_MODIFIED};
        Cursor cursor = getContentResolver().query(uri, proj, MediaStore.Video.Media.MIME_TYPE + "=?",
                new String[]{"video/mp4"},
                MediaStore.Video.Media.DATE_MODIFIED + " desc limit " + start + " , " + count);
        if(cursor != null){
            while (cursor.moveToNext()){
                int videoId = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))/1024; //单位kb
                if(size<0){
                    //某些设备获取size<0，直接计算
                    Log.e("dml","this video size < 0 " + path);
                    size = new File(path).length()/1024;
                }
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
//                long modifyTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));//暂未用到
                MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                String[] projection = { MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
                Cursor thumbnailsCursor = getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
                        , projection
                        , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                        , new String[]{String.valueOf(videoId)}
                        , null);
                String thumbPath = "";
                while(thumbnailsCursor.moveToNext()){
                     thumbPath = thumbnailsCursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                }
                thumbnailsCursor.close();
                // 获取该视频的父路径名
                String dirPath = new File(path).getParentFile().getAbsolutePath();
                //存储对应关系
                if (data.containsKey(dirPath)) {
                    List<VideoMedia> videoMediaList = data.get(dirPath);
                    videoMediaList.add(new VideoMedia(path,thumbPath,duration,size,displayName));
                    continue;
                } else {
                    List<VideoMedia> videoMediaList = new ArrayList<>();
                    videoMediaList.add(new VideoMedia(path,thumbPath,duration,size,displayName));
                    data.put(dirPath,videoMediaList);
                }
            }
            cursor.close();
        }
        return data;
    }

    protected int getPeekHeight() {
        int peekHeight = getResources().getDisplayMetrics().heightPixels;
        //设置弹窗高度为屏幕高度的3/4
        return peekHeight - (peekHeight / 4);
    }

    @Override
    protected void onDestroy() {
        ((ViewGroup)view.getParent()).removeView(view);
        super.onDestroy();
    }
}
