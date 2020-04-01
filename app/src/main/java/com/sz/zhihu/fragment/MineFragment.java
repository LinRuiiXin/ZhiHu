package com.sz.zhihu.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.sz.zhihu.R;
import com.sz.zhihu.fragment.mine.MineCardIsLoggedFragment;
import com.sz.zhihu.fragment.mine.MineCardNotLoggedFragment;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.utils.UserUtils;

public class MineFragment extends Fragment implements CustomFragmentFunction {
    private MineCardIsLoggedFragment mineCardIsLoggedFragment;
    private MineCardNotLoggedFragment mineCardNotLoggedFragment;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_mine,container,false);
            if(UserUtils.queryUserHistory() != null){
                getChildFragmentManager().beginTransaction().replace(R.id.mine_card,getLogFragment()).commit();
            }else{
                getChildFragmentManager().beginTransaction().replace(R.id.mine_card,getNotLogFragment()).commit();
            }
        }
        return view;
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

        Log.i("Main","mine refreshing...");
    }
}
