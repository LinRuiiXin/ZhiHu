package com.sz.zhihu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.fragment.AddFragment;
import com.sz.zhihu.fragment.IndexFragment;
import com.sz.zhihu.fragment.MineFragment;
import com.sz.zhihu.fragment.MsgFragment;
import com.sz.zhihu.fragment.VipFragment;
import com.sz.zhihu.utils.SystemUtils;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomTabs;
    private IndexFragment indexFragment;
    private VipFragment vipFragment;
    private AddFragment addFragment;
    private MsgFragment msgFragment;
    private MineFragment mineFragment;
    private FragmentManager supportFragmentManager;
    private int status = 0;
    private final int STATUS_INDEX = 1;
    private final int STATUS_VIP = 2;
    private final int STATUS_ADD = 3;
    private final int STATUS_MSG = 4;
    private final int STATUS_MINE = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        SystemUtils.setStatusBarFullTransparent(this);
        initFragment();
        setDefaultFragment(indexFragment);
        bottomTabs = findViewById(R.id.bottom_tabs);
        bottomTabs.setOnNavigationItemSelectedListener((menuItem)->{
            switch (menuItem.getItemId()){
                case R.id.tab_index:
                    fragmentJump(indexFragment,STATUS_INDEX);
                    break;
                case R.id.tab_vip:
                    fragmentJump(vipFragment,STATUS_VIP);
                    break;
                case R.id.tab_add:
                    fragmentJump(addFragment,STATUS_ADD);
                    break;
                case R.id.tab_msg:
                    fragmentJump(msgFragment,STATUS_MSG);
                    break;
                case R.id.tab_mine:
                    fragmentJump(mineFragment,STATUS_MINE);
                    break;
            }
            return true;
        });
    }


    private void initFragment() {
        indexFragment = new IndexFragment(this);
        vipFragment = new VipFragment();
        addFragment = new AddFragment();
        msgFragment = new MsgFragment();
        mineFragment = new MineFragment();
    }
    private void setDefaultFragment(Fragment fragment) {
        fragmentJump(fragment,STATUS_INDEX);
    }

    private void fragmentJump(Fragment fragment,int status) {
        if(supportFragmentManager == null){
            supportFragmentManager = getSupportFragmentManager();
        }
        if(status != this.status){
            this.status = status;
            supportFragmentManager.beginTransaction().replace(R.id.fragment_main_activity,fragment).commit();
        }else{
            if(fragment instanceof CustomFragmentFunction){
                CustomFragmentFunction customFragmentFunction = (CustomFragmentFunction) fragment;
                customFragmentFunction.refreshPage();
            }
        }
    }
}
