package com.sz.zhihu.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sz.zhihu.R;
import com.sz.zhihu.interfaces.CustomFragmentFunction;

public class MsgFragment extends Fragment implements CustomFragmentFunction {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msg,container,false);
        return view;
    }

    @Override
    public void refreshPage() {
        Log.i("Main","msg refreshing...");
    }
}
