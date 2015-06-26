package com.mnm.conquest.ecs;

import com.google.android.gms.maps.model.BitmapDescriptor;

public interface Event<T>
{
    void notify(T handler);

    interface CombatListener
    {
        void onRecieve(Combat event);
    }

    class Combat implements Event<CombatListener>
    {
        Entity attacker;
        Entity defender;

        @Override
        public void notify(CombatListener handler)
        {
            handler.onRecieve(this);
        }
    }

    interface AnimationListener
    {
        void onRecieve(Animation event);
    }

    class Animation implements Event<AnimationListener>
    {
        Entity animated;
        BitmapDescriptor frame;

        @Override
        public void notify(AnimationListener handler)
        {
            handler.onRecieve(this);
        }
    }
}
