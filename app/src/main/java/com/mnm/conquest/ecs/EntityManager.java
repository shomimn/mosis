package com.mnm.conquest.ecs;

import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.mnm.conquest.R;

import java.util.ArrayList;
import java.util.HashMap;

public class EntityManager
{
    private ArrayList<Entity> entities = new ArrayList<Entity>();

    public EntityManager()
    {
        Entity green = new Entity.Unit();
        Entity red = new Entity.Unit();
        Entity blue = new Entity.Unit();

        entities.add(green);
        entities.add(red);
        entities.add(blue);

        green.addComponent(new Component.Health(100)).addComponent(new Component.Attack(10))
                .addComponent(new Component.Position(new LatLng(43.3, 21.1)))
                .addComponent(new Component.Appearance(BitmapDescriptorFactory.fromResource(R.mipmap.green_marker)));

        green.addComponent(new Component.Animation().addFrame(BitmapDescriptorFactory.fromResource(R.mipmap.green_marker))
                .addFrame(BitmapDescriptorFactory.fromResource(R.mipmap.green_marker1)).addFrame(BitmapDescriptorFactory.fromResource(R.mipmap.green_marker2)));

        red.addComponent(new Component.Health(66)).addComponent(new Component.Attack(5))
                .addComponent(new Component.Position(new LatLng(43.3, 21.50)))
                .addComponent(new Component.Appearance(BitmapDescriptorFactory.fromResource(R.mipmap.red_marker)));

        blue.addComponent(new Component.Health(33))
                .addComponent(new Component.Position(new LatLng(43.3, 22.0)))
                .addComponent(new Component.Appearance(BitmapDescriptorFactory.fromResource(R.mipmap.blue_marker)));
    }

    public ArrayList<Entity> getEntities()
    {
        return entities;
    }
}
