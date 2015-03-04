package wba.citta.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.EventObject;
import java.util.LinkedHashSet;

public class EventPublisherSupport<E extends EventObject, L extends EventListener> {
    final Class<E> eventClass;
    final Class<L> listenerClass;
    final LinkedHashSet<L> listeners = new LinkedHashSet<L>();

    public EventPublisherSupport(Class<E> eventClass, Class<L> listenerClass) {
        this.eventClass = eventClass;
        this.listenerClass = listenerClass;
    }

    @SuppressWarnings("unchecked")
    public void addEventListener(L listener) {
        final Class<L> klass = (Class<L>) listener.getClass();
        if (!listenerClass.isAssignableFrom(klass))
            throw new IllegalArgumentException(String.format("%s does not implement %s", klass.getName(), listenerClass.getName()));
        listeners.add(listener);
    }

    public void removeEventListener(L listener) {
        listeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public L[] getEventListeners() {
        return (L[]) listeners.toArray();
    }

    public void fire(String methodName, E e) {
        Method method = null;
        try {
            method = listenerClass.getMethod(methodName, eventClass);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(String.format("undefined method %s for %s", methodName, listenerClass.getName()), ex);
        }
        for (final L listener: listeners) {
            try {
                method.invoke(listener, e);
            } catch (IllegalAccessException ex) {
                // should never happen
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex) {
                throw (RuntimeException)ex.getCause();
            }
        }
    }
}
