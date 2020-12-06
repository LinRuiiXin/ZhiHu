package com.sz.zhihu.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.sz.zhihu.R;
import com.sz.zhihu.po.Question;

import java.util.List;
import java.util.function.Consumer;

public class HotListAdapter extends RecyclerView.Adapter {

    private final Activity activity;
    private final List<Question> data;
    private final LayoutInflater inflater;
    private Consumer<Question> consumer;
    private final int[] rankingColors;
    private final int defaultColor;

    public HotListAdapter(Activity activity, List<Question> data, Consumer<Question> consumer){
        this.activity = activity;
        this.data = data;
        inflater = LayoutInflater.from(activity);
        this.consumer = consumer;
        rankingColors = new int[]{Color.parseColor("#FF0000"),Color.parseColor("#FF4500"),Color.parseColor("#FF7F50")};
        defaultColor = Color.parseColor("#B0C4DE");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.hot_list_item,parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder) holder;
            Question question = data.get(position);
            if(position < rankingColors.length)
                viewHolder.ranking.setTextColor(rankingColors[position]);
            else
                viewHolder.ranking.setTextColor(defaultColor);
            viewHolder.ranking.setText(String.valueOf(position+1));
            viewHolder.title.setText(question.getName());
            viewHolder.hotPoint.setText(String.valueOf(question.getBrowseSum()));
            viewHolder.subscribeSum.setText(String.valueOf(question.getSubscribeSum()));
            viewHolder.answerSum.setText(String.valueOf(question.getAnswerSum()));
            viewHolder.itemView.setOnClickListener(v->consumer.accept(question));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView ranking;
        private final TextView title;
        private final TextView hotPoint;
        private final TextView subscribeSum;
        private final TextView answerSum;

        public ViewHolder(@NonNull View view) {
            super(view);
            ranking = view.findViewById(R.id.hli_ranking);
            title = view.findViewById(R.id.hli_title);
            hotPoint = view.findViewById(R.id.hli_hot_point);
            subscribeSum = view.findViewById(R.id.hli_subscribe_sum);
            answerSum = view.findViewById(R.id.hli_answer_sum);
        }
    }
}
