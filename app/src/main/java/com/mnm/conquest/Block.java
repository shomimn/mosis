package com.mnm.conquest;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

/**
 * Created by Milan on 21.6.2015.
 */
public class Block
{
    private static final float TOPZ = 4.0f;
    private static final float NEAR = 3.0f;
    private static final float FAR = 2.0f;
    private PolygonOptions p1;
    private PolygonOptions p2;
    private PolygonOptions p3;
    private PolygonOptions p4;
    private PolygonOptions top;
    private int strength;
    private double height;
    private double width;
    private double length;

    private LatLng pos;

    public Block(LatLng latLng, int s)
    {
        pos = latLng;
        strength = s;

        initBlock();
    }
    private void initBlock()
    {
        double lat = pos.latitude;
        double lon = pos.longitude;
        top = new PolygonOptions().add(new LatLng(lat - length, lon - width),
                new LatLng(lat - length, lon + width),
                new LatLng(lat + length, lon + width),
                new LatLng(lat + length, lon - width))
                .zIndex(TOPZ);

        p1 = new PolygonOptions().add(new LatLng(lat - length, lon - width),
                new LatLng(lat - length, lon + width),
                new LatLng(lat + length, lon + width),
                new LatLng(lat + length, lon - width))
                .zIndex(NEAR);

    }

}
