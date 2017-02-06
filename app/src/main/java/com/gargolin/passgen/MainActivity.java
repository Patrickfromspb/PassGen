package com.gargolin.passgen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);



    final Switch numberSwitch = (Switch) findViewById(R.id.switch2);
    final Switch bigCaseSwitch = (Switch) findViewById(R.id.switch3);
    final Switch smallCaseSwitch = (Switch) findViewById(R.id.switch4);
    final SeekBar numberSeekBar = (SeekBar) findViewById(R.id.seekBar4);
    final SeekBar bigCaseSeekBar = (SeekBar) findViewById(R.id.seekBar5);
    final SeekBar smallCaseSeekBar = (SeekBar) findViewById(R.id.seekBar6);
    final SeekBar lengthSeekBar = (SeekBar) findViewById(R.id.seekBar7);
    final Button button = (Button) findViewById(R.id.button);
    final EditText required = (EditText) findViewById(R.id.editText8);
    final EditText possible = (EditText) findViewById(R.id.editText5);

    addThumb(numberSeekBar, lengthSeekBar, numberSeekBar, bigCaseSeekBar, smallCaseSeekBar, required);

    addThumb(bigCaseSeekBar, lengthSeekBar, numberSeekBar, bigCaseSeekBar, smallCaseSeekBar, required);

    addThumb(smallCaseSeekBar, lengthSeekBar, numberSeekBar, bigCaseSeekBar, smallCaseSeekBar, required);

    addThumb(lengthSeekBar, lengthSeekBar, numberSeekBar, bigCaseSeekBar, smallCaseSeekBar, required);

    addChecker(numberSwitch, numberSeekBar);

    addChecker(bigCaseSwitch, bigCaseSeekBar);

    addChecker(smallCaseSwitch, smallCaseSeekBar);

    button.setOnClickListener(new View.OnClickListener()

    {
        public void onClick (View v){
        int digits = numberSwitch.isChecked() ? numberSeekBar.getProgress() : 0;
        int bigCase = bigCaseSwitch.isChecked() ? bigCaseSeekBar.getProgress() : 0;
        int smallCase = smallCaseSwitch.isChecked() ? smallCaseSeekBar.getProgress() : 0;
        Log.d(required.getText().toString(), required.getText().toString());
        Password mPassword = new Password(numberSwitch.isChecked(), bigCaseSwitch.isChecked(), smallCaseSwitch.isChecked(),
                digits, bigCase, smallCase, required.getText().toString(),
                possible.getText().toString(), lengthSeekBar.getProgress());
        ((TextView) findViewById(R.id.textView)).setText(mPassword.creation());
    }
    }

    );
    required.setOnFocusChangeListener(new View.OnFocusChangeListener()

    {
        @Override
        public void onFocusChange (View v,boolean hasFocus){
        if (!hasFocus)
            addThumb(lengthSeekBar, lengthSeekBar, numberSeekBar, bigCaseSeekBar, smallCaseSeekBar, required);
    }
    }

    );
}

    public void addChecker(Switch mSwitch, final SeekBar mSeekBar){
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mSeekBar.setVisibility(View.VISIBLE);
                else  {
                    mSeekBar.setVisibility(View.INVISIBLE);
                    mSeekBar.setProgress(0);
                }
            }
        });
    };

    public void addThumb( SeekBar mSeekBar,final SeekBar lengthSeekBar, final SeekBar numberSeekBar,final SeekBar bigCaseSeekBar,final SeekBar smallCaseSeekBar, final EditText require ){
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
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_image);
                    Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888  , true);
                    Canvas c = new Canvas(bmp);
                    String text;
                    if (mSeekBar!=lengthSeekBar) text = Integer.toString(mSeekBar.getProgress());
                    else{
                        int i=numberSeekBar.getProgress()
                                +bigCaseSeekBar.getProgress()+smallCaseSeekBar.getProgress()+require.getText().length();
                        text = Integer.toString(mSeekBar.getProgress()+i);
                        mSeekBar.setMax(20-i);

                    }

                    Paint p = new Paint();
                    p.setTypeface(Typeface.DEFAULT_BOLD);
                    p.setTextSize(60);
                    p.setColor(Color.BLUE);
                    int width = (int) p.measureText(text);
                    int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
                    c.drawText(text, (bmp.getWidth()-width)/2, yPos, p);
                    mSeekBar.setThumb(new BitmapDrawable(getResources(), bmp));
                    if (mSeekBar!=lengthSeekBar) onProgressChanged(lengthSeekBar,0,true);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

