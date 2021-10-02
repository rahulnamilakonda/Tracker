package com.example.tracker;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private String PhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startups = findViewById(R.id.Submit);

        AutoCompleteTextView textView = findViewById(R.id.PhoneField);
        // Get the string array
        String[] phoneNumbers = getResources().getStringArray(R.array.phone_array);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, phoneNumbers);
        textView.setAdapter(adapter);

        startups.setOnClickListener(view -> {
            PhoneNumber = textView.getEditableText().toString();
            Log.d(TAG, PhoneNumber);
            Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_LONG).show();
            Intent intent;
            intent = new Intent(MainActivity.this, NextScreen.class);
            intent.putExtra("PhoneNum", PhoneNumber);
            startActivity(intent);
        });
    }
}