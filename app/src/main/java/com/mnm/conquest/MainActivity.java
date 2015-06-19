package com.mnm.conquest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sign_up_button = (Button)findViewById(R.id.sign_up_login_button);
        sign_up_button.setOnClickListener(this);

        Button sign_in_button = (Button)findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(this);
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
        if(id == R.id.sign_in_button)
        {
            EditText user = (EditText)findViewById(R.id.username_login);
            EditText pass = (EditText)findViewById(R.id.password_login);

            String username = user.getText().toString();
            String password = pass.getText().toString();

            Log.d("username:",username);

            if(username.trim() == "" || password.trim() == "")
            {
               AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error!");
                builder.setMessage("You have empty fields!");
                builder.setCancelable(true);
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        }
    }
}
