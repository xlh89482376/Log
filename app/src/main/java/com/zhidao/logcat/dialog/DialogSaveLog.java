package com.zhidao.logcat.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhidao.logcat.R;

public class DialogSaveLog extends Dialog {

    private EditText etSaveLog;

    private OnCommitListener mOnCommitListener;

    public DialogSaveLog(@NonNull Context context) {
        this(context, 0);
    }

    public DialogSaveLog(@NonNull Context context, int themeResId) {
        this(context, true, null);
    }

    public DialogSaveLog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // instantiate the dialog with the custom Theme
        assert inflater != null;
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.logcat_save_log, null);
        setContentView(dialogView);
        setTitle("请手动log文件名");

        etSaveLog = (EditText) findViewById(R.id.etSaveLog);

        Button btnSetCancel = (Button) findViewById(R.id.btnSetCancel);
        Button btnSetCommit = (Button) findViewById(R.id.btnSetCommit);

        btnSetCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSaveLog.this.dismiss();
            }
        });
        btnSetCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logName = etSaveLog.getText().toString();

                if (logName.isEmpty()) {
                    // 使用默认值
//                    logName = etSaveLog.getHint().toString();
//                    toast("请输入log名称");
                    Toast.makeText(context,"请输入log名称", Toast.LENGTH_LONG).show();
                }
                if (mOnCommitListener != null) {
                    mOnCommitListener.onCommit(logName);
                }

                DialogSaveLog.this.dismiss();
            }
        });
    }
    public void setOnCommitListener(OnCommitListener onCommitListener) {
        this.mOnCommitListener = onCommitListener;
    }

    public interface OnCommitListener {
        void onCommit(String setLatLng);
    }



}
