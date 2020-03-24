package com.sz.zhihu.fragment.register;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.sz.zhihu.R;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.interfaces.MailCallBack;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;
import com.sz.zhihu.utils.ValidateUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MailFragment extends Fragment implements CustomFragmentFunction {
    private MailCallBack callBack;
    private View view;
    private EditText mail;
    private EditText code;
    private Button sendMail;
    private ImageView next;
    private Activity activity;
    //邮箱地址以最后一次点击发送验证码的邮箱地址为准
    private String mailNumber = "";
    private Gson gson;
    private String serverLocation;
    private int defaultColor;
    private Drawable enable;
    private Drawable disable;

    public MailFragment(MailCallBack callBack){
        this.callBack = callBack;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_register_mail,container,false);
            mail = view.findViewById(R.id.register_mail_mail);
            code = view.findViewById(R.id.register_mail_code);
            sendMail = view.findViewById(R.id.register_mail_sendMail);
            next = view.findViewById(R.id.register_mail_next);
            serverLocation = activity.getResources().getString(R.string.server_location);
            disable = activity.getResources().getDrawable(R.drawable.login_log_button_shape_disable);
            enable = activity.getResources().getDrawable(R.drawable.login_log_button_shape_enable);
            gson = new Gson();
            setListeners();

        }
        return view;
    }

    private void setListeners() {
        mail.addTextChangedListener(new CustomEditTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!ValidateUtil.validate(s.toString(),ValidateUtil.mailRegex)){
                    mail.setError("输入正确的邮箱格式");
                }
            }
        });
        sendMail.setOnClickListener((v)->{
            if(!ValidateUtil.validate(mail.getText().toString(),ValidateUtil.mailRegex)){
                Toast.makeText(activity,"邮箱格式不正确",Toast.LENGTH_SHORT).show();
            }else{
                this.mailNumber = mail.getText().toString();
                //请求服务器发送邮件
                String url = serverLocation+"/Register/SecurityCode/"+this.mailNumber;
                RequestUtils.sendSimpleRequest(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        activity.runOnUiThread(()-> Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //耗时任务不能在UI线程执行，所以先解析数据
                        SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                        //切换至UI线程
                        activity.runOnUiThread(()->{
                            if(simpleDto.isSuccess()){
                                sendMail.setEnabled(false);
                                sendMail.setBackground(disable);
                                sendMail.setText("60s");
                                CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        sendMail.setText((millisUntilFinished/1000)+"s");
                                    }

                                    @Override
                                    public void onFinish() {
                                        sendMail.setEnabled(true);
                                        sendMail.setBackground(enable);
                                        sendMail.setText("再次获取");
                                    }
                                };
                                countDownTimer.start();
                            }else{
                                Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
        next.setOnClickListener((v)->{
            String content = code.getText().toString().trim();
            if(!StringUtils.isEmpty(mailNumber)){
                if(!StringUtils.isEmpty(content)){
                    if(content.length() != 6){
                        Toast.makeText(activity,"验证码错误",Toast.LENGTH_SHORT).show();
                    }else{
                        String url = serverLocation + "/Register/SecurityCode/"+mailNumber+"/"+content;
                        //请求服务器校验验证码
                        RequestUtils.sendSimpleRequest(url, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                activity.runOnUiThread(()->Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                                activity.runOnUiThread(()->{
                                    if(simpleDto.isSuccess()){
                                        callBack.callBackMail(mailNumber);
                                    }else{
                                        Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }else{
                    Toast.makeText(activity,"验证码不能为空",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(activity,"请先获取验证码",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void refreshPage() {

    }
}
