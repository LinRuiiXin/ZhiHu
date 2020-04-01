package com.sz.zhihu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sz.zhihu.adapter.RecommendQuestionCardAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.manager.SwipeCardCallBack;
import com.sz.zhihu.manager.SwipeCardLayoutManager;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.vo.RecommendQuestionViewBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecommendQuestionActivity extends AppCompatActivity {

    private TextView close;
    private RecyclerView recyclerView;
    private Button writeAnswer;
    private List<RecommendQuestionViewBean> beans = new ArrayList<>();
    private String serverLocation;
    private Gson gson;
    private RecommendQuestionCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_question);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    private void init() {
        gson = new Gson();
        serverLocation = getResources().getString(R.string.server_location);
        close = findViewById(R.id.arq_close);
        recyclerView = findViewById(R.id.arq_card);
        writeAnswer = findViewById(R.id.arq_write_answer);
        adapter = new RecommendQuestionCardAdapter(this, beans);
        SwipeCardLayoutManager swipeCardLayoutManager = new SwipeCardLayoutManager(this);
        SwipeCardCallBack swipeCardCallBack = new SwipeCardCallBack(){
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //当已经滑动删除了的时候会被回掉--删除数据，循环的效果
                beans.remove(viewHolder.getLayoutPosition());
                adapter.notifyDataSetChanged();
                if(beans.size()<=2){
                    getData();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCardCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(swipeCardLayoutManager);
        recyclerView.setAdapter(adapter);
        getData();
        close.setOnClickListener(v->{
            finish();
        });
        writeAnswer.setOnClickListener(v->{
            Intent intent = new Intent(RecommendQuestionActivity.this,EditActivity.class);
            intent.putExtra("question",beans.get((beans.size()-1)).getQuestion());
            startActivity(intent);
        });
    }

    private void getData() {
        String url = serverLocation + "/Question/Random";
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()-> Toast.makeText(RecommendQuestionActivity.this,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        String s = gson.toJson(simpleDto.getObject());
                        List list = gson.fromJson(s,ArrayList.class);
                        list.forEach(o -> {

                            RecommendQuestionViewBean recommendQuestionCardBean = gson.fromJson(gson.toJson(o), RecommendQuestionViewBean.class);
                            beans.add(recommendQuestionCardBean);
                        });
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
