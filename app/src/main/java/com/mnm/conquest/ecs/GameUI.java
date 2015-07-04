package com.mnm.conquest.ecs;

import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

public class GameUI
{
    private GoogleMap map;
    private SparseArray<Marker> markers;
    private HashMap<Marker, Entity> entities;

    public GameUI()
    {
        markers = new SparseArray<>();
        entities = new HashMap<>();
    }

    public void setMap(GoogleMap m)
    {
        map = m;
    }

    public GoogleMap getMap()
    {
        return map;
    }

    public SparseArray<Marker> getMarkers()
    {
        return markers;
    }

    public Marker getMarker(int id)
    {
        return markers.get(id);
    }

    public Entity getEntity(Marker m)
    {
        return entities.get(m);
    }

    public void insert(Entity entity, Marker marker)
    {
        markers.put(entity.getId(), marker);
        entities.put(marker, entity);
    }

    public void remove(Marker marker)
    {
        Entity entity = entities.remove(marker);
        markers.remove(entity.getId());
    }

    public void remove(int id)
    {
        Marker marker = markers.get(id);
        markers.remove(id); 
        entities.remove(marker);
    }
}
