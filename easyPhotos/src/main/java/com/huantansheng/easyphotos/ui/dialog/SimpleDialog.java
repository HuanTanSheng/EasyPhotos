package com.huantansheng.easyphotos.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Window;

/**
 * 简单的dialog，利用系统样式
 * Created by huan on 2017/9/5.
 */

public class SimpleDialog extends DialogFragment {
    private static final String KEY_CANCELABLE = "keyOfCancelable";
    private static final String KEY_RES_ID_TITLE = "keyOfTitleResId";
    private static final String KEY_RES_ID_MSG = "keyOfMsgResId";
    private static final String KEY_RES_ID_POSITIVE = "keyOfPositiveResId";
    private static final String KEY_RES_ID_NEUTRAL = "keyOfNeutralResId";
    private static final String KEY_RES_ID_NEGATIVE = "keyOfNegativeResId";
    private String title, message, positive, neutral, negative;

    private boolean cancelable;

    public static SimpleDialog newInstance(boolean cancelable, String titleText, String messageText, String positiveText, String neutralText, String negativeText) {
        Bundle args = new Bundle();
        SimpleDialog instance = new SimpleDialog();
        args.putBoolean(KEY_CANCELABLE, cancelable);
        args.putString(KEY_RES_ID_TITLE, titleText);
        args.putString(KEY_RES_ID_MSG, messageText);
        args.putString(KEY_RES_ID_POSITIVE, positiveText);
        args.putString(KEY_RES_ID_NEUTRAL, neutralText);
        args.putString(KEY_RES_ID_NEGATIVE, negativeText);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cancelable = getArguments().getBoolean(KEY_CANCELABLE, true);
            title = getArguments().getString(KEY_RES_ID_TITLE);
            message = getArguments().getString(KEY_RES_ID_MSG);
            positive = getArguments().getString(KEY_RES_ID_POSITIVE);
            neutral = getArguments().getString(KEY_RES_ID_NEUTRAL);
            negative = getArguments().getString(KEY_RES_ID_NEGATIVE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (TextUtils.isEmpty(message))
            builder.setMessage(message);
        if (TextUtils.isEmpty(positive))
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((OnClickListener) getActivity()).onSimiplePositiveClick();
                }
            });
        if (TextUtils.isEmpty(neutral))
            builder.setNeutralButton(neutral, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((OnClickListener) getActivity()).onSimipleNeutralClick();
                }
            });
        if (TextUtils.isEmpty(negative))
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((OnClickListener) getActivity()).onSimipleNegativeClick();
                }
            });
        if (TextUtils.isEmpty(title))
            builder.setTitle(title);
        else
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        setCancelable(cancelable);
        return builder.create();
    }

    public interface OnClickListener {
        void onSimiplePositiveClick();

        void onSimipleNeutralClick();

        void onSimipleNegativeClick();
    }
}