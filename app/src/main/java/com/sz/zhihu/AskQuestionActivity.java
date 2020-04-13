package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rex.editor.view.RichEditorNew;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.Classify;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.ArrayUtils;
import com.sz.zhihu.utils.DiaLogUtils;
import com.sz.zhihu.utils.FileUtils;
import com.sz.zhihu.utils.HtmlUtils;
import com.sz.zhihu.utils.PermissionUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.utils.UserUtils;
import com.sz.zhihu.view.ChoiceTypeView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class AskQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView back;
    private TextView commit;
    private EditText question;
    private RichEditorNew questionDescribe;
    private Button bold;
    private Button italic;
    private Button title;
    private Button order;
    private Button photo;
    private Button video;
    private final int CODE_IMAGE = 1;
    private final int CODE_VIDEO = 2;
    private String serverLocation;
    private User user;
    private Gson gson;
    public static List<Classify> types = new ArrayList<>();
    private TextView choiceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    private void init() {
        back = findViewById(R.id.aq_back);
        choiceType = findViewById(R.id.aq_choice_type);
        commit = findViewById(R.id.aq_commit);
        question = findViewById(R.id.aq_question);
        questionDescribe = findViewById(R.id.aq_question_describe);
        bold = findViewById(R.id.aq_button_bold);
        italic = findViewById(R.id.aq_button_italic);
        title = findViewById(R.id.aq_button_title);
        order = findViewById(R.id.aq_button_order);
        photo = findViewById(R.id.aq_button_photo);
        video = findViewById(R.id.aq_button_video);
        questionDescribe.setPlaceholder("对问题补充说明，可以更快获得解答（选填）");
        serverLocation = getResources().getString(R.string.server_location);
        user = UserUtils.queryUserHistory();
        gson = new Gson();
        back.setOnClickListener(this);
        choiceType.setOnClickListener(this);
        italic.setOnClickListener(this);
        title.setOnClickListener(this);
        order.setOnClickListener(this);
        photo.setOnClickListener(this);
        video.setOnClickListener(this);
        commit.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.aq_back:
                finish();
                break;
            case R.id.aq_choice_type:
                DiaLogUtils.showChoiceTypeDialog(this);
                break;
            case R.id.aq_commit:
                String question = this.question.getText().toString();
                if(!StringUtils.isEmpty(question)){
                    if(!types.isEmpty()){
                        if(question.trim().length()>3){
                            commit.setEnabled(false);
                            String url = serverLocation + "/Question";
                            Map<String,String> map = new HashMap<>();
                            map.put("userId",String.valueOf(user.getUserId()));
                            map.put("questionName",question);
                            map.put("typeStr",getTypeStr());
                            String content = questionDescribe.getHtml();
                            File file = new File("");
                            if(!StringUtils.isEmpty(HtmlUtils.getImgFromHtml(content))||!StringUtils.isEmpty(HtmlUtils.getVideoFromHtml(content))||HtmlUtils.getContentFromHtml(content).trim().length()>0){
                                file = FileUtils.getTemporaryText(content);
                            }
                            RequestUtils.sendFileWithParam(file, url, "describe",map, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(()->{
                                        Toast.makeText(AskQuestionActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                                        commit.setEnabled(true);
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                                    runOnUiThread(()->{
                                        if(simpleDto.isSuccess()){
                                            Toast.makeText(AskQuestionActivity.this,"提问成功",Toast.LENGTH_SHORT).show();
                                            types.clear();
                                            ChoiceTypeView.refresh();
                                            finish();
                                        }else{
                                            Toast.makeText(AskQuestionActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                            commit.setEnabled(true);
                                        }
                                    });
                                }
                            });
                        }else{
                            Toast.makeText(this,"问题不少于3个字符",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        DiaLogUtils.showChoiceTypeDialog(this);
                    }
                }else{
                    Toast.makeText(this,"问题不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.aq_button_bold:
                questionDescribe.setBold();
                break;
            case R.id.aq_button_italic:
                questionDescribe.setItalic();
                break;
            case R.id.aq_button_title:
                questionDescribe.setHeading(3);
                break;
            case R.id.aq_button_order:
                questionDescribe.setNumbers();
                break;
            case R.id.aq_button_photo:
                //如果权限已通过
                if(PermissionUtils.registerPerMission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    getBitMapFromPhotoAlbum();
                }
                break;
            case R.id.aq_button_video:
                //如果权限已通过
                if(PermissionUtils.registerPerMission(this, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    getVideoFromPhotoAlbum();
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getTypeStr() {
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0;i < types.size();i++){
            stringBuffer.append(types.get(i).getId());
            if(i != (types.size()-1)){
                stringBuffer.append("-");
            }
        }
        return stringBuffer.toString();
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
                            String url = serverLocation + "/Upload/Image/" + user.getUserId();
                            Toast.makeText(this,"正在上传...",Toast.LENGTH_SHORT).show();
                            RequestUtils.sendSingleFile(file, url, "image", new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(()-> Toast.makeText(AskQuestionActivity.this,"上传失败",Toast.LENGTH_SHORT).show());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                                    runOnUiThread(()->{
                                        if(simpleDto.isSuccess()){
                                            String imageUrl = serverLocation + "/res/Image/"+simpleDto.getMsg();
                                            questionDescribe.insertImage(imageUrl);
                                        }else{
                                            Toast.makeText(AskQuestionActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
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
                            String url = serverLocation + "/Upload/Video/"+user.getUserId();
                            Toast.makeText(this,"正在上传...",Toast.LENGTH_SHORT).show();
                            RequestUtils.sendSingleFile(file, url, "video", new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(()->Toast.makeText(AskQuestionActivity.this,"上传失败",Toast.LENGTH_SHORT).show());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                                    runOnUiThread(()->{
                                        if(simpleDto.isSuccess()){
                                            String url = serverLocation + "/res/Video/" + simpleDto.getMsg();
                                            questionDescribe.insertVideo(url);
                                        }else{
                                            Toast.makeText(AskQuestionActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
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
