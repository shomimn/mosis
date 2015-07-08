package com.mnm.conquest.ecs;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mnm.conquest.ConquestApplication;
import com.mnm.conquest.R;
import com.mnm.conquest.ServerConnection;
import com.mnm.conquest.Task;
import com.mnm.conquest.TaskManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntityManager implements Event.EntityDeadListener, Event.DetachedDeadListener
{
    private CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<Entity>();
    private SparseArray<Class<?>> componentClasses = new SparseArray<>();
    private ConcurrentHashMap<String, Entity> userToEntity = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ArrayList<Entity.Detached>> userToDetached = new ConcurrentHashMap<>();

    public EntityManager()
    {
        componentClasses.put(Component.ANIMATION, Component.Animation.class);
        componentClasses.put(Component.APPEARANCE, Component.Appearance.class);
        componentClasses.put(Component.ATTACK, Component.Attack.class);
        componentClasses.put(Component.HEALTH, Component.Health.class);
        componentClasses.put(Component.PLAYER, Component.Player.class);
        componentClasses.put(Component.POSITION, Component.Position.class);
        componentClasses.put(Component.ROTATION, Component.Rotation.class);

        Game.getEventManager().register(Event.EntityDead.class, this);
        Game.getEventManager().register(Event.DetachedDead.class, this);
    }

    public CopyOnWriteArrayList<Entity> getEntities()
    {
        return entities;
    }

    private Class<?> getComponentClass(int type)
    {
        return componentClasses.get(type);
    }

    public Entity createUnit(int mask)
    {
        Entity entity = new Entity.Unit();

        for (int i = 0; i < 7; ++i)
        {
            int type = 1 << i;
            if ((type & mask) == type)
                entity.addComponent(createComponent(type));
        }
        entities.add(entity);

        return entity;
    }

    private Component createComponent(int type)
    {
        try
        {
            Class<?> componentClass = getComponentClass(type);
            return (Component)componentClass.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public Entity createUnit(LatLng position, float rotation, JSONObject data) throws JSONException
    {
        rotation = 30;

        Entity entity = new Entity.Unit();

        entity.addComponent(new Component.Position(position))
                .addComponent(new Component.Appearance(ConquestApplication.getContext().getResources()
                        .getIdentifier(data.getString("marker"), "id", ConquestApplication.getContext().getPackageName())))
                .addComponent(new Component.Health(data.getInt("health")))
                .addComponent(new Component.Attack(data.getInt("attack")))
                .addComponent(new Component.Rotation(rotation))
                .addComponent(new Component.Player(data.getString("username")))
                .addComponent(new Component.Army(data.getInt("interceptors"), data.getInt("scouts"),
                                data.getInt("fighters"), data.getInt("gunships"), data.getInt("bombers")))
                .addComponent(new Component.Coins(data.getInt("coins")));

        Entity dummy = new Entity.Unit();

        rotation = 30 + 180;

        dummy.addComponent(new Component.Position(new LatLng(position.latitude + 0.01f, position.longitude + 0.01f)))
                .addComponent(new Component.Appearance(ConquestApplication.getContext().getResources()
                        .getIdentifier(data.getString("marker"), "id", ConquestApplication.getContext().getPackageName())))
                .addComponent(new Component.Health(data.getInt("health")))
                .addComponent(new Component.Attack(0))
                .addComponent(new Component.Rotation(rotation))
                .addComponent(new Component.Player("dummy"))
                .addComponent(new Component.Army(data.getInt("interceptors"), data.getInt("scouts"),
                        data.getInt("fighters"), data.getInt("gunships"), data.getInt("bombers")))
                .addComponent(new Component.Coins(data.getInt("coins")));

        String marker = data.getString("marker");
//        marker = marker.substring(0, marker.length() - 1);
        Component.Animation anim = new Component.Animation();
        entity.addComponent(anim);


        anim.addMoveFrame(ConquestApplication.getContext().getResources()
                .getIdentifier(marker, "id", ConquestApplication.getContext().getPackageName()));
        for (int i = 1; i <= 5; ++i)
            anim.addMoveFrame(ConquestApplication.getContext().getResources()
                    .getIdentifier(marker + "manim" + String.valueOf(i), "id", ConquestApplication.getContext().getPackageName()));

        anim.addBattleFrame(ConquestApplication.getContext().getResources()
                .getIdentifier(marker, "id", ConquestApplication.getContext().getPackageName()));
        for (int i = 1; i <= 3; ++i)
            anim.addBattleFrame(ConquestApplication.getContext().getResources()
                    .getIdentifier(marker + "banim" + String.valueOf(i), "id", ConquestApplication.getContext().getPackageName()));


        anim = new Component.Animation();
        dummy.addComponent(anim);


        anim.addMoveFrame(ConquestApplication.getContext().getResources()
                .getIdentifier(marker, "id", ConquestApplication.getContext().getPackageName()));
        for (int i = 1; i <= 5; ++i)
            anim.addMoveFrame(ConquestApplication.getContext().getResources()
                    .getIdentifier(marker + "manim" + String.valueOf(i), "id", ConquestApplication.getContext().getPackageName()));

        anim.addBattleFrame(ConquestApplication.getContext().getResources()
                .getIdentifier(marker, "id", ConquestApplication.getContext().getPackageName()));
        for (int i = 1; i <= 3; ++i)
            anim.addBattleFrame(ConquestApplication.getContext().getResources()
                    .getIdentifier(marker + "banim" + String.valueOf(i), "id", ConquestApplication.getContext().getPackageName()));


//        if (!data.getString("username").equals("m"))
//            entity.removeComponent(Component.PLAYER);


        entities.add(entity);
        userToEntity.put(data.getString("username"), entity);

        entities.add(dummy);
        userToEntity.put("dummy", dummy);

        MarkerOptions options = new MarkerOptions();
        options.position(position).rotation(rotation).icon(BitmapDescriptorFactory.fromResource(Game.getPlayerInfo().getMarkerId())).anchor(0.5f, 0.5f).flat(true);

        Marker m = Game.ui().getMap().addMarker(options);
        Game.ui().insert(dummy, m);

        return entity;
    }

    public Entity createFortress(LatLng position, JSONObject data) throws JSONException
    {
        Entity entity = new Entity.Fortress();

        entity.addComponent(new Component.Position(position))
                .addComponent(new Component.Appearance(ConquestApplication.getContext().getResources()
                        .getIdentifier(data.getString("marker"), "id", ConquestApplication.getContext().getPackageName())))
                .addComponent(new Component.Health(data.getInt("health")))
                .addComponent(new Component.Attack(data.getInt("attack")))
                .addComponent(new Component.Player(data.getString("username")));

        entities.add(entity);

        return entity;
    }

    public Entity getEntity(String username)
    {
        return userToEntity.get(username);
    }

    public void createExplosion(final LatLng position)
    {
        TaskManager.getMainHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                final Entity entity = new Entity.Unit();

                entity.addComponent(new Component.Position(position))
                        .addComponent(new Component.Appearance(R.mipmap.explosion0));

                Component.Animation anim = new Component.Animation().addMoveFrame(R.mipmap.explosion0).addMoveFrame(R.mipmap.explosion1).addMoveFrame(R.mipmap.explosion2);
                anim.setState(Component.Animation.MOVE);

                entity.addComponent(anim);

                entities.add(entity);

                Marker m = Game.ui().getMap().addMarker(new MarkerOptions().position(position).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.explosion0)).flat(true));

                Game.ui().insert(entity, m);

                TaskManager.getMainHandler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Game.getEventManager().emit(new Event.EntityDead(entity));
                    }
                }, 4 * Game.TICK);
            }
        });
    }

    public Entity.Detached createDetached(Entity owner, LatLng position, LatLng destination, Polyline line, int type, int n)
    {
        Component.Player player = owner.getComponent(Component.PLAYER);
        Component.Army pArmy = owner.getComponent(Component.ARMY);
        pArmy.removeUnits(Component.Army.INTERCEPTOR, 1);

        Entity.Detached entity = new Entity.Detached();

        entity.addComponent(new Component.Position(position))
                .addComponent(new Component.Appearance(R.mipmap.interceptor))
                .addComponent(new Component.OwnedBy(owner, line))
                .addComponent(new Component.Destination(destination))
                .addComponent(new Component.Health(100))
                .addComponent(new Component.Attack(0));

        Component.Army army = new Component.Army();
        army.setUnit(Component.Army.INTERCEPTOR, n);

        Component.Animation animation = new Component.Animation();
        animation.addBattleFrame(R.mipmap.interceptor).addMoveFrame(R.mipmap.interceptor).addMoveFrame(R.mipmap.interceptor).addBattleFrame(R.mipmap.interceptor);

        entity.addComponent(army);
        entity.addComponent(animation);

        entities.add(entity);

        if (userToDetached.get(player.getUsername()) == null)
            userToDetached.put(player.getUsername(), new ArrayList<Entity.Detached>());

        userToDetached.get(player.getUsername()).add(entity);

        Marker m = Game.ui().getMap().addMarker(new MarkerOptions().position(position).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.interceptor)).flat(true));

        Game.ui().insert(entity, m);

        return entity;
    }

    public void createDetached(String username, LatLng position, LatLng destination, int type, int n)
    {
        Entity owner = getEntity(username);

        Polyline line = Game.ui().getMap().addPolyline(new PolylineOptions().add(position).add(destination));
        line.setWidth(2);

        createDetached(owner, position, destination, line, type, n);
    }

    public void playerAttack(final Entity defender)
    {
        final Entity player = Game.getEntityManager().getEntity(Game.getPlayerInfo().getUsername());
        if (player.getComponent(Component.ATTACKING) != null)
            Log.d("GAME", "Entity already attacking");
        else
        {
            Task.Waitable task = new Task.Waitable()
            {
                @Override
                public void executeImpl()
                {
                    Component.Player pDefender = defender.getComponent(Component.PLAYER);

                    ServerConnection.startAttack(Game.getPlayerInfo().getUsername(), pDefender.getUsername());
                }

                @Override
                public void uiExecute()
                {
                    player.addComponent(new Component.Attacking(defender));
                    Component.Animation anim = player.getComponent(Component.ANIMATION);
                    anim.setState(Component.Animation.BATTLE);

                    defender.addComponent(new Component.Attacking(player, true));
                    anim = defender.getComponent(Component.ANIMATION);
                    anim.setState(Component.Animation.BATTLE);
                }
            };

            TaskManager.getTaskManager().executeAndPost(task);
        }
    }

    public void startAttack(final Entity attacker, final Entity defender)
    {
        if (attacker.getComponent(Component.ATTACKING) != null)
            Log.d("GAME", "Entity already attacking");
        else
        {
            attacker.addComponent(new Component.Attacking(defender));
            Component.Animation anim = attacker.getComponent(Component.ANIMATION);
            anim.setState(Component.Animation.BATTLE);

            defender.addComponent(new Component.Attacking(attacker, true));
            anim = defender.getComponent(Component.ANIMATION);
            anim.setState(Component.Animation.BATTLE);
        }
    }

    public void detachedAttack(final Entity attacker, final Entity defender)
    {
        if (attacker.getComponent(Component.ATTACKING) != null)
            Log.d("GAME", "Entity already attacking");
        else
        {
            Task.Waitable task = new Task.Waitable()
            {
                @Override
                public void executeImpl()
                {
                    Component.Player pDefender = defender.getComponent(Component.PLAYER);
                    Component.Destination dest = attacker.getComponent(Component.DESTINATION);

                    ServerConnection.startDetachedAttack(Game.getPlayerInfo().getUsername(), pDefender.getUsername(), dest.getLatLng());
                }

                @Override
                public void uiExecute()
                {
                    attacker.addComponent(new Component.Attacking(defender));
                    Component.Animation anim = attacker.getComponent(Component.ANIMATION);
                    anim.setState(Component.Animation.BATTLE);

                    defender.addComponent(new Component.Attacking(attacker, true));
                    anim = defender.getComponent(Component.ANIMATION);
                    anim.setState(Component.Animation.BATTLE);
                }
            };

            TaskManager.getTaskManager().executeAndPost(task);
        }
    }

    @Override
    public void onReceive(final Event.EntityDead event)
    {
        if (event.entity.equals(getEntity(Game.getPlayerInfo().getUsername())))
            return;

        TaskManager.getMainHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                removeUserMapping(event.entity);
                removeFromUi(event.entity);

                for (Entity e : entities)
                    if (e.equals(event.entity))
                        entities.remove(e);
            }
        });
    }

    public void removeUserMapping(Entity entity)
    {
        for (Map.Entry<String, Entity> entry : userToEntity.entrySet())
            if (entry.getValue().equals(entity))
                userToEntity.remove(entry.getKey());
    }

    public void removeFromUi(Entity entity)
    {
        Game.ui().remove(entity.getId());
    }

    @Override
    public void onReceive(final Event.DetachedDead event)
    {
        TaskManager.getMainHandler().post(new Runnable()
        {


            @Override
            public void run()
            {
                for (Entity e : entities)
                    if (e.equals(event.detached))
                        entities.remove(e);
            }
        });
    }
}
