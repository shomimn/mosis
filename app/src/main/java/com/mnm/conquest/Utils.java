package com.mnm.conquest;


import android.graphics.Point;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mnm.conquest.ecs.Component;
import com.mnm.conquest.ecs.Entity;

public class Utils
{
    static void findRotation(Entity e1, Entity e2, Projection projection)
    {
        double rot = 0;

        Point first = projection.toScreenLocation(((Component.Position)e1.getComponent(Component.POSITION)).getLatLng());
        Point second = projection.toScreenLocation(((Component.Position)e2.getComponent(Component.POSITION)).getLatLng());

        double des = Math.sqrt(Math.pow(first.x - second.x,2) + Math.pow(first.y - second.y,2));
        double a = Math.abs(first.y - second.y);
        rot = Math.acos(a/des);

        if(first.x < second.x)
        {
            if(first.y > second.y)
            {
                ((Component.Rotation)e1.getComponent(Component.ROTATION)).setRotation((float)rot);
                ((Component.Rotation)e2.getComponent(Component.ROTATION)).setRotation((float)(- 180 + rot));
            }
            else
            {
                ((Component.Rotation)e1.getComponent(Component.ROTATION)).setRotation(180 - (float)rot);
                ((Component.Rotation)e2.getComponent(Component.ROTATION)).setRotation(-(float)rot);
            }
        }
        else
        {
            if (first.y > second.y)
            {
                ((Component.Rotation)e1.getComponent(Component.ROTATION)).setRotation(-(float)rot);
                ((Component.Rotation)e2.getComponent(Component.ROTATION)).setRotation(180 - (float)rot);
            }
            else
            {
                ((Component.Rotation)e2.getComponent(Component.ROTATION)).setRotation((float)rot);
                ((Component.Rotation)e1.getComponent(Component.ROTATION)).setRotation((float)(- 180 + rot));
            }
        }
    }
}
