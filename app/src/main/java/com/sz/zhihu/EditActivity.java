package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.span.ImageCentreSpan;
import com.sz.zhihu.utils.ArrayUtils;
import com.sz.zhihu.utils.PermissionUtils;
import com.sz.zhihu.utils.SystemUtils;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView question;
    private Button commit;
    private Button bold;
    private Button italic;
    private Button title;
    private Button reference;
    private Button photo;
    private Button video;
    private Button divider;
    private EditText editText;
    private int STATE_NOW = 1;
    private final int STATE_DEFAULT = 1;
    private final int STATE_BOLD = 2;
    private final int STATE_ITALIC = 3;
    private final int STATE_TITLE = 4;
    private final int STATE_REFERENCE = 5;
    private int index = -1;
    private final int CODE_IMAGE = 6;
    private final int CODE_VIDEO = 7;
    private int imagesCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    private void init() {
        question = findViewById(R.id.edit_question);
        commit = findViewById(R.id.edit_button_commit);
        editText = findViewById(R.id.edit_edit_text);
        bold = findViewById(R.id.edit_button_bold);
        italic = findViewById(R.id.edit_button_italic);
        title = findViewById(R.id.edit_button_title);
        reference = findViewById(R.id.edit_button_reference);
        photo = findViewById(R.id.edit_button_photo);
        video = findViewById(R.id.edit_button_video);
        divider = findViewById(R.id.edit_button_divider);
        editText.setMovementMethod(LinkMovementMethod.getInstance());
        setListener();
    }

    private void setListener() {
        editText.addTextChangedListener(textChangeListener());
        commit.setOnClickListener(this);
        bold.setOnClickListener(this);
        italic.setOnClickListener(this);
        title.setOnClickListener(this);
        reference.setOnClickListener(this);
        photo.setOnClickListener(this);
        video.setOnClickListener(this);
        divider.setOnClickListener(this);
    }

    private TextWatcher textChangeListener() {
        return new CustomEditTextListener(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                index = editText.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(STATE_NOW != STATE_DEFAULT && count>=1){
                    switch (STATE_NOW){
                        case STATE_BOLD:
                            setSpans(new StyleSpan(Typeface.BOLD),count);
                            break;
                        case STATE_ITALIC:
                            setSpans(new StyleSpan(Typeface.ITALIC),count);
                            break;
                        case STATE_TITLE:
                            break;
                        case STATE_REFERENCE:
                            break;
                    }
                }
            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_button_commit:
                break;
            case R.id.edit_button_bold:
                setState(STATE_BOLD);
                break;
            case R.id.edit_button_italic:
                setState(STATE_ITALIC);
                break;
            case R.id.edit_button_title:
                setState(STATE_TITLE);
                break;
            case R.id.edit_button_reference:
                setState(STATE_REFERENCE);
                break;
            case R.id.edit_button_photo:
                index = editText.getSelectionStart();
                //如果权限已通过
                if(PermissionUtils.registerPerMission(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    getBitMapFromPhotoAlbum();
                }
                break;
            case R.id.edit_button_video:
                break;
            case R.id.edit_button_divider:
                break;
        }
    }

    public void setSpans(Object span,int count){
        CharSequence charSequence = editText.getText().subSequence(index, index+count);
        Editable editableText = editText.getEditableText();
        editableText.setSpan(span,index,index+count,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    public void setImageSpan(Object span){
        editText.append("\n");
        int selectionStart = editText.getSelectionStart();
        String location = getString(R.string.server_location)+"/answer/id/image/"+imagesCount;
        String labelAndLocation = "<img src='"+location+"'/>";
        SpannableStringBuilder builder = new SpannableStringBuilder(labelAndLocation);
        builder.setSpan(span,0,labelAndLocation.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
            }
        },0,labelAndLocation.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.append(builder);
        editText.append("\n\n");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
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
                        Uri uri = data.getData();
                        Bitmap oldBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        Bitmap newBitmap = zoomImg(oldBitmap, 400,400);
                        ImageSpan imageSpan = new ImageCentreSpan(this, newBitmap);
                        ++imagesCount;
                        //异步提交图片至服务器(还没写)
                        setImageSpan(imageSpan);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case CODE_VIDEO:
                    try {
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    private void getBitMapFromPhotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent,CODE_IMAGE);
    }
    public boolean setState(int state){
        if(STATE_NOW == state){
            STATE_NOW = STATE_DEFAULT;
            setButtonBackground(state,R.color.black);
            return false;
        }else{
            STATE_NOW = state;
            setButtonBackground(state,R.color.blue);
            return true;
        }
    }
    public void setButtonBackground(int CODE,int color){
        switch (CODE){
            case STATE_BOLD:
                bold.setTextColor(color);
                break;
            case STATE_ITALIC:
                italic.setTextColor(color);
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
