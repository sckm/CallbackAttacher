package com.github.scache.callbackattacher.processor;


import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.github.scache.callbackattacher.processor.AttachCallback")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CallbackAttacherProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        StringBuilder sb = new StringBuilder()
                .append("package com.github.scache.callbackattacher.processor.generated;\n\n")
                .append("public class GeneratedClass {\n\n") // open class
                .append("\tpublic String getMessage() {\n") // open method
                .append("\t\treturn \"");

        for (Element element : roundEnv.getElementsAnnotatedWith(AttachCallback.class)) {
            String objectType = element.getSimpleName().toString();

            sb.append(objectType).append((": hello world!\\n"));
        }

        sb.append("\";\n")
                .append("\t}\n")
                .append("}\n");

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile("com.github.scache.callbackattacher.processor.generated.GeneratedClass");

            Writer writer = source.openWriter();
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
