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
//        TaskManager.getTaskManager().execute(new Task(Task.GENERAL)
//        {
//            @Override
//            public void execute()
//            {
                updateImpl();
//            }
//        });
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
                    {
                        e.removeComponent(Component.DESTINATION);
                        Component.Animation anim = e.getComponent(Component.ANIMATION);
                        anim.setState(Component.Animation.BATTLE);
                    }

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
        public void update()
        {
            updateImpl();
        }

        @Override
        public void updateImpl()
        {
            for (Entity e : Game.getEntityManager().getEntities())
            {
                int mask = e.getComponentMask();
                if ((mask & Component.ATTACK) == Component.ATTACK
                        && (mask & Component.ATTACKING) == Component.ATTACKING && (mask & Component.DESTINATION) != Component.DESTINATION)
                {
                    Component.Attack attack = e.getComponent(Component.ATTACK);
                    Component.Attacking behavior = e.getComponent(Component.ATTACKING);
                    Component.Army army = e.getComponent(Component.ARMY);

                    if (behavior.isDelayed())
                    {
                        behavior.setDelayed(false);
                    }
                    else
                    {
                        int dmg = attack.getDamage() + army.getCombinedDamage();

                        Entity defender = behavior.getDefender();

                        if ((defender.getComponentMask() & Component.DESTINATION) != Component.DESTINATION)
                        {
                            Component.Health health = defender.getComponent(Component.HEALTH);
                            health.damage(dmg);

                            if (health.getHealth() <= 0)
                            {
                                Component.Animation anim = e.getComponent(Component.ANIMATION);
                                if (anim != null)
                                    anim.reset();

                                Component.OwnedBy ownedBy = defender.getComponent(Component.OWNED_BY);
                                if (ownedBy != null)
                                    ownedBy.removeOwnership();

                                Component.Position pos = defender.getComponent(Component.POSITION);

                                e.removeComponent(Component.ATTACKING);

                                Game.getEntityManager().createExplosion(pos.getLatLng());
                                Game.getEventManager().emit(new Event.EntityDead(defender));
                            }
                        }
                    }
                }
            }
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

    public static class Heal extends System
    {
        @Override
        public void updateImpl()
        {
            for (Entity e : Game.getEntityManager().getEntities())
            {
                int mask = e.getComponentMask();
                if ((mask & Component.HEALTH) == Component.HEALTH &&
                        (mask & Component.ATTACKING) != Component.ATTACKING)
                {
                    Component.Health pHealth = e.getComponent(Component.HEALTH);
                    pHealth.setHealth(pHealth.getMaxHealth());
                }
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
                        && (mask & Component.HEALTH) == Component.HEALTH)
                {
                    Component.Position position = e.getComponent(Component.POSITION);
                    Component.Health health = e.getComponent(Component.HEALTH);

                    m.setPosition(position.getLatLng());
                    m.setAlpha((float) health.getHealth() / 100);
                }
                if ((mask & Component.ANIMATION) == Component.ANIMATION)
                {
                    Component.Animation animation = e.getComponent(Component.ANIMATION);
                    Component.Appearance appearance = e.getComponent(Component.APPEARANCE);

                    if (animation.getState() != Component.Animation.NONE && animation.getState() != Component.Animation.RESET)
                        m.setIcon(animation.getCurrentFrame());
                    else if (animation.getState() == Component.Animation.RESET)
                    {
                        animation.setState(Component.Animation.NONE);
                        m.setIcon(animation.getCurrentFrame());
                    }
                }
                if ((mask & Component.ROTATION) == Component.ROTATION)
                {
                    Component.Rotation rotation = e.getComponent(Component.ROTATION);

                    m.setRotation(rotation.getRotation());
                }
                if ((mask & Component.OWNED_BY) == Component.OWNED_BY)
                {
                    Component.OwnedBy ownedBy = e.getComponent(Component.OWNED_BY);

                    ownedBy.updateOwnership();
                }
            }
        }
    }
}
