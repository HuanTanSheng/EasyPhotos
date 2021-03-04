package com.huantansheng.easyphotos.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.huantansheng.easyphotos.R;

public class LoadingDialog extends Dialog {

    private LoadingDialog(@NonNull Context context) {
        super(context);
        Window window = getWindow();
        if (null != window) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public static LoadingDialog get(Context context) {
        LoadingDialog loading = new LoadingDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading_easy_photos, null);
        loading.setContentView(view);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        return loading;
    }

    @Override
    public void show() {
        Window window = getWindow();
        if (null != window) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            window.setBackgroundDrawableResource(R.color.transparent_easy_photos);
            window.setGravity(Gravity.CENTER);
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(attributes);
        }
        super.show();
    }

}

