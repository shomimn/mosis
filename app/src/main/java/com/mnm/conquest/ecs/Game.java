package com.mnm.conquest.ecs;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mnm.conquest.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Game
{
    private static Game instance = new Game();

    private static EntityManager entityManager;
    private static System movement;
    private static System attack;
    private static System graphics;
    private static System animation;

    private static GoogleMap map;
    private static HashMap<Integer, Marker> markers;

    public static Event.System<Event.CombatEvent> eventSystem;

    private static Runnable loop = new Runnable()
    {
        @Override
        public void run()
        {
            movement.update();
            animation.update();
            graphics.update();

            TaskManager.getMainHandler().postDelayed(this, 200);
        }
    };

    private Game()
    {
        eventSystem = new Event.System<>();

        entityManager = new EntityManager();

        markers = new HashMap<Integer, Marker>();

        movement = new System.Movement();
        attack = new System.Combat();
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

    public static HashMap<Integer, Marker> getMarkers()
    {
        return markers;
    }
}
