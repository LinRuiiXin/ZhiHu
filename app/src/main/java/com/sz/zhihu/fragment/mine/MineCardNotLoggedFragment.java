package com.sz.zhihu.fragment.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sz.zhihu.LoginActivity;
import com.sz.zhihu.MainActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.interfaces.CustomFragmentFunction;

public class MineCardNotLoggedFragment extends Fragment implements CustomFragmentFunction {

    private View view;
    private Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_mine_card_is_not_logged, container, false);
            Button login = view.findViewById(R.id.mine_card_nl_login);
            login.setOnClickListener(onclickListener());
        }
        return view;
    }

    private View.OnClickListener onclickListener() {
        return (v)->{
          switch (v.getId()){
              case R.id.mine_card_nl_login:
                  /**
                   * 事故发生地：
                   *    fragment中直接调用startActivity()很有可能有异常:Fragment:not attached to Activity
                   *    应该调用父容器(MainActivity).startActivity();
                   */
                  if(context instanceof MainActivity){
                      Activity activity = (Activity) context;
                      Intent intent = new Intent(activity,LoginActivity.class);
                      activity.startActivityForResult(intent,1);
                  }
                  break;
          }
        };
    }

    @Override
    public void refreshPage() {

    }
}
