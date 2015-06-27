package com.mnm.conquest;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;


public class AllianceActivity extends ActionBarActivity
{

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int DISCOVERABLE_BT = 2;
    private Menu menu;
    private boolean bluetoothEnabled;
    private BluetoothDialog d;
    private BluetoothAdapter btAdapter;
    private BluetoothServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alliance);

        ListView listView = (ListView)findViewById(R.id.ally_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

            }
        });

        Button newAlly = (Button)findViewById(R.id.make_alliance_button);
        newAlly.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                d = new BluetoothDialog(AllianceActivity.this);
                d.show();
            }
        });
        bluetoothEnabled = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT)
            if(resultCode == RESULT_OK)
                d.findDevices();
            else
            {

            }
        if(requestCode == DISCOVERABLE_BT)
        {
            if(resultCode == 300)
                initializeServer();
            else
            {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alliance, menu);
        this.menu = menu;
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
        switch (id)
        {
            case R.id.bluetooth_enable:
                if(!bluetoothEnabled)
                {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivityForResult(discoverableIntent, DISCOVERABLE_BT );
                    bluetoothEnabled = true;
                }
                else
                {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();
                    if(btAdapter.isEnabled())
                        btAdapter.disable();
                    MenuItem m = menu.findItem(R.id.bluetooth_enable);
                    m.setIcon(R.mipmap.ic_action_bluetooth);
                    bluetoothEnabled = false;
                }
                break;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initializeServer()
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error_title);
            builder.setMessage(R.string.error_bluetooth_message);
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
            return;
        }
        MenuItem m = menu.findItem(R.id.bluetooth_enable);
        m.setIcon(R.mipmap.ic_action_bluetooth_connected);
        try
        {
            serverSocket = btAdapter.listenUsingRfcommWithServiceRecord(getResources().getString(R.string.app_name), java.util.UUID.fromString("28078c90-1c1f-11e5-9a21-1697f925ec7b"));
            BluetoothSocket socket;
            while(true)
            {
                try{
                    socket = serverSocket.accept();
                }
                catch (IOException e)
                {
                    break;
                }

                if(socket != null)
                {
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytes = inputStream.read(buffer);
                    Toast.makeText(this,new String(buffer),1000).show();
                    socket.close();
                    break;
                }
            }
        }
        catch (IOException e)
        {

        }
    }



}
