package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sz.zhihu.adapter.AnswerCommentLevelOneAdapter;
import com.sz.zhihu.adapter.AnswerCommentLevelTwoAdapter;
import com.sz.zhihu.adapter.AnswerFragmentAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.fragment.answer.AnswerFragment;
import com.sz.zhihu.holder.CommentHolder;
import com.sz.zhihu.po.AnswerCommentLevelOne;
import com.sz.zhihu.po.AnswerCommentLevelTwo;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DiaLogUtils;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.view.AnswerCommentDialogView;
import com.sz.zhihu.vo.AnswerCommentLevelOneVo;
import com.sz.zhihu.vo.AnswerCommentLevelTwoVo;
import com.sz.zhihu.vo.AnswerVo;
import com.sz.zhihu.vo.RecommendViewBean;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sz.zhihu.R.id.answer_tool_bar;
import static com.sz.zhihu.R.id.content;

public class AnswerActivity extends AbstractCustomActivity {
    public static final int TO_QUESTION_CODE = 3;
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
    private TextView editText;
    private boolean isCommentLoading = false;
    private CommentHolder commentHolder;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView level1;
    private RecyclerView level2;
    private List<AnswerCommentLevelOneVo> answerCommentLevelOneVos;
    private AnswerCommentLevelOneAdapter commentLevel1Adapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean inLevelOne = true;
    //    private AnswerCommentLevelTwoAdapter answerCommentLevelTwoAdapter;
    private int commentLevelTwoLimit = 0;
    //初始化二级评论
    List<AnswerCommentLevelTwoVo> commentLevelTwoVos = new ArrayList<>();
    AnswerCommentLevelTwoAdapter answerCommentLevelTwoAdapter;
    private View replyView;
    private BottomSheetDialog replyDialog;
    private EditText replyEditText;
    private AnswerCommentLevelOneVo lv1ToLv2;
    private TextView replyTitle;
    private TextView replyObjectName;
    private TextView submitReply;
    private View optionView;
    private BottomSheetDialog optionDialog;
    private RelativeLayout optionSupport;
    private RelativeLayout optionReply;
    private RelativeLayout optionDelete;
    private ImageView supportTriangle;

    public AnswerActivity(){
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        prepare();
        initOptionDialog();
        initReplyDialog();
        initCommentDialog();
        recordBrowse();
        initToolBar();
        initViewPager();
        comment.setOnClickListener(commentClickListener());
        support.setOnClickListener(supportListener());
        buttonControl(false);
    }



    /*
    * 绑定组件与初始化所需实例
    * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void prepare() {
        bindComponent();
        fragments = new ArrayList<>();
        gson = GsonUtils.getGson();
        this.user = DBUtils.queryUserHistory();
        commentHolder = new CommentHolder();
        linearLayoutManager = new LinearLayoutManager(AnswerActivity.this);
        viewBean = (RecommendViewBean) getIntent().getSerializableExtra("viewBean");
        serverLocation = getResources().getString(R.string.server_location);
        answerCommentLevelTwoAdapter = new AnswerCommentLevelTwoAdapter(this, commentLevelTwoVos, answerCommentLevelTwoVo -> showOptionDialog(answerCommentLevelTwoVo));
    }

    private void bindComponent() {
        toolbarLayout = findViewById(R.id.aa_tool_bar_layout);
        toolbar = findViewById(answer_tool_bar);
        viewPager = findViewById(R.id.answer_view_pager);
        support = findViewById(R.id.cb_support);
        collect = findViewById(R.id.cb_collect);
        comment = findViewById(R.id.cb_comment);
        supportTriangle = findViewById(R.id.cb_support_triangle);
    }

    /*
     * 初始化评论提示框
     * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initCommentDialog() {
        dialogView = AnswerCommentDialogView.getView(this);
        refreshLayout = dialogView.findViewById(R.id.acd_swipeRefreshLayout);
        refreshLayout.setEnabled(false);
        level1 = dialogView.findViewById(R.id.acd_recyclerview_level_one);
        level2 = dialogView.findViewById(R.id.acd_recyclerview_level_two);
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initDialogView() {
        TextView close = dialogView.findViewById(R.id.acd_close);
        editText = dialogView.findViewById(R.id.acd_write_comment);
        close.setOnClickListener(v -> {
            if (inLevelOne) {
                dialog.dismiss();
            } else {
                showLevelOne();
                close.setBackground(getResources().getDrawable(R.drawable.icon_close_activity));
                lv1ToLv2 = null;

            }
        });
        editText.setOnClickListener(v -> {
            if (user == null) {
                DiaLogUtils.showPromptLoginDialog(this);
            } else {
                User user = inLevelOne ? getCurrentAnswer().getUser() : lv1ToLv2.getUser();
                showReplyDialog(inLevelOne ? 1 : 2, inLevelOne ? getCurrentAnswerId() : lv1ToLv2.getCommentLevelOne().getId(), user);
            }
        });
    }


    //    初始化"菜单"对话框
    private void initOptionDialog() {
        optionView = View.inflate(this, R.layout.comment_option_dialog, null);
        optionDialog = new BottomSheetDialog(this);
        optionDialog.setContentView(optionView);
        optionDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        behavior = BottomSheetBehavior.from((View) optionView.getParent());
        behavior.setPeekHeight(getPeekHeight());
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    optionDialog.dismiss();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
        optionSupport = optionView.findViewById(R.id.cod_support);
        optionReply = optionView.findViewById(R.id.cod_reply);
        optionDelete = optionView.findViewById(R.id.cod_delete);

    }

    //  显示"菜单"对话框，并判断是否本人操作？显示"删除评论"选项，否则不显示
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showOptionDialog(AnswerCommentLevelOneVo answerCommentLevelOneVo) {
        if (user == null) {
            DiaLogUtils.showPromptLoginDialog(this);
            return;
        }
        boolean b = answerCommentLevelOneVo.getUser().getId() == user.getUserId();
        optionSupport.setOnClickListener(v -> {
            commentLevel1Adapter.supportOrUnSupportComment(answerCommentLevelOneVo,()->optionDialog.dismiss());
        });
        optionReply.setOnClickListener(v -> {
            optionDialog.dismiss();
            showReplyDialog(2, answerCommentLevelOneVo.getCommentLevelOne().getId(), answerCommentLevelOneVo.getUser());
        });
        if (b) {
            optionDelete.setBackground(getDrawable(R.drawable.shape_radius_right_delete));
            optionDelete.setOnClickListener(v -> {
                deleteCommentLevelOne(answerCommentLevelOneVo);
            });
        } else {
            optionDelete.setVisibility(View.GONE);
            optionReply.setBackground(getDrawable(R.drawable.shape_radius_right_reply));
        }
        optionDialog.show();
    }

    /*
    * 删除一级评论
    * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void deleteCommentLevelOne(AnswerCommentLevelOneVo answerCommentLevelOneVo) {
        optionDelete.setEnabled(false);
        String url = serverLocation + "/CommentService/AnswerComment/LevelOne";
        Map<String,String> params = new HashMap<>(1);
        params.put("commentId",String.valueOf(answerCommentLevelOneVo.getCommentLevelOne().getId()));
        RequestUtils.deleteWithParams(url, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()-> {
                    Toast.makeText(AnswerActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    optionDelete.setEnabled(true);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        answerCommentLevelOneVos.remove(answerCommentLevelOneVo);
                        commentLevel1Adapter.notifyDataSetChanged();
                        optionDialog.dismiss();
                        optionDelete.setEnabled(true);
                    }else{
                        Toast.makeText(AnswerActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                        optionDelete.setEnabled(true);
                    }
                });
            }
        });

    }

    //  显示"菜单"对话框，并判断是否本人操作？显示"删除评论"选项，否则不显示
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showOptionDialog(AnswerCommentLevelTwoVo answerCommentLevelTwoVo) {
        if (user == null) {
            DiaLogUtils.showPromptLoginDialog(this);
            return;
        }
        boolean b = answerCommentLevelTwoVo.getReplyUser().getId() == user.getUserId();
        optionSupport.setOnClickListener(v -> {
            answerCommentLevelTwoAdapter.supportOrUnSupportComment(answerCommentLevelTwoVo,()->optionDialog.dismiss());
        });
        optionReply.setOnClickListener(v -> {
            optionDialog.dismiss();
            showReplyDialog(3, answerCommentLevelTwoVo.getAnswerCommentLevelTwo().getLevelOneId(), answerCommentLevelTwoVo.getUserReplyTo());
        });
        if (b) {
            optionDelete.setBackground(getDrawable(R.drawable.shape_radius_right_delete));
            optionDelete.setOnClickListener(v -> {
                deleteCommentLevelTwo(answerCommentLevelTwoVo);
            });
        } else {
            optionDelete.setVisibility(View.GONE);
            optionReply.setBackground(getDrawable(R.drawable.shape_radius_right_reply));
        }
        optionDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void deleteCommentLevelTwo(AnswerCommentLevelTwoVo answerCommentLevelTwoVo) {
        String url = serverLocation + "/CommentService/AnswerComment/LevelTwo";
        Map<String,String> param = new HashMap<>(1);
        param.put("replyId",String.valueOf(answerCommentLevelTwoVo.getAnswerCommentLevelTwo().getId()));
        RequestUtils.deleteWithParams(url, param, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->{
                    Toast.makeText(AnswerActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        commentLevelTwoVos.remove(answerCommentLevelTwoVo);
                        answerCommentLevelTwoAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(AnswerActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //初始化"回复"对话框
    private void initReplyDialog() {
        replyView = View.inflate(this, R.layout.answer_comment_reply_dialog, null);
        replyDialog = new BottomSheetDialog(this);
        replyDialog.setContentView(replyView);
        replyDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        behavior = BottomSheetBehavior.from((View) replyView.getParent());
        behavior.setPeekHeight(getPeekHeight());
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    replyDialog.dismiss();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });

        replyEditText = replyView.findViewById(R.id.acrd_edit_text);
        replyTitle = replyView.findViewById(R.id.acrd_title);
        replyObjectName = replyView.findViewById(R.id.acrd_user_reply_to);
        submitReply = replyView.findViewById(R.id.acrd_submit);
    }

    /*
     * 显示"回复"对话框，并传递回复对象
     * @code: 1-针对回答回复 2-针对一级评论回复 3-针对二级评论回复
     * @answerIdOrCommentId: 如果针对回答回复，则传递回答的id，为一级评论
     * @replyObject: 被回复对象
     * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showReplyDialog(int code, Long answerIdOrCommentId, User replyObject) {
        replyDialog.show();
        replyEditText.setFocusable(true);
        replyEditText.setFocusableInTouchMode(true);
        replyEditText.requestFocus();

        replyObjectName.setText(replyObject.getUserName());
        if (code == 1) {
            replyTitle.setText("评论给 ");
            submitReply.setOnClickListener(v -> {
                String url = serverLocation + "/CommentService/AnswerComment/LevelOne";
                Map<String, String> map = new HashMap<>();
                map.put("answerId", String.valueOf(answerIdOrCommentId));
                map.put("userId", String.valueOf(user.getUserId()));
                map.put("content", replyEditText.getText().toString());
                RequestUtils.postWithParams(url, map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(AnswerActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                        runOnUiThread(() -> {
                            if (simpleDto.isSuccess()) {
                                Long newLevelOneId = gson.fromJson(gson.toJson(simpleDto.getObject()), Long.class);
                                AnswerCommentLevelOneVo oneVo = convertNewCommentToLv1Vo(newLevelOneId,replyEditText.getText().toString());
                                answerCommentLevelOneVos.add(oneVo);
                                commentLevel1Adapter.notifyDataSetChanged();
                                Toast.makeText(AnswerActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AnswerActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
                            }
                            replyEditText.setText("");
                            replyDialog.dismiss();
                        });
                    }
                });
            });
        } else {
            replyTitle.setText("回复给 ");
            submitReply.setOnClickListener(v -> {
                String content = replyEditText.getText().toString();
                if (StringUtils.isEmpty(content)) {
                    Toast.makeText(AnswerActivity.this,"评论不能为空",Toast.LENGTH_SHORT).show();
                } else {
                    String url = serverLocation + "/CommentService/AnswerComment/LevelTwo";
                    Map<String, String> map = new HashMap<>();
                    map.put("commentLevelOneId", String.valueOf(answerIdOrCommentId));
                    map.put("userId", String.valueOf(user.getUserId()));
                    map.put("replyUserId", String.valueOf(replyObject.getId()));
                    map.put("content", content);
                    RequestUtils.postWithParams(url, map, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(AnswerActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                            runOnUiThread(() -> {
                                Long newLevelTwoId = gson.fromJson(gson.toJson(simpleDto.getObject()), Long.class);
                                answerCommentLevelOneVos.forEach(vo->{if(vo.getCommentLevelOne().getId() == answerIdOrCommentId) vo.getCommentLevelOne().incrementReply(); });
                                commentLevel1Adapter.notifyDataSetChanged();
                                if(!inLevelOne){
                                    AnswerCommentLevelTwoVo levelTwoVo = convertNewCommentToLv2Vo(newLevelTwoId,answerIdOrCommentId,replyObject,content);
                                    commentLevelTwoVos.add(levelTwoVo);
                                    answerCommentLevelTwoAdapter.notifyDataSetChanged();
                                }
                                Toast.makeText(AnswerActivity.this, simpleDto.getMsg(), Toast.LENGTH_SHORT).show();
                                replyEditText.setText("");
                                replyDialog.dismiss();
                            });
                        }
                    });
                }

            });
        }
    }

    public void initViewPager() {
        adapter = new AnswerFragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                clearComment();
                if (position == (fragments.size() - 1)) {
                    getNextAnswer();
                }
                updateUI(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        getInitData();
    }


    //获取页面初始数据
    private void getInitData() {
        String url = "";
        if (user == null) {
            url = serverLocation + "/AnswerService/Answer/Page/-1/" + viewBean.getQuestionId() + "/" + viewBean.getContentId();
        } else {
            url = serverLocation + "/AnswerService/Answer/Page/" + user.getUserId() + "/" + viewBean.getQuestionId() + "/" + viewBean.getContentId();
        }
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AnswerActivity.this, "请求失败", Toast.LENGTH_SHORT).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(() -> {
                    if (simpleDto.isSuccess()) {
                        String json = gson.toJson(simpleDto.getObject());
                        List list = gson.fromJson(json, List.class);
                        list.forEach(o -> {
                            String j = gson.toJson(o);
                            AnswerVo answerVo = gson.fromJson(j, AnswerVo.class);
                            fragments.add(new AnswerFragment(answerVo));
                        });
                        adapter.notifyDataSetChanged();
                        updateUI(0);
                        buttonControl(true);
                    } else {
                        Toast.makeText(AnswerActivity.this, simpleDto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /*
     * 获取当前回答评论数据
     * */
    private void getCommentLevelOne() {
        refreshLayout.setRefreshing(true);
        Long userId = user == null ? -1 : user.getUserId();
        String url = serverLocation + "/CommentService/AnswerComment/LevelOne/" + getCurrentAnswerId() + "/" + userId + "/" + commentHolder.getLimit();
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(AnswerActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(() -> {
                    if (simpleDto.isSuccess()) {
                        Object object = simpleDto.getObject();
                        List list = gson.fromJson(gson.toJson(object), List.class);
                        List<AnswerCommentLevelOneVo> res = new ArrayList<>(list.size());
                        for (Object o : list) {
                            res.add(gson.fromJson(gson.toJson(o), AnswerCommentLevelOneVo.class));
                        }
                        loadCommentLevelOneFinish(res);
                    }
                });
            }
        });
    }

    /*
     * 一级评论加载完成后回调此方法
     *
     * */

    private void loadCommentLevelOneFinish(List<AnswerCommentLevelOneVo> res) {
        if (!commentHolder.isLoad()) {
            commentHolder.setLoad(true);
            level1.setVisibility(View.VISIBLE);
            level2.setVisibility(View.GONE);
        }
        refreshLayout.setRefreshing(false);
        if (res.size() == 0 || res.size() < 10) {
            Toast.makeText(AnswerActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
        }
        if (res.size() == 0) return;
        commentHolder.addLimit(res.size());
        answerCommentLevelOneVos.addAll(res);
        commentLevel1Adapter.notifyDataSetChanged();
    }

    //翻页时重置评论弹窗
    private void clearComment() {
        commentHolder.clear();
        level1.setVisibility(View.GONE);
        level2.setVisibility(View.GONE);
        if(answerCommentLevelOneVos != null){
            answerCommentLevelOneVos.clear();
        }
        if(commentLevelTwoVos != null){
            commentLevelTwoVos.clear();
        }
        inLevelOne = true;
        clearCommentLevelTwoData();
    }


    /*
     * 由一级评论点击"查看回复"回调此方法，并传递一级评论id
     * */
    private void toCommentLevel2(Long id) {
        commentLevelTwoVos.clear();
        commentLevelTwoLimit=0;
        showLevelTwo();
        level2.setAdapter(answerCommentLevelTwoAdapter);
        level2.setLayoutManager(new LinearLayoutManager(this));
        //修改图标
        TextView close = dialogView.findViewById(R.id.acd_close);
        close.setBackground(getResources().getDrawable(R.drawable.icon_back_lv1));
        getCommentLevelTwo(id, list -> {
            if (list.size() == 0)
                return;
            if (list.size() < 10)
                Toast.makeText(AnswerActivity.this, "没有更多内容了", Toast.LENGTH_SHORT).show();
            commentLevelTwoVos.addAll(list);
            commentLevelTwoLimit += list.size();
            answerCommentLevelTwoAdapter.notifyDataSetChanged();
        });

    }

    private void getCommentLevelTwo(Long id, Consumer<List<AnswerCommentLevelTwoVo>> callback) {
        Long userId = user == null ? -1 : user.getUserId();
        String url = serverLocation + "/CommentService/AnswerComment/LevelTwo/" + id + "/" + userId + "/" + commentLevelTwoLimit;
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(AnswerActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(() -> {
                    if (simpleDto.isSuccess()) {
                        List list = gson.fromJson(gson.toJson(simpleDto.getObject()), List.class);
                        List<AnswerCommentLevelTwoVo> res = new ArrayList<>(list.size());
                        for (Object o : list) {
                            res.add(gson.fromJson(gson.toJson(o), AnswerCommentLevelTwoVo.class));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            callback.accept(res);
                        }
                    } else {
                        Toast.makeText(AnswerActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*
     * 翻页后会回调这个方法，在这里更新底部赞同、收藏、评论数据
     * */
    private void updateUI(int index) {
        AnswerVo currentVo = ((AnswerFragment) fragments.get(index)).getAnswerVo();
//        support.setText(currentVo.getAnswer().getSupportSum() + "赞同");
        boolean support = currentVo.isSupport();
        if(support)
            changeButtonToSupport(currentVo);
        else
            changeButtonToUnSupport(currentVo);
        editText.setText("");
    }

    private void getNextAnswer() {
        Long answerId = ((AnswerFragment) fragments.get(viewPager.getCurrentItem())).getAnswerVo().getAnswer().getId();
        String url = user == null ? serverLocation + "/Answer/Next/-1/" + viewBean.getQuestionId() + "/" + answerId : serverLocation + "/Answer/Next/" + user.getUserId() + "/" + viewBean.getQuestionId() + "/" + answerId;
        getAnswer(url, answerVo -> {
            fragments.add(new AnswerFragment(answerVo));
            adapter.notifyDataSetChanged();
        });
    }


    private void getAnswer(String url, Consumer<AnswerVo> callBack) {
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AnswerActivity.this, "请求失败", Toast.LENGTH_SHORT).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(() -> {
                    if (simpleDto.isSuccess()) {
                        String json = gson.toJson(simpleDto.getObject());
                        AnswerVo answerVo = gson.fromJson(json, AnswerVo.class);
                        callBack.accept(answerVo);
                    } else {
                        Toast.makeText(AnswerActivity.this, simpleDto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View.OnClickListener commentClickListener() {
        return v -> {
            if (!commentHolder.isLoad()) {
                answerCommentLevelOneVos = new ArrayList<>();
                commentLevel1Adapter = new AnswerCommentLevelOneAdapter(AnswerActivity.this, answerCommentLevelOneVos, answerCommentLevelOneVo -> {
                    lv1ToLv2 = answerCommentLevelOneVo;
                    toCommentLevel2(answerCommentLevelOneVo.getCommentLevelOne().getId());
                }, answerCommentLevelOneVo -> {
                    showOptionDialog(answerCommentLevelOneVo);
                });
                level1.setAdapter(commentLevel1Adapter);
                level1.setLayoutManager(linearLayoutManager);
                getCommentLevelOne();
            }
            dialog.show();
        };
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private View.OnClickListener supportListener() {
        return v -> {
            support.setClickable(false);
            AnswerVo currentAnswer = getCurrentAnswer();
            Long currentAnswerId = currentAnswer.getAnswer().getId();
            Long userId = user.getUserId();
            boolean isSupport = currentAnswer.isSupport();
            String api = isSupport ? "UnSupport" : "Support";
            String url = serverLocation + "/AnswerService/Answer/" + api;
            Map<String,String> map = new HashMap<>(2);
            map.put("answerId",String.valueOf(currentAnswerId));
            map.put("userId",String.valueOf(userId));
            RequestUtils.postWithParams(url,map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(()->Toast.makeText(AnswerActivity.this,"请求失败",Toast.LENGTH_SHORT));
                    support.setClickable(true);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try{
                        SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                        runOnUiThread(() -> {
                            if(simpleDto.isSuccess()){
                                Long answerIdNow = getCurrentAnswerId();
                                if(isSupport){
                                    currentAnswer.unSupport();
                                    if(answerIdNow == currentAnswerId)
                                        changeButtonToUnSupport(currentAnswer);
                                }else{
                                    currentAnswer.support();
                                    if(answerIdNow == currentAnswerId)
                                        changeButtonToSupport(currentAnswer);
                                }
                            }else {
                                Toast.makeText(AnswerActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT);
                            }
                        });
                    }finally {
                        support.setClickable(true);
                    }
                }
            });
        };
    }

    private void changeButtonToSupport(AnswerVo currentAnswer){
        support.setBackground(getResources().getDrawable(R.drawable.support_button_background));
        support.setTextColor(getResources().getColor(R.color.white));
        support.setText(currentAnswer.getAnswer().getSupportSum()+"赞同");
        supportTriangle.setBackground(getResources().getDrawable(R.drawable.icon_triangle_white));
    }
    private void changeButtonToUnSupport(AnswerVo currentAnswer){
        support.setBackground(getResources().getDrawable(R.drawable.un_support_button_background));
        support.setTextColor(getResources().getColor(R.color.ZhiHuBlue));
        support.setText(currentAnswer.getAnswer().getSupportSum()+"赞同");
        supportTriangle.setBackground(getResources().getDrawable(R.drawable.icon_triangle_blue));
    }

    private void initToolBar() {
        toolbar.setTitle(viewBean.getTitle());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnClickListener(v->toQuestionInfoActivity());
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void toQuestionInfoActivity() {
        Intent intent = new Intent(this, QuestionInfoActivity.class);
        intent.putExtra("questionId",viewBean.getQuestionId());
        intent.putExtra("title",viewBean.getTitle());
        startActivityForResult(intent,TO_QUESTION_CODE);
    }

    /*
    * 添加一级评论后服务器会返回此评论的Id，将这个Id包装为新的评论VO
    * @newLevelOneId 新的评论Id
    * @return 新的评论Vo 默认点赞为false
    * */
    private AnswerCommentLevelOneVo convertNewCommentToLv1Vo(Long newLevelOneId,String content) {
        AnswerCommentLevelOne levelOne = new AnswerCommentLevelOne(newLevelOneId, getCurrentAnswerId(), user.getUserId(), content, new Date(), 0l, 0);
        return new AnswerCommentLevelOneVo(false,user.clone(),levelOne);
    }

    /*
     * 添加二级评论后服务器会返回此评论的Id，将这个Id包装为新的评论VO
     * @newLevelOneId 新的评论Id
     * @return 新的评论Vo 默认点赞为false
    * */
    private AnswerCommentLevelTwoVo convertNewCommentToLv2Vo(Long newLevelTwoId,Long levelOneId,User targetUser, String content) {
        AnswerCommentLevelTwo levelTwo = new AnswerCommentLevelTwo(newLevelTwoId, levelOneId, targetUser.getId(), user.getUserId(), content, new Date(), 0l);
        return new AnswerCommentLevelTwoVo(false,levelTwo,targetUser,user.clone());
    }

    private Long getCurrentAnswerId() {
        int currentItem = viewPager.getCurrentItem();
        AnswerFragment fragment = (AnswerFragment) fragments.get(currentItem);
        return fragment.getAnswerVo().getAnswer().getId();
    }

    private AnswerVo getCurrentAnswer() {
        int currentItem = viewPager.getCurrentItem();
        AnswerFragment fragment = (AnswerFragment) fragments.get(currentItem);
        return fragment.getAnswerVo();
    }

    protected int getPeekHeight() {
        return getResources().getDisplayMetrics().heightPixels;
        /*//设置弹窗高度为屏幕高度的3/4
        return peekHeight - (peekHeight / 4);*/
    }

    private void buttonControl(boolean b) {
        support.setClickable(b);
        collect.setClickable(b);
        comment.setClickable(b);
    }

    /*
     * 记录此次浏览记录
     * */
    private void recordBrowse() {
        if (user != null) {
            String url = serverLocation + "/Question/Browse/" + viewBean.getQuestionId() + "/" + user.getUserId();
            RequestUtils.sendSimpleRequest(url);
        }
    }

    private void clearCommentLevelTwoData() {
        commentLevelTwoLimit = 0;
        commentLevelTwoVos.clear();
        answerCommentLevelTwoAdapter.notifyDataSetChanged();
    }

    private void showLevelTwo() {
        inLevelOne = false;
        level1.setVisibility(View.GONE);
        level2.setVisibility(View.VISIBLE);
    }

    private void showLevelOne() {
        inLevelOne = true;
        level1.setVisibility(View.VISIBLE);
        level2.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == TO_QUESTION_CODE){
            destroyAllDiaLog();
            getIntent().putExtra("viewBean",data.getSerializableExtra("viewBean"));
            init();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void destroyAllDiaLog() {
        dialog.dismiss();
        replyDialog.dismiss();
        optionDialog.dismiss();
        ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        ((ViewGroup) replyView.getParent()).removeView(replyView);
        ((ViewGroup) optionView.getParent()).removeView(optionView);
    }
    @Override
    protected void onDestroy() {
        destroyAllDiaLog();
        super.onDestroy();
    }

}
