package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rex.editor.view.RichEditorNew;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.Question;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.ArrayUtils;
import com.sz.zhihu.utils.FileUtils;
import com.sz.zhihu.utils.HtmlUtils;
import com.sz.zhihu.utils.PermissionUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.utils.DBUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class EditAnswerActivity extends AbstractCustomActivity implements View.OnClickListener {

    private TextView question;
    private Button commit;
    private Button bold;
    private Button italic;
    private Button title;
    private Button order;
    private Button photo;
    private Button video;
    private Button divider;
    private RichEditorNew editText;
    private final int CODE_IMAGE = 6;
    private final int CODE_VIDEO = 7;
    private Question questionBean;
    private String serverLocation;
    private Gson gson;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }


    private void init() {
        questionBean = (Question) getIntent().getSerializableExtra("question");
        user = DBUtils.queryUserHistory();
        this.question = findViewById(R.id.edit_question);
        commit = findViewById(R.id.edit_button_commit);
        editText = findViewById(R.id.edit_edit_text);
        bold = findViewById(R.id.edit_button_bold);
        italic = findViewById(R.id.edit_button_italic);
        title = findViewById(R.id.edit_button_title);
        order = findViewById(R.id.edit_button_order);
        photo = findViewById(R.id.edit_button_photo);
        video = findViewById(R.id.edit_button_video);
        divider = findViewById(R.id.edit_button_divider);
        serverLocation = getResources().getString(R.string.server_location);
        gson = new Gson();
        question.setText(questionBean.getName());
        editText.setPlaceholder("详细描述你的知识、经验或见解吧~");
        editText.setNeedSetNewLineAfter(true);
        setListener();
    }

    private void setListener() {
        commit.setOnClickListener(this);
        bold.setOnClickListener(this);
        italic.setOnClickListener(this);
        title.setOnClickListener(this);
        order.setOnClickListener(this);
        photo.setOnClickListener(this);
        video.setOnClickListener(this);
        divider.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_button_commit:
                if(PermissionUtils.registerPermissionWithCode(this,PermissionUtils.REQUEST_WRITE,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    uploadAnswer();
                }
                break;
            case R.id.edit_button_bold:
                editText.setBold();
                break;
            case R.id.edit_button_italic:
                editText.setItalic();
                break;
            case R.id.edit_button_title:
                editText.setHeading(1);
                break;
            case R.id.edit_button_order:
                editText.setNumbers();
                break;
            case R.id.edit_button_photo:
                //如果权限已通过
                if(PermissionUtils.registerPermissionWithCode(this,PermissionUtils.REQUEST_IMAGE,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    getBitMapFromPhotoAlbum();
                }
                break;
            case R.id.edit_button_video:
                //如果权限已通过
                if(PermissionUtils.registerPermissionWithCode(this,PermissionUtils.REQUEST_VIDEO,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    getVideoFromPhotoAlbum();
                }
                break;
            case R.id.edit_button_divider:
                break;
        }
    }

    private void getVideoFromPhotoAlbum() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, CODE_VIDEO);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PermissionUtils.REQUEST_IMAGE){
            String[] permissionNotAgrees = ArrayUtils.getPermissionNotAgrees(permissions, grantResults);
            if(permissionNotAgrees == null || permissionNotAgrees.length ==0){
                getBitMapFromPhotoAlbum();
            }
        }else if(requestCode == PermissionUtils.REQUEST_VIDEO){
            String[] permissionNotAgrees = ArrayUtils.getPermissionNotAgrees(permissions, grantResults);
            if(permissionNotAgrees == null || permissionNotAgrees.length ==0){
                getVideoFromPhotoAlbum();
            }
        }else if(requestCode == PermissionUtils.REQUEST_WRITE){
            uploadAnswer();
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
                                String url = serverLocation + "/Upload/Image/"+user.getUserId();
                                RequestUtils.sendSingleFile(file, url, "image", new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(()->Toast.makeText(EditAnswerActivity.this,"上传失败",Toast.LENGTH_SHORT).show());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                                        runOnUiThread(()->{
                                            if(simpleDto.isSuccess()){
                                                String url = serverLocation + "/res/Image/" + simpleDto.getMsg();
                                                editText.insertImage(url);
                                            }else{
                                                Toast.makeText(EditAnswerActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
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
                case CODE_VIDEO:
                    try {
                        Toast.makeText(this,"正在上传视频",Toast.LENGTH_SHORT).show();
                        Uri uri = data.getData();
                        String filePath = FileUtils.getRealPathFromURI(this, uri);
                        File file = new File(filePath);
                        if(file.exists()){
                            if(FileUtils.checkOutFileSize(file,20971520)){
                                String url = serverLocation + "/Upload/Video/" + user.getUserId();
                                RequestUtils.sendSingleFile(file, url, "video", new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(()->Toast.makeText(EditAnswerActivity.this,"上传失败",Toast.LENGTH_SHORT).show());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                                        runOnUiThread(()->{
                                            if(simpleDto.isSuccess()){
                                                String url = serverLocation + "/res/Video/" + simpleDto.getMsg();
                                                editText.insertVideo(url);
                                            }else{
                                                Toast.makeText(EditAnswerActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }else{
                                Toast.makeText(this,"文件大小不能超过20MB",Toast.LENGTH_SHORT).show();
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void uploadAnswer(){
        String html = editText.getHtml();
        Integer contentType = HtmlUtils.getContentType(html);
        if(contentType == HtmlUtils.TYPE_ALL_TEXT){
            if(StringUtils.isEmpty(HtmlUtils.getContentFromHtml(html))){
                Toast.makeText(this,"回答不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        commit.setEnabled(false);
        String url = serverLocation + "/Answer";
        Map<String,String> map = new HashMap<>();
        map.put("questionId",String.valueOf(questionBean.getId()));
        map.put("userId",String.valueOf(user.getUserId()));
        map.put("contentType",String.valueOf(contentType));
        String contentFromHtml = HtmlUtils.getContentFromHtml(html);
        int length = contentFromHtml.length();
        map.put("content",contentFromHtml.substring(0,length <= 52 ? length : 52));
        map.put("thumbnail",getResUrl(contentType,html));
        File temporaryText = FileUtils.getTemporaryText(html);
        if(temporaryText.exists()){
            RequestUtils.sendFileWithParam(temporaryText, url, "answer", map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(()->{
                        Toast.makeText(EditAnswerActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                        commit.setEnabled(true);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                    runOnUiThread(()->{
                        if(simpleDto.isSuccess()){
                            Toast.makeText(EditAnswerActivity.this,"回答成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(EditAnswerActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                            commit.setEnabled(true);
                        }
                    });
                }
            });
        }else{
            Toast.makeText(this,"上传失败",Toast.LENGTH_SHORT).show();
        }
    }

    private String getResUrl(Integer contentType, String contentFromHtml) {
        String resUrl = "";
        if(contentType.equals(HtmlUtils.TYPE_ALL_TEXT)){
            resUrl = "";
        }else if(contentType.equals(HtmlUtils.TYPE_HAS_IMAGE)){
            String imgFromHtml = HtmlUtils.getImgFromHtml(contentFromHtml);
            resUrl = getProcessUrl(imgFromHtml);
        }else if(contentType.equals(HtmlUtils.TYPE_HAS_VIDEO)){
            String videoFromHtml = HtmlUtils.getVideoFromHtml(contentFromHtml);
            resUrl = getProcessUrl(videoFromHtml);
        }
        return resUrl;
    }

    private String getProcessUrl(String resUrl) {
        String [] sp = resUrl.split("/");
        return sp[(sp.length)-1];
    }

    private void getBitMapFromPhotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent,CODE_IMAGE);
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
