package com.libprocessor.view;

import com.modules.BindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by Administrator on 2017/12/27.
 * BindView对象
 */
public class BindViewIdField {
    /**
     * PackageElement        包
     * ExecutableElement    方法、构造方法
     * VariableElement       成员变量、enum常量、方法或构造方法参数、局部变量或异常参数。
     * TypeElement           类、接口
     * TypeParameterElement  在方法或构造方法、类、接口处定义的泛型参数。
     */
    private VariableElement mVariableElement;//包裹注解的对象
    private int mResId;//
    public BindViewIdField(Element element) throws IllegalArgumentException {
        // 判断是否是成员变量
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(String.format("Only field can be annotated with @%s", BindView.class.getSimpleName()));
        }
        mVariableElement = (VariableElement) element;
        // 获取注解和id值
        BindView  bindViewId = mVariableElement.getAnnotation(BindView.class);
        mResId = bindViewId.value();
        if (mResId < 0) {
            throw new IllegalArgumentException(String.format("value() in %s for field %s is not valid", BindView.class.getSimpleName(), mVariableElement.getSimpleName()));
        }
    }

    /**
     * 获取变量名
     *
     * @return Name
     */
    public Name getFieldName() {
        return mVariableElement.getSimpleName();
    }

    /**
     * 获取id
     *
     * @return int
     */
    public int getFieldResId() {
        return mResId;
    }

    /**
     * 获取变量类型
     *
     * @return TypeMirror
     */
    public TypeMirror getFieldType() {
        return mVariableElement.asType();
    }

    /**
     * 获取变量类型，如TextView
     * @return
     */
    public String getFieldClass() {
        String className = getFieldType().toString();
        return className.substring(className.lastIndexOf(".") + 1);
    }
}
