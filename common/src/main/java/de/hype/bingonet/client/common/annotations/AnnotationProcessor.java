package de.hype.bingonet.client.common.annotations;

import de.hype.bingonet.client.common.chat.IsABBChatModule;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;

public class AnnotationProcessor {
    private Map<Class<? extends Event>, Map<String, Method>> eventHandlers = new HashMap<>();
    private Map<Method, Object> handlerInstances = new HashMap<>();
    private Map<String, Boolean> featureStatus = new HashMap<>();

    public AnnotationProcessor(){
        Reflections reflections = new Reflections("de.hype.bingonet"); // Ersetze durch dein Paketnamen
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(IsABBChatModule.class);

        // Erstelle Instanzen der gefundenen Klassen und registriere sie
        for (Class<?> clazz : annotatedClasses) {
            try {
                register(clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void register(Class<?> listener) {
        for (Method method : listener.getDeclaredMethods()) {
            if (method.isAnnotationPresent(MessageSubscribe.class)) {
                MessageSubscribe annotation = method.getAnnotation(MessageSubscribe.class);
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0])) {
                    Class<? extends Event> eventType = (Class<? extends Event>) parameterTypes[0];
                    eventHandlers.computeIfAbsent(eventType, k -> new HashMap<>()).put(annotation.name(), method);
                    try {
                        handlerInstances.put(method, listener.getDeclaredConstructor().newInstance());
                        featureStatus.put(annotation.name(), annotation.enabled());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void triggerEvent(Event event) {
        Map<String, Method> handlers = eventHandlers.get(event.getClass());
        if (handlers == null) handlers = eventHandlers.get(event.getClass().getSuperclass());
        if (handlers != null) {
            for (Map.Entry<String, Method> entry : handlers.entrySet()) {
                if (featureStatus.getOrDefault(entry.getKey(), false)) {
                    try {
                        entry.getValue().invoke(handlerInstances.get(entry.getValue()), event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setFeatureStatus(String name, boolean enabled) {
        featureStatus.put(name, enabled);
        saveFeatureStatus(); // Persist the status
    }

    public boolean getFeatureStatus(String name) {
        return featureStatus.getOrDefault(name, false);
    }

    public List<String> getFeaturesByName(String name) {
        List<String> features = new ArrayList<>();
        for (String key : featureStatus.keySet()) {
            if (key.contains(name)) {
                features.add(key);
            }
        }
        return features;
    }

    private void saveFeatureStatus() {
        // Save featureStatus to a persistent storage (file, database, etc.)
        // Implement your persistence logic here
    }

    private void loadFeatureStatus() {
        // Load featureStatus from persistent storage
        // Implement your loading logic here
    }
    public interface Event {
        // Marker interface for events
    }
}

