package com.mnm.conquest.ecs;

import android.location.Location;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mnm.conquest.ConquestApplication;
import com.mnm.conquest.PlayerInfo;
import com.mnm.conquest.ServerConnection;
import com.mnm.conquest.Task;
import com.mnm.conquest.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Game
{
    private static Game instance = new Game();
    private static GameUI gameUi;
    private static PlayerInfo playerInfo = null;

    private static EntityManager entityManager;
    private static System movement;
    private static System combat;
    private static System graphics;
    private static System animation;

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

//            movement.update();
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

        gameUi = new GameUI();

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

            Marker m = gameUi.getMap().addMarker(new MarkerOptions().position(position.getLatLng()).icon(appearance.getIcon()).alpha((float)health.getHealth() / 100));
            gameUi.insert(e, m);
        }
    }

    public static void createPlayer(LatLng position, float rotation)
    {
        try
        {
            Entity player = entityManager.createUnit(position, rotation, playerInfo.getData());

            MarkerOptions options = new MarkerOptions();
            options.position(position).rotation(rotation).icon(BitmapDescriptorFactory.fromResource(playerInfo.getMarkerId())).anchor(0.5f, 0.5f);

            Marker m = gameUi.getMap().addMarker(options);
            gameUi.insert(player, m);

            playerInfo.setMarker(m);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void setMap(GoogleMap m)
    {
        gameUi.setMap(m);
    }

    public static SparseArray<Marker> getMarkers()
    {
        return gameUi.getMarkers();
    }

    public static GameUI ui()
    {
        return gameUi;
    }

    public static EventManager getEventManager()
    {
        return eventManager;
    }

    public static PlayerInfo getPlayerInfo()
    {
        return playerInfo;
    }

    public static void setPlayerInfo(PlayerInfo info)
    {
        playerInfo = info;
    }

    public static void playerPositionChanged(Location position)
    {
        Marker marker = playerInfo.getMarker();
        Entity entity = gameUi.getEntity(marker);

        Component.Position cPosition = entity.getComponent(Component.POSITION);
        Component.Rotation cRotation = entity.getComponent(Component.ROTATION);

        cPosition.setLatLng(new LatLng(position.getLatitude(), position.getLongitude()));
        cRotation.setRotation(position.getBearing());
    }

    public static void asyncUpdate(String payload)
    {
        try
        {
            JSONObject object = new JSONObject(payload);
            int type = object.getInt("type");

            Log.d("async", payload);

            switch(type)
            {
                case ServerConnection.Request.INIT:
                    createEntities(object.getJSONArray("data"));
                    break;
                case ServerConnection.Request.POSITION:
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void createEntities(JSONArray objects)
    {
        try
        {
            for (int i = 0; i < objects.length(); ++i)
            {
                JSONObject object = objects.getJSONObject(i);

                double lat = object.getDouble("latitude");
                double lng = object.getDouble("longitude");
                String markerString = object.getString("marker");

                int id = ConquestApplication.getContext().getResources().getIdentifier(markerString, "id", ConquestApplication.getContext().getPackageName());
                LatLng position = new LatLng(lat, lng);

                Entity player = entityManager.createUnit(position, 0, object);

                MarkerOptions options = new MarkerOptions();
                options.position(position).icon(BitmapDescriptorFactory.fromResource(id)).anchor(0.5f, 0.5f);

                Marker m = gameUi.getMap().addMarker(options);
                gameUi.insert(player, m);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
