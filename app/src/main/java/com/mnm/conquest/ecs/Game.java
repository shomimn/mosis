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
import com.mnm.conquest.R;
import com.mnm.conquest.Task;
import com.mnm.conquest.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Game
{
    public static final int NORMAL = 0;
    public static final int DETACHING = 1;

    private static int state = NORMAL;

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

    public static void setState(int s)
    {
        state = s;
    }

    public static int getState()
    {
        return state;
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

    public static void createFortress(LatLng position)
    {
        final double lat = position.latitude;
        final double lng = position.longitude;

        final String markerName = playerInfo.getMarkerName().substring(0, playerInfo.getMarkerName().length() - 1) + "fortress";

        int markerId = ConquestApplication.getContext().getResources().getIdentifier(markerName, "id", ConquestApplication.getContext().getPackageName());

        Entity fortress = new Entity.Fortress();
        fortress.addComponent(new Component.Health(100))
                .addComponent(new Component.Defense(10))
                .addComponent(new Component.Attack(10))
                .addComponent(new Component.Position(position))
                .addComponent(new Component.Appearance(markerId))
                .addComponent(new Component.Army(2, 3, 1, 1, 1));

        entityManager.getEntities().add(fortress);

        MarkerOptions options = new MarkerOptions();
        options.position(position).icon(BitmapDescriptorFactory.fromResource(markerId)).anchor(0.5f, 0.5f).title(String.valueOf(markerId));
        Marker marker = gameUi.getMap().addMarker(options);

        gameUi.insert(fortress, marker);

        TaskManager.getTaskManager().execute(new Task(Task.SERVER)
        {
            @Override
            public void execute()
            {
                ServerConnection.sendFortress(playerInfo.getUsername(), lat, lng, markerName);
            }
        });
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
        final double lat = position.getLatitude();
        final double lng = position.getLongitude();

        Marker marker = playerInfo.getMarker();
        Entity entity = gameUi.getEntity(marker);

        Component.Position cPosition = entity.getComponent(Component.POSITION);
        Component.Rotation cRotation = entity.getComponent(Component.ROTATION);

//        final double lat = cPosition.getLatLng().latitude;
//        final double lng = cPosition.getLatLng().longitude;

        cPosition.setLatLng(new LatLng(lat, lng));
//        cRotation.setRotation(position.getBearing());

        TaskManager.getTaskManager().execute(new Task(Task.SERVER)
        {
            @Override
            public void execute()
            {
                ServerConnection.sendPosition(playerInfo.getUsername(), lat, lng, ServerConnection.Request.POSITION);
            }
        });
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
                    updatePosition(object.getJSONObject("data"));
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
                options.position(position).icon(BitmapDescriptorFactory.fromResource(id)).anchor(0.5f, 0.5f).flat(true);

                Marker m = gameUi.getMap().addMarker(options);
                gameUi.insert(player, m);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void updatePosition(JSONObject object)
    {
        try
        {
            Entity entity = entityManager.getEntity(object.getString("username"));
            Component.Position position = entity.getComponent(Component.POSITION);
            position.setLatLng(new LatLng(object.getDouble("latitude"), object.getDouble("longitude")));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
