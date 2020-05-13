package com.sz.zhihu.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.sz.zhihu.AskQuestionActivity;
import com.sz.zhihu.EditArticleActivity;
import com.sz.zhihu.EditVideoActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.RecommendQuestionActivity;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DBUtils;

import java.util.Date;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
            TextView days = view.findViewById(R.id.add_days);
            days.setText(getDays());
            addQuestion.setOnClickListener(listenerLogged);
            answerQuestion.setOnClickListener(listenerLogged);
            addVideo.setOnClickListener(listenerLogged);
            writeArticle.setOnClickListener(listenerLogged);
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getDays() {
        User user = DBUtils.queryUserHistory();
        if(user != null){
            Date registerDate = user.getRegisterDate();
            long time = registerDate.getTime();
            long time1 = new Date().getTime();
            long bet = time1 - time;
            long days = bet/86400000;
            return "今天是你加入ZhiHu的第"+(days+1)+"天";
        }else return "您还未登录";
    }

    private View.OnClickListener onClickListenerLogged() {
        return v -> {
            if(DBUtils.checkIsLogged(activity)){
                Intent intent = null;
                switch (v.getId()){
                    case R.id.add_et_question:
                        intent = new Intent(activity,AskQuestionActivity.class);
                        break;
                    case R.id.add_answer_question:
                        intent = new Intent(activity, RecommendQuestionActivity.class);
                        break;
                    case R.id.add_add_video:
                        intent = new Intent(activity, EditVideoActivity.class);
                        break;
                    case R.id.add_write_article:
                        intent = new Intent(activity, EditArticleActivity.class);
                        break;
                }
                activity.startActivity(intent);
//                activity.overridePendingTransition(R.anim.amin_in,0);

            }
        };
    }
}
