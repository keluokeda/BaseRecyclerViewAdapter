package com.keluokeda;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

public class MultiItemClass {
    private List<MethodSpec> mMethodSpecs;

    public MultiItemClass() {
        mMethodSpecs = new ArrayList<>();
    }

    /**
     * 创建一个 带有指定参数类型的构造器
     * @param beanTypeName bean的Typename
     * @param layoutId bean所关联的item layout id
     */
    void addMultiItemConstructor(TypeName beanTypeName, int layoutId) {
        MethodSpec methodSpec = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(beanTypeName, "object")
                .addStatement("this.object = object")
                .addStatement("this.layoutId = $L", layoutId)
                .build();
        mMethodSpecs.add(methodSpec);
    }

    void createJavaFile(String multiItemPackageName, String multiItemName, Filer filer) throws IOException {
        FieldSpec layoutIdFieldSpec = FieldSpec.builder(TypeName.INT, "layoutId")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        FieldSpec objectFieldSpec = FieldSpec.builder(TypeName.OBJECT, "object")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        MethodSpec getTypeMethodSpec = MethodSpec.methodBuilder("getItemType")
                .returns(TypeName.INT)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return layoutId")
                .build();

        MethodSpec getBeanMethodSpec = MethodSpec.methodBuilder("getBean")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.OBJECT)
                .addStatement("return object")
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(multiItemName)
                .addField(layoutIdFieldSpec)
                .addField(objectFieldSpec)
                .addMethod(getTypeMethodSpec)
                .addMethod(getBeanMethodSpec)
                .addMethods(mMethodSpecs)
                .addSuperinterface(TypeUtil.CLASSNAME_MULTI_ITEM)
                .build();

        JavaFile.builder(multiItemPackageName, typeSpec).build().writeTo(filer);

    }
}
