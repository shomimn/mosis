package com.mnm.conquest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by Tomasevic on 4.7.2015.
 */
public class BuildingView extends RelativeLayout
{
    Button no;
    Button yes;
    View headerView;
    LayoutInflater inflater;

    public BuildingView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        inflater = LayoutInflater.from(context);
        init();

        yes = (Button)findViewById(R.id.building_yes);
        no = (Button)findViewById(R.id.building_no);
    }

    public BuildingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        //View.inflate(context, R.layout.building_view, null);
        inflater = LayoutInflater.from(context);
        init();

        yes = (Button)findViewById(R.id.building_yes);
        no = (Button)findViewById(R.id.building_no);
    }

    public BuildingView(Context context)
    {
        super(context);
        inflater = LayoutInflater.from(context);
        init();

        yes = (Button)findViewById(R.id.building_yes);
        no = (Button)findViewById(R.id.building_no);
    }
    public void setYesListener(OnClickListener listener)
    {
        yes.setOnClickListener(listener);
    }
    public void setNoListener(OnClickListener listener)
    {
        no.setOnClickListener(listener);
    }
    private void init()
    {
        inflater.inflate(R.layout.building_view, this, true);
    }
}
