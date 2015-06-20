package com.mnm.conquest;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import android.widget.LinearLayout;


public class MainActivity extends ActionBarActivity implements View.OnClickListener
{
    private Button mapButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        Log.d("id",String.valueOf(id));
        if(id == R.id.sign_up_login_button)
        {
            Intent i1 = new Intent(this, RegisterActivity.class);
            startActivity(i1);
        }
        else if(id == R.id.sign_in_button)
        {
            EditText user = (EditText)findViewById(R.id.username_login);
            EditText pass = (EditText)findViewById(R.id.password_login);

            String username = user.getText().toString();
            String password = pass.getText().toString();

            if(username.trim().length() == 0 || password.trim().length() == 0)
            {
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
            else
            {
                LinearLayout l = (LinearLayout)findViewById(R.id.login_layout);
                AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setFillAfter(true);
                anim.setDuration(1500);
                l.startAnimation(anim);
                ProgressDialog progDialog = new ProgressDialog(this);
                progDialog.setTitle(R.string.progress_logging_title);
                progDialog.setMessage(getResources().getString(R.string.progress_logging_message));
                progDialog.show();
            }
        }
        else if (id == R.id.map_button)
        {
            Intent i = new Intent(this, MapActivity.class);
            startActivity(i);
        }
    }
}
