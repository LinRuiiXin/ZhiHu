package com.sz.zhihu.fragment.answer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.sz.zhihu.R;
import com.sz.zhihu.vo.AnswerVo;

public class AnswerFragment extends Fragment {
    private AppCompatActivity activity;
    private View view;
    private AnswerVo answerVo;
    public AnswerFragment(AnswerVo answerVo){
        this.answerVo = answerVo;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof AppCompatActivity){
            this.activity = (AppCompatActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_answer,container,false);
            Toolbar toolbar = view.findViewById(R.id.fa_tool_bar);
            activity.setSupportActionBar(toolbar);
            TextView content = view.findViewById(R.id.fa_content);
            content.setText(Html.fromHtml(answerVo.getAnswer().getContent()));
        }
        return view;
    }
}
