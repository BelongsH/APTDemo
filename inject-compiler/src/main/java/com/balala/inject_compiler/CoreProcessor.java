package com.balala.inject_compiler;

import com.balala.inject_annotation.BindView;
import com.balala.inject_compiler.template.ActivityTemplate;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * <pre>
 *     author : 刘辉良
 *     time   : 2019年11月19日15:04:38
 *     desc   :
 *     version: 1.0
 * </pre>
 */

@AutoService(Processor.class)
public class CoreProcessor extends AbstractProcessor {

    private static final String ACTIVITY_NAME = "android.app.Activity";


    private Filer mFiler;   // 生成文件需要的类
    private Elements mElementUtils; // element的工具类
    private Types mTypeUtils;
    private Messager messager;  // 可以通过它打印一些日志

    private ProcessingEnvironment environment;


    /***
     *  对应的类型
     */
    protected TypeMirror mActivityTypeMirror;


    /***
     *  对应的类型参数
     */
    protected ParameterSpec mActivityParameterSpec;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.environment = processingEnv;
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mTypeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        prepareInit();
    }

    /**
     * 实例化相关帮助类
     */
    private void prepareInit() {
        mActivityTypeMirror = mElementUtils.getTypeElement(ACTIVITY_NAME).asType();
        mActivityParameterSpec = ParameterSpec.builder(TypeName.get(mActivityTypeMirror), "target").build();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        HashMap<TypeElement, List<Element>> datas = new HashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            TypeElement originalType = (TypeElement) element.getEnclosingElement();
            if (datas.get(originalType) == null) {
                List<Element> elements = new ArrayList<>();
                datas.put(originalType, elements);
            }
            List<Element> dd = datas.get(originalType);
            dd.add(element);
        }

        for (Map.Entry<TypeElement, List<Element>> entry : datas.entrySet()) {
            TypeElement key = entry.getKey();
            List<Element> value = entry.getValue();
            createFile(key, value);
        }
        return false;
    }

    private void createFile(TypeElement typeElement, List<Element> elements) {
        boolean isActivity = mTypeUtils.isSubtype(typeElement.asType(), mActivityTypeMirror);
        if (isActivity) {
            new ActivityTemplate(environment).createCustomerConstructor(typeElement, elements);
        }
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(BindView.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


}
