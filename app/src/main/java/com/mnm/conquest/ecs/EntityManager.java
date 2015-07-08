package com.mnm.conquest.ecs;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mnm.conquest.ConquestApplication;
import com.mnm.conquest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

public class EntityManager
{
    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private SparseArray<Class<?>> componentClasses = new SparseArray<>();
    private HashMap<String, Entity> userToEntity = new HashMap<>();

    public EntityManager()
    {
//        Entity green = new Entity.Unit();
//        Entity red = new Entity.Unit();
//        Entity blue = new Entity.Unit();
//
//        entities.add(green);
//        entities.add(red);
//        entities.add(blue);
//
//        green.addComponent(new Component.Health(100))
//                .addComponent(new Component.Attack(1))
//                .addComponent(new Component.Position(new LatLng(43.3, 21.1)))
//                .addComponent(new Component.Appearance(R.mipmap.air1));
//
//        green.addComponent(new Component.Animation().addFrame(R.mipmap.air1)
//                .addFrame(R.mipmap.air2).addFrame(R.mipmap.air3)
//                .addFrame(R.mipmap.air4).addFrame(R.mipmap.air5));
//
//        red.addComponent(new Component.Health(100)).addComponent(new Component.Attack(5))
//                .addComponent(new Component.Position(new LatLng(43.3, 22.0)))
//                .addComponent(new Component.Appearance(R.mipmap.red_marker));
//
//        red.addComponent(new Component.Animation().addFrame(R.mipmap.air1)
//                .addFrame(R.mipmap.air2).addFrame(R.mipmap.air3)
//                .addFrame(R.mipmap.air4).addFrame(R.mipmap.air5));
//
//        blue.addComponent(new Component.Health(100)).addComponent(new Component.Attack(1))
//                .addComponent(new Component.Position(new LatLng(43.3, 23.0)))
//                .addComponent(new Component.Appearance(R.mipmap.blue_marker));
//
//        blue.addComponent(new Component.Animation().addFrame(R.mipmap.air1)
//                .addFrame(R.mipmap.air2).addFrame(R.mipmap.air3)
//                .addFrame(R.mipmap.air4).addFrame(R.mipmap.air5));

        componentClasses.put(Component.ANIMATION, Component.Animation.class);
        componentClasses.put(Component.APPEARANCE, Component.Appearance.class);
        componentClasses.put(Component.ATTACK, Component.Attack.class);
        componentClasses.put(Component.HEALTH, Component.Health.class);
        componentClasses.put(Component.PLAYER, Component.Player.class);
        componentClasses.put(Component.POSITION, Component.Position.class);
        componentClasses.put(Component.ROTATION, Component.Rotation.class);
    }

    public ArrayList<Entity> getEntities()
    {
        return entities;
    }

    private Class<?> getComponentClass(int type)
    {
        return componentClasses.get(type);
    }

    public Entity createUnit(int mask)
    {
        Entity entity = new Entity.Unit();

        for (int i = 0; i < 7; ++i)
        {
            int type = 1 << i;
            if ((type & mask) == type)
                entity.addComponent(createComponent(type));
        }
        entities.add(entity);

        return entity;
    }

    private Component createComponent(int type)
    {
        try
        {
            Class<?> componentClass = getComponentClass(type);
            return (Component)componentClass.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public Entity createUnit(LatLng position, float rotation, JSONObject data) throws JSONException
    {
        rotation = 30;

        Entity entity = new Entity.Unit();

        entity.addComponent(new Component.Position(position))
                .addComponent(new Component.Appearance(ConquestApplication.getContext().getResources()
                        .getIdentifier(data.getString("marker"), "id", ConquestApplication.getContext().getPackageName())))
                .addComponent(new Component.Health(data.getInt("health")))
                .addComponent(new Component.Attack(data.getInt("attack")))
                .addComponent(new Component.Rotation(rotation))
                .addComponent(new Component.Player(data.getString("username")))
                .addComponent(new Component.Army(data.getInt("interceptors"), data.getInt("scouts"),
                                data.getInt("fighters"), data.getInt("gunships"), data.getInt("bombers")))
                .addComponent(new Component.Coins(data.getInt("coins")));

        Entity dummy = new Entity.Unit();

        rotation = 30 + 180;

        dummy.addComponent(new Component.Position(new LatLng(position.latitude + 0.01f, position.longitude + 0.01f)))
                .addComponent(new Component.Appearance(ConquestApplication.getContext().getResources()
                        .getIdentifier(data.getString("marker"), "id", ConquestApplication.getContext().getPackageName())))
                .addComponent(new Component.Health(data.getInt("health")))
                .addComponent(new Component.Attack(data.getInt("attack")))
                .addComponent(new Component.Rotation(rotation))
                .addComponent(new Component.Player("dummy"))
                .addComponent(new Component.Army(data.getInt("interceptors"), data.getInt("scouts"),
                        data.getInt("fighters"), data.getInt("gunships"), data.getInt("bombers")))
                .addComponent(new Component.Coins(data.getInt("coins")));

        String marker = data.getString("marker");
//        marker = marker.substring(0, marker.length() - 1);
        Component.Animation anim = new Component.Animation();
        entity.addComponent(anim);


        anim.addMoveFrame(ConquestApplication.getContext().getResources()
                .getIdentifier(marker, "id", ConquestApplication.getContext().getPackageName()));
        for (int i = 1; i <= 5; ++i)
            anim.addMoveFrame(ConquestApplication.getContext().getResources()
                    .getIdentifier(marker + "manim" + String.valueOf(i), "id", ConquestApplication.getContext().getPackageName()));

        anim.addBattleFrame(ConquestApplication.getContext().getResources()
                .getIdentifier(marker, "id", ConquestApplication.getContext().getPackageName()));
        for (int i = 1; i <= 3; ++i)
            anim.addBattleFrame(ConquestApplication.getContext().getResources()
                    .getIdentifier(marker + "banim" + String.valueOf(i), "id", ConquestApplication.getContext().getPackageName()));


        anim = new Component.Animation();
        dummy.addComponent(anim);


        anim.addMoveFrame(ConquestApplication.getContext().getResources()
                .getIdentifier(marker, "id", ConquestApplication.getContext().getPackageName()));
        for (int i = 1; i <= 5; ++i)
            anim.addMoveFrame(ConquestApplication.getContext().getResources()
                    .getIdentifier(marker + "manim" + String.valueOf(i), "id", ConquestApplication.getContext().getPackageName()));

        anim.addBattleFrame(ConquestApplication.getContext().getResources()
                .getIdentifier(marker, "id", ConquestApplication.getContext().getPackageName()));
        for (int i = 1; i <= 3; ++i)
            anim.addBattleFrame(ConquestApplication.getContext().getResources()
                    .getIdentifier(marker + "banim" + String.valueOf(i), "id", ConquestApplication.getContext().getPackageName()));


//        if (!data.getString("username").equals("m"))
//            entity.removeComponent(Component.PLAYER);


        entities.add(entity);
        userToEntity.put(data.getString("username"), entity);

        entities.add(dummy);
        userToEntity.put("dummy", dummy);

        MarkerOptions options = new MarkerOptions();
        options.position(position).rotation(rotation).icon(BitmapDescriptorFactory.fromResource(Game.getPlayerInfo().getMarkerId())).anchor(0.5f, 0.5f).flat(true);

        Marker m = Game.ui().getMap().addMarker(options);
        Game.ui().insert(dummy, m);

        return entity;
    }

    public Entity createFortress(LatLng position, JSONObject data) throws JSONException
    {
        Entity entity = new Entity.Fortress();

        int markerId = ConquestApplication.getContext().getResources()
                .getIdentifier(data.getString("marker"), "id", ConquestApplication.getContext().getPackageName());

        entity.addComponent(new Component.Position(position))
                .addComponent(new Component.Appearance(markerId))
                .addComponent(new Component.Health(data.getInt("health")))
                .addComponent(new Component.Attack(data.getInt("attack")))
                .addComponent(new Component.Player(data.getString("username")));

        entities.add(entity);

        MarkerOptions options = new MarkerOptions();
        options.position(position).icon(BitmapDescriptorFactory.fromResource(markerId)).anchor(0.5f, 0.5f).title(String.valueOf(markerId));
        Marker marker = Game.ui().getMap().addMarker(options);

        Game.ui().insert(entity, marker);

        return entity;
    }

    public Entity getEntity(String username)
    {
        return userToEntity.get(username);
    }

    public void createExplosion(LatLng position)
    {
        Entity entity = new Entity.Unit();

        entity.addComponent(new Component.Position(position))
                .addComponent(new Component.Appearance(R.mipmap.explosion0));

        Component.Animation anim = new Component.Animation().addMoveFrame(R.mipmap.explosion0).addMoveFrame(R.mipmap.explosion1).addMoveFrame(R.mipmap.explosion2);
        anim.setState(Component.Animation.MOVE);

        entity.addComponent(anim);

        entities.add(entity);

        Marker m = Game.ui().getMap().addMarker(new MarkerOptions().position(position).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.explosion0)).flat(true));

        Game.ui().insert(entity, m);
    }

    public void createDetached(Entity owner, LatLng position, LatLng destination, int type)
    {
        Entity entity = new Entity.Detached(type);

        entity.addComponent(new Component.Position(position))
                .addComponent(new Component.Appearance(R.mipmap.interceptor))
                .addComponent(new Component.OwnedBy(owner))
                .addComponent(new Component.Destination(destination))
                .addComponent(new Component.Health(100));

        entities.add(entity);

        Marker m = Game.ui().getMap().addMarker(new MarkerOptions().position(position).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.interceptor)).flat(true));

        Game.ui().insert(entity, m);
    }
}
