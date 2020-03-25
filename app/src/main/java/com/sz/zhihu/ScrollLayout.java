package com.sz.zhihu;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ScrollLayout extends LinearLayout {

    /**
     * true：开始下拽
     */
    private boolean mIsBeingDragged;

    /**
     * 初始坐标
     */
    private float mInitialY, mInitialX;
    /**
     * 头部高度
     */
    private int mHeaderHeight = 0;

    /**
     * 头部宽度
     */
    private int mHeaderWidth = 0;

    /**
     * 头部
     */
    private View mHeaderView;
    /**
     * 继承自带方法，不用理
     */
    public ScrollLayout(Context context) {
        super(context);
    }

    public ScrollLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScrollLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialX = ev.getX();
                mInitialY = ev.getY();
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float diffY = ev.getY() - mInitialY;
                float diffX = ev.getX() - mInitialX;
                if (diffY > 0 && diffY / Math.abs(diffX) > 2) {
                    mIsBeingDragged = true;
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mIsBeingDragged) {
                    //得到下拉的距离
                    float diffY = ev.getY() - mInitialY;
                    changeHeader((int) diffY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    //重置头部
                    resetHeader();
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
    /**
     * 设置头部
     */
    public ScrollLayout setHeader(View header) {
        mHeaderView = header;
        mHeaderView.post(new Runnable() {
            @Override
            public void run() {
                mHeaderHeight = mHeaderView.getHeight();
                mHeaderWidth = mHeaderView.getWidth();
            }
        });
        return this;
    }
    /**
     * 改变头部大小
     */
    private void changeHeader(int offsetY) {
        int pullOffset = (int) Math.pow(offsetY, 0.8);
        int newHeight = pullOffset + mHeaderHeight;
        int newWidth = (int) ((((float) newHeight / mHeaderHeight)) * mHeaderWidth);
        mHeaderView.getLayoutParams().height = newHeight;
        mHeaderView.getLayoutParams().width = newWidth;
//        int margin = (newWidth - mHeaderWidth) / 2;
//        mHeaderView.setTranslationX(-margin);
        mHeaderView.requestLayout();
    }
    /**
     * 重置头部
     */
    private void resetHeader() {
        mHeaderView.getLayoutParams().height = mHeaderHeight;
        mHeaderView.getLayoutParams().width = mHeaderWidth;
        mHeaderView.setTranslationX(0);
        mHeaderView.requestLayout();
    }
}
