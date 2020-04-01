package com.sz.zhihu.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.sz.zhihu.MainActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.fragment.message.AdviceFragment;
import com.sz.zhihu.fragment.message.MessageFragment;
import com.sz.zhihu.interfaces.CustomFragmentFunction;

public class MsgFragment extends Fragment implements CustomFragmentFunction {

    private View view;
    private MainActivity activity;
    private PagerSlidingTabStrip strip;
    private ViewPager viewPager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MainActivity){
            activity = (MainActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_msg,container,false);
            viewPager = view.findViewById(R.id.msg_viewPager);
            viewPager.setAdapter(new FragmentPagerAdapter(activity.getSupportFragmentManager()) {
                @Nullable
                @Override
                public CharSequence getPageTitle(int position) {
                    switch (position){
                        case 0:
                            return "通知";
                        case 1:
                            return "消息";
                    }
                    return null;
                }

                @NonNull
                @Override
                public Fragment getItem(int position) {
                    switch (position){
                        case 0:
                            return new AdviceFragment();
                        case 1:
                            return new MessageFragment();
                    }
                    return null;
                }

                @Override
                public int getCount() {
                    return 2;
                }
            });
            strip = view.findViewById(R.id.msg_pagerTabStrip);
            strip.setShouldExpand(true);
            strip.setViewPager(viewPager);
            strip.setDividerColor(Color.TRANSPARENT); //设置每个标签之间的间隔线颜色 ->透明
            strip.setUnderlineHeight(3); //设置标签栏下边的间隔线高度，单位像素
            strip.setIndicatorHeight(6); //设置Indicator 游标 高度，单位像素
        }
        return view;
    }

    @Override
    public void refreshPage() {
        Log.i("Main","msg refreshing...");
    }
}
