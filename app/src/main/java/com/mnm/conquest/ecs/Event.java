package com.mnm.conquest.ecs;

import com.google.android.gms.maps.model.BitmapDescriptor;

public interface Event<T>
{
    void notify(T handler);

    interface CombatListener
    {
        void onReceive(Combat event);
    }

    class Combat implements Event<CombatListener>
    {
        public Entity attacker;
        public Entity defender;

        @Override
        public void notify(CombatListener handler)
        {
            handler.onReceive(this);
        }
    }

    interface AnimationListener
    {
        void onReceive(Animation event);
    }

    class Animation implements Event<AnimationListener>
    {
        public Entity animated;
        public BitmapDescriptor frame;

        @Override
        public void notify(AnimationListener handler)
        {
            handler.onReceive(this);
        }
    }

    interface EntityDeadListener
    {
        void onReceive(EntityDead event);
    }

    class EntityDead implements Event<EntityDeadListener>
    {
        public Entity entity;

        public EntityDead(Entity dead)
        {
            entity = dead;
        }

        @Override
        public void notify(EntityDeadListener handler)
        {
            handler.onReceive(this);
        }
    }

    interface DetachedDeadListener
    {
        void onReceive(DetachedDead event);
    }

    class DetachedDead implements Event<DetachedDeadListener>
    {
        public Entity.Detached detached;

        public DetachedDead(Entity.Detached dead)
        {
            detached = dead;
        }

        @Override
        public void notify(DetachedDeadListener handler)
        {
            handler.onReceive(this);
        }
    }
}
