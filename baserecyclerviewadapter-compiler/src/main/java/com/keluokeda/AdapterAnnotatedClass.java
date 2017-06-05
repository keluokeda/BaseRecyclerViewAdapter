package com.keluokeda;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import static com.keluokeda.TypeUtil.CLASSNAME_RECYCLERVIEW_ADAPTER;
import static com.keluokeda.TypeUtil.CLASSNAME_VIEW;
import static com.keluokeda.TypeUtil.CLASSNAME_VIEW_HOLDER;

class AdapterAnnotatedClass {
    private Element annotationClassElement;
    private List<? extends TypeMirror> typeMirrorList;
    private Set<? extends Element> adapterBeanElements;
    private Set<? extends Element> bindElements;
    private Elements mElements;

    /**
     * @param annotationClassElement 带有 {@link MultipleItem } 注解的 类
     * @param typeMirrorList         {@link MultipleItem#beans()} 注解返回的类集合
     * @param adapterBeanElements    带有{@link AdapterBean} 注解的 类
     * @param bindElements           带有 {@link Bind} 注解的方法
     * @param elements               工具
     */
    AdapterAnnotatedClass(Element annotationClassElement, List<? extends TypeMirror> typeMirrorList, Set<? extends Element> adapterBeanElements, Set<? extends Element> bindElements, Elements elements) {
        this.annotationClassElement = annotationClassElement;
        this.typeMirrorList = typeMirrorList;
        this.adapterBeanElements = adapterBeanElements;
        this.bindElements = bindElements;
        this.mElements = elements;
    }


    void createJavaFile(Filer filer) throws IOException {
        TypeElement adapterTypeElement = (TypeElement) annotationClassElement;
        String adapterName = adapterTypeElement.getSimpleName().toString() + "_RecyclerView_Adapter";
        String adapterPackageName = mElements.getPackageOf(adapterTypeElement).getQualifiedName().toString();
        String viewHolderName = adapterTypeElement.getSimpleName().toString() + "_RecyclerView_ViewHolder";
        String multiItemName = "Multi_" + adapterTypeElement.getSimpleName().toString();

        MethodSpec.Builder adapterConstructorMethodSpecBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(List.class,"list")
                .addStatement("super(list)");

        ClassName multiItemClassName = ClassName.get(adapterPackageName,multiItemName);
        MethodSpec.Builder createMultiItemMethodSpecBuilder = MethodSpec.methodBuilder("createMultiItem")
                .returns(TypeUtil.CLASSNAME_MULTI_ITEM)
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(TypeName.OBJECT, "obj");


        ViewHolderAnnotatedClass viewHolderAnnotatedClass = new ViewHolderAnnotatedClass();
        MultiItemClass multiItemClass = new MultiItemClass();
        for (TypeMirror mirror : typeMirrorList) {
            for (Element element : adapterBeanElements) {
                TypeMirror beanTypeMirror = element.asType();
                if (mirror.equals(beanTypeMirror)) {
                    //create bean binder
                    List<Element> elementList = new ArrayList<>();
                    for (Element bindElement : bindElements) {
                        if (bindElement.getEnclosingElement() == element) {
                            elementList.add(bindElement);
                        }
                    }
                    if (elementList.isEmpty()) {
                        throw new RuntimeException(String.format("AdapterBean class should has method which with annotation %s", Bind.class.getSimpleName()));
                    }

                    ClassName beanBinderClassName = createBeanBinder(element, elementList, filer);

                    AdapterBean adapterBean = element.getAnnotation(AdapterBean.class);
                    int itemLayoutId = adapterBean.layoutId();
                    TypeName beanTypeName = TypeName.get(beanTypeMirror);
                    viewHolderAnnotatedClass.addBeanBinderClass(beanBinderClassName, itemLayoutId);
                    multiItemClass.addMultiItemConstructor(beanTypeName, itemLayoutId);
                    createMultiItemMethodSpecBuilder.beginControlFlow("if (obj instanceof $T)",beanTypeName)
                            .addStatement("return new $T( ($T) obj)",multiItemClassName,beanTypeName)
                            .endControlFlow();

                }
            }
        }


        ClassName viewHolderClassName = viewHolderAnnotatedClass.createJavaFile(adapterPackageName, viewHolderName, filer);
        multiItemClass.createJavaFile(adapterPackageName, multiItemName, filer);


        MethodSpec adapterCreateViewHolderMethodSpec = MethodSpec.methodBuilder("createViewHolder")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(CLASSNAME_VIEW, "view")
                .addParameter(TypeName.INT, "layoutId")
                .returns(CLASSNAME_VIEW_HOLDER)
                .addStatement("return new $T(view,layoutId)", viewHolderClassName)
                .build();

        createMultiItemMethodSpecBuilder.addStatement("throw new $T($S)",RuntimeException.class,"obj not a valid type");


        TypeSpec adapterTypeSpec = TypeSpec.classBuilder(adapterName)
                .addMethod(adapterCreateViewHolderMethodSpec)
                .addMethod(adapterConstructorMethodSpecBuilder.build())
                .addMethod(createMultiItemMethodSpecBuilder.build())
                .superclass(CLASSNAME_RECYCLERVIEW_ADAPTER)
                .build();


        JavaFile.builder(adapterPackageName, adapterTypeSpec).build().writeTo(filer);
    }


    private ClassName createBeanBinder(Element beanElement, List<Element> bindElements, Filer mFiler) throws IOException {

        TypeElement typeElement = (TypeElement) beanElement;
        String packageName = mElements.getPackageOf(beanElement).getQualifiedName().toString();
        BeanBinderAnnotatedClass beanBinderAnnotatedClass = new BeanBinderAnnotatedClass(typeElement, bindElements);

        return beanBinderAnnotatedClass.createJavaFileAndReturnBeanBindClassName(mFiler, packageName);
    }
}
