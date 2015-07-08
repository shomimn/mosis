package com.mnm.conquest.ecs;

import android.util.SparseArray;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mnm.conquest.Task;
import com.mnm.conquest.TaskManager;

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
                int mask = e.getComponentMask();
                if ((mask & Component.POSITION) == Component.POSITION
                        && (mask & Component.DESTINATION) == Component.DESTINATION)
                {
                    Component.Position c = e.getComponent(Component.POSITION);
                    Component.Destination d = e.getComponent(Component.DESTINATION);

                    LatLng oldPos = c.getLatLng();
                    LatLng dest = d.getLatLng();

                    double diffLat = dest.latitude - oldPos.latitude;
                    double diffLng = dest.longitude - oldPos.longitude;

                    double latStep = diffLat / (double) d.steps;
                    double lngStep = diffLng / (double) d.steps;

                    --d.steps;

                    if (d.steps == 0)
                        e.removeComponent(Component.DESTINATION);

                    LatLng newPos = new LatLng(oldPos.latitude + latStep, oldPos.longitude + lngStep);
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
        public void onReceive(Event.Combat event)
        {
            Component.Attack attack = event.attacker.getComponent(Component.ATTACK);
            Component.Health health = event.defender.getComponent(Component.HEALTH);
            health.damage(attack.getDamage());

            if (health.getHealth() <= 0)
            {
                final Component.Position pos = event.defender.getComponent(Component.POSITION);
                TaskManager.getMainHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Game.getEntityManager().createExplosion(pos.getLatLng());
                    }
                });
            }
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
                    if (animation.getState() != Component.Animation.NONE)
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

                    if (m != null)
                    {
                        m.setPosition(position.getLatLng());
                        m.setAlpha((float) health.getHealth() / 100);
                    }
                }
                if ((mask & Component.ANIMATION) == Component.ANIMATION)
                {
                    Component.Animation animation = e.getComponent(Component.ANIMATION);

                    if (animation.getState() != Component.Animation.NONE)
                        m.setIcon(animation.getCurrentFrame());
                }
                if ((mask & Component.ROTATION) == Component.ROTATION)
                {
                    Component.Rotation rotation = e.getComponent(Component.ROTATION);

                    m.setRotation(rotation.getRotation());
                }
            }
        }
    }
}
