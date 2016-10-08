package com.github.scache.callbackattacher.processor;

import com.squareup.javapoet.JavaFile;

import java.util.List;

import javax.lang.model.element.Element;

public interface ProcessorUnit {

    JavaFile createJavaFile(Element enclosingElement, List<Element> fields);

}
