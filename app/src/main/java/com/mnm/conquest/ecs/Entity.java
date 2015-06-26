package com.mnm.conquest.ecs;

import java.util.HashMap;

public abstract class Entity
{
    static int idGenerator = 0;

    protected int componentMask = 0;
    protected int id;

    protected HashMap<Integer, Component> components = new HashMap<Integer, Component>();

    public Entity()
    {
        id = idGenerator++;
    }

    public Entity addComponent(Component c)
    {
        components.put(c.getType(), c);
        componentMask |= c.getType();

        return this;
    }

    public Entity removeComponent(int type)
    {
        components.remove(type);
        componentMask &= ~type;

        return this;
    }

    public <T extends Component> T getComponent(int type)
    {
        return (T)components.get(type);
    }

    public int getComponentMask()
    {
        return componentMask;
    }

    public int getId()
    {
        return id;
    }

    public static class Unit extends Entity
    {
        public Unit()
        {
            super();
        }
    }

    public static class Building extends Entity
    {
        public Building()
        {
            super();
        }
    }
}
