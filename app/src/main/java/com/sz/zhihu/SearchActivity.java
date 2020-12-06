package com.sz.zhihu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sz.zhihu.adapter.SearchHistoryAdapter;
import com.sz.zhihu.adapter.SearchKeywordResultAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.Keyword;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchActivity extends AbstractCustomActivity {

    private static final int DEFAULT_SEARCH_SIZE = 10;

    private Gson gson;
    private String serverLocation;
    private EditText search;
    private TextView cancel;
    private List<Keyword> historyData;
    private SearchHistoryAdapter searchHistoryAdapter;
    private LinearLayout history;
    private RecyclerView historyRecyclerView;
    private LinearLayoutManager historyLayoutManager;
    private LinearLayout searchResult;
    private RecyclerView searchResultRecyclerView;
    private List<Keyword> searchResultData;
    private SearchKeywordResultAdapter searchKeywordResultAdapter;
    private LinearLayoutManager searchResultLayoutManager;
    private RelativeLayout additional;
    private TextView additionalText;
    private TextView clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    private void init() {
        prepare();
        bindComponent();
        addComponentListener();
    }

    private void prepare() {
        gson = GsonUtils.getGson();
        serverLocation = getString(R.string.server_location);
        historyData = DBUtils.getSearchHistory();
        searchResultData = new ArrayList<>();
        searchHistoryAdapter = new SearchHistoryAdapter(this, historyData,this::onKeywordSelect);
        searchKeywordResultAdapter = new SearchKeywordResultAdapter(this, searchResultData, this::onKeywordSelect);
        historyLayoutManager = new LinearLayoutManager(this);
        searchResultLayoutManager = new LinearLayoutManager(this);
    }


    private void bindComponent() {
        search = findViewById(R.id.as_search);
        cancel = findViewById(R.id.as_cancel);
        history = findViewById(R.id.as_history);
        clear = findViewById(R.id.as_clear_history);
        historyRecyclerView = findViewById(R.id.as_history_recycler_view);
        searchResult = findViewById(R.id.as_search_result);
        searchResultRecyclerView = findViewById(R.id.as_search_result_recycler_view);
        additional = findViewById(R.id.as_additional);
        additionalText = findViewById(R.id.as_additional_text);
        historyRecyclerView.setAdapter(searchHistoryAdapter);
        historyRecyclerView.setLayoutManager(historyLayoutManager);
        searchResultRecyclerView.setAdapter(searchKeywordResultAdapter);
        searchResultRecyclerView.setLayoutManager(searchResultLayoutManager);
    }

    private void addComponentListener() {
        search.addTextChangedListener(inputListener());
        search.setOnEditorActionListener(enterListener());
        cancel.setOnClickListener(v->finish());
        clear.setOnClickListener(v -> {
            DBUtils.clearSearchHistory();
            historyData.clear();
            searchHistoryAdapter.notifyDataSetChanged();
        });
        additional.setOnClickListener(v -> {
            additional.setClickable(false);
            String s = search.getText().toString();
            onKeywordSelect(new Keyword(s));
        });
    }

    private TextWatcher inputListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                if(StringUtils.isEmpty(s)){
                    showHistory();
                }else{
                    searchResultData.clear();
                    searchHistoryAdapter.notifyDataSetChanged();
                    additionalText.setText("查看「"+s+"」的搜索结果");
                    showSearchResult();
                    searchKeyword(s,searchResults -> {
                        //检查搜索框中的词汇是否已经改变 如改变即不再刷新结果
                        String input = search.getText().toString();
                        if(input.equals(s)){
                            searchResultData.addAll(searchResults);
                            searchKeywordResultAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };
    }


    private TextView.OnEditorActionListener enterListener() {
        return (TextView textView, int i, KeyEvent keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    onKeywordSelect(new Keyword(search.getText().toString()));

                }
                return false;
            };
    }

    /*
    * 根据用户输入的词汇向服务器请求搜索关键词
    * @input 用户输入的词汇
    * @consumer 服务器返回的关键词回调接口
    * */
    private void searchKeyword(String input, Consumer<List<Keyword>> consumer){
        String url = serverLocation + "/SearchService/Search/Keyword/" + input + "/" +DEFAULT_SEARCH_SIZE;
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->Toast.makeText(SearchActivity.this,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                runOnUiThread(() -> {
                    if(simpleDto.isSuccess()){
                        List<Keyword> res = gson.fromJson(gson.toJson(simpleDto.getObject()),new TypeToken<List<Keyword>>(){}.getType());
                        consumer.accept(res);
                    }
                });
            }
        });
    }

    /*
    * 当用户点击 "历史" "搜索" item 或 enter 键后选中搜索关键词时回调，记录用户此次搜索，并跳转到搜索结果页面
    * @keyword 用户所选中的关键词
    * */
    private void onKeywordSelect(Keyword keyword) {
        DBUtils.saveIfNotExistKeyword(keyword);
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("keyword",keyword);
        startActivity(intent);
        finish();
    }

    public void showHistory(){
        history.setVisibility(View.VISIBLE);
        searchResult.setVisibility(View.GONE);
    }

    public void showSearchResult(){
        history.setVisibility(View.GONE);
        searchResult.setVisibility(View.VISIBLE);

    }
}