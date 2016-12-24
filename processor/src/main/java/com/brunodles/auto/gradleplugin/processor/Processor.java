package com.brunodles.auto.gradleplugin.processor;

import com.brunodles.auto.gradleplugin.Plugin;
import com.github.brunodles.annotationprocessorhelper.ProcessorBase;
import com.github.brunodles.annotationprocessorhelper.SupportedAnnotations;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SupportedAnnotations(Plugin.class)
@com.google.auto.service.AutoService(javax.annotation.processing.Processor.class)
public class Processor extends ProcessorBase {

    private static final String TAG = "Processor";

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeMirror, String[]> objects = new HashMap<javax.lang.model.type.TypeMirror, String[]>();
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                Plugin plugin = element.getAnnotation(Plugin.class);
                if (plugin == null) continue;
                String[] value = plugin.value();
//                log(TAG, String.format("%s - %s", element.asType().toString(), value));
                objects.put(element.asType(), value);
            }
        }
        if (objects.size() == 0) return false;
        for (Map.Entry<TypeMirror, String[]> entry : objects.entrySet()) {
            String name = entry.getKey().toString();
            tryWriteResources(name, name);
            for (String s : entry.getValue()) {
                tryWriteResources(s, name);
            }
        }
        return true;
    }

    private void tryWriteResources(String pluginKey, String implementationClass) {
        try {
            writeResources(pluginKey, implementationClass);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            fatalError(TAG, e.getLocalizedMessage());
        }
    }

    private void writeResources(String pluginKey, String implementationClass) throws IOException {
        log(TAG, String.format("%s - %s", pluginKey, implementationClass));
        if ("".equals(pluginKey)) pluginKey = implementationClass;
        String filename = "META-INF/services/" + pluginKey + ".properties";
        FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", filename);
        writeFile(resource, String.format("implementation-class=%s", implementationClass));
    }
}
