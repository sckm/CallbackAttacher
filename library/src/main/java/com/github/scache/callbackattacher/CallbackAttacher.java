package com.github.scache.callbackattacher;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class CallbackAttacher {
    private static final String TAG = CallbackAttacher.class.getSimpleName();

    // attach(A,Object)の第一引数(A)の型を決めるためのMap
    static final Map<Class<?>, Class<?>> TARGETS = new LinkedHashMap<Class<?>, Class<?>>();

    static final Map<Class<?>, Class<?>> ATTACHERS = new LinkedHashMap<Class<?>, Class<?>>();

    private static boolean debug = true;

    public static void setDebug(boolean debug) {
        CallbackAttacher.debug = debug;
    }

    public static void attach(@NonNull Object target, @NonNull Object source) {
        Class<?> targetClass = target.getClass();
        Class<?> attacherClass = findAttacherForClass(targetClass);
        Method attachMethod;
        if (attacherClass == null) {
            if (debug)
                Log.d(TAG, "cannot find attacher class: target class=" + targetClass.getSimpleName());
            return;
        }

        attachMethod = findMethod(attacherClass, "attach", TARGETS.get(targetClass), Object.class);
        if (attachMethod == null) {
            if (debug)
                Log.d(TAG, "cannot find attach method: class=" + attacherClass.getSimpleName());
            return;
        }

        invokeMethod(attachMethod, null, target, source);
    }

    public static void detach(@NonNull Object target) {
        Class<?> targetClass = target.getClass();
        Class<?> attacherClass = findAttacherForClass(targetClass);
        Method detachMethod;
        if (attacherClass == null) {
            if (debug)
                Log.d(TAG, "cannot find detacher class: target class=" + targetClass.getSimpleName());
            return;
        }

        detachMethod = findMethod(attacherClass, "detach", TARGETS.get(targetClass));
        if (detachMethod == null) {
            if (debug)
                Log.d(TAG, "cannot find detach method: class=" + attacherClass.getSimpleName());
            return;
        }

        invokeMethod(detachMethod, null, target);
    }

    @Nullable @CheckResult
    private static Class<? extends Object> findAttacherForClass(Class<?> cls) {
        Class<?> attacherClass = ATTACHERS.get(cls);
        if (attacherClass != null) {
            if (debug) Log.d(TAG, "findAttacherTargetForClass: Hit cache " + cls.getName());
            return attacherClass;
        }

        if (debug) Log.d(TAG, "findAttacherTargetForClass: Miss cache " + cls.getName());

        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            TARGETS.put(cls, null);
            ATTACHERS.put(cls, null);
            return null;
        }

        try {
            attacherClass = Class.forName(clsName + "_CallbackAttacher");
            TARGETS.put(cls, cls);
            ATTACHERS.put(cls, attacherClass);
        } catch (ClassNotFoundException e) {
            if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            attacherClass = findAttacherForClass(cls.getSuperclass());
            TARGETS.put(cls, TARGETS.get(cls.getSuperclass()));
            ATTACHERS.put(cls, attacherClass);
        }
        return attacherClass;
    }


    @Nullable
    private static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            if (debug)
                Log.d(TAG, "attacher class not have " + methodName + " method: declaring class=" + clazz.getSimpleName());
            return null;
        }
    }

    private static void invokeMethod(Method method, Object receiver, Object... args) {
        try {
            method.invoke(receiver, args);
            if (debug)
                Log.d(TAG, "invoke " + method.getDeclaringClass().getName() + "#" + method.getName());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + method, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to invoke " + method, e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to invoke " + method, e);
        }
    }

}
