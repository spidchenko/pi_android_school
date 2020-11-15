package com.spidchenko.week1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView textInvisible;
    private TextView replyText;
    private String[] planets;
    public static final int TEXT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        replyText = findViewById(R.id.text_to_share);
        textInvisible = findViewById(R.id.text_invisible);
        planets = getResources().getStringArray(R.array.planets);

        if (intent != null) {
            if (intent.getAction() == Intent.ACTION_SEND) {
                String textFromImplicitIntent = intent.getExtras().getString(Intent.EXTRA_TEXT);
                textInvisible.setText(textFromImplicitIntent);
                textInvisible.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showVariants(View view) {
        Intent intent = new Intent(this, PlanetChooserActivity.class);
        startActivityForResult(intent, TEXT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                int reply = data.getIntExtra(PlanetChooserActivity.EXTRA_REPLY, 0);
                replyText.setText(planets[reply]);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("textView1", textInvisible.getText().toString());
        outState.putString("textView2", replyText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textInvisible.setText(savedInstanceState.getString("textView1"));
        replyText.setText(savedInstanceState.getString("textView2"));
    }

    public void shareText(View view) {
        String text = replyText.getText().toString();
        ShareCompat.IntentBuilder
                .from(this)
                .setType("text/plain")
                .setText(text)
                .startChooser();
    }
}