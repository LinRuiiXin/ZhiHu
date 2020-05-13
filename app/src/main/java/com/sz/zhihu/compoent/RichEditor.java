package com.sz.zhihu.compoent;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.rex.editor.view.RichEditorNew;

public class RichEditor extends RichEditorNew {
    public RichEditor(Context context) {
        super(context);
    }

    public RichEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void insertVideo(String videoUrl, String custom) {
        this.focusEditor();
        if (TextUtils.isEmpty(custom)) {
            custom = "controls=\"controls\" initial-time=\"0.01\" height=\"300\"  style=\"margin-top:10px;max-width:100%; width:100%;\"";
        }

        System.out.println("videoUrl = [" + videoUrl + "], custom = [" + custom + "]");
        this.exec("javascript:RE.prepareInsert();");
        this.exec("javascript:RE.insertVideo('" + videoUrl + "', '" + custom + "');");
    }

}
