package com.mnm.conquest.ecs;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

public abstract class System
{
    public abstract void update();

    public static class Movement extends System
    {
        @Override
        public void update()
        {
            for (Entity e : Game.getEntityManager().getEntities())
            {
                if ((e.getComponentMask() & Component.POSITION) == Component.POSITION)
                {
                    Component.Position c = e.getComponent(Component.POSITION);
                    LatLng oldPos = c.getLatLng();
                    LatLng newPos = new LatLng(oldPos.latitude + 0.01, oldPos.longitude + 0.01);
                    c.setLatLng(newPos);
                }
            }
        }
    }

    public static class Combat extends System
    {
        public Combat()
        {
        }

        @Override
        public void update()
        {

        }
    }

    public static class Animation extends System
    {
        @Override
        public void update()
        {
            for (Entity e : Game.getEntityManager().getEntities())
            {
                int mask = e.getComponentMask();
                if ((mask & Component.ANIMATION) == Component.ANIMATION)
                {
                    Component.Animation animation = e.getComponent(Component.ANIMATION);
                    animation.animate();
                }
            }
        }
    }

    public static class Graphics extends System
    {
        @Override
        public void update()
        {
            HashMap<Integer, Marker> markers = Game.getMarkers();
            for (Entity e : Game.getEntityManager().getEntities())
            {
                int mask = e.getComponentMask();
                Marker m = markers.get(e.getId());

                if ((mask & Component.POSITION) == Component.POSITION
                        && (mask & Component.APPEARANCE) == Component.APPEARANCE && (mask & Component.HEALTH) == Component.HEALTH)
                {
                    Component.Position position = e.getComponent(Component.POSITION);
                    Component.Appearance appearance = e.getComponent(Component.APPEARANCE);
                    Component.Health health = e.getComponent(Component.HEALTH);

                    m.setPosition(position.getLatLng());
                    m.setAlpha((float) health.getHealth() / 100);
                }
                if ((mask & Component.ANIMATION) == Component.ANIMATION)
                {
                    Component.Animation animation = e.getComponent(Component.ANIMATION);

                    m.setIcon(animation.getCurrentFrame());
                }
            }
        }
    }
}
