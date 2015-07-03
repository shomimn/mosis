package com.mnm.conquest.ecs;

import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mnm.conquest.Task;
import com.mnm.conquest.TaskManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Game
{
    private static Game instance = new Game();

    private static EntityManager entityManager;
    private static System movement;
    private static System combat;
    private static System graphics;
    private static System animation;

    private static GoogleMap map;
//    private static HashMap<Integer, Marker> markers;
    private static SparseArray<Marker> markers;

    private static EventManager eventManager;

    private static Task.Ui updateTask = new Task.Ui(Task.GENERAL)
    {
        @Override
        public void execute()
        {
//            for (int i = 0; i < 30; i += 3)
//            {
//                Event.Combat event = new Event.Combat();
//                event.attacker = getEntityManager().getEntities().get(i);
//                event.defender = getEntityManager().getEntities().get(i + 1);
//
//                eventManager.emit(event);
//            }

            movement.update();
            animation.update();
        }

        @Override
        public void uiExecute()
        {
            graphics.update();
        }
    };

    private static Runnable loop = new Runnable()
    {
        @Override
        public void run()
        {
//            Event.Combat event = new Event.Combat();
//            event.attacker = getEntityManager().getEntities().get(0);
//            event.defender = getEntityManager().getEntities().get(1);
//
//            eventManager.emit(event);
//
//            movement.update();
//            animation.update();
//            graphics.update();

            TaskManager.getTaskManager().executeAndPost(updateTask);

            TaskManager.getMainHandler().postDelayed(this, 150);
        }
    };

    private Game()
    {
        eventManager = new EventManager();

        entityManager = new EntityManager();

//        markers = new HashMap<Integer, Marker>();
        markers = new SparseArray<>();

        movement = new System.Movement();
        combat = new System.Combat();
        graphics = new System.Graphics();
        animation = new System.Animation();
    }

    public static EntityManager getEntityManager()
    {
        return entityManager;
    }

    public static void play()
    {
        createMarkers();

        TaskManager.getMainHandler().post(loop);
    }

    public static void stop()
    {
        TaskManager.getMainHandler().removeCallbacks(loop);
    }

    public static void createMarkers()
    {
        ArrayList<Entity> entities = entityManager.getEntities();

        for (Entity e : entities)
        {
            Component.Position position = e.getComponent(Component.POSITION);
            Component.Appearance appearance = e.getComponent(Component.APPEARANCE);
            Component.Health health = e.getComponent(Component.HEALTH);

            Marker m = map.addMarker(new MarkerOptions().position(position.getLatLng()).icon(appearance.getIcon()).alpha((float)health.getHealth() / 100));
            markers.put(e.getId(), m);
        }
    }

    public static void setMap(GoogleMap m)
    {
        map = m;
    }

//    public static HashMap<Integer, Marker> getMarkers()
//    {
//        return markers;
//    }

    public static SparseArray<Marker> getMarkers()
    {
        return markers;
    }

    public static EventManager getEventManager()
    {
        return eventManager;
    }
}
