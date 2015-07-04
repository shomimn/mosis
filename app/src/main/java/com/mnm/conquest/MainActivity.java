package com.mnm.conquest;

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

public class MainActivity extends ActionBarActivity implements View.OnClickListener, View.OnKeyListener, Animator.AnimatorListener
{
    private Button mapButton;
    private AnimatorSet animSetLogIn;
    private AnimatorSet animSetLogOut;
    private LinearLayout layoutLogin;
    private LinearLayout layoutLogged;

    EditText usernameET;
    EditText passwordET;

    private Bitmap playerImage;
    private ImageView playerImageView;

    private Menu menu;

    private boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final ProgressDialog progDialog = new ProgressDialog(this);
        progDialog.setTitle("Initializing");
        progDialog.setMessage("Any second now");
        progDialog.setCanceledOnTouchOutside(false);
        progDialog.show();
        SharedPreferences sharedPrefs = getSharedPreferences(ConquestApplication.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        loggedIn = sharedPrefs.contains("username");

        final Task.Data task = new Task.Data(sharedPrefs.getString("username", ""),
                new Task.Data.DataReadyCallback()
                {
                    @Override
                    public void dataReady()
                    {
                        progDialog.dismiss();
                        setContentView(R.layout.activity_main);

                        usernameET = (EditText) findViewById(R.id.username_login);
                        usernameET.setOnKeyListener(MainActivity.this);
                        passwordET = (EditText) findViewById(R.id.password_login);
                        passwordET.setOnKeyListener(MainActivity.this);

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

                        try
                        {
                            JSONObject data = getData().getJSONObject(0);

                            byte[] bitmap = Base64.decode(data.getString("photo"), Base64.DEFAULT);
                            playerImage = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
                            playerImageView.setImageBitmap(playerImage);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        if (loggedIn)
                            animSetLogIn.start();
                    }
                });

        TaskManager.getMainHandler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (ServerConnection.isValid())
                {
                    TaskManager.getTaskManager().executeAndPost(task);
                    TaskManager.getMainHandler().removeCallbacks(this);
                }
                else
                    TaskManager.getMainHandler().postDelayed(this, 1000);
            }
        }, 1000);

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

//        if (loggedIn)
//            animSetLogIn.start();

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

                    TaskManager.getTaskManager().executeAndPost(new Task.Login(progDialog, username, password, animSetLogIn));

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
                final ProgressDialog progDialog = new ProgressDialog(this);
                progDialog.setTitle("Retrieving data");
                progDialog.setMessage("Please wait");
                progDialog.setCanceledOnTouchOutside(false);
                progDialog.show();

                final Task.Data task = new Task.Data(getSharedPreferences(ConquestApplication.SHARED_PREF_KEY, Context.MODE_PRIVATE).getString("username", ""),
                        new Task.Data.DataReadyCallback()
                        {
                            @Override
                            public void dataReady()
                            {
                                progDialog.dismiss();

                                JSONArray dataArray = getData();
                                Intent profSettings = new Intent(ConquestApplication.getContext(), RegisterActivity.class);
                                String button = "profile_settings";
                                profSettings.putExtra("from", button);
                                try
                                {
                                    JSONObject data = dataArray.getJSONObject(0);
                                    profSettings.putExtra("username", data.getString("username"));
                                    profSettings.putExtra("name", data.getString("name"));
                                    profSettings.putExtra("lastname", data.getString("lastname"));
                                    profSettings.putExtra("password", data.getString("password"));
                                    profSettings.putExtra("email", data.getString("email"));
                                    profSettings.putExtra("marker", data.getString("marker"));
                                    profSettings.putExtra("photo", data.getString("photo"));
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                                startActivity(profSettings);
                            }
                        });
                TaskManager.getTaskManager().executeAndPost(task);
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.username_login:
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    usernameET.clearFocus();
                    passwordET.requestFocus();
                    return true;
                }
                break;
            case R.id.password_login:
                if (passwordET.getText().toString().length() != 0 && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    passwordET.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordET.getWindowToken(), 0);
                    return true;
                }
                break;
        }
        return false;
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
