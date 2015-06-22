package com.mnm.conquest;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.net.ssl.KeyManager;


public class MainActivity extends ActionBarActivity implements View.OnClickListener
{
    private Button mapButton;
    EditText usernameET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameET = (EditText)findViewById(R.id.username_login);
        usernameET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    usernameET.clearFocus();
                    passwordET.requestFocus();
                }
                return true;
            }
        });
        passwordET = (EditText)findViewById(R.id.password_login);
        passwordET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (passwordET.getText().toString().length() != 0) {
                    passwordET.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordET.getWindowToken(), 0);
                }
                return true;
            }
        });

        Button signUpButton = (Button)findViewById(R.id.sign_up_login_button);
        signUpButton.setOnClickListener(this);

        Button signInButton = (Button)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);



        mapButton = (Button)findViewById(R.id.map_button);
        mapButton.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        else if(id == R.id.logOut)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final LinearLayout layoutLogin = (LinearLayout) findViewById(R.id.login_layout);
                    final LinearLayout layoutLoged = (LinearLayout) findViewById(R.id.loged_layout);

                    EditText usernameEdit = (EditText)findViewById(R.id.username_login);
                    EditText passwordEdit = (EditText)findViewById(R.id.password_login);

                    usernameEdit.setText("");
                    passwordEdit.setText("");
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            layoutLogin.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            layoutLoged.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animatorSet.play(ObjectAnimator.ofFloat(layoutLoged, "alpha", 1.0f, 0.0f).setDuration(1500))
                            .before(ObjectAnimator.ofFloat(layoutLogin, "alpha", 0.0f, 1.0f).setDuration(1500));
                    animatorSet.start();

                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
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
                startActivity(i1);
                break;
            case R.id.sign_in_button:
                EditText user = (EditText)findViewById(R.id.username_login);
                EditText pass = (EditText)findViewById(R.id.password_login);

                String username = user.getText().toString();
                String password = pass.getText().toString();

                if(username.trim().length() == 0 || password.trim().length() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.error_title);
                    builder.setMessage(R.string.error_message);
                    builder.setCancelable(true);
                    builder.setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {

                    final ProgressDialog progDialog = new ProgressDialog(this);
                    progDialog.setTitle(R.string.progress_logging_title);
                    progDialog.setMessage(getResources().getString(R.string.progress_logging_message));
                    progDialog.show();


                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progDialog.dismiss();

                            final LinearLayout layoutLogin = (LinearLayout) findViewById(R.id.login_layout);
                            final LinearLayout layoutLoged = (LinearLayout) findViewById(R.id.loged_layout);



                            getSupportActionBar().setTitle(getResources().getString(R.string.optionsActionBarName) + " " + ((EditText) findViewById(R.id.username_login)).getText());
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    layoutLoged.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    layoutLogin.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            animatorSet.play(ObjectAnimator.ofFloat(layoutLogin, "alpha", 1.0f, 0.0f).setDuration(1500))
                                    .before(ObjectAnimator.ofFloat(layoutLoged, "alpha", 0.0f, 1.0f).setDuration(1500));
                            animatorSet.start();
                        }
                    }, 3000);
                }
                break;
            case R.id.map_button:
                Intent i = new Intent(this, MapActivity.class);
                startActivity(i);
                break;
            //case R.id.log_out_button:


        }
    }
}
