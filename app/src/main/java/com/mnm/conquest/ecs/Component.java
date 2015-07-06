package com.mnm.conquest.ecs;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public abstract class Component
{
    public static final int POSITION = 1 << 0; //1
    public static final int HEALTH = 1 << 1; //2
    public static final int APPEARANCE = 1 << 2; //4
    public static final int ANIMATION = 1 << 3; //8
    public static final int ATTACK = 1 << 4; //16
    public static final int PLAYER = 1 << 5; //32
    public static final int ROTATION = 1 << 6; //64
    public static final int DEFENSE = 1 << 7; // 128
    public static final int ARMY = 1 << 8; // 256

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

        public Position()
        {
            type = POSITION;
        }

        public Position(LatLng l)
        {
            this();
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

        public Health()
        {
            type = HEALTH;
        }

        public Health(int h)
        {
            this();
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
        private int iconId;

        public Appearance()
        {
            type = APPEARANCE;
        }

        public Appearance(int i)
        {
            this();
            icon = BitmapDescriptorFactory.fromResource(i);
            iconId = i;
        }

        public void setIcon(int i)
        {
            icon = BitmapDescriptorFactory.fromResource(i);
            iconId = i;
        }

        public BitmapDescriptor getIcon()
        {
            return icon;
        }

        public int getIconId()
        {
            return iconId;
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

        public Attack()
        {
            type = ATTACK;
        }

        public Attack(int dmg)
        {
            this();
            damage = dmg;
        }

        public void setDamage(int d)
        {
            damage = d;
        }

        public int getDamage()
        {
            return damage;
        }
    }

    public static class Player extends Component
    {
        private String username;

        public Player()
        {
            type = PLAYER;
        }

        public Player(String u)
        {
            this();
            username = u;
        }

        public String getUsername()
        {
            return username;
        }
    }

    public static class Rotation extends Component
    {
        private float rotation;

        public Rotation()
        {
            type = ROTATION;
        }

        public Rotation(float r)
        {
            this();
            setRotation(r);
        }

        public void setRotation(float r)
        {
            rotation = r;
        }

        public float getRotation()
        {
            return rotation;
        }
    }

    public static class Army extends Component
    {
        public static int INTERCEPTOR = 0;
        public static int SCOUT = 1;
        public static int FIGHTER = 2;
        public static int GUNSHIP = 4;
        public static int BOMBER = 5;

        private static int INTERCEPTOR_DAMAGE = 2;
        private static int SCOUT_DAMAGE = 1;
        private static int FIGHTER_DAMAGE = 3;
        private static int GUNSHIP_DAMAGE = 4;
        private static int BOMBER_DAMAGE = 5;

        private int[] units = new int[5];

        public Army()
        {
            type = ARMY;
        }

        public Army(int i, int s, int f, int g, int b)
        {
            this();
            units[INTERCEPTOR] = i;
            units[SCOUT] = s;
            units[FIGHTER] = f;
            units[GUNSHIP] = g;
            units[BOMBER] = b;
        }

        public void setUnit(int unit, int n)
        {
            units[unit] = n;
        }

        public int getUnit(int unit)
        {
            return units[unit];
        }

        public int combinedDamage()
        {
            return units[INTERCEPTOR] * INTERCEPTOR_DAMAGE
                    + units[SCOUT] * SCOUT_DAMAGE
                    + units[FIGHTER] * FIGHTER_DAMAGE
                    + units[GUNSHIP] * GUNSHIP_DAMAGE
                    + units[BOMBER] * BOMBER_DAMAGE;
        }
    }

    public static class Defense extends Component
    {
        private int defense;

        public Defense()
        {
            type = DEFENSE;
        }

        public Defense(int def)
        {
            this();
            defense = def;
        }

        public void setDefense(int def)
        {
            defense = def;
        }

        public int getDefense()
        {
            return defense;
        }
    }
}
