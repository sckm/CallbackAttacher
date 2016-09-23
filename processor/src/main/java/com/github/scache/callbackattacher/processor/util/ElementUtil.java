package com.github.scache.callbackattacher.processor.util;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;

public class ElementUtil {
    public static PackageElement getPackage(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }

        return (PackageElement) element;
    }

    public static String getPackageName(Element element) {
        return getPackage(element).getQualifiedName().toString();
    }
}
