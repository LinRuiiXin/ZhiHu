package com.sz.zhihu.view;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.sz.zhihu.LoginActivity;
import com.sz.zhihu.R;

public class PromptLoginView {
    private static View promptView;
    public static View getPromptView(Activity activity){
        if(promptView == null){
            promptView = View.inflate(activity,R.layout.dialog_prompt_login,null);
            Button login = promptView.findViewById(R.id.dpl_login);
            login.setOnClickListener(v->{
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
            });
        }
        return promptView;
    }
}
