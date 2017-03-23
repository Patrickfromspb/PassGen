package com.gargolin.passgen;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gargolin.passgen.swipedismiss.SwipeDismissListViewTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.gargolin.passgen.PasswordAdapter.savedPasswordClip;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, DialogInterface.OnClickListener {
    private Dialog languageDialog;
    private Locale locale=null;
    private static String possibleSymbols="";
    private static String requiredSymbols="";
    private PasswordAdapter adapter;
    private ArrayList<PasswordHolder> passwordList=new ArrayList<PasswordHolder>() ;
    EditText required;
    Switch numberSwitch;
    Switch bigCaseSwitch;
    Switch smallCaseSwitch;
    SeekBar numberSeekBar;
    SeekBar bigCaseSeekBar;
    SeekBar smallCaseSeekBar;
    SeekBar lengthSeekBar;
    FloatingActionButton saveButton;
    FloatingActionButton addButton;
    Button showButton;
    EditText possible;
    TextView password;
    ListView savedPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences("general_settings", Context.MODE_PRIVATE);
        String lanSettings = prefs.getString("language", Locale.getDefault().getLanguage());
        getResources().getConfiguration().setLocale(new Locale(lanSettings));
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        numberSwitch = (Switch) findViewById(R.id.switchNumbers);
        bigCaseSwitch = (Switch) findViewById(R.id.switchBigLatin);
        smallCaseSwitch = (Switch) findViewById(R.id.switchSmallLatin);
        numberSeekBar = (SeekBar) findViewById(R.id.seekBarNumbers);
        bigCaseSeekBar = (SeekBar) findViewById(R.id.seekBarBigLatin);
        smallCaseSeekBar = (SeekBar) findViewById(R.id.seekBarSmallLatin);
        lengthSeekBar = (SeekBar) findViewById(R.id.seekBarPasswordLength);
        saveButton = (FloatingActionButton) findViewById(R.id.buttonSave);
        addButton = (FloatingActionButton) findViewById(R.id.buttonAdd);
        showButton=(Button) findViewById(R.id.buttonShow) ;
        possible = (EditText) findViewById(R.id.editTextPossible);
        required = (EditText) findViewById(R.id.editTextRequired);
        password=(TextView) findViewById(R.id.textView3);
        savedPassword = (ListView) findViewById(R.id.savedPassword);
        addThumb(numberSeekBar, lengthSeekBar, numberSeekBar, bigCaseSeekBar, smallCaseSeekBar, required);
        addThumb(bigCaseSeekBar, lengthSeekBar, numberSeekBar, bigCaseSeekBar, smallCaseSeekBar, required);
        addThumb(smallCaseSeekBar, lengthSeekBar, numberSeekBar, bigCaseSeekBar, smallCaseSeekBar, required);
        addThumb(lengthSeekBar, lengthSeekBar, numberSeekBar, bigCaseSeekBar, smallCaseSeekBar, required);
        addChecker(numberSwitch, numberSeekBar);
        addChecker(bigCaseSwitch, bigCaseSeekBar);
        addChecker(smallCaseSwitch, smallCaseSeekBar);
        saveButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        showButton.setOnClickListener(this);
        setPasswords();
        adapter=new PasswordAdapter(this,passwordList);
        savedPassword.setAdapter(adapter);
        Log.d("we","2sdds");
        SwipeDismissListViewTouchListener touchListener =
                 new SwipeDismissListViewTouchListener(
                         savedPassword,
                         new SwipeDismissListViewTouchListener.DismissCallbacks() {

                             @Override
                             public boolean canDismiss(int position) {
                                 Log.d("Hey","Hey");
                                 return true;
                             }
                             @Override
                             public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                 Log.d("Past","Past");
                                      for (int position : reverseSortedPositions) {
                                                 adapter.remove(adapter.getItem(position));
                                            }
                                        adapter.notifyDataSetChanged();                    }
                             });
         savedPassword.setOnTouchListener(touchListener);
         savedPassword.setOnScrollListener(touchListener.makeScrollListener());
    }

    public void setPasswords() {
       SQLiteDatabase database=new DatabaseHelper(this).getReadableDatabase();
        long n= DatabaseUtils.queryNumEntries(database, "passwords");
        passwordList.clear();
        if (adapter!=null)  adapter.clear();
        Cursor  cursor = database.rawQuery("select * from passwords",null);
        if (cursor .moveToFirst())
                while (cursor.isAfterLast() == false) {
                    passwordList.add(new PasswordHolder(cursor));
                    cursor.moveToNext();
                }



        database.close();
      if (adapter!=null) adapter.notifyDataSetChanged();
    }

    public void addChecker(Switch mSwitch, final SeekBar mSeekBar) {
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mSeekBar.setVisibility(View.VISIBLE);
                else {
                    mSeekBar.setVisibility(View.INVISIBLE);
                    mSeekBar.setProgress(0);
                }
            }
        });
    }

    ;

    public void addThumb(SeekBar mSeekBar, final SeekBar lengthSeekBar, final SeekBar numberSeekBar, final SeekBar bigCaseSeekBar, final SeekBar smallCaseSeekBar, final EditText require) {
        if (mSeekBar != null) {
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar mSeekBar, int arg1, boolean arg2) {
                    Drawable drawable= getResources().getDrawable(R.drawable.circle_seekbar);
                    Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bmp);
                    String text;
                    if (mSeekBar != lengthSeekBar) text = Integer.toString(mSeekBar.getProgress());
                    else {
                        int i = numberSeekBar.getProgress()
                                + bigCaseSeekBar.getProgress() + smallCaseSeekBar.getProgress() + require.getText().length();
                        text = Integer.toString(mSeekBar.getProgress() + i);
                        mSeekBar.setMax(20 - i);

                    }
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    drawable.draw(canvas);
                    Paint p = new Paint();
                    p.setTypeface(Typeface.DEFAULT_BOLD);
                    p.setTextSize(60);
                    p.setColor(Color.BLUE);
                    int width = (int) p.measureText(text);
                    int yPos = (int) ((canvas.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
                    canvas.drawText(text, (bmp.getWidth() - width) / 2, yPos, p);
                    mSeekBar.setThumb(new BitmapDrawable(getResources(), bmp));
                    if (mSeekBar != lengthSeekBar) onProgressChanged(lengthSeekBar, 0, true);
                    else passwordCreation();
                }
            });
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mSeekBar.setProgress(0);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {

            final CharSequence[] items = {" English "," Russian "};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog levelDialog=builder.create();
            builder.setTitle("Select Language");
            builder.setSingleChoiceItems(items, -1, this);
            languageDialog = builder.create();
            languageDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void passwordCreation(){
        int digits = numberSwitch.isChecked() ? numberSeekBar.getProgress() : 0;
        int bigCase = bigCaseSwitch.isChecked() ? bigCaseSeekBar.getProgress() : 0;
        int smallCase = smallCaseSwitch.isChecked() ? smallCaseSeekBar.getProgress() : 0;
        if (!(numberSwitch.isChecked() || bigCaseSwitch.isChecked()  ||smallCaseSwitch.isChecked())){
            Toast.makeText(this,getResources().getString(R.string.no_password_response),Toast.LENGTH_SHORT).show();
            return;
        }
        Password mPassword = new Password(numberSwitch.isChecked(), bigCaseSwitch.isChecked(), smallCaseSwitch.isChecked(),
                digits, bigCase, smallCase, requiredSymbols,
                possibleSymbols, lengthSeekBar.getProgress());
        password.setText(mPassword.creation());

    }
    @Override
    public void onClick(View v) {
        if (v == saveButton) {
            if ((password.getText() == null) || (password.getText().length() < 1)) {
                Toast.makeText(this,getResources().getString(R.string.no_password_response),Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
            final EditText input = new EditText(this);
            builder.setMessage("Comment to the Password");
            builder.setCancelable(false);
            builder.setView(dialogView);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                    SQLiteDatabase database = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("password", password.getText().toString());
                    values.put("comment", input.getText().toString());
                    values.put("date", new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
                    database.insert("passwords", null, values);
                    setPasswords();
                    database.close();
                    NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notify=new Notification.Builder
                            (getApplicationContext()).setContentTitle("PassGen").setContentText(password.getText().toString()).
                            setContentTitle("Comment").setSmallIcon(R.drawable.circle_seekbar).build();

                    notify.flags |= Notification.FLAG_AUTO_CANCEL;
                    notif.notify(0, notify);
                }
            })

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        if (v == addButton) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            final EditText passwordBox = new EditText(this);
            passwordBox.setHint("Password");
            layout.addView(passwordBox);
            passwordBox.setText(savedPasswordClip);
            final EditText comment = new EditText(this);
            comment.setHint("Comment");
            layout.addView(comment);
            builder.setCancelable(false);
            builder.setMessage("Add a Password");
            builder.setView(layout);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if  (passwordBox.getText().length()<4) {
                    Toast.makeText(MainActivity.this,"Password is too short; fail" ,Toast.LENGTH_SHORT).show();
                    return;
                    }
                    DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                    SQLiteDatabase database = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("password", passwordBox.getText().toString());
                    values.put("comment", comment.getText().toString());
                  values.put("date", new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
                    database.insert("passwords", null, values);
                    setPasswords();
                    database.close();
                }
            })

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if (v == showButton) {
            Button button=(Button)v;
            if (button.getText().equals(getResources().getString(R.string.show_additional))){
                findViewById(R.id.add_settings).setVisibility(View.VISIBLE);
                button.setText(R.string.hide_additional);
                required.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    requiredSymbols=s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                possible.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        possibleSymbols=s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }else{
                findViewById(R.id.add_settings).setVisibility(View.GONE);
                button.setText(getResources().getString(R.string.show_additional));
            }
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences settings = getSharedPreferences("general_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("language", getResources().getConfiguration().locale.getLanguage());
        editor.commit();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
               getResources().getConfiguration().setLocale(new Locale("en"));
                break;
            case 1:
                getResources().getConfiguration().setLocale(new Locale("ru"));

                break;
        }
        languageDialog.dismiss();
    }

}
