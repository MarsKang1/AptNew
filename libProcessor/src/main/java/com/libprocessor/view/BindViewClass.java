package com.libprocessor.view;

import com.libprocessor.ClassNameType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Administrator on 2017/12/27.
 */

public class BindViewClass {
    private TypeElement mTypeElement;//类名
    private List<BindViewIdField> mBindViewIdFields;// 具有@BindViewId注解的变量list
    private List<OnClickMethod> mOnClickMethods;//具有@OnClick注解的变量list
    private Elements mElements;//元素相关的辅助类

    public BindViewClass(Elements elements, TypeElement typeElement) {
        mElements = elements;
        mTypeElement = typeElement;
        mBindViewIdFields = new ArrayList<>();
        mOnClickMethods = new ArrayList<>();
    }

    /**
     * 类名
     */
    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    /**
     * 添加到list
     *
     * @param field 包含注解的变量
     */
    public void addBindViewIdField(BindViewIdField field) {
        mBindViewIdFields.add(field);
    }

    /**
     * 添加到list
     *
     * @param onClickMethod 包含注解的方法
     */
    public void addOnClickMethod(OnClickMethod onClickMethod) {
        mOnClickMethods.add(onClickMethod);
    }

    public JavaFile generateCode() {
        String packageName = getPackageName(mTypeElement);    // 获取包名
        String className = getClassName(mTypeElement, packageName);  // 获取类名
        ClassName bindClassName = ClassName.get(packageName, className);// 获取ClassName
        // 创建unBind方法
        MethodSpec.Builder unBinderMethod = MethodSpec.methodBuilder("unBind").addAnnotation(Override.class).addModifiers(Modifier.PUBLIC);
        unBinderMethod.addStatement("$T target = this.target", bindClassName);// MainActivity target = this.target;
        unBinderMethod.beginControlFlow("if(target == null)");// if(target == null) {
        unBinderMethod.addStatement("throw new $T(\"Bindings already cleared.\")", IllegalStateException.class);//throw new IllegalStateException("Bindings already cleared.");
        unBinderMethod.endControlFlow();//}
        unBinderMethod.beginControlFlow("else");//else {

        // 构造方法
        MethodSpec.Builder buildViewMethod = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addParameter(ClassNameType.T, "target", Modifier.FINAL).addParameter(ClassNameType.VIEW, "source");
        buildViewMethod.addStatement("this.target = target");

        // 创建findViewById
        if (mBindViewIdFields.size() > 0) {
            for (BindViewIdField bindViewIdField : mBindViewIdFields) {
                // target.tv_contewnt = (TextView) Utils.findRequiredViewAsType(source, 2131558524, "tv_contewnt", TextView.class);
                buildViewMethod.addStatement("target.$N = ($T) $T.findRequiredViewAsType(source, $L, $S, $N)",
                        bindViewIdField.getFieldName(), bindViewIdField.getFieldType(), ClassNameType.UTILS,
                        bindViewIdField.getFieldResId(), bindViewIdField.getFieldName().toString(), bindViewIdField.getFieldClass() + ".class");

                // 创建onClick
                if (mOnClickMethods.size() > 0) {
                    StringBuilder onclick = new StringBuilder();
                    for (OnClickMethod onClickMethod : mOnClickMethods) {
                        if (onClickMethod.getResIds().contains(bindViewIdField.getFieldResId())) {
                            onclick.append("target.$N.setOnClickListener(new $T() {");//target.mFab.setOnClickListener{ (new View.OnClickListener() {
                            onclick.append("public void onClick(View v) {");//ublic void onClick(View v)
                            for (OnClickMethod onClickMethods : mOnClickMethods) {
                                if (onClickMethods.getResIds().contains(bindViewIdField.getFieldResId())) {
                                    onclick.append("target.");
                                    onclick.append(onClickMethods.getOnClickMethodName().toString());//target.onClick (v);
                                    onclick.append(" (v);");
                                }
                            }
                            onclick.append("}})");
                            buildViewMethod.addStatement(onclick.toString(), bindViewIdField.getFieldName(), ClassNameType.ONCLICK);//将StringBuffer填充到方法体里面
                            unBinderMethod.addStatement("target.$N.setOnClickListener(($T) null)", bindViewIdField.getFieldName(), ClassNameType.ONCLICK);//  target.mContentMain.setOnClickListener((View.OnClickListener) null);
                            break;
                        }
                    }
                }
                unBinderMethod.addStatement("target.$N = null", bindViewIdField.getFieldName());
            }
        }
        unBinderMethod.addStatement("target = null");
        unBinderMethod.endControlFlow();

        // 构造类
        TypeSpec.Builder viewBinder = TypeSpec.classBuilder(String.format("%s_ViewBinder", bindClassName.simpleName()))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassNameType.UNBINDER)
                .addField(FieldSpec.builder(ClassNameType.T, "target", Modifier.PROTECTED).build())
                .addTypeVariable(TypeVariableName.get("T", bindClassName)) // 添加泛型<T extends MainActivity>
                .addMethod(buildViewMethod.build())
                .addMethod(unBinderMethod.build());
        return JavaFile.builder(packageName, viewBinder.build()).build();

    }

    /**
     * 获取包名
     *
     * @return String
     */
    private String getPackageName(TypeElement typeElement) {
        return mElements.getPackageOf(typeElement).getQualifiedName().toString();
    }

}
