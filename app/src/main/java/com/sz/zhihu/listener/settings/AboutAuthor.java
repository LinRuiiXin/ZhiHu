package com.sz.zhihu.listener.settings;

import android.app.Activity;
import android.view.View;

import com.sz.zhihu.utils.DiaLogUtils;

public class AboutAuthor extends AbstractCustomListener {
    private Activity activity;
    public AboutAuthor(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        DiaLogUtils.showAboutAuthorDialog(activity);
    }
}
