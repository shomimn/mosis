package com.mnm.conquest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.Projection;
import com.mnm.conquest.ecs.Component;
import com.mnm.conquest.ecs.Entity;
import com.mnm.conquest.ecs.Event;
import com.mnm.conquest.ecs.Game;

import org.json.JSONException;

public class EntityView extends LinearLayout implements View.OnClickListener
{
    private static class UnitStats
    {
        public int health;
        public int defense;
        public int damage;
        public int cost;

        public UnitStats(int h, int a, int d, int c)
        {
            health = h;
            defense = a;
            damage = d;
            cost = c;
        }
    }

    private RatingBar healthRating;
    private RatingBar armorRating;
    private RatingBar damageRating;
    private Button buyButton;
    private Button exitButton;
    private Button healthUpgrade;
    private Button armorUpgrade;
    private Button damageUpgrade;
    private Button healthB;
    private Button armourB;
    private Button damageB;
    private TextView unitName;
    private LinearLayout seekPart;
    private SeekBar seekBar;
    private Button detachButton;
    private TextView seekValue;
    private TextView armySize;

    private ViewFlipper unitFlipper;

    private Entity displayedEntity;

    private boolean isPlayer;

    private int[] images = new int[] { R.mipmap.air1, R.mipmap.interceptor, R.mipmap.scout, R.mipmap.fighter, R.mipmap.gunship, R.mipmap.bomber };
    private String[] names = new String[] { "username", "fighter", "interceptor", "scout", "gunship", "bomber" };
    private UnitStats[] stats = new UnitStats[] { new UnitStats(50, 60, 90, 0), new UnitStats(50,  50, 70, 50), new UnitStats(40, 50, 60, 45), new UnitStats(30, 60, 40, 30),
                                                  new UnitStats(70, 80, 60, 60), new UnitStats(70, 80, 80, 80) };

    private LinearLayout.LayoutParams entityLp;
    private LinearLayout.LayoutParams armyLp;

    public EntityView(Context context)
    {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.entity_view, this, true);

        init();
    }

    public EntityView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.entity_view, this, true);

        init();
    }

    public EntityView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.entity_view, this, true);

        init();
    }

    public void setEntity(Entity entity)
    {
        displayedEntity = entity;

        Component.Appearance appearance = entity.getComponent(Component.APPEARANCE);
        Component.Army army = entity.getComponent(Component.ARMY);
        Component.Player player = entity.getComponent(Component.PLAYER);

        names[0] = player != null ? player.getUsername() : "Fortress";
        images[0] = appearance.getIconId();

        isPlayer = player.getUsername().equals(Game.getPlayerInfo().getUsername());

        ImageView view = (ImageView) unitFlipper.getChildAt(0);

        unitName.setText(names[0]);
        view.setBackgroundResource(images[0]);

        buyButton.setText(isPlayer ? "buy" : "attack");

        healthUpgrade.setVisibility(isPlayer ? View.VISIBLE : View.INVISIBLE);
        armorUpgrade.setVisibility(isPlayer ? View.VISIBLE : View.INVISIBLE);
        damageUpgrade.setVisibility(isPlayer ? View.VISIBLE : View.INVISIBLE);

        buyButton.setVisibility(isPlayer ? View.INVISIBLE : View.VISIBLE);

        healthRating.setLayoutParams(isPlayer ? entityLp : armyLp);
        armorRating.setLayoutParams(isPlayer ? entityLp : armyLp);
        damageRating.setLayoutParams(isPlayer ? entityLp : armyLp);

        unitFlipper.setDisplayedChild(0);

        healthRating.setRating(2);
        armorRating.setRating(3);
        damageRating.setRating(4);

        if(!isPlayer)
        {
            seekPart.setVisibility(View.INVISIBLE);
        }
    }

    private void init()
    {
        healthRating = (RatingBar) findViewById(R.id.health_rating);
        armorRating = (RatingBar) findViewById(R.id.armor_rating);
        damageRating = (RatingBar) findViewById(R.id.damage_rating);
        unitFlipper = (ViewFlipper) findViewById(R.id.unit_flipper);
        buyButton = (Button) findViewById(R.id.buy_button);
        exitButton = (Button) findViewById(R.id.exit_button);
        healthUpgrade = (Button) findViewById(R.id.health_upgrade);
        armorUpgrade = (Button) findViewById(R.id.armor_upgrade);
        damageUpgrade = (Button) findViewById(R.id.damage_upgrade);
        unitName = (TextView) findViewById(R.id.unit_name);
        detachButton = (Button)findViewById(R.id.detach);
        seekValue = (TextView)findViewById(R.id.seek_value);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        armySize = (TextView)findViewById(R.id.armySize);
        healthB = (Button)findViewById(R.id.health_upgrade);
        armorUpgrade = (Button)findViewById(R.id.armor_upgrade);
        damageUpgrade = (Button)findViewById(R.id.damage_upgrade);

        healthB.setOnClickListener(EntityView.this);
        armorUpgrade.setOnClickListener(EntityView.this);
        damageUpgrade.setOnClickListener(EntityView.this);

        entityLp = new LinearLayout.LayoutParams(healthRating.getLayoutParams());
        entityLp.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        armyLp = new LinearLayout.LayoutParams(healthRating.getLayoutParams());
        armyLp.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        armyLp.width = LayoutParams.MATCH_PARENT;

        exitButton.setOnClickListener(this);
        buyButton.setOnClickListener(this);

        unitFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_grow_fade_in_from_bottom));
        unitFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_shrink_fade_out_from_bottom));

        try
        {
            int health = Game.getPlayerInfo().getData().getInt("health");
            int damage = Game.getPlayerInfo().getData().getInt("attack");
            int armor = Game.getPlayerInfo().getData().getInt("defense");

            healthRating.setMax(100);
            healthRating.setRating(health);
            damageRating.setMax(10);
            damageRating.setRating(damage);
            armorRating.setMax(50);
            armorRating.setRating(armor);
        }
        catch (JSONException e) {e.printStackTrace();}

        for (int i = 0; i < images.length; ++i)
            setFlipperImage(images[i]);

        unitFlipper.setOnTouchListener(new OnTouchListener()
        {
            float x1, x2;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        if (x1 < x2)
                            unitFlipper.showNext();
                        else if (x1 > x2)
                            unitFlipper.showPrevious();

                        updateView();
                        return true;
                }
                return false;
            }
        });

        setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                seekValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        seekPart = (LinearLayout)findViewById(R.id.seekPart);
        seekPart.setVisibility(View.INVISIBLE);
    }

    private void setFlipperImage(int res)
    {
        ImageView image = new ImageView(getContext());
        image.setBackgroundResource(res);
        unitFlipper.addView(image);
    }

    private void updateView()
    {
        int index = unitFlipper.getDisplayedChild();
        boolean upgrade = index == 0;

        healthUpgrade.setVisibility(upgrade && isPlayer ? View.VISIBLE : View.INVISIBLE);
        armorUpgrade.setVisibility(upgrade && isPlayer ? View.VISIBLE : View.INVISIBLE);
        damageUpgrade.setVisibility(upgrade && isPlayer ? View.VISIBLE : View.INVISIBLE);

        buyButton.setVisibility(upgrade && isPlayer ? View.INVISIBLE : View.VISIBLE);

        healthRating.setLayoutParams(upgrade && isPlayer ? entityLp : armyLp);
        armorRating.setLayoutParams(upgrade && isPlayer ? entityLp : armyLp);
        damageRating.setLayoutParams(upgrade && isPlayer ? entityLp : armyLp);

        unitName.setText(names[index]);

        healthRating.setRating((float) stats[index].health / 100 * 5);
        armorRating.setRating((float) stats[index].defense / 100 * 5);
        damageRating.setRating((float) stats[index].damage / 100 * 5);

        if(!upgrade && isPlayer)
        {
            seekPart.setVisibility(View.VISIBLE);
            try
            {
                int tmp = Game.getPlayerInfo().getData().getInt(names[unitFlipper.getDisplayedChild()] + "s");
                seekBar.setMax(tmp);
                armySize.setText("/"+ String.valueOf(tmp));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            String bt = "BUY" +":"+ String.valueOf(stats[index].cost);
            buyButton.setText(bt);
        }
        else if(upgrade && isPlayer)
        {
            seekPart.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.exit_button:
                setVisibility(View.GONE);
                seekPart.setVisibility(View.INVISIBLE);
                break;
            case R.id.buy_button:
            {
                seekBar.setMax(seekBar.getMax() + 1);
                int cost = stats[unitFlipper.getDisplayedChild()].cost;

                //seekValue.setText(String.valueOf(seekBar.getProgress()) + "/" + String.valueOf(seekBar.getMax()));
                Component.Army a = displayedEntity.getComponent(Component.ARMY);
                a.addUnit(unitFlipper.getDisplayedChild() - 1);

                int n = 0;
                String name = names[unitFlipper.getDisplayedChild()] + "s";
                try
                {

                    n = Game.getPlayerInfo().getData().getInt(name);
                    Game.getPlayerInfo().getData().put(name, n + 1);

                    armySize.setText("/" + String.valueOf(n+1));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                updateUnitiesDB(name, n+1);
            }
            break;
            case R.id.health_upgrade:
            {
                healthRating.setRating(healthRating.getRating() + 1);
            }
            break;
            case R.id.armor_upgrade:
            {
                armorRating.setRating(armorRating.getRating() + 1);
            }
            break;
            case R.id.damage_upgrade:
            {
                damageRating.setRating(damageRating.getRating() + 1);
            }
            break;
        }
    }

    public void updateUnitiesDB(final String unit, final int value)
    {
        Task task = new Task()
        {
            @Override
            public void execute()
            {
                ServerConnection.updateField(Game.getPlayerInfo().getUsername(), unit, value);
            }
        };
        TaskManager.getTaskManager().execute(task);
    }
//    public void seekViewEnemy(int val)
//    {
//        seekBar.setVisibility(View.GONE);
//        detachButton.setVisibility(View.GONE);
//        seekValue.setText("Number of units: "+String.valueOf(val));
//    }
//    public void seekViewMe()
//    {
//        seekBar.setVisibility(View.VISIBLE);
//        detachButton.setVisibility(View.VISIBLE);
//    }
}
