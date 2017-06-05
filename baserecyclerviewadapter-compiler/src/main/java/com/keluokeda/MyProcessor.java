package com.keluokeda;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;



@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer mFiler;
    private Elements mElements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElements = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            Set<? extends Element> adapterBeanElements = roundEnvironment.getElementsAnnotatedWith(AdapterBean.class);
            Set<? extends Element> bindElements = roundEnvironment.getElementsAnnotatedWith(Bind.class);

            Set<? extends Element> multipleItemElements = roundEnvironment.getElementsAnnotatedWith(MultipleItem.class);

            for (Element element : multipleItemElements) {
                for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                    Map<? extends ExecutableElement, ? extends AnnotationValue> map = annotationMirror.getElementValues();
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> m : map.entrySet()) {
                        if ("beans".equals(m.getKey().getSimpleName().toString())) {
                            //如果 注解的返回类型是数组 要用List<AnnotationValue> 去接收
                            List<AnnotationValue> annotationValues = (List<AnnotationValue>) m.getValue().getValue();
                            List<TypeMirror> typeMirrors = new ArrayList<>(annotationValues.size());
                            for (AnnotationValue value : annotationValues) {
                                TypeMirror mirror = (TypeMirror) value.getValue();
                                typeMirrors.add(mirror);
                            }

                            processAnnotation(element, typeMirrors, adapterBeanElements, bindElements);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            error("error %s", e.getMessage());
        }


        return true;
    }


    /**
     * @param annotationClassElement 带有注解MultipleItem 的class
     * @param typeMirrorList         注解的返回值
     * @param adapterBeanElements    带有注解AdapterBean 的 class
     * @param bindElements           带有 注解 bind 的class
     * @throws IOException
     */
    private void processAnnotation(Element annotationClassElement, List<? extends TypeMirror> typeMirrorList, Set<? extends Element> adapterBeanElements, Set<? extends Element> bindElements) throws IOException {
//        TypeElement adapterTypeElement = (TypeElement) annotationClassElement;
//        String adapterName = adapterTypeElement.getSimpleName().toString() + "_Adapter";
//        String adapterPackageName = mElements.getPackageOf(adapterTypeElement).getQualifiedName().toString();
//        String viewHolderName = adapterTypeElement.getSimpleName().toString() + "_ViewHolder";
//
//        MethodSpec.Builder constructorViewHolderBuilder = MethodSpec.constructorBuilder()
//                .addParameter(CLASSNAME_VIEW, "view")
//                .addParameter(TypeName.INT, "type")
//                .addStatement("super(view)");
//        FieldSpec viewHolderBeanBinder = FieldSpec.builder(CLASSNAME_BEAN_BINDER, "beanBinder", Modifier.PRIVATE).build();
//
//        for (TypeMirror mirror : typeMirrorList) {
//            for (Element element : adapterBeanElements) {
//                TypeMirror beanTypeMirror = element.asType();
//                if (mirror.equals(beanTypeMirror)) {
//                    //create bean binder
//                    List<Element> elements = new ArrayList<>();
//                    for (Element bindElement : bindElements) {
//                        if (bindElement.getEnclosingElement() == element) {
//                            elements.add(bindElement);
//                        }
//                    }
//                    if (elements.isEmpty()) {
//                        throw new RuntimeException(String.format("AdapterBean class should has method which with annotation %s", Bind.class.getSimpleName()));
//                    }
//
//                    ClassName beanBinderClassName = createBeanBinder(element, elements);
//
//                    AdapterBean adapterBean = element.getAnnotation(AdapterBean.class);
//                    int itemLayoutId = adapterBean.layoutId();
//
//                    constructorViewHolderBuilder.beginControlFlow("if (type == $L)", itemLayoutId)
//                            .addStatement("this.beanBinder = new $T(view)", beanBinderClassName)
//                            .endControlFlow();
//
//                }
//            }
//        }
//
//        MethodSpec viewHolderBindDataMethodSpec = MethodSpec.methodBuilder("bindData")
//                .addModifiers(Modifier.PUBLIC)
//                .addAnnotation(Override.class)
//                .addParameter(TypeName.OBJECT, "obj")
//                .addParameter(TypeName.INT, "position")
//                .addStatement("this.beanBinder.bindData(obj)")
//                .build();
//
//        TypeSpec viewHolderTypeSpec = TypeSpec.classBuilder(viewHolderName)
//                .addField(viewHolderBeanBinder)
//                .addMethod(constructorViewHolderBuilder.build())
//                .addMethod(viewHolderBindDataMethodSpec)
//                .superclass(CLASSNAME_VIEW_HOLDER)
//                .build();
//
//
//        JavaFile.builder(adapterPackageName, viewHolderTypeSpec).build().writeTo(mFiler);
//
//
//        MethodSpec adapterCreateViewHolderMethodSpec = MethodSpec.methodBuilder("createViewHolder")
//                .addModifiers(Modifier.PROTECTED)
//                .addAnnotation(Override.class)
//                .addParameter(CLASSNAME_VIEW, "view")
//                .addParameter(TypeName.INT, "layoutId")
//                .returns(CLASSNAME_VIEW_HOLDER)
//                .addStatement("return new $T(view,layoutId)", ClassName.get(adapterPackageName, viewHolderName))
//                .build();
//
//        MethodSpec adapterConstructorMethodSpec = MethodSpec.constructorBuilder()
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(ParameterizedTypeName.get(ClassName.get(List.class), CLASSNAME_MULTI_ITEM), "list")
//                .addStatement("super(list)")
//                .build();
//
//
//        TypeSpec adapterTypeSpec = TypeSpec.classBuilder(adapterName)
//                .addMethod(adapterCreateViewHolderMethodSpec)
//                .addMethod(adapterConstructorMethodSpec)
//                .superclass(CLASSNAME_RECYCLERVIEW_ADAPTER)
//                .build();
//
//
//        JavaFile.builder(adapterPackageName, adapterTypeSpec).build().writeTo(mFiler);

        AdapterAnnotatedClass adapterAnnotatedClass = new AdapterAnnotatedClass(annotationClassElement, typeMirrorList, adapterBeanElements, bindElements, mElements);
        adapterAnnotatedClass.createJavaFile(mFiler);
    }
//
//
//    private ClassName createBeanBinder(Element beanElement, List<Element> bindElements) throws IOException {
////        TypeElement beanTypeElement = (TypeElement) beanElement;
////        ClassName beanClassName = ClassName.get(beanTypeElement);
////        String beanBinderName = beanTypeElement.getSimpleName().toString() + "_BeanBinder";
////        TypeSpec.Builder beanBinderTypeSpecBuilder = TypeSpec.classBuilder(beanBinderName)
////                .addSuperinterface(CLASSNAME_BEAN_BINDER);
////        MethodSpec.Builder constructorMethodSpecBuilder = MethodSpec.constructorBuilder()
////                .addParameter(CLASSNAME_VIEW, "view")
////                .addModifiers(Modifier.PUBLIC);
////
////        String beanName = beanTypeElement.getSimpleName().toString().toLowerCase();
////
////        MethodSpec.Builder bindDataMethodSpec = MethodSpec.methodBuilder("bindData")
////                .addAnnotation(Override.class)
////                .addParameter(TypeName.OBJECT, "object")
////                .addModifiers(Modifier.PUBLIC)
////                .addStatement("$T isBean = object instanceof $T", TypeName.BOOLEAN, beanTypeElement)
////                .beginControlFlow("if (!isBean)")
////                .addStatement("return")
////                .endControlFlow()
////                .addStatement("$T $L = ($T) object", beanClassName, beanName, beanClassName);
////
////        for (Element element : bindElements) {
////            ExecutableElement executableElement = (ExecutableElement) element;
////            String beanMethodName = executableElement.getSimpleName().toString();
////            Integer viewId = null;
////            TypeMirror viewClassTypeMirror = null;
////            TypeMirror binderClassTypeMirror = null;
////            for (AnnotationMirror annotationMirror : executableElement.getAnnotationMirrors()) {
////
////
////                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
////                    if ("viewId".equals(entry.getKey().getSimpleName().toString())) {
////                        viewId = (Integer) entry.getValue().getValue();
////                    } else if ("viewClass".equals(entry.getKey().getSimpleName().toString())) {
////                        viewClassTypeMirror = (TypeMirror) entry.getValue().getValue();
////                    } else if ("binderClass".equals(entry.getKey().getSimpleName().toString())) {
////                        binderClassTypeMirror = (TypeMirror) entry.getValue().getValue();
////                    }
////                }
////            }
////
////            //生成 view 成员变量
////            ClassName viewTypeName = (ClassName) TypeName.get(viewClassTypeMirror);
////            String viewFieldName = viewTypeName.simpleName().toLowerCase() + String.valueOf(viewId);
////            FieldSpec viewFieldSpec = FieldSpec
////                    .builder(viewTypeName, viewFieldName)
////                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
////                    .build();
////            beanBinderTypeSpecBuilder.addField(viewFieldSpec);
////
////            //初始化 view
////            constructorMethodSpecBuilder.addStatement("this.$L = ($T) view.findViewById($L)", viewFieldName, viewTypeName, viewId);
////
////
////            //生成 ViewValueBinder 成员变量
////            ClassName binderTypeName = (ClassName) TypeName.get(binderClassTypeMirror);
////            String binderFieldName = binderTypeName.simpleName().toLowerCase() + String.valueOf(viewId);
////            FieldSpec binderFieldSpec = FieldSpec
////                    .builder(binderTypeName, binderFieldName)
////                    .addModifiers(Modifier.PRIVATE)
////                    .build();
////            beanBinderTypeSpecBuilder.addField(binderFieldSpec);
////
////            //初始化 binder
////            constructorMethodSpecBuilder.addStatement("this.$L = new $T()", binderFieldName, binderTypeName);
////
////            //在 bindData 里面绑定数据
////            bindDataMethodSpec.addStatement("this.$L.bind(this.$L,$L.$L())", binderFieldName, viewFieldName, beanName, beanMethodName);
////
////
////        }
////        beanBinderTypeSpecBuilder
////                .addMethod(constructorMethodSpecBuilder.build())
////                .addMethod(bindDataMethodSpec.build());
////
////        String packageName = mElements.getPackageOf(beanElement).getQualifiedName().toString();
////        JavaFile.builder(packageName, beanBinderTypeSpecBuilder.build()).build().writeTo(mFiler);
////
////        return Pair.create(packageName,beanBinderName);
//        TypeElement typeElement = (TypeElement) beanElement;
//        String packageName = mElements.getPackageOf(beanElement).getQualifiedName().toString();
//        BeanBinderAnnotatedClass beanBinderAnnotatedClass = new BeanBinderAnnotatedClass(typeElement, bindElements);
//
//        return beanBinderAnnotatedClass.createJavaFileAndReturnBeanBindClassName(mFiler, packageName);
//    }


    //给开发者提供错误信息
    private void error(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> strings = new LinkedHashSet<>();
        strings.add(AdapterBean.class.getCanonicalName());
        strings.add(Bind.class.getCanonicalName());
        strings.add(MultipleItem.class.getCanonicalName());
        return strings;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
