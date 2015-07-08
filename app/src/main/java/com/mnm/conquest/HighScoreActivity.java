package com.mnm.conquest;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mnm.conquest.ecs.Component;
import com.mnm.conquest.ecs.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class HighScoreActivity extends ActionBarActivity
{
    private ListView listView;
    public ArrayList<PlayerInfo> players;
    public ArrayList<Score> playerStats;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        players = new ArrayList<>();
        listView = (ListView)findViewById(R.id.high_score_list);

        getPlayers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_high_score, menu);
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
    public void getPlayers()
    {
        Task.Data task = new Task.Data(Game.getPlayerInfo().getUsername(), new Task.Data.DataReadyCallback()
        {
            @Override
            public void dataReady()
            {
                JSONArray data =  getData();
                try
                {
                    HighScoreActivity.this.players.clear();
                    playerStats = new ArrayList<>();
                    for(int i=0; i<data.length(); ++i)
                    {
                        JSONObject object = (JSONObject)data.get(i);
                        int level = object.getInt("level");
                        int kills = object.getInt("kills");
                        int deaths = object.getInt("deaths");

                        int score;
                        if((kills - deaths) > 0)
                            score = level*(kills - deaths);
                        else score = 0;

                        //playerStats.add(object.get("username").toString()+" "+String.valueOf(score));
                        byte[] bitmap = Base64.decode(object.getString("photo"), Base64.DEFAULT);
                        Bitmap photo = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);

                        playerStats.add(new Score(score, object.getString("username"), photo));
                        HighScoreActivity.this.players.add(new PlayerInfo(object));
                    }
                    if(playerStats != null)
                    {
                        Collections.sort(playerStats, new Comparator<Score>(){
                        public int compare(Score s1, Score s2) {
                            //return s1.getScore().compareTo(s2.getScoreStr());
                            return Integer.valueOf(s2.getScore()).compareTo(s1.getScore());
                        }});
                        HighScoreAdapter adapter = new HighScoreAdapter(HighScoreActivity.this, playerStats);
                        listView.setAdapter(adapter);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        })
        {
            @Override
            public void executeImpl()
            {
                ServerConnection.getAllPlayers();
            }
        };
        TaskManager.getTaskManager().execute(task);
    }
    public class HighScoreAdapter extends ArrayAdapter<Score>
    {
        private final Context context;
        private final List<Score> values;
        public HighScoreAdapter(Context context, List<Score> values)
        {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.list_view_high_score, parent, false);
            TextView username = (TextView)rowView.findViewById(R.id.high_score_username);
            TextView highScore = (TextView)rowView.findViewById(R.id.high_score_value);
            //String[] un = values.get(position).split(" ");
            username.setText(values.get(position).getUsername());
            highScore.setText(String.valueOf(values.get(position).getScore()));

            ImageView image = (ImageView)rowView.findViewById(R.id.high_score_photo);
            //image.setImageBitmap(getResources().getDrawable(values.get(position).getPhoto(), null));
            image.setImageBitmap(values.get(position).getPhoto());
            //image.setImageResource(players.get(position).getMarkerId());

            return rowView;
        }
    }
    private class Score
    {
        int score;
        String username;
        Bitmap photo;

        public Score(int i, String s, Bitmap b)
        {
            score = i;
            username = s;
            photo = b;
        }
        public int getScore(){return score;}
        public String getUsername() {return username;}
        public Bitmap getPhoto() {return photo;}
    }
}
