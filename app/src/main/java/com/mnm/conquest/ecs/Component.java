package com.mnm.conquest.ecs;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public abstract class Component
{
    public static final int POSITION = 1 << 0;
    public static final int HEALTH = 1 << 1;
    public static final int APPEARANCE = 1 << 2;
    public static final int ANIMATION = 1 << 3;
    public static final int ATTACK = 1 << 4;

    protected int type;

    public int getType()
    {
        return type;
    }

    public static class Position extends Component
    {
        private float screenX;
        private float screenY;
        private LatLng latLng;

        public Position(LatLng l)
        {
            type = POSITION;
            latLng = l;
        }

        public float getScreenX()
        {
            return screenX;
        }

        public void setScreenX(float screenX)
        {
            this.screenX = screenX;
        }

        public float getScreenY()
        {
            return screenY;
        }

        public void setScreenY(float screenY)
        {
            this.screenY = screenY;
        }

        public LatLng getLatLng()
        {
            return latLng;
        }

        public void setLatLng(LatLng latLng)
        {
            this.latLng = latLng;
        }
    }

    public static class Health extends Component
    {
        private int health;

        public Health(int h)
        {
            type = HEALTH;
            health = h;
        }

        public int getHealth()
        {
            return health;
        }

        public void setHealth(int health)
        {
            this.health = health;
        }

        public void damage(int damage)
        {
            health -= damage;
        }
    }

    public static class Appearance extends Component
    {
        private BitmapDescriptor icon;

        public Appearance(int i)
        {
            type = APPEARANCE;
            icon = BitmapDescriptorFactory.fromResource(i);
        }

        public void setIcon(BitmapDescriptor icon)
        {
            this.icon = icon;
        }

        public BitmapDescriptor getIcon()
        {
            return icon;
        }
    }

    public static class Animation extends Component
    {
        private ArrayList<BitmapDescriptor> frames;
        private int current;
        private boolean forward;

        public Animation()
        {
            type = ANIMATION;
            frames = new ArrayList<BitmapDescriptor>();
            current = 0;
            forward = true;
        }

        public Animation addFrame(int frame)
        {
            frames.add(BitmapDescriptorFactory.fromResource(frame));

            return this;
        }

        public BitmapDescriptor getCurrentFrame()
        {
            return frames.get(current);
        }

        public void animate()
        {
            if (current == frames.size() - 1)
                forward = false;
            else if (current == 0)
                forward = true;

            current = current + (forward ? 1 : -1);
        }
    }

    public static class Attack extends Component
    {
        private int damage;

        public Attack(int dmg)
        {
            type = ATTACK;
            damage = dmg;
        }

        public int getDamage()
        {
            return damage;
        }
    }
}
