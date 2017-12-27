package com.libprocessor;

import com.squareup.javapoet.ClassName;

/**
 * Created by Administrator on 2017/12/27.
 */

public class ClassNameType {
    public static final ClassName VIEW = ClassName.get("android.view", "View");
    public static final ClassName UNBINDER = ClassName.get("com.commen", "UnBinder");
    public static final ClassName UTILS = ClassName.get("com.commen", "BindUtil");
    public static final ClassName T = ClassName.get("", "T");
    public static final ClassName ONCLICK = ClassName.get("android.view", "View", "OnClickListener");
}
