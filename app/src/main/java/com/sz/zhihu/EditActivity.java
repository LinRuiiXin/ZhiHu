package com.sz.zhihu;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.QuoteSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.utils.SystemUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

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
//    private StyleSpan boldSpan;
//    private StyleSpan italicSpan;

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
//        boldSpan = new StyleSpan(Typeface.BOLD);
//        italicSpan = new StyleSpan(Typeface.ITALIC);
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
                break;
            case R.id.edit_button_video:
                break;
            case R.id.edit_button_divider:
                break;
        }
    }
    public void setSpans(Object spans,int count){
        CharSequence charSequence = editText.getText().subSequence(index, index+count);
        Editable editableText = editText.getEditableText();
        editableText.setSpan(spans,index,index+count,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    public boolean setState(int state){
        if(STATE_NOW == state){
            STATE_NOW = STATE_DEFAULT;
            return false;
        }else{
            STATE_NOW = state;
            return true;
        }
    }
}
