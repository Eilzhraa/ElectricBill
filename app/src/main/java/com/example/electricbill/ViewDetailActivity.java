package com.example.electricbill;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewDetailActivity extends AppCompatActivity {
    protected Cursor cursor;
    DataHelper dbHelper;
    TextView tvId, tvMonth, tvKwh, tvRebate, tvTotal, tvFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);

        dbHelper = new DataHelper(this);
        tvId = findViewById(R.id.tvId);
        tvMonth = findViewById(R.id.tvMonth);
        tvKwh = findViewById(R.id.tvKwh);
        tvRebate = findViewById(R.id.tvRebate);
        tvTotal = findViewById(R.id.tvTotal);
        tvFinal = findViewById(R.id.tvFinal);
        Button btnBack = findViewById(R.id.btnBack);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM bill_history WHERE id = '" + getIntent().getStringExtra("bill_id") + "'", null);

        if (cursor.moveToFirst()) {
            // Kita set nilai angka sahaja supaya susunan sebaris kiri-kanan tak pecah
            tvId.setText("ID: #" + cursor.getString(0));
            tvMonth.setText(cursor.getString(1));
            tvKwh.setText(cursor.getString(2) + " kWh");
            tvRebate.setText(cursor.getString(3) + "%");
            tvTotal.setText("RM " + String.format("%.2f", cursor.getDouble(4)));
            tvFinal.setText("RM " + String.format("%.2f", cursor.getDouble(5)));
        }
        cursor.close();

        btnBack.setOnClickListener(v -> finish());
    }
}