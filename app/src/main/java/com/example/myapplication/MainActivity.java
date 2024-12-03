package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private RecyclerView emailRecyclerView;
    private ImageButton composeButton, statsButton, alertsButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        emailRecyclerView = findViewById(R.id.emailRecyclerView);
        composeButton = findViewById(R.id.composeButton);
        statsButton = findViewById(R.id.statsButton);
        alertsButton = findViewById(R.id.alertsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // Set RecyclerView adapter
        emailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emailRecyclerView.setAdapter(new EmailAdapter(getEmailList()));

        // Set button actions
        composeButton.setOnClickListener(v -> startActivity(new Intent(this, ComposeActivity.class)));
        statsButton.setOnClickListener(v -> {/* Open stats */});
        alertsButton.setOnClickListener(v -> {/* Open alerts */});
        settingsButton.setOnClickListener(v -> {/* Open settings */});
    }

    private List<Email> getEmailList() {
        List<Email> emails = new ArrayList<>();
        emails.add(new Email("AAA 교수님", "Subject goes here", "Lorem ipsum dolor sit amet", "Mar 1"));
        emails.add(new Email("BBB 교수님", "Subject goes here", "Lorem ipsum dolor sit amet", "Mar 1"));
        return emails;
    }
}