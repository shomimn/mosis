package com.mnm.conquest;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mnm.conquest.ecs.Game;


public class ChatActivity extends ActionBarActivity
{

    String ally_username;
    Button sendBtn;
    EditText message;
    TextView history;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null)
            ally_username = bundle.getString("username");

        this.setTitle("Chating with: " + ally_username);

        sendBtn = (Button)findViewById(R.id.send_msg_button);
        sendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String msg = Game.getPlayerInfo().getUsername() +": " + message.getText().toString();
                history.setText(msg);
            }
        });
        message = (EditText)findViewById(R.id.send_msg);
        history = (TextView)findViewById(R.id.chat_history);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
}
