package com.sz.zhihu.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.sz.zhihu.R;
import com.sz.zhihu.listener.settings.AbstractCustomListener;
import com.sz.zhihu.po.Settings;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SettingItemAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private Settings settings;
    public SettingItemAdapter(Activity activity, Settings settings){
        this.activity = activity;
        this.settings = settings;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SettingItemViewHolder(LayoutInflater.from(activity).inflate(R.layout.settings_son_item,parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SettingItemViewHolder){
            String[] params = settings.getParams();
            String[] className = settings.getClassName();
            SettingItemViewHolder viewHolder = (SettingItemViewHolder) holder;
            viewHolder.param.setText(params[position]);
            viewHolder.itemView.setOnClickListener(getListener(settings.getClassName()[position]));
        }
    }

    @Override
    public int getItemCount() {
        return settings == null ? 0 : settings.getParams().length;
    }
    class SettingItemViewHolder extends RecyclerView.ViewHolder{

        private final TextView param;

        public SettingItemViewHolder(@NonNull View view) {
            super(view);
            param = view.findViewById(R.id.ssi_param);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private AbstractCustomListener getListener(String className) {
        try {
            Class<?> clazz = Class.forName("com.sz.zhihu.listener.settings." + className);
            Constructor<?> constructor = clazz.getConstructor(Activity.class);
            AbstractCustomListener o = (AbstractCustomListener) constructor.newInstance(activity);
            return o;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
