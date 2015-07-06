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
import android.widget.TextView;
import android.widget.ViewFlipper;

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
    private TextView unitName;

    private ViewFlipper unitFlipper;

    private int[] images = new int[] { R.mipmap.air1, R.mipmap.fighter, R.mipmap.interceptor, R.mipmap.scout, R.mipmap.gunship, R.mipmap.bomber };
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

        entityLp = new LinearLayout.LayoutParams(healthRating.getLayoutParams());
        entityLp.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        armyLp = new LinearLayout.LayoutParams(healthRating.getLayoutParams());
        armyLp.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        armyLp.width = LayoutParams.MATCH_PARENT;

        unitName.setText(names[0]);

        exitButton.setOnClickListener(this);

//        healthRating.setRating((float) stats[0].health / 100 * 5);
//        armorRating.setRating((float) stats[0].defense / 100 * 5);
//        damageRating.setRating((float) stats[0].damage / 100 * 5);

        healthRating.setRating(3);
        armorRating.setRating(4);
        damageRating.setRating(4.5f);

        for (int i = 0; i < images.length; ++i)
            setFlipperImage(images[i]);

        unitFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_grow_fade_in_from_bottom));
        unitFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_shrink_fade_out_from_bottom));

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

        healthUpgrade.setVisibility(upgrade ? View.VISIBLE : View.INVISIBLE);
        armorUpgrade.setVisibility(upgrade ? View.VISIBLE : View.INVISIBLE);
        damageUpgrade.setVisibility(upgrade ? View.VISIBLE : View.INVISIBLE);

        healthRating.setLayoutParams(upgrade ? entityLp : armyLp);
        armorRating.setLayoutParams(upgrade ? entityLp : armyLp);
        damageRating.setLayoutParams(upgrade ? entityLp : armyLp);

        unitName.setText(names[index]);

        healthRating.setRating((float) stats[index].health / 100 * 5);
        armorRating.setRating((float) stats[index].defense / 100 * 5);
        damageRating.setRating((float) stats[index].damage / 100 * 5);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.exit_button:
                setVisibility(View.GONE);
                break;
        }
    }
}
