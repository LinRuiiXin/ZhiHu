package com.sz.zhihu.fragment.answer;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rex.editor.view.RichEditorNew;
import com.sz.zhihu.R;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.vo.AnswerVo;

public class AnswerFragment extends Fragment {
    private AppCompatActivity activity;
    private View view;
    private AnswerVo answerVo = null;
    private RoundedImageView portrait;
    private TextView userName;
    private TextView profile;
    private Button attention;
    private RichEditorNew content;
    private String serverLocation;
    private User user;

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
    public AnswerVo getAnswerVo(){
        return answerVo;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_answer,container,false);
            serverLocation = activity.getResources().getString(R.string.server_location);
            content = view.findViewById(R.id.fa_content);
            portrait = view.findViewById(R.id.tbl_portrait);
            userName = view.findViewById(R.id.tbl_user_name);
            profile = view.findViewById(R.id.tbl_profile);
            attention = view.findViewById(R.id.tbl_attention);
            user = DBUtils.queryUserHistory();
            bindData();
        }
        return view;
    }

    private void bindData() {
        content.loadRichEditorCode(answerVo.getAnswer().getContent());
        content.setClickable(false );
        User user = answerVo.getUser();
        String resUrl = serverLocation + "/res/User/" + user.getId() + "/" + user.getPortraitFileName();
        Glide.with(activity).load(resUrl).into(portrait);
        userName.setText(user.getUserName());
        profile.setText(user.getProfile());
        if(this.user != null){
            if(this.user.getUserId() == answerVo.getAnswer().getUserId()){
                attention.setVisibility(View.GONE);
            }
        }
    }
}
