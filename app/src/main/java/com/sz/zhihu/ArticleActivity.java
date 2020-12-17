package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.rex.editor.view.RichEditorNew;
import com.sz.zhihu.adapter.AnswerCommentLevelOneAdapter;
import com.sz.zhihu.adapter.AnswerCommentLevelTwoAdapter;
import com.sz.zhihu.adapter.ArticleCommentLevelOneAdapter;
import com.sz.zhihu.adapter.ArticleCommentLevelTwoAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.holder.CommentHolder;
import com.sz.zhihu.po.AnswerCommentLevelOne;
import com.sz.zhihu.po.ArticleCommentLevelOne;
import com.sz.zhihu.po.ArticleCommentLevelTwo;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.DiaLogUtils;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.view.AnswerCommentDialogView;
import com.sz.zhihu.vo.AnswerCommentLevelOneVo;
import com.sz.zhihu.vo.AnswerCommentLevelTwoVo;
import com.sz.zhihu.vo.ArticleCommentLevelOneVo;
import com.sz.zhihu.vo.ArticleCommentLevelTwoVo;
import com.sz.zhihu.vo.ArticleVo;
import com.sz.zhihu.vo.RecommendViewBean;

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

import static com.sz.zhihu.R.id.article_tool_bar;

public class ArticleActivity extends AbstractCustomActivity {

    private RecommendViewBean recommendViewBean;
    private RichEditorNew content;
    private Gson gson;
    private User user;
    private String serverLocation;
    private Button support;
    private LinearLayout collect;
    private LinearLayout comment;
    private ArticleToken articleToken;
    private ImageView supportTriangle;
    private View dialogView;
    private View replyView;
    private View optionView;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView level1;
    private RecyclerView level2;
    private BottomSheetDialog dialog;
    private BottomSheetDialog replyDialog;
    private BottomSheetDialog optionDialog;
    private BottomSheetBehavior<View> behavior;
    private boolean inLevelOne = true;
    private ArticleCommentLevelOneVo lv1ToLv2;
    private User author;
    private EditText replyEditText;
    private TextView replyTitle;
    private TextView replyObjectName;
    private TextView submitReply;
    private CommentHolder commentHolder;
    private List<ArticleCommentLevelOneVo> articleCommentLevelOneVos;
    private ArticleCommentLevelOneAdapter commentLevel1Adapter;
    private ArticleCommentLevelTwoAdapter articleCommentLevelTwoAdapter;
    List<ArticleCommentLevelTwoVo> commentLevelTwoVos = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private Long articleId;
    private RelativeLayout optionSupport;
    private RelativeLayout optionReply;
    private RelativeLayout optionDelete;
    //初始化二级评论
    private int commentLevelTwoLimit = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        prepare();
        bindComponent();
        initToolBar();
        initReplyDialog();
        initOptionDialog();
        initCommentDialog();
        getContent(articleVoCallBack());
    }

    /*
    * 获取到文章数据时回调
    * */
    private Consumer<ArticleVo> articleVoCallBack() {
        return articleVo -> {
            articleToken = new ArticleToken(articleVo.isSupport(),articleVo.isAttention(),articleVo.getSupportSum());
            changeSupportButton(articleToken);
            content.loadRichEditorCode(articleVo.getContent());
            buttonControl(true);
        };
    }


    /*
    * 准备工作，如果传递的类型不为2--即文章，退出
    * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void prepare() {
        recommendViewBean = (RecommendViewBean) getIntent().getSerializableExtra("viewBean");
        if(recommendViewBean == null || recommendViewBean.getContentType() != 2){
            Toast.makeText(this,"发生未知异常",Toast.LENGTH_SHORT).show();
            finish();
        }
        articleId = recommendViewBean.getContentId();
        author = new User(recommendViewBean.getUserId(),0l,recommendViewBean.getUsername(),null,null,null,recommendViewBean.getIntroduction(),recommendViewBean.getPortraitFileName(),null);
        gson = GsonUtils.getGson();
        user = DBUtils.queryUserHistory();
        serverLocation = getString(R.string.server_location);
        commentHolder = new CommentHolder();
        linearLayoutManager = new LinearLayoutManager(this);
        articleCommentLevelTwoAdapter = new ArticleCommentLevelTwoAdapter(this,commentLevelTwoVos,this::showOptionDialog);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void bindComponent() {
        content = findViewById(R.id.aa_content);
        support = findViewById(R.id.cb_support);
//        collect = findViewById(R.id.cb_collect);
        comment = findViewById(R.id.cb_comment);
        supportTriangle = findViewById(R.id.cb_support_triangle);
        buttonControl(false);
        support.setOnClickListener(supportListener());
        comment.setOnClickListener(commentClickListener());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View.OnClickListener supportListener() {
        return v -> {
            if(DBUtils.checkIsLogged(this)){
                if(articleToken != null){
                    support.setClickable(false);
                    String api = articleToken.isSupport() ? "UnSupport" : "Support";
                    Map<String,String> params = new HashMap<>(2);
                    params.put("userId",String.valueOf(user.getUserId()));
                    params.put("articleId",String.valueOf(recommendViewBean.getContentId()));
                    String url = serverLocation + "/ArticleService/Article/" + api;
                    RequestUtils.postWithParams(url,params, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(()-> {
                                Toast.makeText(ArticleActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                                support.setClickable(true);
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                            runOnUiThread(()->{
                                if(simpleDto.isSuccess()){
                                    articleToken.reverseSupport();
                                    changeSupportButton(articleToken);
                                }else
                                    Toast.makeText(ArticleActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                support.setClickable(true);
                            });
                        }
                    });
                }
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View.OnClickListener commentClickListener() {
        return v -> {
            if (!commentHolder.isLoad()) {
                articleCommentLevelOneVos = new ArrayList<>();
                commentLevel1Adapter = new ArticleCommentLevelOneAdapter(ArticleActivity.this, articleCommentLevelOneVos, articleCommentLevelOneVo -> {
                    lv1ToLv2 = articleCommentLevelOneVo;
                    toCommentLevel2(articleCommentLevelOneVo.getArticleCommentLevelOne().getId());
                },this::showOptionDialog);
                level1.setAdapter(commentLevel1Adapter);
                level1.setLayoutManager(linearLayoutManager);
                getCommentLevelOne();
            }
            dialog.show();
        };
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
    private void showOptionDialog(ArticleCommentLevelTwoVo articleCommentLevelTwoVo) {
        if (user == null) {
            DiaLogUtils.showPromptLoginDialog(this);
            return;
        }
        boolean b = articleCommentLevelTwoVo.getReplyUser().getId() == user.getUserId();
        optionSupport.setOnClickListener(v -> {
            articleCommentLevelTwoAdapter.supportOrUnSupportComment(articleCommentLevelTwoVo,()->optionDialog.dismiss());
        });
        optionReply.setOnClickListener(v -> {
            optionDialog.dismiss();
            showReplyDialog(3, articleCommentLevelTwoVo.getAnswerCommentLevelTwo().getLevelOneId(), articleCommentLevelTwoVo.getUserReplyTo());
        });
        if (b) {
            optionDelete.setBackground(getDrawable(R.drawable.shape_radius_right_delete));
            optionDelete.setOnClickListener(v -> {
                deleteCommentLevelTwo(articleCommentLevelTwoVo);
            });
        } else {
            optionDelete.setVisibility(View.GONE);
            optionReply.setBackground(getDrawable(R.drawable.shape_radius_right_reply));
        }
        optionDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void deleteCommentLevelTwo(ArticleCommentLevelTwoVo articleCommentLevelTwoVo) {
        String url = serverLocation + "/CommentService/ArticleComment/LevelTwo";
        Map<String,String> params = new HashMap<>(1);
        params.put("replyId",String.valueOf(articleCommentLevelTwoVo.getAnswerCommentLevelTwo().getId()));
        RequestUtils.deleteWithParams(url, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->{Toast.makeText(ArticleActivity.this,"请求失败",Toast.LENGTH_SHORT).show(); });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        commentLevelTwoVos.remove(articleCommentLevelTwoVo);
                        articleCommentLevelTwoAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(ArticleActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
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
        String url = serverLocation + "/CommentService/ArticleComment/LevelOne/" + articleId + "/" + userId + "/" + commentHolder.getLimit();
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ArticleActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(() -> {
                    if (simpleDto.isSuccess()) {
                        Object object = simpleDto.getObject();
                        List list = gson.fromJson(gson.toJson(object), List.class);
                        List<ArticleCommentLevelOneVo> res = new ArrayList<>(list.size());
                        for (Object o : list) {
                            res.add(gson.fromJson(gson.toJson(o), ArticleCommentLevelOneVo.class));
                        }
                        loadCommentLevelOneFinish(res);
                    }
                });
            }
        });
    }

    private void loadCommentLevelOneFinish(List<ArticleCommentLevelOneVo> res) {
        if (!commentHolder.isLoad()) {
            commentHolder.setLoad(true);
            level1.setVisibility(View.VISIBLE);
            level2.setVisibility(View.GONE);
        }
        refreshLayout.setRefreshing(false);
        if (res.size() == 0 || res.size() < 10) {
            Toast.makeText(ArticleActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
        }
        if (res.size() == 0) return;
        commentHolder.addLimit(res.size());
        articleCommentLevelOneVos.addAll(res);
        commentLevel1Adapter.notifyDataSetChanged();
    }

    //  显示"菜单"对话框，并判断是否本人操作？显示"删除评论"选项，否则不显示
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showOptionDialog(ArticleCommentLevelOneVo articleCommentLevelOneVo) {
        if (user == null) {
            DiaLogUtils.showPromptLoginDialog(this);
            return;
        }
        boolean b = articleCommentLevelOneVo.getUser().getId() == user.getUserId();
        optionSupport.setOnClickListener(v -> {
            commentLevel1Adapter.supportOrUnSupportComment(articleCommentLevelOneVo,()->optionDialog.dismiss());
        });
        optionReply.setOnClickListener(v -> {
            optionDialog.dismiss();
            showReplyDialog(2, articleCommentLevelOneVo.getArticleCommentLevelOne().getId(), articleCommentLevelOneVo.getUser());
        });
        if (b) {
            optionDelete.setBackground(getDrawable(R.drawable.shape_radius_right_delete));
            optionDelete.setOnClickListener(v -> {
                deleteCommentLevelOne(articleCommentLevelOneVo);
            });
        } else {
            optionDelete.setVisibility(View.GONE);
            optionReply.setBackground(getDrawable(R.drawable.shape_radius_right_reply));
        }
        optionDialog.show();
    }

    /*
     * 由一级评论点击"查看回复"回调此方法，并传递一级评论id
     * */
    private void toCommentLevel2(Long id) {
        commentLevelTwoVos.clear();
        articleCommentLevelTwoAdapter.notifyDataSetChanged();
        commentLevelTwoLimit = 0;
        showLevelTwo();
        level2.setAdapter(articleCommentLevelTwoAdapter);
        level2.setLayoutManager(new LinearLayoutManager(this));
        //修改图标
        TextView close = dialogView.findViewById(R.id.acd_close);
        close.setBackground(getResources().getDrawable(R.drawable.icon_back_lv1));
        getCommentLevelTwo(id, list -> {
            if (list.size() == 0)
                return;
            if (list.size() < 10)
                Toast.makeText(ArticleActivity.this, "没有更多内容了", Toast.LENGTH_SHORT).show();
            commentLevelTwoVos.addAll(list);
            commentLevelTwoLimit += list.size();
            articleCommentLevelTwoAdapter.notifyDataSetChanged();
        });

    }

    private void getCommentLevelTwo(Long id, Consumer<List<ArticleCommentLevelTwoVo>> callback) {
        Long userId = user == null ? -1 : user.getUserId();
        String url = serverLocation + "/CommentService/ArticleComment/LevelTwo/" + id + "/" + userId + "/" + commentLevelTwoLimit;
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ArticleActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(() -> {
                    if (simpleDto.isSuccess()) {
                        List list = gson.fromJson(gson.toJson(simpleDto.getObject()), List.class);
                        List<ArticleCommentLevelTwoVo> res = new ArrayList<>(list.size());
                        for (Object o : list) {
                            res.add(gson.fromJson(gson.toJson(o), ArticleCommentLevelTwoVo.class));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            callback.accept(res);
                        }
                    } else {
                        Toast.makeText(ArticleActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*
     * 删除一级评论
     * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void deleteCommentLevelOne(ArticleCommentLevelOneVo articleCommentLevelOneVo) {
        optionDelete.setEnabled(false);
        String url = serverLocation + "/CommentService/ArticleComment/LevelOne";
        Map<String,String> params = new HashMap<>(1);
        params.put("commentId",String.valueOf(articleCommentLevelOneVo.getArticleCommentLevelOne().getId()));
        RequestUtils.deleteWithParams(url, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()-> {
                    Toast.makeText(ArticleActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    optionDelete.setEnabled(true);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        articleCommentLevelOneVos.remove(articleCommentLevelOneVo);
                        commentLevel1Adapter.notifyDataSetChanged();
                        optionDialog.dismiss();
                        optionDelete.setEnabled(true);
                    }else{
                        Toast.makeText(ArticleActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                        optionDelete.setEnabled(true);
                    }
                });
            }
        });

    }

    /*
     * 获取文章内容
     * */
    private void getContent(Consumer<ArticleVo> consumer) {
        Long uId = user != null ? user.getUserId() : -1;
        String url = serverLocation + "/ArticleService/Article/" + recommendViewBean.getContentId() + "/" + recommendViewBean.getUserId() + "/" + uId;
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->Toast.makeText(ArticleActivity.this,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        ArticleVo articleVo = gson.fromJson(gson.toJson(simpleDto.getObject()), ArticleVo.class);
                        consumer.accept(articleVo);
                    }else
                        Toast.makeText(ArticleActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                });
            }
        });
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
        TextView editText = dialogView.findViewById(R.id.acd_write_comment);
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
                User user = inLevelOne ? author : lv1ToLv2.getUser();
                showReplyDialog(inLevelOne ? 1 : 2, inLevelOne ? recommendViewBean.getContentId() : lv1ToLv2.getArticleCommentLevelOne().getId(), user);
            }
        });
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
                String url = serverLocation + "/CommentService/ArticleComment/LevelOne";
                Map<String, String> map = new HashMap<>();
                map.put("articleId", String.valueOf(answerIdOrCommentId));
                map.put("userId", String.valueOf(user.getUserId()));
                map.put("content", replyEditText.getText().toString());
                RequestUtils.postWithParams(url, map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(ArticleActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                        runOnUiThread(() -> {
                            if (simpleDto.isSuccess()) {
                                Long newLevelOneId = gson.fromJson(gson.toJson(simpleDto.getObject()), Long.class);
                                ArticleCommentLevelOneVo vo = convertLv1IdToVo(newLevelOneId,replyEditText.getText().toString());
                                articleCommentLevelOneVos.add(vo);
                                commentLevel1Adapter.notifyDataSetChanged();
                                Toast.makeText(ArticleActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ArticleActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ArticleActivity.this,"评论不能为空",Toast.LENGTH_SHORT).show();
                } else {
                    String url = serverLocation + "/CommentService/ArticleComment/LevelTwo";
                    Map<String, String> map = new HashMap<>();
                    map.put("levelOneId", String.valueOf(answerIdOrCommentId));
                    map.put("replyUserId", String.valueOf(user.getUserId()));
                    map.put("replyToUserId", String.valueOf(replyObject.getId()));
                    map.put("content", content);
                    RequestUtils.postWithParams(url, map, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(ArticleActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                            runOnUiThread(() -> {
//                                Toast.makeText(ArticleActivity.this, simpleDto.getMsg(), Toast.LENGTH_SHORT).show();
                                Long newCommentId = gson.fromJson(gson.toJson(simpleDto.getObject()), Long.class);
                                articleCommentLevelOneVos.forEach(vo->{
                                    if(vo.getArticleCommentLevelOne().getId()==answerIdOrCommentId)
                                        vo.getArticleCommentLevelOne().incrementReply();
                                });
                                commentLevel1Adapter.notifyDataSetChanged();
                                if(!inLevelOne){
                                    ArticleCommentLevelTwoVo vo = new ArticleCommentLevelTwoVo(false, new ArticleCommentLevelTwo(newCommentId, answerIdOrCommentId, replyObject.getId(), user.getUserId(), content, new Date(), 0l), replyObject, user.clone());
                                    commentLevelTwoVos.add(vo);
                                    articleCommentLevelTwoAdapter.notifyDataSetChanged();
                                }

                                replyEditText.setText("");
                                replyDialog.dismiss();
                            });
                        }
                    });
                }

            });
        }
    }

    private ArticleCommentLevelOneVo convertLv1IdToVo(Long newLevelOneId, String content) {
        ArticleCommentLevelOne levelOne = new ArticleCommentLevelOne(newLevelOneId, articleId, user.getUserId(), content, new Date(), 0l, 0);
        return new ArticleCommentLevelOneVo(false,user.clone(),levelOne);
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


    private void initToolBar() {
        Toolbar toolbar = findViewById(article_tool_bar);
        toolbar.setTitle(recommendViewBean.getTitle());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v-> finish());
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void changeSupportButton(ArticleToken articleToken){
        if(articleToken.isSupport())
            changeButtonToSupport(articleToken);
        else
            changeButtonToUnSupport(articleToken);
    }

    private void changeButtonToSupport(ArticleToken articleToken){
        support.setBackground(getResources().getDrawable(R.drawable.support_button_background));
        support.setTextColor(getResources().getColor(R.color.white));
        support.setText(articleToken.getSupportSum()+"赞同");
        supportTriangle.setBackground(getResources().getDrawable(R.drawable.icon_triangle_white));
    }
    private void changeButtonToUnSupport(ArticleToken articleToken){
        support.setBackground(getResources().getDrawable(R.drawable.un_support_button_background));
        support.setTextColor(getResources().getColor(R.color.ZhiHuBlue));
        support.setText(articleToken.getSupportSum()+"赞同");
        supportTriangle.setBackground(getResources().getDrawable(R.drawable.icon_triangle_blue));
    }

    private void showLevelTwo() {
        inLevelOne = false;
        level1.setVisibility(View.GONE);
        level2.setVisibility(View.VISIBLE);
    }

    private void buttonControl(boolean b) {
        support.setClickable(b);
//        collect.setClickable(b);
        comment.setClickable(b);
    }

    private void showLevelOne() {
        inLevelOne = true;
        level1.setVisibility(View.VISIBLE);
        level2.setVisibility(View.GONE);
    }
    
    protected int getPeekHeight() {
        return getResources().getDisplayMetrics().heightPixels;
        /*//设置弹窗高度为屏幕高度的3/4
        return peekHeight - (peekHeight / 4);*/
    }
    @Override
    protected void onDestroy() {
        dialog.dismiss();
        replyDialog.dismiss();
        optionDialog.dismiss();
        ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        ((ViewGroup) replyView.getParent()).removeView(replyView);
        ((ViewGroup) optionView.getParent()).removeView(optionView);
        super.onDestroy();
    }



    class ArticleToken{
        private boolean isSupport;
        private boolean isAttention;
        private Long supportSum;

        public ArticleToken() {}

        public ArticleToken(boolean isSupport,boolean isAttention, Long supportSum) {
            this.isSupport = isSupport;
            this.isAttention = isAttention;
            this.supportSum = supportSum;
        }

        public void support(){
            this.isSupport = true;
            this.supportSum++;
        }

        public void unSupport(){
            this.isSupport = false;
            this.supportSum--;
        }

        public void reverseSupport(){
            if(isSupport)
                unSupport();
            else
                support();
        }

        public void attention(){
            this.isAttention = true;
        }

        public void cancelAttention(){
            this.isAttention = false;
        }

        public boolean isSupport() {
            return isSupport;
        }

        public void setSupport(boolean support) {
            isSupport = support;
        }

        public boolean isAttention() {
            return isAttention;
        }

        public void setAttention(boolean attention) {
            isAttention = attention;
        }

        public Long getSupportSum() {
            return supportSum;
        }

        public void setSupportSum(Long supportSum) {
            this.supportSum = supportSum;
        }
    }

}