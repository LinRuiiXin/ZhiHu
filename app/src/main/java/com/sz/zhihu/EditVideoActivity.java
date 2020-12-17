package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.sz.zhihu.adapter.EditVideoDialogAdapter;
import com.sz.zhihu.po.User;
import com.sz.zhihu.po.VideoMedia;
import com.sz.zhihu.utils.ArrayUtils;
import com.sz.zhihu.utils.CallBackUtil;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.PermissionUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.view.EditVideoDialogView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditVideoActivity extends AbstractCustomActivity {
    private final int DATA_SIZE = 20;
    private int DATA_START = 0;
    private RecyclerView recyclerView;
    private BottomSheetDialog dialog;
    private BottomSheetBehavior<View> behavior;
    private View view;
    private TextView close;
    private TextView getVideo;
    private EditVideoDialogAdapter adapter;
    private VideoMedia choiceMedia;
    private LoadMoreView loadMoreView = new LoadMoreView() {
        @Override
        public int getLayoutId() {
            return 0;
        }

        @Override
        protected int getLoadingViewId() {
            return 0;
        }

        @Override
        protected int getLoadFailViewId() {
            return 0;
        }

        @Override
        protected int getLoadEndViewId() {
            return 0;
        }
    };
    private JCVideoPlayerStandard video;
    private EditText title;
    private RelativeLayout submitButton;
    private Gson gson = GsonUtils.getGson();
    private String serverLocation;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);
        init();
    }

    private void init() {
        try {
            showBottomDialog();
            close = findViewById(R.id.aev_close);
            getVideo = findViewById(R.id.aev_get_video);
            submitButton = findViewById(R.id.aev_submit_button);
            title = findViewById(R.id.aev_title);
            title.clearFocus();
            video = findViewById(R.id.aev_video);
            video.backButton.setVisibility(View.GONE);
            video.tinyBackImageView.setVisibility(View.GONE);
            serverLocation = getString(R.string.server_location);
            user = DBUtils.queryUserHistory();
            setListeners();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void setListeners() {
        close.setOnClickListener(v->finish());
        getVideo.setOnClickListener(v->dialog.show());
        submitButton.setOnClickListener(v->{
            submitButton.setClickable(false);
            String titleStr = title.getText().toString();
            if(!TextUtils.isEmpty(titleStr)){
                if(choiceMedia != null){
                    File file = new File(choiceMedia.getPath());
                    if(file.length() <= 15728640){
                        String url = serverLocation + "/ArticleService/Article/Video";
                        Map<String,String> params = new HashMap<>(2);
                        params.put("userId",String.valueOf(user.getUserId()));
                        params.put("title",titleStr);
                        RequestUtils.sendFileWithParam(file, url, "video",params, CallBackUtil.commonCallBack(this,
                                ()->{
                                    Toast.makeText(EditVideoActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                                    submitButton.setClickable(true);
                                },
                                simpleDto -> {
                                    if(simpleDto.isSuccess()){
                                        Toast.makeText(EditVideoActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else{
                                        Toast.makeText(EditVideoActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                    }
                                    submitButton.setClickable(true);
                                }));
                    }else{
                        Toast.makeText(EditVideoActivity.this,"视频大小不能超过15MB，请重新选择",Toast.LENGTH_SHORT).show();
                        dialog.show();
                    }
                }else
                    dialog.show();
            }else
                Toast.makeText(EditVideoActivity.this,"请输入标题",Toast.LENGTH_SHORT).show();

        });
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
        List<VideoMedia> allData = getData();
        adapter = new EditVideoDialogAdapter(this, allData);
        if(allData.size()<20){
            adapter.loadMoreEnd();
//            adapter.setLoadMoreView(loadMoreView);
        }
        adapter.setOnLoadMoreListener(()->{
            List<VideoMedia> data = getData();
            adapter.addData(data);
            adapter.loadMoreComplete();
            if(data.size()<20){
                adapter.loadMoreEnd();
                adapter.setLoadMoreView(loadMoreView);
            }
        },recyclerView);
        adapter.setOnItemClickListener((adapter,view,position)->{
            VideoMedia videoMedia = (VideoMedia) adapter.getData().get(position);
            String path = videoMedia.getPath();
            if((new File(path).length()) > 15728640){
                Toast.makeText(EditVideoActivity.this,"视频大小不能超过15MB",Toast.LENGTH_SHORT).show();
                return;
            }
            video.setUp(path,JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
            video.thumbImageView.setImageBitmap(BitmapFactory.decodeFile(videoMedia.getThumbPath()));
            video.setVisibility(View.VISIBLE);
            dialog.dismiss();
            this.choiceMedia = videoMedia;
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
    }

    private List<VideoMedia> getData() {
        Map<String, List<VideoMedia>> videoMedias = getVideoMedia(DATA_START,DATA_SIZE);
        Set<String> strings = videoMedias.keySet();
        List<VideoMedia> allData = new ArrayList<>();
        strings.forEach(s -> {
            List<VideoMedia> videoMediaList = videoMedias.get(s);
            videoMediaList.forEach(allData::add);
        });
        return allData;
    }

    private Map<String, List<VideoMedia>> getVideoMedia(int start,int count) {
        int end = start + count;
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
                MediaStore.Video.Media.DATE_MODIFIED + " desc limit " + start + " , " + end);
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
        DATA_START += count;
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
