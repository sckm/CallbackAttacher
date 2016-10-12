package com.github.scache.callbackattacher.processor;


import com.github.scache.callbackattacher.processor.impl.AttacherProcessorUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;


@SupportedAnnotationTypes("com.github.scache.callbackattacher.processor.AttachCallback")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CallbackAttacherProcessor extends AbstractProcessor {
    HashMap<Element, List<Element>> map;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Filer filer = processingEnv.getFiler();

        map = new HashMap<Element, List<Element>>();

        for (Element element : roundEnv.getElementsAnnotatedWith(AttachCallback.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

            List<Element> fields = getOrCreateFieldsList(enclosingElement);
            fields.add(element);
        }

        for (Map.Entry<Element, List<Element>> entry : map.entrySet()) {
            Element enclosingElement = entry.getKey();
            List<Element> elements = entry.getValue();

            boolean hasError = false;
            for (Element element : elements) {
                hasError |= isInaccessibleFromGeneratedClass(element);
            }

            if (hasError) {
                continue;
            }

            Element parentElement = findParent(enclosingElement, map.keySet());
            try {
                new AttacherProcessorUnit(parentElement)
                        .createJavaFile(enclosingElement, elements)
                        .writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private List<Element> getOrCreateFieldsList(Element enclosingElement) {
        List<Element> fieldElements = map.get(enclosingElement);
        if (fieldElements == null) {
            fieldElements = new ArrayList<Element>();
            map.put(enclosingElement, fieldElements);
        }

        return fieldElements;
    }

    private Element findParent(Element element, Set<Element> parents) {
        while (true) {
            TypeMirror p = ((TypeElement) element).getSuperclass();
            if (p.getKind() == TypeKind.NONE) {
                return null;
            }
            element = ((DeclaredType) p).asElement();
            if (parents.contains(element)) {
                return element;
            }
        }
    }

    private boolean isInaccessibleFromGeneratedClass(Element element) {
        Element enclosingElement = element.getEnclosingElement();

        Set<Modifier> modifiers = element.getModifiers();
        // verify field modifiers
        if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)) {
            error(element, element.getSimpleName() + " must not be private or static.");
            return true;
        }

        // verify member field
        if (enclosingElement.getKind() != ElementKind.CLASS) {
            error(element, element.getSimpleName() + " may only be contained in classes.");
            return true;
        }

        // verify enclosing class is not private
        if (enclosingElement.getModifiers().contains(Modifier.PRIVATE)) {
            error(element, element.getSimpleName() + " may only be contained in private classes.");
            return true;
        }

        return false;
    }

    private void error(Element element, String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
