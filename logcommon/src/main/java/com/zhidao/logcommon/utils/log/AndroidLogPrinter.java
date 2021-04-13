package com.zhidao.logcommon.utils.log;

import android.util.Log;

import com.zhidao.logcommon.utils.FileUtils;
import com.zhidao.logcommon.utils.ThreadPoolManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * @author Xuanlh
 * @desc Android打印器
 * @date 2021/2/7
 */

public class AndroidLogPrinter implements IPrinter {

    //是否打印log
    private boolean isPrinterLog = true;

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public void v(String tag, String msg) {
        if (!isPrinterLog()) {
            return;
        }
        Log.v(tag, msg);
    }


    @Override
    public void d(String tag, String msg) {
        if (!isPrinterLog()) {
            return;
        }
        Log.d(tag, msg);
    }

    @Override
    public void dNoFilter(String tag, String msg) {
        Log.d(tag, msg);
    }


    @Override
    public void w(String tag, String msg) {
        if (!isPrinterLog()) {
            return;
        }
        Log.w(tag, msg);
    }


    @Override
    public void e(String tag, String msg) {
        if (!isPrinterLog()) {
            return;
        }
        Log.e(tag, msg);
    }

    @Override
    public void json(String tag, String msg) {
        if (!isPrinterLog()) {
            return;
        }
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }
        printLine(tag, true);
        message = LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.d(tag, "║" + line);
        }
        printLine(tag, false);
    }

    private void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═════════════════════════════════════");
        } else {
            Log.d(tag, "╚═════════════════════════════════════");
        }
    }

    @Override
    public void saveDisk(final IPrinterDiskConfig config, final String msg) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                //把日志保存到文件
                boolean isCreateFileSuccess = FileUtils.createOrExistsFile(config.getDiskDir() + File.separator + config.getDiskFileName());
                if (isCreateFileSuccess) {
                    FileUtils.writeFile(config.getDiskDir() + File.separator + config.getDiskFileName(), msg, true);
                }
            }
        });
    }

    @Override
    public boolean isPrinterLog() {
        return isPrinterLog;
    }

    @Override
    public void clear() {

    }

    @Override
    public void setPrinterLog(boolean printerLog) {
        isPrinterLog = printerLog;
    }
}
