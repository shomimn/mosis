package com.mnm.conquest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener, View.OnKeyListener
{
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    ViewFlipper markerFlipper;
    ImageView chooseImage;
    ImageView image;
    int[] markerIds;
    EditText name;
    EditText lastName;
    EditText userName;
    EditText password;
    EditText email;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        name = (EditText)findViewById(R.id.name_sign_up);
        name.setOnKeyListener(this);
        lastName = (EditText)findViewById(R.id.last_name_sign_up);
        lastName.setOnKeyListener(this);
        userName = (EditText)findViewById(R.id.username_sign_up);
        userName.setOnKeyListener(this);
        password = (EditText)findViewById(R.id.password_sign_up);
        password.setOnKeyListener(this);
        email = (EditText)findViewById(R.id.email_sign_up);
        email.setOnKeyListener(this);
        image = (ImageView)findViewById(R.id.image);
        chooseImage = (ImageView)findViewById(R.id.image);
        Button sign_save = (Button)findViewById(R.id.sign_up_button);

        from = null;
        String usernameExtra, passWordExtra;
        Intent fromIntent = getIntent();
        Bundle bundle = fromIntent.getExtras();
        if(bundle != null)
        {
            from = (String)bundle.get("from");
            if(from.contains("profile_settings"))
            {
                usernameExtra = (String)bundle.get("username");
                passWordExtra = (String)bundle.get("password");
                userName.setText(usernameExtra);
                password.setText(passWordExtra);
                sign_save.setText("Save");
                setTitle("Profile settings");
                findViewById(R.id.username_sign_up).setEnabled(false);
            }
        }

        markerIds = new int[]{R.mipmap.blue_marker, R.mipmap.red_marker, R.mipmap.green_marker, R.mipmap.purple_marker};

        chooseImage.setOnClickListener(this);
        markerFlipper = (ViewFlipper)findViewById(R.id.marker_flipper);

        markerFlipper.setInAnimation(AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.abc_popup_enter));

        markerFlipper.setOutAnimation(AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.abc_popup_exit));

        for(int i=0; i<markerIds.length; i++)
        {
            setFlipperImage(markerIds[i]);
        }

        markerFlipper.setOnTouchListener(new View.OnTouchListener()
        {
            float x1, x2;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        if(x1 < x2)
                        {
                            markerFlipper.showNext();
                        }
                        else if (x1 > x2)
                        {
                            markerFlipper.showPrevious();
                        }
                        return true;

                }
                return false;
            }
        });

//        Transition enterTrans = new Slide();
//        getWindow().setEnterTransition(enterTrans);
//
//        Transition returnTrans = new Slide();
//        getWindow().setReturnTransition(returnTrans);
    }

    private void setFlipperImage(int res) {
        ImageView image = new ImageView(getApplicationContext());
        image.setBackgroundResource(res);
        markerFlipper.addView(image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
    public void onClick(View v)
    {
        if(v == image)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose");
            builder.setMessage("Where from do you want to take your image?");
            builder.setPositiveButton("Camera", new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            });
            builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
//                Intent galleryIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
//                        "content://media/internal/images/media"));

                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Bitmap photo = BitmapFactory.decodeResource(getResources(), R.mipmap.player_default);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            photo = (Bitmap) data.getExtras().get("data");
        }
        else if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            Uri selectedImageUri = data.getData();
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();

            String selectedImagePath = cursor.getString(columnIndex);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath, options);
            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                    && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            photo = BitmapFactory.decodeFile(selectedImagePath, options);
        }
        chooseImage.setImageBitmap(photo);
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        int id = v.getId();

        if(keyCode == KeyEvent.KEYCODE_ENTER)
        {
            switch (id)
            {
                case R.id.name_sign_up:
                {
                    name.clearFocus();
                    lastName.requestFocus();
                }
                return true;
                case R.id.last_name_sign_up:
                {
                    lastName.clearFocus();
                    if(from != null)
                    {
                        password.requestFocus();
                    }
                    else
                        userName.requestFocus();
                }
                return true;
                case R.id.username_sign_up:
                    userName.clearFocus();
                    password.requestFocus();
                    return true;
                case R.id.password_sign_up:
                    password.clearFocus();
                    email.requestFocus();
                    return true;
                case R.id.email_sign_up:
                    if(email.getText().length() != 0)
                    {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                    }
                    return true;
            }
        }
        return false;
    }
}
