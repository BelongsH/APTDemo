package com.balala.inject_compiler.template;

import com.balala.inject_annotation.BindView;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * <pre>
 *     author : 刘辉良
 *     time   : 2019年11月19日
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ActivityTemplate {


    private static final String ACTIVITY_NAME = "android.app.Activity";
    private static final String VIEW_NAME = "android.view.View";


    private ParameterSpec mViewParameterSpec;

    private ProcessingEnvironment environment;


    public ActivityTemplate(ProcessingEnvironment environment) {
        this.environment = environment;
        Elements elements = environment.getElementUtils();
        TypeMirror mViewTypeMirror = elements.getTypeElement(VIEW_NAME).asType();
        this.mViewParameterSpec = ParameterSpec.builder(TypeName.get(mViewTypeMirror), "source").build();
    }

    public void createCustomerConstructor(TypeElement originalType, List<Element> elements) {
        ParameterSpec targetParamSpec = ParameterSpec.builder(TypeName.get(originalType.asType()), "target").build();

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this(target, target.getWindow().getDecorView())")
                .addParameter(targetParamSpec);

        TypeSpec viewBindClass = TypeSpec.classBuilder(originalType.getSimpleName() + "$ViewBinding")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(createCustomerConstructor2View(originalType, elements))
                .addMethod(constructor.build())
                .build();

        PackageElement packageElement = environment.getElementUtils().getPackageOf(originalType);
        JavaFile javaFile = JavaFile.builder(packageElement.getQualifiedName().toString(), viewBindClass).build();
        try {
            javaFile.writeTo(environment.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 构建2个参数的方法
     *
     * @param originalType 原始类型
     * @param elements     注解元素
     * @return 方法
     */
    private MethodSpec createCustomerConstructor2View(TypeElement originalType, List<Element> elements) {
        ParameterSpec targetParamSpec = ParameterSpec.builder(TypeName.get(originalType.asType()), "target").build();
        MethodSpec.Builder constructor1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(targetParamSpec)
                .addParameter(mViewParameterSpec)
                .addStatement("if(target == null)  return")
                .addStatement("if(source == null)  return");

        for (Element element : elements) {
            String variateName = element.getSimpleName().toString();
            String variateType = element.asType().toString();
            int resId = element.getAnnotation(BindView.class).value();
            constructor1.addStatement("target.$L = ($N)source.findViewById($L)", variateName, variateType, resId);
        }
        return constructor1.build();
    }

}
