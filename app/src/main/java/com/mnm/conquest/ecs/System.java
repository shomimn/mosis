package com.mnm.conquest.ecs;

import android.util.SparseArray;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mnm.conquest.Task;
import com.mnm.conquest.TaskManager;

import java.util.HashMap;

public abstract class System
{
    public void update()
    {
        TaskManager.getTaskManager().execute(new Task(Task.GENERAL)
        {
            @Override
            public void execute()
            {
                updateImpl();
            }
        });
    }

    public abstract void updateImpl();

    public static class Movement extends System
    {
        @Override
        public void updateImpl()
        {
            for (Entity e : Game.getEntityManager().getEntities())
            {
                if ((e.getComponentMask() & Component.POSITION) == Component.POSITION)
                {
                    Component.Position c = e.getComponent(Component.POSITION);
                    LatLng oldPos = c.getLatLng();
                    LatLng newPos = new LatLng(oldPos.latitude + 0.01, oldPos.longitude);
                    c.setLatLng(newPos);
                }
            }
        }
    }

    public static class Combat extends System implements Event.CombatListener
    {
        public Combat()
        {
            Game.getEventManager().register(Event.Combat.class, this);
        }

        @Override
        public void updateImpl()
        {

        }

        @Override
        public void onRecieve(Event.Combat event)
        {
            Component.Attack attack = event.attacker.getComponent(Component.ATTACK);
            Component.Health health = event.defender.getComponent(Component.HEALTH);
            health.damage(attack.getDamage());
        }
    }

    public static class Animation extends System
    {
        @Override
        public void updateImpl()
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

        public void update()
        {
            updateImpl();
        }

        @Override
        public void updateImpl()
        {
//            HashMap<Integer, Marker> markers = Game.getMarkers();
            SparseArray<Marker> markers = Game.getMarkers();
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
