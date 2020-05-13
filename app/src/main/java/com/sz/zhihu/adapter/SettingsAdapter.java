package com.sz.zhihu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sz.zhihu.R;
import com.sz.zhihu.po.Settings;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private List<Settings> data;

    public SettingsAdapter(Activity activity, List<Settings> data){
        this.activity = activity;
        this.data = data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SettingsViewHolder(LayoutInflater.from(activity).inflate(R.layout.settings_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SettingsViewHolder viewHolder = null;
        if(holder instanceof SettingsViewHolder){
            Settings settings = data.get(position);
            viewHolder = (SettingsViewHolder) holder;
            viewHolder.name.setText(settings.getName());
            viewHolder.recyclerView.setAdapter(new SettingItemAdapter(activity,settings));
            viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
    class SettingsViewHolder extends RecyclerView.ViewHolder{

        private final TextView name;
        private final RecyclerView recyclerView;

        public SettingsViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.si_name);
            recyclerView = view.findViewById(R.id.si_recyclerview);
        }
    }
}
