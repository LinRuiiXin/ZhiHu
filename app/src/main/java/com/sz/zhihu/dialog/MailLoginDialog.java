package com.sz.zhihu.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.sz.zhihu.MainActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.ValidateUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MailLoginDialog extends Dialog {

    public static String mail = "";

    public MailLoginDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

}
