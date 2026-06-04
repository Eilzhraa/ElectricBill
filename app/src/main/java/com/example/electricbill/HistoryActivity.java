package com.example.electricbill;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {
    String[] register;
    String[] idList;
    ListView ListView01;
    protected Cursor cursor;
    DataHelper dbcenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dbcenter = new DataHelper(this);
        RefreshList();

        Button btnHistoryBack = findViewById(R.id.btnHistoryBack);
        btnHistoryBack.setOnClickListener(v -> {
            finish(); // Arahan ni sepantas kilat tutup page history dan terus nampak dashboard semula
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        RefreshList();
    }

    public void RefreshList() {
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM bill_history", null);

        register = new String[cursor.getCount()];
        idList = new String[cursor.getCount()];
        cursor.moveToFirst();

        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            register[cc] = "Month: " + cursor.getString(1) + " | Cost: RM " + String.format("%.2f", cursor.getDouble(5));
            idList[cc] = cursor.getString(0);
        }

        ListView01 = findViewById(R.id.listView1);
        ListView01.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, register));
        ListView01.setSelected(true);

        ListView01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                final String selectionId = idList[arg2];
                final CharSequence[] dialogitem = {"View Details", "Update Record", "Delete Record"};

                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setTitle("Select Action");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Intent i = new Intent(getApplicationContext(), ViewDetailActivity.class);
                                i.putExtra("bill_id", selectionId);
                                startActivity(i);
                                break;
                            case 1:
                                Intent in = new Intent(getApplicationContext(), UpdateActivity.class);
                                in.putExtra("bill_id", selectionId);
                                startActivity(in);
                                break;
                            case 2:
                                SQLiteDatabase db = dbcenter.getWritableDatabase();
                                db.execSQL("delete from bill_history where id = '" + selectionId + "'");
                                Toast.makeText(getApplicationContext(), "Record Deleted Successfully", Toast.LENGTH_SHORT).show();
                                RefreshList(); // Refresh list to show updated logs
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
    }
}