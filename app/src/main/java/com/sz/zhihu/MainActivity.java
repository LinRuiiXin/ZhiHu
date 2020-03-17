package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.fragment.AddFragment;
import com.sz.zhihu.fragment.IndexFragment;
import com.sz.zhihu.fragment.MineFragment;
import com.sz.zhihu.fragment.MsgFragment;
import com.sz.zhihu.fragment.VipFragment;
import com.sz.zhihu.utils.ArrayUtils;
import com.sz.zhihu.utils.PermissionUtils;
import com.sz.zhihu.utils.SystemUtils;
/*
* 主活动，控制应用五个模块的跳转
* */
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
        //设置状态栏透明
        SystemUtils.setStatusBarFullTransparent(this);
        //申请权限
        PermissionUtils.registerPerMission(this,Manifest.permission.INTERNET);
        //初始化碎片
        initFragment();
        //打开默认碎片---首页
        setDefaultFragment(indexFragment);
        bottomTabs = findViewById(R.id.bottom_tabs);
        //设置点击事件
        bottomTabs.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener());
    }

    /*
    * 初始化5个分类fragment
    * */
    private void initFragment() {
        indexFragment = new IndexFragment();
        vipFragment = new VipFragment();
        addFragment = new AddFragment();
        msgFragment = new MsgFragment();
        mineFragment = new MineFragment();
    }
    /*
     * 设置监听，点击不同按钮跳转到不同的fragment，并记录
     * */
    public BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener(){
        return (menuItem)->{
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
        };
    }
    /*
    * 默认显示首页
    * */
    private void setDefaultFragment(Fragment fragment) {
        fragmentJump(fragment,STATUS_INDEX);
    }
    /*
    * 传入要显示的fragment，并记录当前页面显示哪个fragment
    * */
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
    /*
    *权限回调
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String[] permissionNotAgrees = ArrayUtils.getPermissionNotAgrees(permissions, grantResults);
        if(requestCode == 1 && permissionNotAgrees.length == 0){

        }else{
            for(String permissionNotAgree : permissionNotAgrees){
                Toast.makeText(this,"权限:"+permissionNotAgree+"未授权",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
