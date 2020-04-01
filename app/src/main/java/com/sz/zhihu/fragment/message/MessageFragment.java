package com.sz.zhihu.fragment.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sz.zhihu.LoginActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.UserUtils;

public class MessageFragment extends Fragment {
    private View viewLogged = null;
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

        User user = UserUtils.queryUserHistory();
        if(user == null){
            View view =  inflater.inflate(R.layout.fragment_msg_have_not_logged,container,false);
            TextView prompt = view.findViewById(R.id.fragment_msg_hnl_prompt);
            prompt.setText("和其他知友互动的消息，会显示到这里");
            view.findViewById(R.id.fragment_msg_hnl_button).setOnClickListener(v->{
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
            });
            return view;
        }else{
            if(viewLogged == null){
                viewLogged = inflater.inflate(R.layout.fragment_msg_message,container,false);
            }
            return viewLogged;
        }
    }
}
