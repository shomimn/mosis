package com.mnm.conquest;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mnm.conquest.ecs.Game;
import com.nineoldandroids.view.ViewPropertyAnimator;


public class MapActivity extends AppCompatActivity
{

    private GoogleMap map;
    private CircularView circularView;
    private ImageView image;
    private static FrameLayout imageWrapper;
    private LatLng position;
    private MySupportMapFragment fragment;

    public static class MySupportMapFragment extends SupportMapFragment
    {
        public View mOriginalContentView;
        public MapWrapperLayout mMapWrapperLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
        {
            mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
            mMapWrapperLayout = new MapWrapperLayout(getActivity());
            mMapWrapperLayout.addView(mOriginalContentView);
            mMapWrapperLayout.init(getMap());
            return mMapWrapperLayout;
        }

        @Override
        public View getView()
        {
            return mOriginalContentView;
        }

        public MapWrapperLayout getMapWrapper()
        {
            return mMapWrapperLayout;
        }

        public class MapWrapperLayout extends FrameLayout implements GestureDetector.OnGestureListener
        {
            private GoogleMap map;
            private LatLng pos;
            private GestureDetector detector;

            public MapWrapperLayout(Context context)
            {
                super(context);
                detector = new GestureDetector(context, this);
            }

            public void init(GoogleMap m)
            {
                map = m;
            }

            public void setPos(LatLng p)
            {
                pos = p;
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev)
            {
                switch(ev.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:

                        if (pos != null)
                        {
                            Point p = map.getProjection().toScreenLocation(pos);
                            LatLngBounds b = map.getProjection().getVisibleRegion().latLngBounds;
                            if (b.contains(pos))
                            {
                                imageWrapper.setVisibility(View.VISIBLE);
                                imageWrapper.setX(p.x);
                                imageWrapper.setY(p.y);
                            }
                            else
                            {
                                imageWrapper.setVisibility(View.GONE);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (pos != null)
                        {
                            Point p = map.getProjection().toScreenLocation(pos);
                            LatLngBounds b = map.getProjection().getVisibleRegion().latLngBounds;
                            if (b.contains(pos))
                            {
                                imageWrapper.setVisibility(View.VISIBLE);
                                imageWrapper.setX(p.x);
                                imageWrapper.setY(p.y);
                            }
                            else
                            {
                                imageWrapper.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
                if (detector.onTouchEvent(ev))
                    return true;
                else
                return super.onInterceptTouchEvent(ev);
            }

            @Override
            public boolean onDown(MotionEvent e)
            {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e)
            {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
            {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e)
            {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
            {
                return true;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fragment = (MySupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map = fragment.getMap();
        map.setMyLocationEnabled(true);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        circularView = (CircularView) findViewById(R.id.circularView);
        circularView.setVisibility(View.GONE);

        image = (ImageView) findViewById(R.id.animImage);
        image.setVisibility(View.GONE);
        imageWrapper = (FrameLayout) findViewById(R.id.imageWrapper);

        final AnimationDrawable anim = new AnimationDrawable();
        anim.addFrame(getResources().getDrawable(R.mipmap.blue_marker), 200);
        anim.addFrame(getResources().getDrawable(R.mipmap.green_marker), 200);
        anim.addFrame(getResources().getDrawable(R.mipmap.red_marker), 200);
        anim.addFrame(getResources().getDrawable(R.mipmap.purple_marker), 200);
        anim.setOneShot(false);

        image.setBackgroundDrawable(anim);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
        {
            @Override
            public void onMapLongClick(LatLng latLng)
            {
////                circularView.setVisibility(View.VISIBLE);
//                position = latLng;
//                fragment.getMapWrapper().setPos(position);
//                Point point = map.getProjection().toScreenLocation(latLng);
//                imageWrapper.setX(point.x);
//                imageWrapper.setY(point.y);
////
////                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
////                lp.leftMargin = point.x;
////                lp.topMargin = point.y;
////                image.setLayoutParams(lp);
////
//                image.setVisibility(View.VISIBLE);
//                anim.start();
                Game.play();
            }
        });

//        Marker m = map.addMarker(new MarkerOptions());
        Game.setMap(map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop()
    {
        Game.stop();

        super.onStop();
    }
}
