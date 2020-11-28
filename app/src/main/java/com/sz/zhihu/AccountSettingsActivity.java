package com.sz.zhihu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.sz.zhihu.databinding.ActivityAccountSettingsBinding;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;
import com.sz.zhihu.utils.ValidateUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class AccountSettingsActivity extends AbstractCustomActivity{

    private ActivityAccountSettingsBinding binding;
    private View logOutDialogView;
    private BottomSheetDialog logOutDialog;
    private View changePasswordDialogView;
    private BottomSheetDialog changePasswordDialog;
    private User user;
    private String serverLocation;
    private Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_account_settings);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        user = DBUtils.queryUserHistory();
        serverLocation = getString(R.string.server_location);
        gson = new Gson();
        initActionBar();
        initLogOutDialog();
        initChangePasswordDialog();
        initComponent();
    }

    private void initComponent() {
        binding.assChangePassword.setOnClickListener(view -> {
            changePasswordDialog.show();
        });
        binding.assLogOut.setOnClickListener(view -> {
            logOutDialog.show();
        });
    }

    private void initActionBar() {
        binding.aasBar.setTitle("账号与安全");
        setSupportActionBar(binding.aasBar);
        binding.aasBar.setNavigationOnClickListener(v->finish());
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initLogOutDialog() {
        logOutDialogView = View.inflate(this, R.layout.account_logout_dialog, null);
        TextView logout = logOutDialogView.findViewById(R.id.ald_logout);
        logout.setOnClickListener(v->{
            clearUserAndJump();
        });
        logOutDialog = newDialog(logOutDialogView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initChangePasswordDialog() {
        changePasswordDialogView = View.inflate(this, R.layout.change_password_dialog, null);
        EditText password = changePasswordDialogView.findViewById(R.id.cpd_password);
        EditText newPassword = changePasswordDialogView.findViewById(R.id.cpd_new_password);
        TextView submit = changePasswordDialogView.findViewById(R.id.cpd_submit);
        submit.setOnClickListener(v -> {
            String passwordStr = password.getText().toString();
            String newPasswordStr = newPassword.getText().toString();
            if(!StringUtils.isEmpty(passwordStr) && ValidateUtil.validatePassword(passwordStr)){
                if(!StringUtils.isEmpty(newPasswordStr) && ValidateUtil.validatePassword(newPasswordStr)){
                    submit.setClickable(false);
                    String url = serverLocation + "/UserService/User/ChangePassword";
                    Map<String,String> params = new HashMap<>(3);
                    params.put("userId",String.valueOf(user.getUserId()));
                    params.put("password",passwordStr);
                    params.put("newPassword",newPasswordStr);
                    RequestUtils.postWithParams(url, params, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(()-> {
                                Toast.makeText(AccountSettingsActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                                submit.setClickable(true);
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                            runOnUiThread(()->{
                                Toast.makeText(AccountSettingsActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                if(simpleDto.isSuccess()){
                                    clearUserAndJump();
                                }else{
                                    submit.setClickable(true);
                                }
                            });
                        }
                    });

                }else{
                    newPassword.setError("为空或数据格式错误(密码至少包含 数字和英文，长度6-20)");
                }
            }else{
                password.setError("为空或数据格式错误(密码至少包含 数字和英文，长度6-20)");
            }
        });
        changePasswordDialog = newDialog(changePasswordDialogView);
    }

    private void clearUserAndJump() {
        DBUtils.clearUserHistory();
        Intent intent = new Intent(AccountSettingsActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    private BottomSheetDialog newDialog(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return bottomSheetDialog;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        logOutDialog.dismiss();
        changePasswordDialog.dismiss();
        ((ViewGroup)logOutDialogView.getParent()).removeView(logOutDialogView);
        ((ViewGroup)changePasswordDialogView.getParent()).removeView(changePasswordDialogView);
    }


}