package com.jiyouliang.log;

import android.text.TextUtils;
import android.util.Log;

/**
 * 日志工具
 */
public class Logger {

    private static final LoggerBuilder builder = LoggerBuilder.getInstance();

    public static void setDebug(boolean debug) {
        builder.setDebug(debug);
    }


    public static void setLogLevel(LogLevel level) {
        builder.setLogLevel(level);
    }


    public static void v(String tag, String msg) {
        builder.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        builder.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        builder.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        builder.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        builder.e(tag, msg);
    }

   /* public static void a(String tag, String msg) {
        if (ASSERT >= mCurrentLevel && mDebug) {
            Log.a(tag, msg);
        }
    }*/

    public enum LogLevel {
        VERBOSE(2), DEBUG(3), INFO(4), WARN(5), ERROR(6);

        LogLevel(int value) {
            this.value = value;
        }

        private int value;
    }


    static class LoggerBuilder {
        private static LoggerBuilder instance;

        private LoggerBuilder() {
        }

        private boolean mDebug = true;

        public static LoggerBuilder getInstance() {
            if (instance == null) {
                synchronized (LoggerBuilder.class) {
                    instance = new LoggerBuilder();
                }
            }
            return instance;
        }


        /**
         * Priority constant for the println method; use Log.v.
         */
        private static final int VERBOSE = 2;

        /**
         * Priority constant for the println method; use Log.d.
         */
        private static final int DEBUG = 3;

        /**
         * Priority constant for the println method; use Log.i.
         */
        private static final int INFO = 4;

        /**
         * Priority constant for the println method; use Log.w.
         */
        private static final int WARN = 5;

        /**
         * Priority constant for the println method; use Log.e.
         */
        private static final int ERROR = 6;

        /**
         * Priority constant for the println method.
         */
        private static final int ASSERT = 7;

        /**
         * 当前日志级别
         */
        private static int mCurrentLevel = VERBOSE;

        /**
         * 设置是否打开日志
         *
         * @param debug
         */
        public void setDebug(boolean debug) {
            mDebug = debug;
        }

        /**
         * 设置日志级别，默认VERBOSE
         *
         * @param level
         */
        public void setLogLevel(LogLevel level) {
            if (level == null) {
                return;
            }
            mCurrentLevel = level.value;
        }


        public void v(String tag, String msg) {
            if (VERBOSE >= mCurrentLevel && mDebug) {
                if (TextUtils.isEmpty(msg)) {
                    Log.e(tag, "parameter msg cannot be null");
                    return;
                }
                Log.v(tag, msg);
            }
        }

        public void d(String tag, String msg) {
            if (DEBUG >= mCurrentLevel && mDebug) {
                if (TextUtils.isEmpty(msg)) {
                    Log.e(tag, "parameter msg cannot be null");
                    return;
                }
                Log.d(tag, msg);
            }
        }

        public void i(String tag, String msg) {
            if (INFO >= mCurrentLevel && mDebug) {
                if (TextUtils.isEmpty(msg)) {
                    Log.e(tag, "parameter msg cannot be null");
                    return;
                }
                Log.i(tag, msg);
            }
        }

        public void w(String tag, String msg) {
            if (WARN >= mCurrentLevel && mDebug) {
                if (TextUtils.isEmpty(msg)) {
                    Log.e(tag, "parameter msg cannot be null");
                    return;
                }
                Log.w(tag, msg);
            }
        }

        public void e(String tag, String msg) {
            if (ERROR >= mCurrentLevel && mDebug) {
                if (TextUtils.isEmpty(msg)) {
                    Log.e(tag, "parameter msg cannot be null");
                    return;
                }
                Log.e(tag, msg);
            }
        }


    }
}
