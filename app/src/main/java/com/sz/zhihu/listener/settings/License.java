package com.sz.zhihu.listener.settings;

import android.app.Activity;
import android.view.View;

import com.sz.zhihu.utils.DiaLogUtils;

public class License extends AbstractCustomListener {
    private Activity activity;
    public License(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        DiaLogUtils.showLicenses(activity);
    }
}
