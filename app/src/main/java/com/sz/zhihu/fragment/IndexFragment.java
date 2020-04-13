package com.sz.zhihu.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.sz.zhihu.MainActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.fragment.index.CareFragment;
import com.sz.zhihu.fragment.index.HotFragment;
import com.sz.zhihu.fragment.index.RecommendFragment;
/*
* 主页
* */
public class IndexFragment extends Fragment implements CustomFragmentFunction {
    View indexView = null;
    Activity activity;
    private View view = null;
    private PagerSlidingTabStrip strip;
    private ViewPager viewPager;
    private CareFragment careFragment;
    private RecommendFragment recommendFragment;
    private HotFragment hotFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            this.activity = (Activity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_index, container, false);
            viewPager = view.findViewById(R.id.index_viewPager);
            careFragment = new CareFragment();
            recommendFragment = new RecommendFragment(activity);
            hotFragment = new HotFragment();
            viewPager.setAdapter(new FragmentPagerAdapter(((MainActivity) activity).getSupportFragmentManager()) {
                @Nullable
                @Override
                public CharSequence getPageTitle(int position) {
                    switch (position){
                        case 0:
                            return "关注";
                        case 1:
                            return "推荐";
                        case 2:
                            return "热榜";
                    }
                    return "Title"+(position+1);
                }

                @NonNull
                @Override
                public Fragment getItem(int position) {
                    switch (position) {
                        case 0:
                            return careFragment;
                        case 1:
                            return recommendFragment;
                        case 2:
                            return hotFragment;
                    }
                    return null;
                }

                @Override
                public int getCount() {
                    return 3;
                }

            });
            viewPager.setCurrentItem(1,false);//默认打开首页---推荐
            strip = view.findViewById(R.id.index_pagerTabStrip);
            strip.setShouldExpand(true);
            strip.setViewPager(viewPager);
            strip.setDividerColor(Color.TRANSPARENT); //设置每个标签之间的间隔线颜色 ->透明
            strip.setUnderlineHeight(3); //设置标签栏下边的间隔线高度，单位像素
            strip.setIndicatorHeight(6); //设置Indicator 游标 高度，单位像素

        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void refreshPage() {
        switch (viewPager.getCurrentItem()){
            case 0:
                careFragment.refreshPage();
                break;
            case 1:
                recommendFragment.refreshPage();
                break;
            case 2:
                hotFragment.refreshPage();
                break;
        }
    }
}
