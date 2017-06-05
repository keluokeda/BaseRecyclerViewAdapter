package com.keluokeda;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.keluokeda.TypeUtil.CLASSNAME_BEAN_BINDER;
import static com.keluokeda.TypeUtil.CLASSNAME_VIEW;

class BeanBinderAnnotatedClass {

    private TypeElement beanTypeElement;
    private List<Element> mMethodElements;

     BeanBinderAnnotatedClass(TypeElement beanTypeElement, List<Element> methodElements) {
        this.beanTypeElement = beanTypeElement;
        this.mMethodElements = methodElements;
    }

    ClassName createJavaFileAndReturnBeanBindClassName(Filer filer,String packageName) throws IOException{
        ClassName beanClassName = ClassName.get(beanTypeElement);
        String beanBinderName = beanTypeElement.getSimpleName().toString() + "_BeanBinder";
        TypeSpec.Builder beanBinderTypeSpecBuilder = TypeSpec.classBuilder(beanBinderName)
                .addSuperinterface(CLASSNAME_BEAN_BINDER);
        MethodSpec.Builder constructorMethodSpecBuilder = MethodSpec.constructorBuilder()
                .addParameter(CLASSNAME_VIEW, "view")
                .addModifiers(Modifier.PUBLIC);

        String beanName = beanTypeElement.getSimpleName().toString().toLowerCase();

        MethodSpec.Builder bindDataMethodSpec = MethodSpec.methodBuilder("bindData")
                .addAnnotation(Override.class)
                .addParameter(TypeName.OBJECT, "object")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T isBean = object instanceof $T", TypeName.BOOLEAN, beanTypeElement)
                .beginControlFlow("if (!isBean)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T $L = ($T) object", beanClassName, beanName, beanClassName);

        for (Element element : mMethodElements) {
            ExecutableElement executableElement = (ExecutableElement) element;
            String beanMethodName = executableElement.getSimpleName().toString();
            Integer viewId = null;
            TypeMirror viewClassTypeMirror = null;
            TypeMirror binderClassTypeMirror = null;
            for (AnnotationMirror annotationMirror : executableElement.getAnnotationMirrors()) {


                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
                    if ("viewId".equals(entry.getKey().getSimpleName().toString())) {
                        viewId = (Integer) entry.getValue().getValue();
                    } else if ("viewClass".equals(entry.getKey().getSimpleName().toString())) {
                        viewClassTypeMirror = (TypeMirror) entry.getValue().getValue();
                    } else if ("binderClass".equals(entry.getKey().getSimpleName().toString())) {
                        binderClassTypeMirror = (TypeMirror) entry.getValue().getValue();
                    }
                }
            }



            //生成 view 成员变量
            ClassName viewTypeName = (ClassName) TypeName.get(viewClassTypeMirror);
            String viewFieldName = viewTypeName.simpleName().toLowerCase() + String.valueOf(viewId);
            FieldSpec viewFieldSpec = FieldSpec
                    .builder(viewTypeName, viewFieldName)
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            beanBinderTypeSpecBuilder.addField(viewFieldSpec);

            //初始化 view
            constructorMethodSpecBuilder.addStatement("this.$L = ($T) view.findViewById($L)", viewFieldName, viewTypeName, viewId);


            //生成 ViewValueBinder 成员变量
            ClassName binderTypeName = (ClassName) TypeName.get(binderClassTypeMirror);
            String binderFieldName = binderTypeName.simpleName().toLowerCase() + String.valueOf(viewId);
            FieldSpec binderFieldSpec = FieldSpec
                    .builder(binderTypeName, binderFieldName)
                    .addModifiers(Modifier.PRIVATE)
                    .build();
            beanBinderTypeSpecBuilder.addField(binderFieldSpec);

            //初始化 binder
            constructorMethodSpecBuilder.addStatement("this.$L = new $T()", binderFieldName, binderTypeName);

            //在 bindData 里面绑定数据
            bindDataMethodSpec.addStatement("this.$L.bind(this.$L,$L.$L())", binderFieldName, viewFieldName, beanName, beanMethodName);


        }
        beanBinderTypeSpecBuilder
                .addMethod(constructorMethodSpecBuilder.build())
                .addMethod(bindDataMethodSpec.build());

        JavaFile.builder(packageName, beanBinderTypeSpecBuilder.build()).build().writeTo(filer);

        return ClassName.get(packageName,beanBinderName);
    }
}
