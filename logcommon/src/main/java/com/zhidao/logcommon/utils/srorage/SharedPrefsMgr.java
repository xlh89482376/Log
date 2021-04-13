package com.zhidao.logcommon.utils.srorage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.Set;

/**
 * @author: xuanlonghua
 * @date: 2021/1/31
 * @version: 1.0.0
 * @description:
 */

public class SharedPrefsMgr {

    private static final String FILE_NAME = "app_shared_pref";
    private static SharedPrefsMgr sInstance;
    private static SharedPreferences sSharedPrefs;

    public synchronized static SharedPrefsMgr getInstance( @NonNull Context context ) {
        if ( sInstance == null ) {
            try {
                sInstance = new SharedPrefsMgr( context.getApplicationContext() );
            } catch ( Exception e ) {
                sInstance = new SharedPrefsMgr();
            }
        }
        return sInstance;
    }

    private SharedPrefsMgr() {

    }

    private SharedPrefsMgr( Context context ) {
        try {
            sSharedPrefs = context.getSharedPreferences( FILE_NAME, Context.MODE_PRIVATE );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void putString( String key, String value ) {
        try {
            SharedPreferences.Editor editor = sSharedPrefs.edit();
            editor.putString( key, value );
            editor.apply();
        } catch ( Exception e ) {
        }
    }

    public String getString( String tag ) {
        try {
            return sSharedPrefs.getString( tag, "" );
        } catch ( Exception e ) {
            return "";
        }
    }

    public String getString( String tag, String defVal ) {
        try {
            return sSharedPrefs.getString( tag, defVal );
        } catch ( Exception e ) {
            return "";
        }
    }

    public boolean getBoolean( String key, boolean defaultValue ) {
        try {
            return sSharedPrefs.getBoolean( key, defaultValue );
        } catch ( Exception e ) {
            return defaultValue;
        }
    }

    public long getLong( String key, long defaultValue ) {
        try {
            return sSharedPrefs.getLong( key, defaultValue );
        } catch ( Exception e ) {
            return defaultValue;
        }
    }

    public float getFloat( String key, float defaultValue ) {
        try {
            return sSharedPrefs.getFloat( key, defaultValue );
        } catch ( Exception e ) {
            return defaultValue;
        }
    }

    public int getInt( String key, int value ) {
        try {
            return sSharedPrefs.getInt( key, value );
        } catch ( Exception e ) {
            return value;
        }
    }

    public void putBoolean( String key, boolean value ) {
        try {
            SharedPreferences.Editor editor = sSharedPrefs.edit();
            editor.putBoolean( key, value );
            editor.apply();
        } catch ( Exception e ) {
        }
    }

    public void putLong( String key, long value ) {
        try {
            SharedPreferences.Editor editor = sSharedPrefs.edit();
            editor.putLong( key, value );
            editor.apply();
        } catch ( Exception e ) {
        }
    }

    public void putInt( String key, int value ) {
        try {
            SharedPreferences.Editor editor = sSharedPrefs.edit();
            editor.putInt( key, value );
            editor.apply();
        } catch ( Exception e ) {
        }
    }

    public void putFloat( String key, float value ) {
        try {
            SharedPreferences.Editor editor = sSharedPrefs.edit();
            editor.putFloat( key, value );
            editor.apply();
        } catch ( Exception e ) {
        }
    }

    public void remove( String key ) {
        try {
            SharedPreferences.Editor editor = sSharedPrefs.edit();
            editor.remove( key );
            editor.apply();
        } catch ( Exception e ) {
        }
    }

    public void putStringSet( String key, Set< String > values ) {
        try {
            SharedPreferences.Editor editor = sSharedPrefs.edit();
            editor.putStringSet( key, values );
            editor.apply();
        } catch ( Exception e ) {
        }
    }

    public Set<String> getStringSet( String key ) {
        return sSharedPrefs.getStringSet( key, null );
    }
}
