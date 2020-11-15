package com.spidchenko.week1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PlanetChooserActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.spidchenko.week1.extra.REPLY";
    private ListView listView;
    private String[] planets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planet_chooser);
        listView = findViewById(R.id.list_view);
        planets = getResources().getStringArray(R.array.planets);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, planets);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, position);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }
}