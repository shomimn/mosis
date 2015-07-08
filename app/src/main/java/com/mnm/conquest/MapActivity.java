package com.mnm.conquest;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.PolyUtil;
import com.mnm.conquest.ecs.Component;
import com.mnm.conquest.ecs.Entity;
import com.mnm.conquest.ecs.Game;

import org.w3c.dom.Text;

public class MapActivity extends AppCompatActivity
{
    private GoogleMap map;
    private CircularView circularView;
    private BuildingView buildingView;
    private Location location;
    private EntityView entityView;
    private TextView coinTextView;

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

        SpannableString s = new SpannableString("map");
        s.setSpan(new FTypefaceSpan(this, "kenvector_future.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);

        map = ((MySupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        circularView = (CircularView) findViewById(R.id.circularView);
        circularView.setVisibility(View.GONE);

        entityView = (EntityView) findViewById(R.id.entity_view);
        entityView.setVisibility(View.GONE);

        buildingView = (BuildingView) findViewById(R.id.fortress);
        buildingView.setVisibility(View.GONE);

        buildingView.setNoListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buildingView.setVisibility(View.GONE);
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
        {
            @Override
            public void onMapLongClick(final LatLng latLng)
            {
                CameraUpdate clickLocation = CameraUpdateFactory.newLatLngZoom(latLng, map.getCameraPosition().zoom);
                map.animateCamera(clickLocation);

                buildingView.setVisibility(View.VISIBLE);
                buildingView.setYesListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        buildingView.setVisibility(View.GONE);
                        int coins = Game.getPlayerInfo().getCoins();

                        if (coins < 70)
                        {
                            buildingView.setVisibility(View.GONE);
                            Toast.makeText(MapActivity.this, "You don't have enough coins!", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(MapActivity.this, "You built a fortress!", Toast.LENGTH_SHORT).show();
                            Game.createFortress(latLng);
                            ServerConnection.updateField(Game.getPlayerInfo().getUsername(), "coins", coins - 70);
                            Game.getPlayerInfo().setCoins(coins - 70);
                        }
                    }
                });
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                Entity entity = Game.ui().getEntity(marker);

                if (Game.getState() == Game.NORMAL)
                {
                    if (entity.getComponent(Component.PLAYER) != null)
                    {
                        entityView.setEntity(entity);
                        entityView.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    if (!entity.equals(Game.getEntityManager().getEntity(Game.getPlayerInfo().getUsername())))
                    {
                        Entity player = Game.getEntityManager().getEntity(Game.getPlayerInfo().getUsername());
                        Component.Position pos = player.getComponent(Component.POSITION);

                        Component.Position enemyPos = entity.getComponent(Component.POSITION);
                        LatLng newPos = new LatLng(enemyPos.getLatLng().latitude - 0.01, enemyPos.getLatLng().longitude);

                        Polyline line = map.addPolyline(new PolylineOptions().add(pos.getLatLng()).add(newPos).color(R.color.white));
                        line.setWidth(2);

                        Entity.Detached detached = Game.getEntityManager().createDetached(player, pos.getLatLng(), newPos, line, R.mipmap.interceptor, 3);

                        Game.getEntityManager().startAttack(detached, entity);

                        Game.setState(Game.NORMAL);
                    }
                }

//                Toast.makeText(MapActivity.this, "id: " + String.valueOf(entity.getId()), Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        String provider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(provider, 1000, 0, new android.location.LocationListener()
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

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                if (Game.getState() == Game.DETACHING)
                {
                    Entity player = Game.getEntityManager().getEntity(Game.getPlayerInfo().getUsername());
                    Component.Position pos = player.getComponent(Component.POSITION);

                    Polyline line = map.addPolyline(new PolylineOptions().add(pos.getLatLng()).add(latLng).color(R.color.white));
                    line.setWidth(2);

                    Game.getEntityManager().createDetached(player, pos.getLatLng(), latLng, line, R.mipmap.interceptor, 3);

                    Game.setState(Game.NORMAL);
                }
            }
        });

        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14);
        map.animateCamera(yourLocation);

        Game.setMap(map);
        Game.play();
        Game.createPlayer(new LatLng(location.getLatitude(), location.getLongitude()), location.getBearing());

        ServerConnection.sendPosition(Game.getPlayerInfo().getUsername(), location.getLatitude(), location.getLongitude(), ServerConnection.Request.INIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        coinTextView = new FTextView(this);
        coinTextView.setText(String.valueOf(Game.getPlayerInfo().getCoins()));

        ImageView image = new ImageView(this);
        image.setBackgroundResource(R.mipmap.coin);

        layout.addView(image);
        layout.addView(coinTextView);

        MenuItem item  = menu.getItem(0);
        item.setActionView(layout);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop()
    {
//        Game.stop();

        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Game.stop();

        super.onDestroy();
    }
}
