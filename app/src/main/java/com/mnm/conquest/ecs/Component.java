package com.mnm.conquest.ecs;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

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
    public static final int COINS = 1 << 9; //512
    public static final int OWNED_BY = 1 << 10; //1024
    public static final int DESTINATION = 1 << 11; // 2048
    public static final int DEFENDING = 1 << 12; // 4096
    public static final int ATTACKING = 1 << 13; // 8192

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
        private int maxHealth;
        private int health;

        public Health()
        {
            type = HEALTH;
        }

        public Health(int h)
        {
            this();
            health = h;
            maxHealth = h;
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

        public void setMaxHealth(int h)
        {
            maxHealth = h;
        }

        public int getMaxHealth()
        {
            return maxHealth;
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
        public static final int NONE = 0;
        public static final int MOVE = 1;
        public static final int BATTLE = 2;
        public static final int RESET = 3;

        protected int state = NONE;

        protected ArrayList<BitmapDescriptor> moveFrames;
        protected ArrayList<BitmapDescriptor> battleFrames;
        protected int currentMove;
        protected int currentBattle;
        protected boolean forward;

        public Animation()
        {
            type = ANIMATION;
            moveFrames = new ArrayList<BitmapDescriptor>();
            battleFrames = new ArrayList<BitmapDescriptor>();
            currentMove = 0;
            currentBattle = 0;
            forward = true;
        }

        public Animation addMoveFrame(int frame)
        {
            moveFrames.add(BitmapDescriptorFactory.fromResource(frame));

            return this;
        }

        public Animation addBattleFrame(int frame)
        {
            battleFrames.add(BitmapDescriptorFactory.fromResource(frame));

            return this;
        }

        public void setState(int s)
        {
            state = s;
        }

        public BitmapDescriptor getCurrentFrame()
        {
            if (state == MOVE)
                return moveFrames.get(currentMove);
            else
                return battleFrames.get(currentBattle);
        }

        public void animate()
        {
            if (state == MOVE)
                animateMove();
            else
                animateBattle();
        }

        public void animateMove()
        {
            if (currentMove == moveFrames.size() - 1)
                forward = false;
            else if (currentMove == 0)
                forward = true;

            currentMove = currentMove + (forward ? 1 : -1);
        }

        public void animateBattle()
        {
            if (currentBattle == battleFrames.size() - 1)
                forward = false;
            else if (currentBattle == 0)
                forward = true;

            currentBattle = currentBattle + (forward ? 1 : -1);
        }

        public int getState()
        {
            return state;
        }

        public void reset()
        {
            state = RESET;
            currentBattle = 0;
            currentMove = 0;
        }
    }

    public static class Attack extends Component
    {
        protected int damage;

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
        public static int GUNSHIP = 3;
        public static int BOMBER = 4;

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

        public void addUnit(int unit) { units[unit]++; }

        public int getUnit(int unit)
        {
            return units[unit];
        }

        public int getCombinedDamage()
        {
            return units[INTERCEPTOR] * INTERCEPTOR_DAMAGE
                    + units[SCOUT] * SCOUT_DAMAGE
                    + units[FIGHTER] * FIGHTER_DAMAGE
                    + units[GUNSHIP] * GUNSHIP_DAMAGE
                    + units[BOMBER] * BOMBER_DAMAGE;
        }

        public void removeUnits(int unit, int n)
        {
            units[unit] -= n;
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

    public static class Coins extends Component
    {
        private int coins;

        public Coins()
        {
            type = COINS;
        }

        public Coins(int c)
        {
            this();
            coins = c;
        }

        public void setCoins(int c)
        {
            coins = c;
        }

        public int getCoins()
        {
            return coins;
        }
    }

    public static class OwnedBy extends Component
    {
        private Entity owner;
        private Polyline line;

        public OwnedBy()
        {
            type = OWNED_BY;
        }

        public OwnedBy(Entity o, Polyline l)
        {
            this();
            owner = o;
            line = l;
        }

        public Entity getOwner()
        {
            return owner;
        }

        public void updateOwnership()
        {
            Component.Position position = owner.getComponent(Component.POSITION);

            List<LatLng> list = line.getPoints();
            list.set(0, position.getLatLng());
            line.setPoints(list);
        }
    }

    public static class Destination extends Component
    {
        private LatLng position;
        public int steps;

        public Destination()
        {
            type = DESTINATION;
        }

        public Destination(LatLng p)
        {
            this();
            position = p;
            steps = 50;
        }

        public LatLng getLatLng()
        {
            return position;
        }
    }

    public static class Defending extends Component
    {
        private Entity attacker;

        public Defending()
        {
            type = DEFENDING;
        }

        public Defending(Entity a)
        {
            this();
            attacker = a;
        }

        public Entity getAttacker()
        {
            return attacker;
        }
    }

    public static class Attacking extends Component
    {
        protected Entity defender;
        protected boolean delayed;

        public Attacking()
        {
            type = ATTACKING;
        }

        public Attacking(Entity d)
        {
            this();
            defender = d;
            delayed = false;
        }

        public Attacking(Entity d, boolean delay)
        {
            this();
            defender = d;
            delayed = delay;
        }

        public Entity getDefender()
        {
            return defender;
        }

        public boolean isDelayed()
        {
            return delayed;
        }

        public void setDelayed(boolean d)
        {
            delayed = d;
        }
    }
}
