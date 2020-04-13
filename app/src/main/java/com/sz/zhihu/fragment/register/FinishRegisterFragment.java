package com.sz.zhihu.fragment.register;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.MainActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.RegisterActivity;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.ArrayUtils;
import com.sz.zhihu.utils.FileUtils;
import com.sz.zhihu.utils.PermissionUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.ValidateUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FinishRegisterFragment extends Fragment implements CustomFragmentFunction {
    private RegisterActivity activity;
    private View view;
    private RoundedImageView portrait;
    private EditText username;
    private EditText password;
    private EditText phone;
    private final int CODE_IMAGE = 1;
    private Button register;
    private String serverLocation;
    private Gson gson;
    private File portraitFile;
    private User newUser;
    public FinishRegisterFragment(){

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof RegisterActivity){
            this.activity = (RegisterActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_register_finish_register,container,false);
            portrait = view.findViewById(R.id.register_finish_register_portrait);
            username = view.findViewById(R.id.register_finish_register_username);
            password = view.findViewById(R.id.register_finish_register_password);
            phone = view.findViewById(R.id.register_finish_register_phone);
            register = view.findViewById(R.id.register_finish_register_button);
            serverLocation = activity.getResources().getString(R.string.server_location);
            gson = new Gson();
            setListeners();
        }
        return view;
    }

    private void setListeners() {
        portrait.setOnClickListener((v)->{
            if(PermissionUtils.registerFragmentPerMission(activity,this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                getBitMapFromPhotoAlbum();
            }
        });
        username.addTextChangedListener(new CustomEditTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!ValidateUtil.validate(s.toString(),ValidateUtil.userNameRegex)){
                    username.setError("中文，英文，数字或下划线");
                }
            }
        });
        password.addTextChangedListener(new CustomEditTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!ValidateUtil.validate(s.toString(),ValidateUtil.passwordRegex)){
                    password.setError("包含 数字和英文，长度6-20");
                }
            }
        });
        phone.addTextChangedListener(new CustomEditTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!ValidateUtil.validate(s.toString(),ValidateUtil.phoneRegex)){
                    phone.setError("输入正确格式的手机号");
                }
            }
        });
        register.setOnClickListener((v)->{
            String username = this.username.getText().toString();
            String password = this.password.getText().toString();
            String phone = this.phone.getText().toString();
            if(ValidateUtil.validate(username,ValidateUtil.userNameRegex)){
                if(ValidateUtil.validate(password,ValidateUtil.passwordRegex)){
                    if(ValidateUtil.validate(phone,ValidateUtil.phoneRegex)){
                        if (ValidateUtil.validate(activity.mail,ValidateUtil.mailRegex)) {
                            User user = new User();
                            user.setUserName(username);
                            user.setPassword(password);
                            user.setMail(activity.mail);
                            user.setPhone(phone);
                            String url = serverLocation + "/Register";
                            RequestUtils.sendRequestWithJson(user, url, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    activity.runOnUiThread(()->Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
                                }

                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                                    activity.runOnUiThread(()->{
                                        if(simpleDto.isSuccess()){
                                            String s = gson.toJson(simpleDto.getObject());
                                            newUser = gson.fromJson(s,User.class);
//                                            newUser = gson.fromJson(simpleDto.getObject().toString(),User.class);
                                            newUser.setUserId(newUser.getId());
                                            newUser.save();
                                            Toast.makeText(activity,"注册成功，正在为您上传头像...",Toast.LENGTH_SHORT).show();
                                            uploadPortrait(newUser);
                                        }else{
                                            if(simpleDto.getObject() != null){
                                                Map<String,String> failMsg = (Map<String, String>) simpleDto.getObject();
                                                Set<String> strings = failMsg.keySet();
                                                strings.forEach(s->Toast.makeText(activity,s,Toast.LENGTH_SHORT).show());
                                            }else{
                                                Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }else{
                            Toast.makeText(activity,"非法注册",Toast.LENGTH_SHORT).show();
                            activity.finish();
                        }
                    }else{
                        this.phone.setError("输入正确格式的手机号");
                    }
                }else{
                    this.password.setError("包含 数字和英文，长度6-20");
                }
            }else{
                this.username.setError("中文，英文，数字或下划线");
            }
        });
    }

    private void uploadPortrait(User user) {
        Long id = user.getUserId();
        String url = serverLocation + "/Upload/Portrait/" + id;
        RequestUtils.sendSingleFile(portraitFile, url, "portrait", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(()->Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto res = gson.fromJson(response.body().string(), SimpleDto.class);
                activity.runOnUiThread(()->{
                    if(!res.isSuccess()){
                        Toast.makeText(activity,res.getMsg(),Toast.LENGTH_SHORT).show();
                    }else{
                        String portraitFileName = res.getMsg();
                        user.setPortraitFileName(portraitFileName);
                        user.save();
                    }
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PermissionUtils.REQUEST_CODE){
            String[] permissionNotAgrees = ArrayUtils.getPermissionNotAgrees(permissions, grantResults);
            if(permissionNotAgrees.length == 0){
                getBitMapFromPhotoAlbum();
            }else{
                Toast.makeText(activity,"请先通过授权",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void getBitMapFromPhotoAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent,CODE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CODE_IMAGE && resultCode == -1){
            Uri uri = data.getData();
            File file = new File(FileUtils.getRealPathFromURI(activity, uri));
            if(file.length()>=5242880){
                Toast.makeText(activity,"头像不能超过5MB",Toast.LENGTH_SHORT).show();
            }else{
                portraitFile = file;
                portrait.setImageURI(uri);
            }
        }
    }

    @Override
    public void refreshPage() {

    }
}
