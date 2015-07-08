package com.mnm.conquest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.sql.Date;
import java.sql.Time;


public class BonusActivity extends ActionBarActivity implements View.OnClickListener
{

    LinearLayout layout;
    SQLiteDatabase database;
    EditText dateFrom;
    EditText dateTo;
    EditText timeFrom;
    EditText timeTo;
    Date dateFromSql;
    Date dateToSql;
    Time timeFromSql;
    Time timeToSql;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus);

        dateFrom = (EditText)findViewById(R.id.date_picker_from);
        dateTo = (EditText)findViewById(R.id.date_picker_to);
        timeFrom = (EditText)findViewById(R.id.time_picker_from);
        timeTo = (EditText)findViewById(R.id.time_picker_to);

        dateFrom.setOnClickListener(this);
        dateTo.setOnClickListener(this);
        timeFrom.setOnClickListener(this);
        timeTo.setOnClickListener(this);

        layout = (LinearLayout)findViewById(R.id.bonus_activity_layout);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bonus, menu);
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
        switch(id)
        {
            case R.id.date_picker_from:
                DatePickerDialog d = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3)
                    {
                        dateFrom.setText(datePicker.getDayOfMonth() + ". " + datePicker.getMonth() + ". " + datePicker.getYear());
                    }
                }, 2015,5,1);
                d.show();
                break;
            case R.id.date_picker_to:
                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3)
                    {
                        dateTo.setText(datePicker.getDayOfMonth() + ". " + datePicker.getMonth() + ". " + datePicker.getYear());
                    }
                }, 2015,5,1);
                dialog.show();
                break;
            case R.id.time_picker_from:
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i2)
                    {
                        timeFrom.setText(timePicker.getCurrentHour() + "h " + timePicker.getCurrentMinute() + "m ");
                    }
                },0,0,true);
                timePickerDialog.show();
                break;
            case R.id.time_picker_to:
                TimePickerDialog t = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i2)
                    {
                        timeTo.setText(timePicker.getCurrentHour() + "h " + timePicker.getCurrentMinute() + "m ");
                    }
                },0,0,true);
                t.show();
                break;
        }
    }


}
