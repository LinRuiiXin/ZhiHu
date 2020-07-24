package com.sz.zhihu.view;

import android.app.Activity;
import android.view.View;

import com.sz.zhihu.R;

public class AnswerCommentDialogView {
    private static View view;
    public static View getView(Activity activity){
        if(view == null){
            view = View.inflate(activity, R.layout.answer_comment_dialog,null);
        }
        return view;
    }
}
