package com.mnm.conquest.ecs;

import com.google.android.gms.maps.model.BitmapDescriptor;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager
{
    HashMap<Class, ArrayList> handlers;

    public EventManager()
    {
        handlers = new HashMap<>();
    }

    public <T> void register(Class<? extends Event<T>> event, T handler)
    {
        handlersOf(event).add(handler);
    }

    public <T> void unregister(Class<? extends Event<T>> event, T handler)
    {
        handlersOf(event).remove(handler);
    }

    public <T> void emit(Event<T> event)
    {
        Class<Event<T>> eventType = (Class<Event<T>>) event.getClass();
        for (T handler : handlersOf(eventType))
            event.notify(handler);
    }

    private <T> ArrayList<T> handlersOf(Class<? extends Event<T>> event)
    {
        ArrayList<T> eventHandlers = handlers.get(event);
        if (eventHandlers == null)
        {
            eventHandlers = new ArrayList<T>();
            handlers.put(event, eventHandlers);
        }

        return eventHandlers;
    }
}
