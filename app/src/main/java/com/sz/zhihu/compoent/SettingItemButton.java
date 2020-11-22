package com.sz.zhihu.compoent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sz.zhihu.R;

public class SettingItemButton extends RelativeLayout {

    private View view;
    private TextView content;
    private RelativeLayout relativeLayout;

    public SettingItemButton(Context context) {
        this(context,null);
    }

    public SettingItemButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingItemButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.setting_item_button, this, true);
        content = view.findViewById(R.id.sib_content);
        relativeLayout = view.findViewById(R.id.sib_relative_layout);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemButton);
        setText(typedArray.getString(R.styleable.SettingItemButton_text));
        typedArray.recycle();
    }

    public void setText(String text){
        content.setText(text);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        relativeLayout.setOnClickListener(l);
    }
}
