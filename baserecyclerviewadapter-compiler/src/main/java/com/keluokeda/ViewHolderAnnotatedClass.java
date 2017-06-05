package com.keluokeda;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import static com.keluokeda.TypeUtil.CLASSNAME_BEAN_BINDER;
import static com.keluokeda.TypeUtil.CLASSNAME_VIEW;
import static com.keluokeda.TypeUtil.CLASSNAME_VIEW_HOLDER;

 class ViewHolderAnnotatedClass {

    private MethodSpec.Builder constructorViewHolderBuilder;

     ViewHolderAnnotatedClass() {
        constructorViewHolderBuilder = MethodSpec.constructorBuilder()
                .addParameter(CLASSNAME_VIEW, "view")
                .addParameter(TypeName.INT, "type")
                .addStatement("super(view)");
    }

    void addBeanBinderClass(TypeName beanBinderClassName,int itemLayoutId) {
        constructorViewHolderBuilder.beginControlFlow("if (type == $L)", itemLayoutId)
                .addStatement("this.beanBinder = new $T(view)", beanBinderClassName)
                .endControlFlow();
    }

    ClassName createJavaFile(String packageName, String viewHolderName, Filer filer) throws IOException {
        constructorViewHolderBuilder.beginControlFlow("if (this.beanBinder == null)")
                .addStatement("throw new $T($S)",RuntimeException.class,"beanBinder is null ,unExpect layoutId")
                .endControlFlow();

        FieldSpec viewHolderBeanBinder = FieldSpec.builder(CLASSNAME_BEAN_BINDER, "beanBinder", Modifier.PRIVATE).build();
        MethodSpec viewHolderBindDataMethodSpec = MethodSpec.methodBuilder("bindData")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.OBJECT, "obj")
                .addParameter(TypeName.INT, "position")
                .addStatement("this.beanBinder.bindData(obj)")
                .build();
        TypeSpec viewHolderTypeSpec = TypeSpec.classBuilder(viewHolderName)
                .addField(viewHolderBeanBinder)
                .addMethod(constructorViewHolderBuilder.build())
                .addMethod(viewHolderBindDataMethodSpec)
                .superclass(CLASSNAME_VIEW_HOLDER)
                .build();
        JavaFile.builder(packageName, viewHolderTypeSpec).build().writeTo(filer);

        return ClassName.get(packageName,viewHolderName);
    }
}
