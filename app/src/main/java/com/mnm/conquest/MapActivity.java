package com.mnm.conquest;

import android.app.ActionBar;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.mnm.conquest.ecs.Game;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class MapActivity extends AppCompatActivity
{
    private GoogleMap map;
    private CircularView circularView;
    private Location location;

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

        public class MapWrapperLayout extends FrameLayout
        {
            private GoogleMap map;

            public MapWrapperLayout(Context context)
            {
                super(context);
            }

            public void init(GoogleMap m)
            {
                map = m;
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev)
            {
                switch(ev.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return super.onInterceptTouchEvent(ev);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MySupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        circularView = (CircularView) findViewById(R.id.circularView);
        circularView.setVisibility(View.GONE);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
        {
            @Override
            public void onMapLongClick(LatLng latLng)
            {
                CameraUpdate clickLocation = CameraUpdateFactory.newLatLngZoom(latLng, map.getCameraPosition().zoom);
//                map.animateCamera(clickLocation);

//                circularView.setVisibility(View.VISIBLE);

//                Game.play();
            }
        });

        LocationManager  locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new android.location.LocationListener()
        {
            @Override
            public void onLocationChanged(Location nLocation)
            {
                location = nLocation;

                Game.playerPositionChanged(nLocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled(String provider)
            {

            }

            @Override
            public void onProviderDisabled(String provider)
            {

            }
        });

        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14);
        map.animateCamera(yourLocation);

        Game.setMap(map);
        Game.play();
        Game.createPlayer(new LatLng(location.getLatitude(), location.getLongitude()), location.getBearing());
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
