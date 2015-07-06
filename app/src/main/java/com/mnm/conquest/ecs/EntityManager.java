package com.mnm.conquest.ecs;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
        Entity entity = new Entity.Unit();

        entity.addComponent(new Component.Position(position))
                .addComponent(new Component.Appearance(ConquestApplication.getContext().getResources()
                        .getIdentifier(data.getString("marker"), "id", ConquestApplication.getContext().getPackageName())))
                .addComponent(new Component.Health(data.getInt("health")))
                .addComponent(new Component.Attack(data.getInt("attack")))
                .addComponent(new Component.Rotation(rotation))
                .addComponent(new Component.Player(data.getString("username")))
                .addComponent(new Component.Army(data.getInt("interceptors"), data.getInt("scouts"),
                                                 data.getInt("fighters"), data.getInt("gunships"), data.getInt("bombers")));

//        String marker = data.getString("marker");
//        marker = marker.substring(0, marker.length() - 1);
//        Component.Animation anim = new Component.Animation();
//        entity.addComponent(anim);
//
//        for (int i = 1; i <= 5; ++i)
//            anim.addFrame(ConquestApplication.getContext().getResources()
//                    .getIdentifier(marker + String.valueOf(i), "id", ConquestApplication.getContext().getPackageName()));

        entities.add(entity);

        return entity;
    }

    public Entity createFortress(LatLng position, JSONObject data) throws JSONException
    {
        Entity entity = new Entity.Fortress();

        entity.addComponent(new Component.Position(position))
                .addComponent(new Component.Appearance(ConquestApplication.getContext().getResources()
                        .getIdentifier(data.getString("marker"), "id", ConquestApplication.getContext().getPackageName())))
                .addComponent(new Component.Health(data.getInt("health")))
                .addComponent(new Component.Attack(data.getInt("attack")))
                .addComponent(new Component.Player(data.getString("username")));

        entities.add(entity);

        return entity;
    }
}
