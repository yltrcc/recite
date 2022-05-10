package com.yltrcc.app.recite.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

public class ResUtils {

    public static int getInteger(Context context, @IntegerRes int resId) {
        return context.getResources().getInteger(resId);
    }

    public static String getString(Context context, @StringRes int strId) {
        return context.getResources().getString(strId);
    }

    public static String getString(Context context, @StringRes int strId, Object... formatArgs) {
        return context.getResources().getString(strId, formatArgs);
    }

    public static int getColor(Context context, @ColorRes int colorResId) {
        return context.getColor(colorResId);
    }

    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        try {
            return AppCompatResources.getDrawable(context, resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

