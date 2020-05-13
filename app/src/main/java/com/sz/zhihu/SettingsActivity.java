package com.sz.zhihu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;

import com.sz.zhihu.adapter.SettingsAdapter;
import com.sz.zhihu.po.Settings;
import com.sz.zhihu.utils.JsonUtils;
import com.sz.zhihu.utils.SystemUtils;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
        initRecyclerView();
    }

    private void init() {
        toolbar = findViewById(R.id.as_tool_bar);
        recyclerView = findViewById(R.id.as_recyclerview);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v->finish());
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initRecyclerView() {
        List<Settings> settingFromJson = JsonUtils.getSettingFromJson(this);
        if(settingFromJson != null){
            SettingsAdapter settingsAdapter = new SettingsAdapter(this, settingFromJson);
            recyclerView.setAdapter(settingsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }
}
