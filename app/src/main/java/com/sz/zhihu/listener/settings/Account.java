package com.sz.zhihu.listener.settings;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

public class Account extends AbstractCustomListener {
    private Activity activity;
    public Account(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {

    }
}
