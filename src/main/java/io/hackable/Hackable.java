package io.hackable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class Hackable {

    private static final Map<String, List<Consumer<?>>> eventHandlersMap = new HashMap<>();
    private static final Map<String, List<Filter<?>>> filterHandlersMap = new HashMap<>();

    private Hackable(){}

    public static <T> void on(String eventName, Consumer<T> eventHandler) {
        doOn(resolveHackableHandlerKey(eventName, null), eventHandler);
    }

    public static <T> void on(String eventName, Class contextClass, Consumer<T> eventHandler) {
        doOn(resolveHackableHandlerKey(eventName, contextClass), eventHandler);
    }

    private static <T> void doOn(String eventHostKey, Consumer<T> eventHandler) {
        List<Consumer<?>> existingHandlers = eventHandlersMap.get(eventHostKey);
        if(existingHandlers == null) {
            existingHandlers = new ArrayList<>();
            eventHandlersMap.put(eventHostKey, existingHandlers);
        }
        existingHandlers.add(eventHandler);
    }

    public static <T> void trigger(String eventName, T eventData) {
        doTrigger(resolveHackableHandlerKey(eventName, null), eventData);
    }

    public static <T> void trigger(String eventName, Class contextClass, T eventData) {
        doTrigger(resolveHackableHandlerKey(eventName, contextClass), eventData);
    }

    private static <T> void doTrigger(String eventHostKey, T eventData) {
        List<Consumer<?>> consumers = eventHandlersMap.get(eventHostKey);
        if(consumers != null) {
            for(Consumer<?> consumer : consumers) {
                ((Consumer<T>)consumer).accept(eventData);
            }
        }
    }

    public static <R> void onFilter(String filterName, Filter<R> filterHandler) {
        doOnFilter(resolveHackableHandlerKey(filterName, null), filterHandler);
    }

    public static <R> void onFilter(String filterName, Class contextClass, Filter<R> filterHandler) {
        doOnFilter(resolveHackableHandlerKey(filterName, contextClass), filterHandler);
    }

    private static <R> void doOnFilter(String filterHostKey, Filter<R> filterHandler) {
        List<Filter<?>> existingHandlers = filterHandlersMap.get(filterHostKey);
        if(existingHandlers == null) {
            existingHandlers = new LinkedList<>();
            filterHandlersMap.put(filterHostKey, existingHandlers);
        }
        existingHandlers.add(filterHandler);
    }

    public static <R> R applyFilter(String filterName, R filterableObject) {
        return doApplyFilter(resolveHackableHandlerKey(filterName, null), filterableObject);
    }

    public static <R> R applyFilter(String filterName, Class contextClass, R filterableObject) {
        return doApplyFilter(resolveHackableHandlerKey(filterName, contextClass), filterableObject);
    }

    private static <R> R doApplyFilter(String filterHostKey, R filterableObject) {
        List<Filter<?>> filters = filterHandlersMap.get(filterHostKey);
        if(filters != null) {
            R fobj = filterableObject;
            for(Filter f : filters) {
                fobj = (R)f.filter(fobj);
            }
            return fobj;
        }
        return filterableObject;
    }

    private static String resolveHackableHandlerKey(String name, Class contextClass) {
        return (contextClass != null ? contextClass.getCanonicalName() : "") + ":" + name;
    }

}
