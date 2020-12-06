package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rex.editor.view.RichEditorNew;
import com.sz.zhihu.compoent.RichEditor;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.ArrayUtils;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.FileUtils;
import com.sz.zhihu.utils.HtmlUtils;
import com.sz.zhihu.utils.PermissionUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditArticleActivity extends AbstractCustomActivity {

    private TextView close;
    private EditText title;
    private RichEditorNew content;
    private Button boldButton;
    private Button italicButton;
    private Button orderButton;
    private Button photoButton;
    private Button titleButton;
    private Button videoButton;
    private Button dividerButton;
    private final int CODE_IMAGE = 1;
    private final int CODE_VIDEO = 2;
    private String serverLocation;
    private User user;
    private Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        serverLocation = getString(R.string.server_location);
        user = DBUtils.queryUserHistory();
        gson = new Gson();
        initComponent();
        initRichEditor();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initComponent() {
        close = findViewById(R.id.aea_close);
        TextView submit = findViewById(R.id.aea_submit);
        title = findViewById(R.id.aea_title);
        content = findViewById(R.id.aea_content);
        boldButton = findViewById(R.id.aea_button_bold);
        italicButton = findViewById(R.id.aea_button_italic);
        orderButton = findViewById(R.id.aea_button_order);
        photoButton = findViewById(R.id.aea_button_photo);
        titleButton = findViewById(R.id.aea_button_title);
        videoButton = findViewById(R.id.aea_button_video);
        dividerButton = findViewById(R.id.aea_button_divider);
        close.setOnClickListener(v -> finish());
        submit.setOnClickListener(v -> submitArticle());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void submitArticle() {
        String html = content.getHtml();
        validate(html,()->{
            Map<String,String> params = new HashMap<>(5);
            parseHtml(params,html);
            params.put("title",title.getText().toString());
            params.put("userId",String.valueOf(user.getUserId()));
            String url = serverLocation + "/ArticleService/Article";
            RequestUtils.sendFileWithParam(FileUtils.getTemporaryText(html), url, "article", params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(()->Toast.makeText(EditArticleActivity.this,"请求失败",Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                    runOnUiThread(()->{
                        if(simpleDto.isSuccess()){
                            Toast.makeText(EditArticleActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(EditArticleActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }

    /*
    * 校验标题，内容是否不为空，校验通过后，回调runnable函数
    * */
    public void validate(String html,Runnable runnable){
        if(!StringUtils.isEmpty(title.getText().toString())){
            if(html != null && !StringUtils.isEmpty(html)){
                runnable.run();
            }else{
                Toast.makeText(this,"请输入内容",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"请输入标题",Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * 解析html并提取所需参数
    * */
    private void parseHtml(Map<String,String> map,String html) {
        Integer contentType = HtmlUtils.getContentType(html);
        String resource = "";
        if(contentType == HtmlUtils.TYPE_HAS_VIDEO)
            resource = HtmlUtils.getVideoFromHtml(html);
        else if (contentType == HtmlUtils.TYPE_HAS_IMAGE)
            resource = HtmlUtils.getImgFromHtml(html);
        if(contentType != HtmlUtils.TYPE_ALL_TEXT)
            resource = getResourceFileName(resource);
        String contentFromHtml = HtmlUtils.getContentFromHtml(html);
        contentFromHtml = contentFromHtml.length() > 50 ? contentFromHtml.substring(0,50) : contentFromHtml;
        map.put("contentType",String.valueOf(contentType));
        map.put("thumbnail",resource);
        map.put("content",contentFromHtml);
    }

    /*
    * 获取资源的文件名
    * 如:由 http://localhost:8080/res/1234.jpg 转化为 1234.jpg
    * */
    private String getResourceFileName(String resource) {
        String[] split = resource.split("/");
        return split[split.length-1];
    }

    private void initRichEditor() {
        content.setPlaceholder("请输入内容");
        boldButton.setOnClickListener(v->content.setBold());
        italicButton.setOnClickListener(v->content.setItalic());
        orderButton.setOnClickListener(v->content.setNumbers());
        titleButton.setOnClickListener(v->content.setHeading(3));
        dividerButton.setOnClickListener(v->{});
        photoButton.setOnClickListener(v -> {
            if(PermissionUtils.registerPerMission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                getBitMapFromPhotoAlbum();
            }
        });
        videoButton.setOnClickListener(v -> {
            //如果权限已通过
            if(PermissionUtils.registerPerMission(this, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                getVideoFromPhotoAlbum();
            }
        });

    }

    private void getBitMapFromPhotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent,CODE_IMAGE);

    }

    private void getVideoFromPhotoAlbum() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, CODE_VIDEO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            String[] permissionNotAgrees = ArrayUtils.getPermissionNotAgrees(permissions, grantResults);
            if(permissionNotAgrees == null || permissionNotAgrees.length ==0){
                getBitMapFromPhotoAlbum();
            }else{
                Toast.makeText(this,"通过权限才能使用此功能",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case CODE_IMAGE:
                    try {
                        Uri uri = data.getData();
                        Bitmap oldBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        Bitmap newBitmap = zoomImg(oldBitmap, 250,250);
                        File file = FileUtils.getFile(newBitmap);
                        if(FileUtils.checkOutFileSize(file,5242880)){
                            String url = serverLocation + "/UploadService/Upload/Image/" + user.getUserId();
                            Toast.makeText(this,"正在上传...",Toast.LENGTH_SHORT).show();
                            RequestUtils.sendSingleFile(file, url, "image", new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(()-> Toast.makeText(EditArticleActivity.this,"上传失败",Toast.LENGTH_SHORT).show());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                                    runOnUiThread(()->{
                                        if(simpleDto.isSuccess()){
                                            String imageUrl = serverLocation + "/res/Image/"+simpleDto.getMsg();
                                            content.insertImage(imageUrl);
                                        }else{
                                            Toast.makeText(EditArticleActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }else{
                            Toast.makeText(this,"图片大小不能超过5MB",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case CODE_VIDEO:
                    try {
                        Uri uri = data.getData();
                        File file = new File(FileUtils.getRealPathFromURI(this,uri));
                        if(FileUtils.checkOutFileSize(file,20971520)){
                            String url = serverLocation + "/UploadService/Upload/Video/"+user.getUserId();
                            Toast.makeText(this,"正在上传...",Toast.LENGTH_SHORT).show();
                            RequestUtils.sendSingleFile(file, url, "video", new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(()->Toast.makeText(EditArticleActivity.this,"上传失败",Toast.LENGTH_SHORT).show());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                                    runOnUiThread(()->{
                                        if(simpleDto.isSuccess()){
                                            String url = serverLocation + "/res/Video/" + simpleDto.getMsg();
                                            content.insertVideo(url);
                                        }else{
                                            Toast.makeText(EditArticleActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }else{
                            Toast.makeText(this,"视频大小不能超过20MB",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
