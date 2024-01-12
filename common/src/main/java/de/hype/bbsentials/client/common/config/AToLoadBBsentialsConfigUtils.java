package de.hype.bbsentials.client.common.config;

import com.google.common.reflect.ClassPath;
import de.hype.bbsentials.client.common.client.BBsentials;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class AToLoadBBsentialsConfigUtils {

    public static List<Class<BBsentialsConfig>> getAnnotatedClasses() {
        List<Class<BBsentialsConfig>> annotatedClasses = new ArrayList<>();
        Package pack = AToLoadBBsentialsConfigUtils.class.getPackage();
        String packageName = (pack != null) ? pack.getName() : "";
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            for (ClassPath.ClassInfo classInfo : ClassPath.from(classLoader).getTopLevelClasses(packageName)) {
                Class<?> clazz = classInfo.load();
                System.out.println("Checking class: " + clazz.getName());

                // Use direct annotation name check
                for (Annotation annotation : clazz.getAnnotations()) {
                    if (annotation.annotationType().getSimpleName().equals(AToLoadBBsentialsConfig.class.getSimpleName())) {
                    annotatedClasses.add((Class<BBsentialsConfig>) clazz);
                    System.out.println("Found annotated class: " + clazz.getName());}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return annotatedClasses;
    }
}
