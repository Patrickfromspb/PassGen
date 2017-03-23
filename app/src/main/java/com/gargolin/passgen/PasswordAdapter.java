package com.gargolin.passgen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by User on 03.03.2017.
 */
public class PasswordAdapter extends ArrayAdapter<PasswordHolder> {
    ArrayList<PasswordHolder> users;
    Context context;
    public static String savedPasswordClip="";
    public PasswordAdapter(Context context, ArrayList<PasswordHolder> users) {
        super(context, 0, users);
        this.context=context;
        this.users=users;
    }
    @Override
    public  void remove(PasswordHolder passwordHolder){
        super.remove(passwordHolder);
        SQLiteDatabase database=new DatabaseHelper(getContext()).getReadableDatabase();
        database.delete("passwords", "id= "+passwordHolder.getId(),null);
        database.close();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PasswordHolder passwordHolder = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.text_view, parent, false);
        }
        if (position==0) {
            ((Button)convertView.findViewById(R.id.buttonning)).setText("To Tail");

        }

        ((Button)convertView.findViewById(R.id.buttonning)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int j=0;
                if  (position >0) j=position-1;
                else j=users.size()-1;
                PasswordHolder first=users.get(position);
                PasswordHolder second=users.get(j);
                Collections.swap(users,position,j);
                SQLiteDatabase database=new DatabaseHelper(getContext()).getReadableDatabase();
                ContentValues values=new ContentValues();
                values.put("password",first.getPassword());
                values.put("comment", first.getComment());
                values.put("date", first.getDate());
                database.update("passwords",values,"id= "+ second.getId(),null);
                values=new ContentValues();
                values.put("password",second.getPassword());
                values.put("comment", second.getComment());
                values.put("date", second.getDate());
                database.update("passwords",values,"id= "+ first.getId(),null);
                database.close();
                notifyDataSetChanged();

            }
        });
      final  TextView passworddb = (TextView) convertView.findViewById(R.id.passworddb);
        TextView commentdb= (TextView) convertView.findViewById(R.id.commentdb);
        TextView datedb= (TextView) convertView.findViewById(R.id.datedb);
        passworddb.setText(passwordHolder.getPassword());
        commentdb.setText(passwordHolder.getComment());
        datedb.setText(passwordHolder.getDate());
        ((Button)convertView.findViewById(R.id.buttonCopy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedPasswordClip=passworddb.getText().toString();
                Toast.makeText(context,"Copied to clipBoard",Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", passworddb.getText().toString());
                clipboard.setPrimaryClip(clip);
            }
        });

        return convertView;
    }

}