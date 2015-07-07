package com.mnm.conquest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mnm.conquest.ecs.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class AllianceActivity extends ActionBarActivity
{

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int DISCOVERABLE_BT = 2;
    private Menu menu;
    private boolean bluetoothEnabled;
    private BluetoothDialog dialog;
    private BluetoothAdapter btAdapter;
    private BluetoothServerSocket serverSocket;
    private ArrayList<PlayerInfo> playerInfoList;
    private ArrayList<String> allies;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alliance);

        listView = (ListView) findViewById(R.id.ally_list_view);
        playerInfoList = new ArrayList<>();

        contactServer();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Dialog dialog = new Dialog(AllianceActivity.this);
                dialog.setTitle(playerInfoList.get(i).getUsername() + ":");
                ImageView img = new ImageView(dialog.getContext());
                img.setImageBitmap(playerInfoList.get(i).getPhoto());
                dialog.setContentView(img, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                dialog.show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(AllianceActivity.this);
                builder.setTitle(R.string.confirm_title);
                builder.setMessage(R.string.confirm_message_delete);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        final ProgressDialog progDialog = new ProgressDialog(AllianceActivity.this);
                        progDialog.setTitle(R.string.deleting_ally);
                        progDialog.setMessage(getResources().getString(R.string.progress_wait_message));
                        progDialog.setCanceledOnTouchOutside(false);
                        progDialog.show();
                        Task.Ally task = new Task.Ally(Game.getPlayerInfo().getUsername(), progDialog,
                                ((TextView) view.findViewById(R.id.ally_username)).getText().toString(), false,
                                new Task.Ally.DataReadyCallback()
                                {
                                    @Override
                                    public void dataReady()
                                    {
                                        JSONArray data = getData();
                                        try
                                        {
                                            AllianceActivity.this.playerInfoList.clear();
                                            allies = new ArrayList<>();
                                            for (int i = 0; i < data.length(); ++i)
                                            {
                                                JSONObject obj = (JSONObject)data.get(i);
                                                allies.add(obj.get("username").toString());
                                                AllianceActivity.this.playerInfoList.add(new PlayerInfo(obj));
                                            }
                                            if(allies != null )
                                            {
                                                AlliancesAdapter adapter = new AlliancesAdapter(AllianceActivity.this, allies);
                                                listView.setAdapter(adapter);
                                            }
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        TaskManager.getTaskManager().executeAndPost(task);
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

                return true;
            }
        });
        Button newAlly = (Button)findViewById(R.id.make_alliance_button);
        newAlly.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog = new BluetoothDialog(AllianceActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface)
                    {
                        contactServer();
                    }
                });
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
                dialog.findDevices();
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


        final ProgressDialog progDialog = new ProgressDialog(AllianceActivity.this);
        progDialog.setTitle(R.string.adding_ally);
        progDialog.setMessage(getResources().getString(R.string.progress_wait_message));
        progDialog.setCanceledOnTouchOutside(false);
        progDialog.show();

        Task.Ui task = new Task.Ui(Task.SERVER)
        {
                @Override
                public void execute()
                {
                    try
                    {
                        serverSocket = btAdapter.listenUsingRfcommWithServiceRecord(getResources().getString(R.string.app_name),
                                UUID.fromString("28078c90-1c1f-11e5-9a21-1697f925ec7b"));
                        BluetoothSocket socket;
                        try{
                            socket = serverSocket.accept();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                            return;
                        }
                        if(socket != null)
                        {
                            InputStream inputStream = socket.getInputStream();
                            byte[] buffer = new byte[1024];
                            int num = inputStream.read(buffer);
                            String ally = new String(buffer, 0, num);
                            Game.getPlayerInfo().addAlly(ally);

                            Task.Ally task = new Task.Ally(Game.getPlayerInfo().getUsername(),progDialog, ally, true,
                                    new Ally.DataReadyCallback()
                                    {
                                        @Override
                                        public void dataReady()
                                        {
                                            JSONArray data = getData();
                                            try
                                            {
                                                AllianceActivity.this.playerInfoList.clear();
                                                allies = new ArrayList<>();
                                                for (int i = 0; i < data.length(); ++i)
                                                {
                                                    JSONObject obj = (JSONObject)data.get(i);
                                                    allies.add(obj.get("username").toString());
                                                    AllianceActivity.this.playerInfoList.add(new PlayerInfo(obj));
                                                }
                                                if(allies != null )
                                                {
                                                    AlliancesAdapter adapter = new AlliancesAdapter(AllianceActivity.this, allies);
                                                    listView.setAdapter(adapter);
                                                }
                                            }
                                            catch (JSONException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            TaskManager.getTaskManager().executeAndPost(task);

                            socket.close();
                         }
                    }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }


            @Override
            public void uiExecute()
            {

            }
        };
        TaskManager.getTaskManager().executeAndPost(task);
    }
    private void contactServer()
    {
        final Task.Data task = new Task.Data(Game.getPlayerInfo().getUsername(), new Task.Data.DataReadyCallback()
        {
            @Override
            public void dataReady()
            {
                JSONArray data = getData();
                try
                {
                    AllianceActivity.this.playerInfoList.clear();
                    allies = new ArrayList<>();
                    for (int i = 0; i < data.length(); ++i)
                    {
                        JSONObject obj = (JSONObject)data.get(i);
                        allies.add(obj.get("username").toString());
                        AllianceActivity.this.playerInfoList.add(new PlayerInfo(obj));
                    }
                    if(allies != null )
                    {
                        AlliancesAdapter adapter = new AlliancesAdapter(AllianceActivity.this, allies);
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
                ServerConnection.getAllies(username);
            }
        };
        TaskManager.getTaskManager().executeAndPost(task);
    }
    public class AlliancesAdapter extends ArrayAdapter<String>
    {
        private final Context context;
        private final List<String> values;
        public AlliancesAdapter(Context context, List<String> values)
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
            View rowView = inflater.inflate(R.layout.list_view_alliances, parent, false);
            TextView username = (TextView)rowView.findViewById(R.id.ally_username);
            username.setText(values.get(position));

            ImageView image = (ImageView)rowView.findViewById(R.id.ally_image);
            image.setBackgroundResource(playerInfoList.get(position).getMarkerId());

            return rowView;
        }
    }
}
