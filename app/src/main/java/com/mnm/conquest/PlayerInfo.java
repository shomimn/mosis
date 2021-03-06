package com.mnm.conquest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlayerInfo
{
    private String name;
    private String lastname;
    private String username;
    private String password;
    private String email;
    private String markerName;
    private int markerId;
    private int level;
    private int kills;
    private int deaths;
    private int bomber;
    private int fighters;
    private int interceptors;
    private int scouts;
    private int gunships;
    private Bitmap photo;
    private Marker marker;

    private JSONObject data;
    private int coins;
    private ArrayList<String> allies;


    public PlayerInfo(JSONObject player)
    {
        try
        {
            name = player.getString("name");
            lastname = player.getString("lastname");
            username = player.getString("username");
            password = player.getString("password");
            email = player.getString("email");
            markerName = player.getString("marker");
            markerId = ConquestApplication.getContext().getResources().getIdentifier(markerName, "id", ConquestApplication.getContext().getPackageName());
            coins = player.getInt("coins");
            level = player.getInt("level");
            kills = player.getInt("kills");
            deaths = player.getInt("deaths");

            bomber = player.getInt("bombers");
            gunships = player.getInt("gunships");
            fighters = player.getInt("fighters");
            scouts = player.getInt("scouts");
            interceptors = player.getInt("interceptors");

            byte[] bitmap = Base64.decode(player.getString("photo"), Base64.DEFAULT);
            photo = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);

            player.remove("photo");
            data = player;

            JSONArray all = player.getJSONArray("allies");
            allies = new ArrayList<>();
            for(int i = 0; i < all.length(); ++i)
                allies.add(all.get(i).toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public String getName() { return name; }

    public String getLastname()
    {
        return lastname;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getEmail()
    {
        return email;
    }

    public String getMarkerName()
    {
        return markerName;
    }

    public int getMarkerId()
    {
        return markerId;
    }

    public Bitmap getPhoto()
    {
        return photo;
    }

    public void setMarker(Marker m)
    {
        marker = m;
    }

    public Marker getMarker()
    {
        return marker;
    }
    public JSONObject getData()
    {
        return data;
    }

    public int getCoins()
    {
        return coins;
    }

    public void setCoins(int c)
    {
        coins = c;
    }

    public ArrayList<String> getAllies()
    {
        return allies;
    }

    public void addAlly(String ally)
    {
        allies.add(ally);
    }

    public  int getLevel() { return level; }
    public void setLevel(int l) { level = l; }

    public int getKills() { return kills; }
    public void setKills(int k) { kills += k; }

    public int getDeaths() { return deaths; }
    public void setDeaths(int d) {deaths += d; }

    public void addUnit(int index)
    {

    }

}
