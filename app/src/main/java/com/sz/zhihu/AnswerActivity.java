package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sz.zhihu.adapter.AnswerFragmentAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.fragment.comment.AnswerCommentLevelOneFragment;
import com.sz.zhihu.fragment.answer.AnswerFragment;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.view.AnswerCommentDialogView;
import com.sz.zhihu.vo.AnswerCommentLevelOneVo;
import com.sz.zhihu.vo.AnswerVo;
import com.sz.zhihu.vo.RecommendViewBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sz.zhihu.R.id.answer_tool_bar;

public class AnswerActivity extends AbstractCustomActivity {

    private ViewPager viewPager;
    private List<Fragment> fragments;
    private String serverLocation;
    private RecommendViewBean viewBean;
    private Toolbar toolbar;
    private User user;
    private Gson gson;
    private AnswerFragmentAdapter adapter;
    private Button support;
    private CollapsingToolbarLayout toolbarLayout;
    private LinearLayout collect;
    private LinearLayout comment;
    private View dialogView;
    private BottomSheetDialog dialog;
    private BottomSheetBehavior<View> behavior;
    private EditText editText;
    private boolean isCommentLoading = false;
    private FragmentManager supportFragmentManager;
    private AnswerCommentLevelOneFragment levelOneFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    private void init() {
        toolbarLayout = findViewById(R.id.aa_tool_bar_layout);
        toolbar = findViewById(answer_tool_bar);
        viewPager = findViewById(R.id.answer_view_pager);
        support = findViewById(R.id.cb_support);
        collect = findViewById(R.id.cb_collect);
        comment = findViewById(R.id.cb_comment);
        viewBean = (RecommendViewBean) getIntent().getSerializableExtra("viewBean");
        serverLocation = getResources().getString(R.string.server_location);
        fragments = new ArrayList<>();
        gson = new GsonBuilder().disableHtmlEscaping().create();
        this.user = DBUtils.queryUserHistory();
        supportFragmentManager = getSupportFragmentManager();
        initCommentDialog();
        recordBrowse();
        initToolBar();
        initViewPager();
        comment.setOnClickListener(v->{
            dialog.show();
            if(!isCommentLoading){
                levelOneFragment = new AnswerCommentLevelOneFragment(serverLocation,getAnswerId(), user.getUserId(),()->{

                });
                supportFragmentManager.beginTransaction().replace(R.id.acd_main,levelOneFragment).commit();
                isCommentLoading = true;
            }
        });
    }


    /*
     * 初始化评论提示框
     * */
    private void initCommentDialog() {
        dialogView = AnswerCommentDialogView.getView(this);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        behavior = BottomSheetBehavior.from((View) dialogView.getParent());
        behavior.setPeekHeight(getPeekHeight());
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.dismiss();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        initDialogView();
    }

    /*
    * 初始化“评论”提示框View
    * */
    private void initDialogView() {
        TextView close = dialogView.findViewById(R.id.acd_close);
        editText = dialogView.findViewById(R.id.acd_write_comment);
//        swipeRefreshLayout = dialogView.findViewById(R.id.acd_swipeRefreshLayout);
//        swipeRefreshLayout.setEnabled(false);
        close.setOnClickListener(v->dialog.dismiss());
    }

    private void initToolBar() {
        toolbar.setTitle(viewBean.getTitle());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v-> finish());
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void initViewPager(){
        adapter = new AnswerFragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if(position == (fragments.size()-1)){
                    getNextAnswer();
                }
                updateUI(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        getInitData();
    }

    private void getInitData() {
        String url = "";
        if(user == null){
            url = serverLocation + "/Answer/Page/-1/" + viewBean.getQuestionId() + "/" + viewBean.getContentId();
        }else{
            url = serverLocation + "/Answer/Page/"+ user.getUserId() +"/" + viewBean.getQuestionId() + "/" + viewBean.getContentId();
        }
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()-> Toast.makeText(AnswerActivity.this,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        String json = gson.toJson(simpleDto.getObject());
                        List list = gson.fromJson(json,List.class);
                        list.forEach(o->{
                            String j = gson.toJson(o);
                            AnswerVo answerVo = gson.fromJson(j, AnswerVo.class);
                            fragments.add(new AnswerFragment(answerVo));
                        });
                        adapter.notifyDataSetChanged();
                        updateUI(0);
                    }else{
                        Toast.makeText(AnswerActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*
    * 获取到一级评论数据后回调此方法
    * */
    private void updateCommentLvOne(List<AnswerCommentLevelOneVo> res) {
        if(!isCommentLoading){

        }
    }

    private Long getAnswerId() {
        int currentItem = viewPager.getCurrentItem();
        AnswerFragment fragment = (AnswerFragment) fragments.get(currentItem);
        return fragment.getAnswerVo().getAnswer().getId();
    }

    /*
    * 记录此次浏览记录
    * */
    private void recordBrowse() {
        if(user != null){
            String url = serverLocation + "/Question/Browse/" + viewBean.getQuestionId() + "/" + user.getUserId();
            RequestUtils.sendSimpleRequest(url);
        }
    }


    /*
    * 翻页后会回调这个方法，在这里更新底部赞同、收藏、评论数据
    * */
    private void updateUI(int index) {
        AnswerVo currentVo = ((AnswerFragment) fragments.get(index)).getAnswerVo();
        support.setText(currentVo.getAnswer().getSupportSum()+"赞同");
        editText.setText("");
    }

    private void getNextAnswer(){
        Long answerId = ((AnswerFragment)fragments.get(viewPager.getCurrentItem())).getAnswerVo().getAnswer().getId();
        String url = user == null ? serverLocation + "/Answer/Next/-1/" + viewBean.getQuestionId() + "/" + answerId : serverLocation + "/Answer/Next/"+ user.getUserId() +"/" + viewBean.getQuestionId() + "/" + answerId;
        getAnswer(url,answerVo -> {
           fragments.add(new AnswerFragment(answerVo));
           adapter.notifyDataSetChanged();
        });
    }

    private void getAnswer(String url,Consumer<AnswerVo> callBack){
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->Toast.makeText(AnswerActivity.this,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        String json = gson.toJson(simpleDto.getObject());
                        AnswerVo answerVo = gson.fromJson(json, AnswerVo.class);
                        callBack.accept(answerVo);
                    }else{
                        Toast.makeText(AnswerActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    protected int getPeekHeight() {
        return getResources().getDisplayMetrics().heightPixels;
        /*//设置弹窗高度为屏幕高度的3/4
        return peekHeight - (peekHeight / 4);*/
    }

    @Override
    protected void onDestroy() {
        ((ViewGroup)dialogView.getParent()).removeView(dialogView);
        super.onDestroy();
    }
}
