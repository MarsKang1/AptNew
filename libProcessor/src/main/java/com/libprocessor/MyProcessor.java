package com.libprocessor;

import com.google.auto.service.AutoService;
import com.libprocessor.view.BindViewClass;
import com.libprocessor.view.BindViewIdField;
import com.libprocessor.view.OnClickMethod;
import com.modules.BindView;
import com.modules.OnClick;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Administrator on 2017/12/27.
 */

@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    private Filer mFiler;//文件相关的辅助类
    private Elements mElementUtils;//元素相关的辅助类
    private Messager mMessager;//日志相关的辅助类
    private Map<String, BindViewClass> mBindViewClassMap = new HashMap<>();//解析的目标注解集合

    /**
     * init方法是在Processor创建时被javac调用并执行初始化操作。
     *
     * @param processingEnv 提供一系列的注解处理工具。
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    /**
     * 注解处理器要处理的注解类型,值为完全限定名（就是带所在包名和路径的类全名）
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName()); // 返回该注解处理器支持的注解集合
        return types;
    }

    /**
     * 注解处理需要执行一次或者多次。每次解析前都要清空，处理器方法被调用，并且传入了当前要处理的注解类型。
     * 可以在这个方法中扫描和处理注解，并生成Java代码。
     *
     * @param annotations 当前要处理的注解类型
     * @param roundEnv    这个对象提供当前或者上一次注解处理中被注解标注的源文件元素。（获得所有被标注的元   素）
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mBindViewClassMap.clear();
        try {
            getBindViewIdForRoundEnv(roundEnv);
            getOnClickForRoundEnv(roundEnv);
            for (BindViewClass bindViewClass : mBindViewClassMap.values()) {
                bindViewClass.generateCode().writeTo(mFiler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* 获取RoundEnvironment里面的@BindViewId注解
    * @param roundEnvironment RoundEnvironment
    */
    private void getBindViewIdForRoundEnv(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            BindViewClass bindViewClass = getBindViewClass(element);
            bindViewClass.addBindViewIdField(new BindViewIdField(element));// 往BindViewClass里面添加field
        }
    }

    /**
     * 获取RoundEnvironment里面的@OnClick注解
     *
     * @param roundEnvironment RoundEnvironment
     */
    private void getOnClickForRoundEnv(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(OnClick.class)) {
            BindViewClass bindViewClass = getBindViewClass(element);
            bindViewClass.addOnClickMethod(new OnClickMethod(element));  // 往BindViewClass里面添加方法
        }
    }


    /**
     * 获取BindViewClass
     *
     * @param element
     * @return
     */
    private BindViewClass getBindViewClass(Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement(); // 获取当前Element所在的TypeElement
        String fullClassName = enclosingElement.getQualifiedName().toString();   // 获取TypeElement的类全名
        BindViewClass bindViewClass = mBindViewClassMap.get(fullClassName); // 如果在map中存在就直接用，不存在就new出来放在map里
        if (bindViewClass == null) {
            bindViewClass = new BindViewClass(mElementUtils, enclosingElement);
            mBindViewClassMap.put(fullClassName, bindViewClass);
        }
        return bindViewClass;
    }
}
