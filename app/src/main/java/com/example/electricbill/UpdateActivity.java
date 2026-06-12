package com.example.electricbill;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {
    protected Cursor cursor;
    DataHelper dbHelper;
    EditText etMonth, etKwh, etRebate;
    Button btnUpdate, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        dbHelper = new DataHelper(this);

        etMonth = findViewById(R.id.etMonth);
        etKwh = findViewById(R.id.etKwh);
        etRebate = findViewById(R.id.etUpdateRebate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnUpdateBack);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM bill_history WHERE id = '" + getIntent().getStringExtra("bill_id") + "'", null);

        if (cursor.moveToFirst()) {
            etMonth.setText(cursor.getString(1));
            etKwh.setText(cursor.getString(2));
            etRebate.setText(cursor.getString(3));
        }
        cursor.close();

        btnUpdate.setOnClickListener(arg0 -> {
            String kwhStr = etKwh.getText().toString().trim();
            String rebateStr = etRebate.getText().toString().trim();

            if (kwhStr.isEmpty() || rebateStr.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            double kwh = Double.parseDouble(kwhStr);
            int rebatePercent = Integer.parseInt(rebateStr);

            double totalCharges = 0;
            if (kwh <= 200) {
                totalCharges = kwh * 0.218;
            } else if (kwh <= 300) {
                totalCharges = (200 * 0.218) + ((kwh - 200) * 0.334);
            } else if (kwh <= 600) {
                totalCharges = (200 * 0.218) + (100 * 0.334) + ((kwh - 300) * 0.516);
            } else {
                totalCharges = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((kwh - 600) * 0.546);
            }

            double rebateAmount = totalCharges * (rebatePercent / 100.0);
            double finalCost = totalCharges - rebateAmount;

            SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
            dbWrite.execSQL("UPDATE bill_history SET month='" + etMonth.getText().toString() +
                    "', kwh='" + kwh +
                    "', rebate='" + rebatePercent +
                    "', total_charges='" + totalCharges +
                    "', final_cost='" + finalCost +
                    "' WHERE id='" + getIntent().getStringExtra("bill_id") + "'");

            Toast.makeText(getApplicationContext(), "Data Updated Successfully!", Toast.LENGTH_SHORT).show();
            finish(); 
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
