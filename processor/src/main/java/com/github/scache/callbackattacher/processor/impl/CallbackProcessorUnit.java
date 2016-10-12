package com.github.scache.callbackattacher.processor.impl;

import com.github.scache.callbackattacher.processor.ProcessorUnit;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.github.scache.callbackattacher.processor.util.ElementUtil.getPackage;

public class CallbackProcessorUnit implements ProcessorUnit {

    @Override public JavaFile createJavaFile(Element enclosingElement, List<Element> fields) {
        String packageName = getPackage(enclosingElement).getQualifiedName().toString();

        return JavaFile.builder(packageName, createTypeSpec(enclosingElement, fields))
                .build();
    }

    private TypeSpec createTypeSpec(Element enclosingElement, List<Element> fields) {
        return TypeSpec.classBuilder(getGeneratedClassName((TypeElement) enclosingElement))
                .addMethod(generateAttachMethod(enclosingElement, fields))
                .addMethod(generateDetachMethod(enclosingElement, fields))
                .build();
    }

    private MethodSpec generateDetachMethod(Element enclosingElement, List<Element> fields) {
        return MethodSpec.methodBuilder("detachCallback")
                .addModifiers(Modifier.PUBLIC)
                .build();
    }

    private MethodSpec generateAttachMethod(Element enclosingElement, List<Element> fields) {
        return MethodSpec.methodBuilder("attachCallback")
                .addModifiers(Modifier.PUBLIC)
                .build();
    }

    protected String getGeneratedClassName(TypeElement source) {
        String packageName = getPackage(source).getQualifiedName().toString();
        String className = source.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');
        ClassName bindingClassName = ClassName.get(packageName, className + "_CallbackAttacher");

        return bindingClassName.toString();
    }
}
