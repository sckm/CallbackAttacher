package com.github.scache.callbackattacher.processor.impl;

import com.github.scache.callbackattacher.processor.ProcessorUnit;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.github.scache.callbackattacher.processor.util.ElementUtil.getPackage;
import static com.github.scache.callbackattacher.processor.util.ElementUtil.getPackageName;

public class AttacherProcessorUnit implements ProcessorUnit {

    private Element parentElement;

    public AttacherProcessorUnit(Element parentElement) {
        this.parentElement = parentElement;
    }

    @Override public JavaFile createJavaFile(Element enclosingElement, List<Element> fields) {
        String packageName = getPackageName(enclosingElement);

        return JavaFile
                .builder(packageName, createTypeSpec(enclosingElement, fields))
                .build();
    }

    private TypeSpec createTypeSpec(Element enclosingElement, List<Element> fields) {
        TypeName targetType = TypeName.get(enclosingElement.asType());
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }

        ClassName className = createClassName(enclosingElement);

        TypeSpec.Builder builder = TypeSpec
                .classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addField(targetType, "target", Modifier.PRIVATE);

        builder.addMethod(createAttachMethod(enclosingElement, fields))
                .addMethod(createDetachMethod(enclosingElement, fields));

        return builder.build();
    }

    private ClassName createClassName(Element element) {
        // remove package name. replace dots(.) to $ if enclosing element is inner class
        TypeElement enclosingTypeElement = (TypeElement) element;
        String packageName = getPackage(element).getQualifiedName().toString();
        String classNameString = enclosingTypeElement.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');

        return ClassName.get(packageName, classNameString + "_CallbackAttacher");
    }

    private MethodSpec createAttachMethod(Element enclosingElement, List<Element> fields) {
        TypeName targetType = TypeName.get(enclosingElement.asType());
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }

        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("attach")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(targetType, "target")
                .addParameter(TypeName.OBJECT, "source");

        if (parentElement != null) {
            builder.addStatement("$T.attach(target, source)", createClassName(parentElement));
        }

        for (Element element : fields) {
            TypeName fieldTypeName = TypeName.get(element.asType());
            builder.addCode("$[if(source instanceof $T)\n"
                            + "target.$L = ($T)source;$]\n"
                            + "$[else\n"
                            + "throw new RuntimeException(source.toString() + \"must implement $T\");$]"
                    , fieldTypeName, element.getSimpleName(), fieldTypeName, fieldTypeName);
            builder.addCode("\n");
        }
        return builder.build();
    }

    private MethodSpec createDetachMethod(Element enclosingElement, List<Element> fields) {
        TypeName targetType = TypeName.get(enclosingElement.asType());
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("detach")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(targetType, "target");

        if (parentElement != null) {
            builder.addStatement("$T.detach(target)", createClassName(parentElement));
        }

        for (Element element : fields) {
            builder.addStatement("target.$L = null", element.getSimpleName().toString());
        }

        return builder.build();
    }

}
