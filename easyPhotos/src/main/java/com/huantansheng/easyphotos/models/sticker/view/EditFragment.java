package com.huantansheng.easyphotos.models.sticker.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huantansheng.easyphotos.R;

/**
 * 文字贴纸，编辑界面
 * Created by huan on 2017/12/13.
 */

public class EditFragment extends DialogFragment implements View.OnClickListener {

    private TextView tvSample;
    private EditText et;
    private SeekBar seekBar;

    private TextSticker textSticker = null;

    private InputMethodManager inputMethodManager;

    public static EditFragment show(FragmentManager fm, TextSticker sticker) {
        EditFragment editFragment = new EditFragment();
        editFragment.textSticker = sticker;
        editFragment.show(fm, "edit");
        return editFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_text_sticker_easy_photos, container);

        tvSample = (TextView) rootView.findViewById(R.id.tv_sample);
        et = (EditText) rootView.findViewById(R.id.et);
        seekBar = (SeekBar) rootView.findViewById(R.id.m_seek_bar);

        l(rootView, R.id.iv_red, R.id.iv_orange, R.id.iv_yellow, R.id.iv_green, R.id.iv_cyan, R.id.iv_blue, R.id.iv_purple, R.id.iv_black, R.id.iv_gray, R.id.iv_white, R.id.tv_done, R.id.iv_clear);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    setTextAlpha(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvSample.setText(editable.toString());
                if (null != textSticker) {
                    textSticker.resetText(editable.toString());
                }
            }
        });
        return rootView;
    }

    private void l(View view, @IdRes int... resIds) {
        for (int resId : resIds) {
            view.findViewById(resId).setOnClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bindingSticker();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Window dialogWindow = getDialog().getWindow();
        if (null != dialogWindow) {
            WindowManager.LayoutParams attrs = dialogWindow.getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            dialogWindow.setAttributes(attrs);
            dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
        }

        super.onActivityCreated(savedInstanceState);

        if (null != dialogWindow) {
            dialogWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }


    public void bindingSticker() {
        String text = textSticker.getText();
        tvSample.setText(text);
        et.setText(text);
        et.setSelection(text.length());
        int alpha = textSticker.getTextAlpha();
        float alphaF = (float) alpha / (float) 255;
        seekBar.setProgress(alpha);
        tvSample.setTextColor(textSticker.getTextColor());
        tvSample.setAlpha(alphaF);
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != inputMethodManager) {
            inputMethodManager.showSoftInput(et, 0);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_red == id) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_sticker_red_easy_photos));

        } else if (R.id.iv_orange == id) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_sticker_orange_easy_photos));

        } else if (R.id.iv_yellow == id) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_sticker_yellow_easy_photos));

        } else if (R.id.iv_green == id) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_sticker_green_easy_photos));

        } else if (R.id.iv_cyan == id) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_sticker_cyan_easy_photos));

        } else if (R.id.iv_blue == id) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_sticker_blue_easy_photos));

        } else if (R.id.iv_purple == id) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_sticker_purple_easy_photos));

        } else if (R.id.iv_black == id) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_sticker_black_easy_photos));

        } else if (R.id.iv_gray == id) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_sticker_gray_easy_photos));

        } else if (R.id.iv_white == id) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_sticker_white_easy_photos));

        } else if (R.id.tv_done == id) {
            dismiss();
        } else if (R.id.iv_clear == id) {
            et.setText(null);
        }

    }

    private void setTextColor(int color) {
        tvSample.setTextColor(color);
        textSticker.setTextColor(color);
    }

    private void setTextAlpha(int alpha) {
        tvSample.setAlpha((float) alpha / (float) 225);
        textSticker.setTextAlpha(alpha);
    }
}
