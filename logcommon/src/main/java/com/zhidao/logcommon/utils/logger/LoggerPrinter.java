package com.zhidao.logcommon.utils.logger;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author: xuanlonghua
 * @date: 2021/1/31
 * @version: 1.0.0
 * @description:
 */

final class LoggerPrinter implements Printer {
    private static final String TAG = "LoggerPrinter";

    private static final int CHUNK_SIZE = 4000;
    private static final int JSON_INDENT = 4;
    private static final int MIN_STACK_OFFSET = 3;
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = "╔════════════════════════════════════════════════════════════════════════════════════════";
    private static final String BOTTOM_BORDER = "╚════════════════════════════════════════════════════════════════════════════════════════";
    private static final String MIDDLE_BORDER = "╟────────────────────────────────────────────────────────────────────────────────────────";

    private final Settings mSettings = new Settings();

    LoggerPrinter() {
    }

    public Settings init(LogLevel logLevel) {
        return mSettings.setLogLevel(logLevel);
    }

    public Settings getSettings() {
        return mSettings;
    }

    public void d( String tag, String message, Object... args) {
        this.log(tag, LogLevel.DEBUG, message, args);
    }

    public void e( String tag, String message, Object... args) {
        this.e(tag, null, message, args);
    }

    public void e( String tag, Throwable throwable, String message, Object... args) {
        if (throwable != null && message != null) {
            message = message + " : " + Log.getStackTraceString( throwable);
        }

        if (throwable != null && message == null) {
            message = throwable.toString();
        }

        if (message == null) {
            message = "No message/exception is set";
        }

        this.log(tag, LogLevel.ERROR, message, args);
    }

    public void w( String tag, String message, Object... args) {
        this.log(tag, LogLevel.WARN, message, args);
    }

    public void i( String tag, String message, Object... args) {
        this.log(tag, LogLevel.INFO, message, args);
    }

    public void v( String tag, String message, Object... args) {
        this.log(tag, LogLevel.VERBOSE, message, args);
    }

    public void json( String tag, String json) {
        if ( TextUtils.isEmpty(json)) {
            this.d(tag, "Empty/Null json content");
        } else {
            try {
                String message;
                if (json.startsWith("{")) {
                    JSONObject e1 = new JSONObject(json);
                    message = e1.toString(4);
                    this.d(tag, message);
                    return;
                }

                if (json.startsWith("[")) {
                    JSONArray e = new JSONArray(json);
                    message = e.toString(4);
                    this.d(tag, message);
                }
            } catch ( JSONException var4) {
                this.e(tag, var4.getCause().getMessage() + "\n" + json);
            }

        }
    }

    public void xml( String tag, String xml) {
        if ( TextUtils.isEmpty(xml)) {
            this.d(tag, "Empty/Null xml content");
        } else {
            try {
                StreamSource e = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty("indent", "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(e, xmlOutput);
                this.d(tag, xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
            } catch ( TransformerException var5) {
                this.e(tag, var5.getCause().getMessage() + "\n" + xml);
            }

        }
    }

    public void normalLog( String tag, String message) {
        if (!TextUtils.isEmpty(message)) {
            this.logChunk(LogLevel.DEBUG, tag, message);
        }
    }

    private synchronized void log( String tag, LogLevel logLevel, String msg, Object... args) {
        String message = this.createMessage(msg, args);
        int methodCount = this.getMethodCount();
        this.logTopBorder(logLevel, tag);
        this.logHeaderContent(logLevel, tag, methodCount);
        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= 4000) {
            if (methodCount > 0) {
                this.logDivider(logLevel, tag);
            }

            this.logContent(logLevel, tag, message);
            this.logBottomBorder(logLevel, tag);
        } else {
            if (methodCount > 0) {
                this.logDivider(logLevel, tag);
            }

            for (int i = 0; i < length; i += 4000) {
                int count = Math.min(length - i, 4000);
                this.logContent(logLevel, tag, new String(bytes, i, count));
            }

            this.logBottomBorder(logLevel, tag);
        }
    }

    private void logTopBorder(LogLevel logLevel, String tag) {
        this.logChunk(logLevel, tag, "╔════════════════════════════════════════════════════════════════════════════════════════");
    }

    private void logHeaderContent( LogLevel logLevel, String tag, int methodCount) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (mSettings.isShowThreadInfo()) {
            this.logChunk(logLevel, tag, "║ Thread: " + Thread.currentThread().getName());
            this.logDivider(logLevel, tag);
        }

        String level = "";
        int stackOffset = this.getStackOffset(trace) + mSettings.getMethodOffset();
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for (int i = methodCount; i > 0; --i) {
            int stackIndex = i + stackOffset;
            if (stackIndex < trace.length) {
                StringBuilder builder = new StringBuilder();
                builder.append("║ ").append(level).append(this.getSimpleClassName(trace[stackIndex].getClassName())).append(".").append(trace[stackIndex].getMethodName()).append(" ").append(" (").append(trace[stackIndex].getFileName()).append(":").append(trace[stackIndex].getLineNumber()).append(")");
                level = level + "   ";
                this.logChunk(logLevel, tag, builder.toString());
            }
        }

    }

    private void logBottomBorder(LogLevel logLevel, String tag) {
        this.logChunk(logLevel, tag, "╚════════════════════════════════════════════════════════════════════════════════════════");
    }

    private void logDivider(LogLevel logLevel, String tag) {
        this.logChunk(logLevel, tag, "╟────────────────────────────────────────────────────────────────────────────────────────");
    }

    private void logContent( LogLevel logLevel, String tag, String chunk) {
        String[] lines = chunk.split( System.getProperty("line.separator"));

        for ( String line : lines) {
            this.logChunk(logLevel, tag, "║ " + line);
        }
    }

    private void logChunk( LogLevel logLevel, String tag, String chunk) {
        String finalTag = this.checkTag(tag);
        switch (logLevel) {
            case VERBOSE:
                Log.v(finalTag, chunk);
                break;
            case INFO:
                Log.i(finalTag, chunk);
                break;
            case DEBUG:
                Log.d(finalTag, chunk);
                break;
            case WARN:
                Log.w(finalTag, chunk);
                break;
            case ERROR:
                Log.e(finalTag, chunk);
                break;
            case OFF:
                break;
        }

    }

    private String getSimpleClassName( String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private String checkTag( String tag) {
        return TextUtils.isEmpty(tag) ? TAG : tag;
    }

    private String createMessage( String message, Object... args) {
        return (args == null || args.length == 0) ? message : String.format(message, args);
    }

    private int getMethodCount() {
        return mSettings.getMethodCount();
    }

    private int getStackOffset( StackTraceElement[] trace) {
        for (int i = 3; i < trace.length; ++i) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LoggerPrinter.class.getName()) && !name.equals(Logger.class.getName())) {
                --i;
                return i;
            }
        }

        return -1;
    }
}
