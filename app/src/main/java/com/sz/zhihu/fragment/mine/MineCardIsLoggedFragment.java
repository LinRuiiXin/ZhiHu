package com.sz.zhihu.fragment.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sz.zhihu.R;
import com.sz.zhihu.interfaces.CustomFragmentFunction;

public class MineCardIsLoggedFragment extends Fragment implements CustomFragmentFunction {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_mine_card_logged, container, false);
        }
        return view;
    }

    @Override
    public void refreshPage() {

    }
}
