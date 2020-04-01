package com.sz.zhihu.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sz.zhihu.AskQuestionActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.RecommendQuestionActivity;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.UserUtils;

public class AddFragment extends Fragment{

    private View view;
    private Activity activity;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            activity = (Activity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_add,container,false);
            View.OnClickListener listenerLogged = onClickListenerLogged();
            RelativeLayout addQuestion = view.findViewById(R.id.add_et_question);
            RelativeLayout answerQuestion = view.findViewById(R.id.add_answer_question);
            RelativeLayout addVideo = view.findViewById(R.id.add_add_video);
            RelativeLayout writeArticle = view.findViewById(R.id.add_write_article);
            addQuestion.setOnClickListener(listenerLogged);
            answerQuestion.setOnClickListener(listenerLogged);
            addVideo.setOnClickListener(listenerLogged);
            writeArticle.setOnClickListener(listenerLogged);
        }
        return view;
    }

    private View.OnClickListener onClickListenerLogged() {
        return v -> {
            if(UserUtils.checkIsLogged(activity)){
                Intent intent;
                switch (v.getId()){
                    case R.id.add_et_question:
                        intent = new Intent(activity,AskQuestionActivity.class);
                        activity.startActivity(intent);
                        break;
                    case R.id.add_answer_question:
                        intent = new Intent(activity, RecommendQuestionActivity.class);
                        activity.startActivity(intent);
                        break;
                    case R.id.add_add_video:
                        break;
                    case R.id.add_write_article:
                        break;
                }
            }
        };
    }
}
