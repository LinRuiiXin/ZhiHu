package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.ArrayUtils;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.FileUtils;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.PermissionUtils;
import com.sz.zhihu.utils.RequestUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditAccountActivity extends AbstractCustomActivity {

    private static final int CODE_IMAGE = 20;
    private Gson gson;
    private String serverLocation;
    private User user;
    private RoundedImageView portrait;
    private EditText userName;
    private EditText profile;
    private Button save;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        init();
    }

    private void init() {
        prepare();
        bindComponent();
        initToolBar();
        addListener();
    }

    private void prepare() {
        gson = GsonUtils.getGson();
        serverLocation = getString(R.string.server_location);
        user = DBUtils.queryUserHistory();
    }

    private void bindComponent() {
        toolbar = findViewById(R.id.aea_tool_bar);
        portrait = findViewById(R.id.aea_portrait);
        userName = findViewById(R.id.aea_user_name);
        profile = findViewById(R.id.aea_profile);
        save = findViewById(R.id.aea_save);
        userName.setText(user.getUserName());
        profile.setText(user.getProfile());
        String portraitUrl = serverLocation + "/res/User/" + user.getUserId() + "/" + user.getPortraitFileName();
        Glide.with(this).load(portraitUrl).into(portrait);
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void addListener() {
        portrait.setOnClickListener(v -> {
            //如果权限已通过
            if(PermissionUtils.registerPermissionWithCode(this,PermissionUtils.REQUEST_IMAGE, Manifest.permission.READ_EXTERNAL_STORAGE)){
                getBitMapFromPhotoAlbum();
            }
        });
        save.setOnClickListener(v -> {
            save.setClickable(false);
            String newUserName = userName.getText().toString();
            String newProfile = profile.getText().toString();
            user.setUserName(newUserName);
            user.setProfile(newProfile);
            user.setId(user.getUserId());
            String url = serverLocation + "/UserService/User/Update";
            String s = gson.toJson(user);
            RequestUtils.sendRequestWithJson(user, url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(()->{
                        Toast.makeText(EditAccountActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                        save.setClickable(true);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                    runOnUiThread(()->{
                        if(simpleDto.isSuccess()){
                            User updatedUser = gson.fromJson(gson.toJson(simpleDto.getObject()), User.class);
                            updatedUser.setUserId(updatedUser.getId());
                            DBUtils.updateUser(updatedUser);
                            finish();
                        }else{
                            Toast.makeText(EditAccountActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        save.setClickable(true);
                    });
                }
            });
        });
    }

    private void getBitMapFromPhotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent,CODE_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PermissionUtils.REQUEST_IMAGE){
            String[] permissionNotAgrees = ArrayUtils.getPermissionNotAgrees(permissions, grantResults);
            if(permissionNotAgrees == null || permissionNotAgrees.length ==0){
                getBitMapFromPhotoAlbum();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1){
            switch (requestCode) {
                case CODE_IMAGE:
                    try {
                        Toast.makeText(this,"正在上传图片",Toast.LENGTH_SHORT).show();
                        Uri uri = data.getData();
                        Bitmap oldBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        Bitmap newBitmap = zoomImg(oldBitmap, 400,400);
                        //异步提交图片至服务器(还没写)
                        File file = FileUtils.getFile(newBitmap);
                        if(file.exists()){
                            if(FileUtils.checkOutFileSize(file,5242880)){
                                String url = serverLocation + "/UserService/User/Portrait/"+user.getUserId();
                                RequestUtils.sendSingleFile(file, url, "portrait", new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(()->Toast.makeText(EditAccountActivity.this,"上传失败",Toast.LENGTH_SHORT).show());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                                        runOnUiThread(()->{
                                            if(simpleDto.isSuccess()){
                                                String newPortraitFileName = simpleDto.getMsg();
                                                user.setPortraitFileName(newPortraitFileName);
                                                String portraitUrl = serverLocation + "/res/User/" + user.getUserId() + "/" + newPortraitFileName;
                                                Glide.with(EditAccountActivity.this).load(portraitUrl).into(portrait);
                                            }else{
                                                Toast.makeText(EditAccountActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }else{
                                Toast.makeText(this,"图片不能超过5MB",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this,"找不到文件",Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = scaleWidth;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}