package com.brunodles.auto.gradleplugin.processor;

import com.brunodles.auto.gradleplugin.AutoPlugin;
import com.brunodles.annotationprocessorhelper.ProcessorBase;
import com.brunodles.annotationprocessorhelper.SupportedAnnotations;
import com.google.auto.service.AutoService;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.brunodles.annotationprocessorhelper.FileUtils.writeFile;

@SupportedAnnotations(AutoPlugin.class)
@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends ProcessorBase {

    private static final String TAG = "Processor";
    private static final String[] META_DIRS = {"services", "gradle-plugins"};
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
                AutoPlugin plugin = element.getAnnotation(AutoPlugin.class);
                if (plugin == null) continue;
                String[] value = plugin.value();
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
        for (String metaDir : META_DIRS)
            try {
                writeResources(metaDir, pluginKey, implementationClass);
            } catch (IOException e) {
                e.printStackTrace(System.err);
                fatalError(TAG, e.getLocalizedMessage());
            }
    }

    private void writeResources(String metaDir, String pluginKey, String implementationClass) throws IOException {
        log(TAG, String.format("%s - %s", pluginKey, implementationClass));
        if ("".equals(pluginKey)) pluginKey = implementationClass;
        String filename = "META-INF/" + metaDir + "/" + pluginKey + ".properties";
        FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", filename);
        writeFile(resource, String.format("implementation-class=%s", implementationClass));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
