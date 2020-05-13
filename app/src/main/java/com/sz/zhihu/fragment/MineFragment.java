package com.sz.zhihu.fragment;

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

import com.sz.zhihu.R;
import com.sz.zhihu.SettingsActivity;
import com.sz.zhihu.fragment.mine.MineCardIsLoggedFragment;
import com.sz.zhihu.fragment.mine.MineCardNotLoggedFragment;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.utils.DBUtils;

public class MineFragment extends Fragment implements CustomFragmentFunction {
    private MineCardIsLoggedFragment mineCardIsLoggedFragment;
    private MineCardNotLoggedFragment mineCardNotLoggedFragment;
    private View view;
    private TextView settings;
    private Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        if(context instanceof Activity){
            this.activity = (Activity) context;
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_mine,container,false);
            if(DBUtils.queryUserHistory() != null){
                getChildFragmentManager().beginTransaction().replace(R.id.mine_card,getLogFragment()).commit();
            }else{
                getChildFragmentManager().beginTransaction().replace(R.id.mine_card,getNotLogFragment()).commit();
            }
            settings = view.findViewById(R.id.mine_setting);
            setListener();
        }
        return view;
    }

    private void setListener() {
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(activity, SettingsActivity.class);
            activity.startActivity(intent);
        });
    }

    private Fragment getLogFragment() {
        if(mineCardIsLoggedFragment == null){
            mineCardIsLoggedFragment = new MineCardIsLoggedFragment();
        }
        return mineCardIsLoggedFragment;
    }

    private Fragment getNotLogFragment() {
        if(mineCardNotLoggedFragment == null){
            mineCardNotLoggedFragment = new MineCardNotLoggedFragment();
        }
        return mineCardNotLoggedFragment;
    }

    @Override
    public void refreshPage() {

    }
}
