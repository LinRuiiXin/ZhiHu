package com.sz.zhihu.listener.settings;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.sz.zhihu.AccountSettingsActivity;
import com.sz.zhihu.utils.DBUtils;

public class Account extends AbstractCustomListener {
    private Activity activity;
    public Account(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        if(DBUtils.checkIsLogged(activity)){
            Intent intent = new Intent(activity, AccountSettingsActivity.class);
            activity.startActivity(intent);
        }
    }
}
