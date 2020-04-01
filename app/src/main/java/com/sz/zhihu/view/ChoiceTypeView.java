package com.sz.zhihu.view;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sz.zhihu.AskQuestionActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.adapter.QuestionTypeAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.po.Classify;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChoiceTypeView {
    private static View view = null;
    private ChoiceTypeView(){}
    public static View getView(Activity activity){
        if(view == null){
            synchronized (ChoiceTypeView.class){
                if(view == null){
                    String severLocation = activity.getResources().getString(R.string.server_location);
                    Gson gson = new Gson();
                    view = View.inflate(activity, R.layout.dialog_choice_type,null);
                    EditText search = view.findViewById(R.id.dialog_ct_search);
                    RecyclerView recyclerView = view.findViewById(R.id.dialog_ct_recyclerView);
                    TextView choiceNum = view.findViewById(R.id.dialog_ct_choice_num);
                    choiceNum.setText("已选:"+ AskQuestionActivity.types.size());
                    QuestionTypeAdapter adapter = new QuestionTypeAdapter(activity,search,size->{
                        choiceNum.setText("已选:"+size);
                    });
                    List<Classify> classifies = new ArrayList<>();
                    adapter.setData(classifies);
                    final GridLayoutManager gridLayoutManager = new GridLayoutManager(activity,2);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(adapter);
                    search.addTextChangedListener(new CustomEditTextListener() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if(!StringUtils.isEmpty(s.toString())){
                                String url = severLocation + "/Classify/" + s.toString().trim() + "/"+ 0;
                                RequestUtils.sendSimpleRequest(url, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        activity.runOnUiThread(()-> Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
                                    }

                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                                        activity.runOnUiThread(()->{
                                            if(simpleDto.isSuccess()){
                                                classifies.clear();
                                                List list = gson.fromJson(simpleDto.getObject().toString(), List.class);
                                                list.forEach(o->{
                                                    classifies.add(gson.fromJson(o.toString(),Classify.class));
                                                });
                                                adapter.setData(classifies);
                                                adapter.notifyDataSetChanged();
                                            }else{

                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
        return view;
    }
}
