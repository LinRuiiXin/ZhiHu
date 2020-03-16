package com.sz.zhihu.fragment.index;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sz.zhihu.R;
import com.sz.zhihu.interfaces.CustomFragmentFunction;

public class HotFragment extends Fragment implements CustomFragmentFunction {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_index_hot, container, false);
        }
        return view;
    }

    @Override
    public void refreshPage() {

    }
}
