package com.example.electricbill;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    DataHelper dbcenter;
    EditText etKwh;
    Spinner spinnerMonth;
    SeekBar seekBarRebate;
    TextView tvRebateValue, tvTotalCharges, tvFinalCost;
    Button btnCalculate, btnViewHistory, btnAbout;
    int rebatePercent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbcenter = new DataHelper(this);

        etKwh = findViewById(R.id.etKwh);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        seekBarRebate = findViewById(R.id.seekBarRebate);
        tvRebateValue = findViewById(R.id.tvRebateValue);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnAbout = findViewById(R.id.btnAbout);

        seekBarRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rebatePercent = progress;
                tvRebateValue.setText(rebatePercent + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = spinnerMonth.getSelectedItem().toString();
                String kwhStr = etKwh.getText().toString().trim();

                if (kwhStr.isEmpty()) {
                    etKwh.setError("Please enter the kWh unit first!");
                    return;
                }

                double kwh = Double.parseDouble(kwhStr);

                if (kwh < 1 || kwh > 1000) {
                    etKwh.setError("Units must be between 1 and 1000 kWh!");
                    return;
                }

                double totalCharges = 0;

                if (kwh <= 200) {
                    totalCharges = kwh * 0.218;
                } else if (kwh <= 300) {
                    totalCharges = (200 * 0.218) + ((kwh - 200) * 0.334);
                } else if (kwh <= 600) {
                    totalCharges = (200 * 0.218) + (100 * 0.334) + ((kwh - 300) * 0.516);
                } else if (kwh <= 1000) {
                    totalCharges = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((kwh - 600) * 0.546);
                }

                double rebateAmount = totalCharges * (rebatePercent / 100.0);
                double finalCost = totalCharges - rebateAmount;

                tvTotalCharges.setText(String.format("Total Charges: RM %.2f", totalCharges));
                tvFinalCost.setText(String.format("Final Cost: RM %.2f", finalCost));

                SQLiteDatabase db = dbcenter.getWritableDatabase();
                db.execSQL("INSERT INTO bill_history(month, kwh, rebate, total_charges, final_cost) VALUES('" +
                        month + "','" + kwh + "','" + rebatePercent + "','" + totalCharges + "','" + finalCost + "')");

                Toast.makeText(getApplicationContext(), "Record Saved Successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }
}