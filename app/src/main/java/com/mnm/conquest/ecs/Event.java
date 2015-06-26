package com.mnm.conquest.ecs;

import com.google.android.gms.maps.model.BitmapDescriptor;

import java.util.ArrayList;
import java.util.HashMap;

public class Event<T extends Event>
{
    public interface Listener<T extends Event>
    {
        void onRecieve(T event);
    }

    public static class CombatEvent extends Event<CombatEvent>
    {
        public Entity e1;
        public Entity e2;
    }

    public static class System<T extends Event>
    {
        ArrayList<Listener<T>> handlers;

        public System()
        {
            handlers = new ArrayList<>();
        }

        public void register(Listener<T> handler)
        {
            handlers.add(handler);
        }

        public void unregister(Listener<T> handler)
        {
            handlers.remove(handler);
        }

        public void emit(T event)
        {
            for (Listener<T> handler : handlers)
                handler.onRecieve(event);
        }
    }
}
