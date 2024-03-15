/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.bexonpak.regions.lib;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * targetSdk = 30
 */
public class Regions {
    private static final String LOG_TAG = Regions.class.getSimpleName();

    @SuppressWarnings({"UnnecessaryBoxing", "rawtypes"})
    private static void setUserSetLocale(Configuration configuration, boolean value) {
        Class clazz = configuration.getClass();
        try {
            Field field = clazz.getField("userSetLocale");
            field.set(configuration, Boolean.valueOf(value));
        } catch (NoSuchFieldException e) {
            Log.e(LOG_TAG, "NoSuchFieldException", e);
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, "IllegalAccessException", e);
        }
    }

    @SuppressWarnings("deprecation")
    public static Locale getLocale(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return configuration.getLocales().get(0);
        } else {
            return configuration.locale;
        }
    }

    public static boolean setLocale(Locale locale) throws InvocationTargetException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return setLocaleList(new LocaleList(locale));
        } else {
            return setSingleLocale(locale);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings({"RedundantArrayCreation", "ConfusingArgumentToVarargsMethod"})
    private static boolean setLocaleList(LocaleList locales) throws InvocationTargetException {
        try {
            @SuppressLint("PrivateApi") Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Method getDefault = activityManagerNative.getMethod("getDefault", null);
            Object am = getDefault.invoke(activityManagerNative, null);
            Method getConfiguration = am.getClass().getMethod("getConfiguration", null);
            Configuration config = (Configuration) getConfiguration.invoke(am, null);
            config.setLocales(locales);
            setUserSetLocale(config, true);
            Method updateConfiguration = am.getClass().getMethod("updateConfiguration", new Class[]{Configuration.class});
            updateConfiguration.invoke(am, new Object[]{config});
            return true;
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "ClassNotFoundException", e);
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, "NoSuchMethodException", e);
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, "IllegalAccessException", e);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception", e);
        }
        return false;
    }
    @SuppressWarnings({"RedundantArrayCreation", "ConfusingArgumentToVarargsMethod"})
    private static boolean setSingleLocale(Locale locale) throws InvocationTargetException {
        try {
            @SuppressLint("PrivateApi") Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Method getDefault = activityManagerNative.getMethod("getDefault", null);
            Object am = getDefault.invoke(activityManagerNative, null);
            Method getConfiguration = am.getClass().getMethod("getConfiguration", null);
            Configuration config = (Configuration) getConfiguration.invoke(am, null);
            config.locale = locale;
            setUserSetLocale(config, true);
            Method updateConfiguration = am.getClass().getMethod("updateConfiguration", new Class[]{Configuration.class});
            updateConfiguration.invoke(am, new Object[]{config});
            return true;
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "ClassNotFoundException", e);
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, "NoSuchMethodException", e);
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, "IllegalAccessException", e);
        }
        return false;
    }

}
