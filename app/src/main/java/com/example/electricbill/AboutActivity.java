package com.example.electricbill;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvGithub = findViewById(R.id.tvGithubUrl);
        tvGithub.setOnClickListener(v -> {
            String url = tvGithub.getText().toString();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        Button btnBack = findViewById(R.id.btnAboutBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}