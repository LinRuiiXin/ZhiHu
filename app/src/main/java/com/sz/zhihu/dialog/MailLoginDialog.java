package com.sz.zhihu.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MailLoginDialog extends Dialog {
    public MailLoginDialog(@NonNull Context context) {
        super(context);
    }

    public MailLoginDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MailLoginDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
