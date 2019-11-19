package com.balala.inject_core;

import android.app.Activity;

import java.lang.reflect.Constructor;

/**
 * <pre>
 *     author : 刘辉良
 *     time   : 2019/11/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Inject {

    public static void inject(Activity activity) {
        String name = activity.getClass().getSimpleName();
        String packName = activity.getPackageName();
        String fullName = name + "$ViewBinding";
        try {
            Class targetClass = activity.getClassLoader().loadClass(packName + "." + fullName);
            Constructor constructor = targetClass.getConstructor(activity.getClass());
            constructor.newInstance(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
