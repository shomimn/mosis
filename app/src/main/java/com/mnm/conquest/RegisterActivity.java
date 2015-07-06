package com.mnm.conquest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
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
import android.widget.ViewFlipper;


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener
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
    Bitmap photo;
    boolean register = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        name = (EditText)findViewById(R.id.name_sign_up);
        lastName = (EditText)findViewById(R.id.last_name_sign_up);
        userName = (EditText)findViewById(R.id.username_sign_up);
        password = (EditText)findViewById(R.id.password_sign_up);
        email = (EditText)findViewById(R.id.email_sign_up);
        image = (ImageView)findViewById(R.id.image);
        chooseImage = (ImageView)findViewById(R.id.image);
        markerFlipper = (ViewFlipper)findViewById(R.id.marker_flipper);
        Button sign_save = (Button)findViewById(R.id.sign_up_button);
        sign_save.setOnClickListener(this);

        photo = BitmapFactory.decodeResource(getResources(), R.mipmap.player_default);

        markerIds = new int[]{R.mipmap.red1, R.mipmap.red2, R.mipmap.red3, R.mipmap.green1, R.mipmap.green2, R.mipmap.green3,
                R.mipmap.blue1, R.mipmap.blue2, R.mipmap.blue3, R.mipmap.orange1, R.mipmap.orange2, R.mipmap.orange3};

        for(int i=0; i<markerIds.length; i++)
            setFlipperImage(markerIds[i]);

        from = null;
        String usernameExtra, passWordExtra;
        Intent fromIntent = getIntent();
        Bundle bundle = fromIntent.getExtras();
        if(bundle != null)
        {
            from = (String)bundle.get("from");
            if(from.contains("profile_settings"))
            {
                userName.setText(bundle.getString("username"));
                password.setText(bundle.getString("password"));
                name.setText(bundle.getString("name"));
                lastName.setText(bundle.getString("lastname"));
                email.setText(bundle.getString("email"));

                String marker = bundle.getString("marker");

                photo = bundle.getParcelable("photo");

                sign_save.setText("Save");
                setTitle("Profile settings");
                findViewById(R.id.username_sign_up).setEnabled(false);
                register = false;

                int id = getResources().getIdentifier(marker, "id", getPackageName());
                for (int i = 0; i < markerIds.length; ++i)
                    if (markerIds[i] == id)
                    {
                        markerFlipper.setDisplayedChild(i);
                        break;
                    }

                image.setImageBitmap(photo);
            }
        }

        chooseImage.setOnClickListener(this);

        markerFlipper.setInAnimation(AnimationUtils.loadAnimation(
                this, R.anim.abc_popup_enter));

        markerFlipper.setOutAnimation(AnimationUtils.loadAnimation(
                this, R.anim.abc_popup_exit));

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
        ImageView image = new ImageView(this);
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
        int id = v.getId();
        if(id == R.id.image)
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
        else if (id == R.id.sign_up_button)
        {
            ProgressDialog progDialog = new ProgressDialog(this);
            progDialog.setTitle(register ? R.string.progress_signing_up_title : R.string.progress_editing_title);
            progDialog.setMessage(getResources().getString(R.string.progress_wait_message));
            progDialog.setCanceledOnTouchOutside(false);
            progDialog.show();

            int markerId = markerIds[markerFlipper.getDisplayedChild()];
            String markerString = getResources().getResourceName(markerId);

            Bundle userInfo = new Bundle();
            userInfo.putString("name", name.getText().toString());
            userInfo.putString("lastname", lastName.getText().toString());
            userInfo.putString("username", userName.getText().toString());
            userInfo.putString("password", password.getText().toString());
            userInfo.putString("email", email.getText().toString());
            userInfo.putString("marker", markerString);

            Task.Register task = new Task.Register(progDialog, this, userInfo, photo, register);
            TaskManager.getTaskManager().executeAndPost(task);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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

//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            photo = BitmapFactory.decodeFile(selectedImagePath, options);
//            final int REQUIRED_SIZE = 200;
//            int scale = 1;
//            while (options.outWidth / scale / 2 >= REQUIRED_SIZE
//                    && options.outHeight / scale / 2 >= REQUIRED_SIZE)
//                scale *= 2;
//            options.inSampleSize = scale;
//            options.inJustDecodeBounds = false;
            photo = BitmapFactory.decodeFile(selectedImagePath);
        }
        photo = Bitmap.createScaledBitmap(photo, photo.getWidth() / 10, photo.getHeight() / 10, true);
        chooseImage.setImageBitmap(photo);
    }




    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString("name", name.getText().toString());
        outState.putString("lastname", lastName.getText().toString());
        outState.putString("username", userName.getText().toString());
        outState.putString("password", password.getText().toString());
        outState.putString("email", email.getText().toString());
        outState.putParcelable("photo", photo);
        outState.putInt("marker", markerFlipper.getDisplayedChild());
        outState.putBoolean("register", register);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        name.setText(savedInstanceState.getString("name"));
        lastName.setText(savedInstanceState.getString("lastname"));
        userName.setText(savedInstanceState.getString("username"));
        password.setText(savedInstanceState.getString("password"));
        email.setText(savedInstanceState.getString("email"));
        photo = savedInstanceState.getParcelable("photo");
        register = savedInstanceState.getBoolean("register");

        image.setImageBitmap(photo);
        markerFlipper.setDisplayedChild(savedInstanceState.getInt("marker"));
    }
}
