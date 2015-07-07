package com.mnm.conquest;

import com.mnm.conquest.ecs.Game;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, Animator.AnimatorListener
{
    private Button mapButton;
    private AnimatorSet animSetLogIn;
    private AnimatorSet animSetLogOut;
    private LinearLayout layoutLogin;
    private LinearLayout layoutLogged;

    EditText usernameET;
    EditText passwordET;

    private ImageView playerImageView;

    private Menu menu;

    private boolean loggedIn = false;

    private Task.Data task;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences sharedPrefs = getSharedPreferences(ConquestApplication.SHARED_PREF_KEY, Context.MODE_PRIVATE);

        usernameET = (EditText) findViewById(R.id.username_login);
        passwordET = (EditText) findViewById(R.id.password_login);

        usernameET.setText(sharedPrefs.getString("username", ""));

        Button signUpButton = (Button) findViewById(R.id.sign_up_login_button);
        signUpButton.setOnClickListener(MainActivity.this);

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(MainActivity.this);

        Button alliance = (Button) findViewById(R.id.alliance_button);
        alliance.setOnClickListener(MainActivity.this);

        Button profileSet = (Button) findViewById(R.id.player_settings);
        profileSet.setOnClickListener(MainActivity.this);

        mapButton = (Button) findViewById(R.id.map_button);
        mapButton.setOnClickListener(MainActivity.this);

        playerImageView = (ImageView) findViewById(R.id.player_image);

        animSetLogIn = new AnimatorSet();
        animSetLogOut = new AnimatorSet();

        animSetLogIn.addListener(MainActivity.this);
        animSetLogOut.addListener(MainActivity.this);

        layoutLogin = (LinearLayout) findViewById(R.id.login_layout);
        layoutLogged = (LinearLayout) findViewById(R.id.loged_layout);

        animSetLogIn.play(ObjectAnimator.ofFloat(layoutLogin, "alpha", 1.0f, 0.0f).setDuration(loggedIn ? 0 : 500))
                .before(ObjectAnimator.ofFloat(layoutLogged, "alpha", 0.0f, 1.0f).setDuration(loggedIn ? 0 : 500));

        animSetLogOut.play(ObjectAnimator.ofFloat(layoutLogged, "alpha", 1.0f, 0.0f).setDuration(loggedIn ? 0 : 500))
                .before(ObjectAnimator.ofFloat(layoutLogin, "alpha", 0.0f, 1.0f).setDuration(loggedIn ? 0 : 500));

//        Transition exitTrans = new Slide();
//        getWindow().setReenterTransition(exitTrans);
//
//        Transition reenterTrans = new Slide();
//        getWindow().setExitTransition(reenterTrans);
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;

        if (loggedIn)
            animSetLogIn.start();

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
        else if(id == R.id.log_out)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    EditText usernameEdit = (EditText)findViewById(R.id.username_login);
                    EditText passwordEdit = (EditText)findViewById(R.id.password_login);

                    usernameEdit.setText("");
                    passwordEdit.setText("");

                    TaskManager.getTaskManager().executeAndPost(new Task.Logout(usernameET, passwordET, animSetLogOut));

                    loggedIn = false;
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch(id)
        {
            case R.id.sign_up_login_button:
                Intent i1 = new Intent(this, RegisterActivity.class);
//                ActivityOptionsCompat options1 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this);
//                startActivity(i1, options1.toBundle());
                startActivity(i1);
                break;
            case R.id.sign_in_button:
                EditText user = (EditText) findViewById(R.id.username_login);
                EditText pass = (EditText) findViewById(R.id.password_login);

                final String username = user.getText().toString();
                final String password = pass.getText().toString();

                if(username.trim().length() == 0 || password.trim().length() == 0)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.error_title);
                    builder.setMessage(R.string.error_message);
                    builder.setCancelable(true);
                    builder.setNegativeButton("OK",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else
                {
                    final ProgressDialog progDialog = new ProgressDialog(this);
                    progDialog.setTitle(R.string.progress_logging_title);
                    progDialog.setMessage(getResources().getString(R.string.progress_logging_message));
                    progDialog.setCanceledOnTouchOutside(false);
                    progDialog.show();

                    TaskManager.getTaskManager().executeAndPost(new Task.Login(progDialog, username, password, animSetLogIn, new Task.Data.DataReadyCallback()
                    {
                        @Override
                        public void dataReady()
                        {
                            try
                            {
                                JSONObject data = getData().getJSONObject(0);

                                PlayerInfo player = new PlayerInfo(data);
                                Game.setPlayerInfo(player);
                                playerImageView.setImageBitmap(player.getPhoto());
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }));

                    loggedIn = true;
                }
                break;
            case R.id.map_button:
                Intent i2 = new Intent(this, MapActivity.class);
                startActivity(i2);
                break;
            case R.id.alliance_button:
                Intent i3 = new Intent(this, AllianceActivity.class);
                startActivity(i3);
                break;
            case R.id.player_settings:
            {
                Intent profSettings = new Intent(ConquestApplication.getContext(), RegisterActivity.class);
                String button = "profile_settings";
                profSettings.putExtra("from", button);
                profSettings.putExtra("username", Game.getPlayerInfo().getUsername());
                profSettings.putExtra("name", Game.getPlayerInfo().getName());
                profSettings.putExtra("lastname", Game.getPlayerInfo().getLastname());
                profSettings.putExtra("password", Game.getPlayerInfo().getPassword());
                profSettings.putExtra("email", Game.getPlayerInfo().getEmail());
                profSettings.putExtra("marker", Game.getPlayerInfo().getMarkerName());
                profSettings.putExtra("photo", Game.getPlayerInfo().getPhoto());

                startActivity(profSettings);
            }
        }
    }
    @Override
    public void onAnimationStart(Animator animation)
    {
        if(animation == animSetLogIn)
        {
            layoutLogged.setVisibility(View.VISIBLE);
        }
        else if(animation == animSetLogOut)
        {
            layoutLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation)
    {
        if(animation == animSetLogIn)
        {
            String username = getSharedPreferences("PREF", Context.MODE_PRIVATE).getString("username", "");
            layoutLogin.setVisibility(View.INVISIBLE);
            MenuItem logOut = menu.findItem(R.id.log_out);
            logOut.setVisible(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.optionsActionBarName)
                    + " " + username);
        }
        else if(animation == animSetLogOut)
        {
            layoutLogged.setVisibility(View.INVISIBLE);
            MenuItem logOut = menu.findItem(R.id.log_out);
            logOut.setVisible(false);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
    }

    @Override
    public void onAnimationCancel(Animator animation)
    {

    }

    @Override
    public void onAnimationRepeat(Animator animation)
    {

    }
}
