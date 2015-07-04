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
    }

    public ArrayList<Entity> getEntities()
    {
        return entities;
    }
}
