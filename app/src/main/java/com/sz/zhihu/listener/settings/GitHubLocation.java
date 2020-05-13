package com.sz.zhihu.listener.settings;

import android.app.Activity;
import android.view.View;

import com.sz.zhihu.utils.DiaLogUtils;

public class GitHubLocation extends AbstractCustomListener {
    private Activity activity;
    public GitHubLocation(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        DiaLogUtils.showGitHubLocation(activity);
    }
}
