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
    // TODO: 15.11.2020 "textInvisible", "replyText" tell us nothing about the purpose of those variables. Use more appropriate naming next time.
    //  E.g. "replyText" can be "tvSelectedPlanet".
    private TextView textInvisible;
    private TextView replyText;
    private String[] planets;
    // TODO: 15.11.2020 Constants should be declared at the top most of the class
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
            // TODO: 15.11.2020 Pay attention to warnings (In this case "String values are compared using '==', not 'equals()'").
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
                // TODO: 15.11.2020 We could pass a selected string in extras and there would be no need to:
                //  - define planets array
                //  - access planets item by received index
                int reply = data.getIntExtra(PlanetChooserActivity.EXTRA_REPLY, 0);
                replyText.setText(planets[reply]);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO "textView1" and "textView2" are not the best names to describe these values purpose.
        // TODO: 15.11.2020 Avoid hardcode and duplicates.
        //  Harcode should be replaced with constant values.
        //  E.g. private static final SAVED_SELECTED_PLANET = "SAVED_SELECTED_PLANET" instead of "textView2" hardcode and duplicate
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