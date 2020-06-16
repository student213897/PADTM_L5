package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //private ArrayList<String> target;
    private SimpleCursorAdapter adapter;
    private MySQLite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String[] values = new String[] {"Pies", "Kot","Koń","Gołąb",
        //"Kruk","Dzik","Karp","Osioł","Chomik","Mysz", "Jeż", "Karaluch"};

        //target = new ArrayList<>();
        //target.addAll(Arrays.asList(values));
        db = new MySQLite(this);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                db.lista(),
                new String[] { "_id", "gatunek" },
                new int[] { android.R.id.text1, android.R.id.text2 },
                SimpleCursorAdapter.IGNORE_ITEM_VIEW_TYPE);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name = view.findViewById(android.R.id.text1);
                Animal zwierz = db.pobierz(Integer.parseInt(name.getText().toString()));
                Intent intencja = new Intent(getApplicationContext(), DodajWpis.class);
                intencja.putExtra("element", zwierz);
                startActivityForResult(intencja, 2);
            }
        });
        listView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView name = view.findViewById(android.R.id.text1);
                        db.usun(name.getText().toString());
                        adapter.changeCursor(db.lista());
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void nowyWpis(MenuItem mi){
        Intent intent = new Intent(this, DodajWpis.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Animal nowy = (Animal) extras.getSerializable("wpis");
            db.dodaj(nowy);
            adapter.changeCursor(db.lista());
            adapter.notifyDataSetChanged();
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Animal stary = (Animal) extras.getSerializable("wpis");
            db.aktualizuuj(stary);
            adapter.changeCursor(db.lista());
            adapter.notifyDataSetChanged();
        }
    }
}
