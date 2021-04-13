package com.zhidao.logcat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.xtoast.XToast;
import com.mogo.commonnet.service.LogServicePaths;
import com.mogo.logcore.logdata.model.GetDataModel;
import com.mogo.logcore.logdata.net.LogNetManage;
import com.zhidao.logcat.adapter.LogcatAdapter;
import com.zhidao.logcat.dialog.DialogSaveLog;
import com.zhidao.logcat.manager.LogcatConfig;
import com.zhidao.logcat.manager.LogcatInfo;
import com.zhidao.logcat.manager.LogcatManager;
import com.zhidao.logcat.ui.ChooseWindow;
import com.zhidao.logcat.ui.FloatingWindow;
import com.zhidao.logcat.utils.DialogUtils;
import com.zhidao.logcat.utils.KeyboardUtil;
import com.zhidao.logcat.utils.LogcatViewer;
import com.zhidao.logcommon.utils.logger.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@Route(path = LogServicePaths.PATH_MAINACTIVITAY)
public class MainActivity extends AppCompatActivity
        implements TextWatcher, View.OnLongClickListener, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, LogcatManager.Listener,
        AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener{

//    public static final String PATH = "/Log/MainActivity";

    // log等级
    private final static String[] ARRAY_LOG_LEVEL = {"Verbose", "Debug", "Info", "Warn", "Error"};
    // log路径
    private final static File LOG_DIRECTORY = new File(Environment.getExternalStorageDirectory(), "Logcat");
    // log过滤
    private final static String LOGCAT_TAG_FILTER_FILE = "logcat_tag_filter.txt";

    private final List<LogcatInfo> mLogData = new ArrayList<>();

    private FloatingWindow floatingWindow;

    private boolean isConfirm = false;

    private boolean isLogcatViewerShowing = false;

    private CheckBox mSwitchView;
    private View mSaveView;
    private TextView mLevelView;
    private EditText mSearchView;
    private View mEmptyView;
    private View mCleanView;
    private View mCloseView;
    private ListView mListView;
    private View view;

    private View mDownView;

    private LogcatAdapter mAdapter;

    private String mLogLevel = "V";

    /** Tag 过滤规则 */
    private final List<String> mTagFilter = new ArrayList<>();

    private KeyboardUtil keyboardUtil;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logcat_window_logcat);

        // 设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.logcat_window_logcat);

        mSwitchView = findViewById(R.id.iv_log_switch);
        mSaveView = findViewById(R.id.iv_log_save);
        mLevelView = findViewById(R.id.tv_log_level);
        mSearchView = findViewById(R.id.et_log_search);
        mEmptyView = findViewById(R.id.iv_log_empty);
        mCleanView = findViewById(R.id.iv_log_clean);
        mCloseView = findViewById(R.id.iv_log_close);
        mListView = findViewById(R.id.lv_log_list);
        mDownView = findViewById(R.id.ib_log_down);

        mAdapter = new LogcatAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mSwitchView.setOnCheckedChangeListener(this);
        mSearchView.addTextChangedListener(this);

        mSearchView.setText(LogcatConfig.getLogcatText());
        setLogLevel(LogcatConfig.getLogcatLevel());

        mSaveView.setOnClickListener(this);
        mLevelView.setOnClickListener(this);
        mEmptyView.setOnClickListener(this);
        mCleanView.setOnClickListener(this);
        mCloseView.setOnClickListener(this);
        mDownView.setOnClickListener(this);

        mSaveView.setOnLongClickListener(this);
        mSwitchView.setOnLongClickListener(this);
        mLevelView.setOnLongClickListener(this);
        mCleanView.setOnLongClickListener(this);
        mCloseView.setOnLongClickListener(this);

        // 获取Activity当前view
        view = getWindow().getDecorView();

        //悬浮窗权限判断
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                DialogUtils.setFloatWindowDialog(this);
            }
        }

        // 开始捕获
        LogcatManager.start(this);

        mListView.postDelayed(() -> {
            // 将第position个item显示在listView的最上面一项
            mListView.setSelection(mAdapter.getCount() - 1);
        }, 1000);

        if (!LOG_DIRECTORY.isDirectory()) {
            LOG_DIRECTORY.delete();
        }
        if (!LOG_DIRECTORY.exists()) {
            LOG_DIRECTORY.mkdirs();
        }
        initFilter();

        GetDataModel getDataModel = new GetDataModel();

//        LogNetManage.getInstance().onTestGetRequest(getDataModel);

        LogNetManage.getInstance().onTestGetRequestWith1();
    }

    @Override
    public void onReceiveLog(LogcatInfo info) {
        // 这个 Tag 必须不在过滤列表中，并且这个日志是当前应用打印的
//        if (Integer.parseInt(info.getPid()) == android.os.Process.myPid()) {
//            if (!mTagFilter.contains(info.getTag())) {
//                mListView.post(new LogRunnable(info));
//            }
//        }

        mListView.post(new LogRunnable(info));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.onItemClick(position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new ChooseWindow(this)
                .setList("复制日志", "分享日志", "删除日志", "屏蔽日志")
                .setListener(location -> {
                    switch (location) {
                        case 0:
                            ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            if (manager != null) {
                                manager.setPrimaryClip(ClipData.newPlainText("log", mAdapter.getItem(position).getLog()));
                                toast("日志复制成功");
                            } else {
                                toast("日志复制失败");
                            }
                            break;
                        case 1:
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, mAdapter.getItem(position).getLog());
                            startActivity(Intent.createChooser(intent, "分享日志"));
                            break;
                        case 2:
                            mLogData.remove(mAdapter.getItem(position));
                            mAdapter.removeItem(position);
                            break;
                        case 3:
                            XXPermissions.with(MainActivity.this)
                                    .permission(Permission.Group.STORAGE)
                                    .request(new OnPermission() {
                                        @Override
                                        public void hasPermission(List<String> granted, boolean isAll) {
                                            addFilter(mAdapter.getItem(position).getTag());
                                        }

                                        @Override
                                        public void noPermission(List<String> denied, boolean quick) {
                                            if (quick) {
                                                XXPermissions.startPermissionActivity(MainActivity.this);
                                                toast("请授予存储权限之后再操作");
                                            }
                                        }
                                    });
                            break;
                        default:
                            break;
                    }
                })
                .show();
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == mSwitchView) {
            toast("日志捕获开关");
        } else if (v == mSaveView) {
            toast("保存日志");
        }else if (v == mLevelView) {
            toast("日志等级过滤");
        } else if (v == mCleanView) {
            toast("清空日志");
        } else if (v == mCloseView) {
            toast("关闭显示");
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == mSaveView) {
            DialogSaveLog mDialogSaveLog = new DialogSaveLog(MainActivity.this);
            mDialogSaveLog.setOnCommitListener((logName) -> {

                XXPermissions.with(this)
                        .permission(Permission.Group.STORAGE)
                        .request(new OnPermission() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                writeLogCat(MainActivity.this, logName);
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                if (quick) {
                                    XXPermissions.startPermissionActivity(MainActivity.this);
                                    toast("请授予存储权限之后再操作");
                                }
                            }
                        });

            });

            if (!mDialogSaveLog.isShowing()) {
                mDialogSaveLog.show();
            }
        } else if (v == mLevelView) {
            new ChooseWindow(this)
                    .setList(ARRAY_LOG_LEVEL)
                    .setListener(position -> {
                        switch (position) {
                            case 0:
                                setLogLevel("V");
                                break;
                            case 1:
                                setLogLevel("D");
                                break;
                            case 2:
                                setLogLevel("I");
                                break;
                            case 3:
                                setLogLevel("W");
                                break;
                            case 4:
                                setLogLevel("E");
                                break;
                            default:
                                break;
                        }
                    })
                    .show();
        } else if (v == mEmptyView) {
            mSearchView.setText("");
        }else if (v == mCleanView) {
            LogcatManager.clear();
            mAdapter.clearData();
        } else if (v == mCloseView) {
            onBackPressed();
        } else if (v == mDownView) {
            // 滚动到列表最底部
            mListView.smoothScrollToPosition(mAdapter.getCount() - 1);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            toast("日志捕捉已暂停");
            LogcatManager.pause();
        } else {
            toast("日志捕捉已开启");
            LogcatManager.resume();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = s.toString().trim();
        LogcatConfig.setLogcatText(keyword);
        mAdapter.setKeyword(keyword);
        mAdapter.clearData();
        for (LogcatInfo info : mLogData) {
            if ("V".equals(mLogLevel) || info.getLevel().equals(mLogLevel)) {
//                if (!"".equals(keyword)) {
//                    if (info.getLog().contains(keyword) || info.getTag().contains(keyword)) {
//                        mAdapter.addItem(info);
//                    }
//                } else {
//                    mAdapter.addItem(info);
                mAdapter.addItem(info);
//                }
            }
        }
        mListView.setSelection(mAdapter.getCount() - 1);
        mEmptyView.setVisibility("".equals(keyword) ? View.GONE : View.VISIBLE);
    }

    private void setLogLevel(String level) {
        if (!level.equals(mLogLevel)) {
            mLogLevel = level;
            LogcatConfig.setLogcatLevel(level);
            afterTextChanged(mSearchView.getText());
            switch (level) {
                case "V":
                    mLevelView.setText(ARRAY_LOG_LEVEL[0]);
                    break;
                case "D":
                    mLevelView.setText(ARRAY_LOG_LEVEL[1]);
                    break;
                case "I":
                    mLevelView.setText(ARRAY_LOG_LEVEL[2]);
                    break;
                case "W":
                    mLevelView.setText(ARRAY_LOG_LEVEL[3]);
                    break;
                case "E":
                    mLevelView.setText(ARRAY_LOG_LEVEL[4]);
                    break;
                default:
                    break;
            }
        }
    }

    private class LogRunnable implements Runnable {

        private final LogcatInfo info;

        private LogRunnable(LogcatInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            if (mLogData.size() > 0) {
                LogcatInfo lastInfo = mLogData.get(mLogData.size() - 1);
                if (info.getLevel().equals(lastInfo.getLevel()) &&
                        info.getTag().equals(lastInfo.getTag())) {

                    lastInfo.addLog(info.getLog());
                    mAdapter.notifyDataSetChanged();
                    return;
                }
            }

            mLogData.add(info);

            String content = mSearchView.getText().toString();
            if ("".equals(content) && "V".equals(mLogLevel)) {
                mAdapter.addItem(info);
            } else {
                if (info.getLevel().equals(mLogLevel)) {
                    if (info.getLog().contains(content) || info.getTag().contains(content)) {
                        mAdapter.addItem(info);
                    }
                }
            }
        }
    }

    /**
     * 初始化 Tag 过滤器
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initFilter() {
        File file = new File(LOG_DIRECTORY, LOGCAT_TAG_FILTER_FILE);
        if (file.exists() && file.isFile() && XXPermissions.hasPermission(this, Permission.Group.STORAGE)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? StandardCharsets.UTF_8 : StandardCharsets.UTF_8))) {
                String tag;
                while ((tag = reader.readLine()) != null) {
                    mTagFilter.add(tag);
                }
            } catch (IOException e) {
                toast("读取屏蔽配置失败");
            }
        }
    }

    /**
     * 添加过滤的 TAG
     */
    private void addFilter(String tag) {
        mTagFilter.add(tag);
        BufferedWriter writer = null;
        try {
            File file = new File(LOG_DIRECTORY, LOGCAT_TAG_FILTER_FILE);
            if (!file.isFile()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false),
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? StandardCharsets.UTF_8 : Charset.forName("UTF-8")));
            for (String temp : mTagFilter) {
                writer.write(temp + "\r\n");
            }
            writer.flush();

            // 从列表中删除关于这个 Tag 的日志
            ArrayList<LogcatInfo> removeData = new ArrayList<>();
            List<LogcatInfo> allData = mAdapter.getData();
            for (LogcatInfo info : allData) {
                if (info.getTag().equals(tag)) {
                    removeData.add(info);
                }
            }

            for (LogcatInfo info : removeData) {
                allData.remove(info);
                mAdapter.notifyDataSetChanged();
            }

            toast("添加屏蔽成功：" + file.getPath());
        } catch (IOException e) {
            toast("添加屏蔽失败");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * 保存日志到本地
     */
    private void saveLogToFile(String LogName) {
        BufferedWriter writer = null;
        try {

//            File directory = new File(Environment.getExternalStorageDirectory(), "Logcat" + File.separator + getPackageName());
            File directory = new File(Environment.getExternalStorageDirectory(), "Logcat");
            if (!directory.isDirectory()) {
                directory.delete();
            }
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, new SimpleDateFormat("yyyyMMdd_kkmmss", Locale.getDefault()).format(new Date()) + LogName + ".txt");
            if (!file.isFile()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), Charset.forName("UTF-8")));
            List<LogcatInfo> data = mAdapter.getData();
            for (LogcatInfo info : data) {
                writer.write(info.toString().replace("\n", "\r\n") + "\r\n\r\n");
            }
            writer.flush();

            toast("保存成功：" + file.getPath());
        } catch (IOException e) {
            toast("保存失败");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {}
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void writeLogCat(Context context, String logcatName) {
        BufferedWriter writer = null;
        try {
            Process process = Runtime.getRuntime().exec("logcat -d -v time");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                log.append("\n");
            }
//            new ProcessBuilder().command("logcat", "-c").redirectErrorStream(true).start();
            File file = logFile(context, logcatName);
            if (!file.isFile()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), Charset.forName("UTF-8")));
            writer.write(log.toString().replace("\n", "\r\n") + "\r\n\r\n");
            writer.flush();
            toast("保存成功：" + file.getPath());
        } catch (IOException e) {
//            e.printStackTrace();
            toast("保存失败");
        } catch (OutOfMemoryError e) {
//            e.printStackTrace();
            toast("保存失败");
        } catch (Exception e) {
//            e.printStackTrace();
            toast("保存失败");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {}
            }
        }
    }

    private static File logFileDir(Context context) {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + File.separator + "Meow"
                + File.separator + context.getApplicationContext().getPackageName());
        if (!dir.isDirectory()) {
            dir.delete();
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
        }
        return dir;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static File logFile(Context context, String logcatName) {
        File dir = logFileDir(context);
        return new File(dir, new SimpleDateFormat("yyyyMMdd_kkmmss", Locale.getDefault()).format(new Date()) + logcatName + ".txt");
    }

    /**
     * 吐司提示
     */
    private void toast(CharSequence text) {
        new XToast(this)
                .setView(R.layout.logcat_window_toast)
                .setDuration(3000)
                .setGravity(Gravity.CENTER)
                .setAnimStyle(android.R.style.Animation_Toast)
                .setText(android.R.id.message, text)
                .show();
    }

    @Override
    public void onBackPressed() {
        // 移动到上一个任务栈
        moveTaskToBack(false);
    }

    @Override
    protected void onStart() {
//        Log.d("MainActivity","activity: onStart");
        Logger.d("MainActivity", "onStart");
        if (isLogcatViewerShowing) {
            LogcatViewer.closeLogcatLoggerView(MainActivity.this);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("MainActivity","activity: onResume");
//        LogcatManager.resume();

        boolean isFloatingWindowStart = false;
        if (isFloatingWindowStart) {
            Log.d("MainActivity", "floatWindow:" + floatingWindow);
            floatingWindow.cancel();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("MainActivity","activity: onPause");
//        LogcatManager.pause();
//        LogcatManager.resume();
//        floatingWindow = new FloatingWindow(getApplication());
//        floatingWindow.show();
//        isFloatingWindowStart = true;
        LogcatViewer.showLogcatLoggerView(MainActivity.this);
        isLogcatViewerShowing = true;
        KeyboardUtil.hideKeyboard(view);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity","activity: onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("MainActivity","activity: onStop");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity","activity: onDestroy");
        LogcatManager.destroy();
        super.onDestroy();
    }
}