package com.zhidao.logcat;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;

import com.hjq.xtoast.OnClickListener;
import com.hjq.xtoast.XToast;
import com.hjq.xtoast.draggable.SpringDraggable;

final class FloatingWindow extends XToast implements OnClickListener {

    FloatingWindow(Application application) {
        super(application);
        setView(R.layout.logcat_window_floating);
        // 设置动画样式
        setAnimStyle(android.R.style.Animation_Toast);
        // 设置成可拖拽的
        setDraggable(new SpringDraggable());
        setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        setOnClickListener(android.R.id.icon, this);
    }

    @Override
    public void onClick(XToast toast, View view) {
        startActivity(new Intent(getContext(), MainActivity.class));
    }
}