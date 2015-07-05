package com.mnm.conquest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.mnm.conquest.ecs.Game;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class BluetoothDialog extends Dialog
{
    private final static int REQUEST_ENABLE_BT = 1;
    private Context context;
    private ListView listView;
    private ArrayList<String> macAddr;
    BluetoothAdapter btAdapter;
    private ProgressDialog progressDialog;
    private BluetoothSocket socket;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ((ArrayAdapter<String>)listView.getAdapter()).add(device.getName());
                macAddr.add(device.getAddress());
                progressDialog.dismiss();
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                if(listView.getChildCount() == 0)
                {
                    progressDialog.dismiss();
                    context.unregisterReceiver(this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.error_title);
                    builder.setMessage(R.string.bluetooth_discovery_error);
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
                    BluetoothDialog.this.dismiss();

                }

            }
        }
    };
    public BluetoothDialog(Context context)
    {
        super(context);
        this.context = context;
        macAddr = new ArrayList<>();
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_dialog);
        setTitle(R.string.bluetooth_dialog);
        listView = (ListView) findViewById(R.id.bt_devices_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String address = macAddr.get(i);
                try
                {
                    socket = btAdapter.getRemoteDevice(address).createRfcommSocketToServiceRecord(java.util.UUID.fromString("28078c90-1c1f-11e5-9a21-1697f925ec7b"));
                }
                catch (IOException e)
                {

                }
                btAdapter.cancelDiscovery();
                try
                {
                    socket.connect();
                    sendRequest();
                }
                catch (IOException e)
                {
                    try{
                        socket.close();
                    }
                    catch (IOException el)
                    {

                    }
                }
            }
        });
        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1));

        Button doneButton = (Button)findViewById(R.id.bluetooth_done_button);
        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                BluetoothDialog.this.dismiss();
            }
        });
        settingUpBT();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

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
    private void settingUpBT()
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        if(!btAdapter.isEnabled())
        {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)context).startActivityForResult(i, REQUEST_ENABLE_BT);
        }
        else
            findDevices();
    }
    public void findDevices()
    {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);

        btAdapter.startDiscovery();

        android.os.Handler h = new android.os.Handler();
        h.post(new Runnable()
        {
            @Override
            public void run()
            {
                progressDialog.setTitle("Searching for devices");
                progressDialog.setMessage("Waiting...");
                progressDialog.show();
            }
        });


    }
    private void sendRequest()
    {
        OutputStream outputStream;
        byte[] buffer;
        String user = Game.getPlayerInfo().getUsername();
        try
        {
            outputStream = socket.getOutputStream();
            buffer = user.getBytes();
            outputStream.write(buffer);
        }
        catch (IOException e)
        {

        }


    }



}
