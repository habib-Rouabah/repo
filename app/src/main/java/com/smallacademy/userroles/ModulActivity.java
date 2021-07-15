package com.smallacademy.userroles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import com.smallacademy.userroles.R;

import static com.smallacademy.userroles.SplashActivity.catList;


public class ModulActivity extends AppCompatActivity {
    private GridView catGrid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modul);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Modules");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        catGrid = findViewById(R.id.catGridview);


        CatGridAdapter adapter = new CatGridAdapter(catList);
        catGrid.setAdapter(adapter);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            ModulActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
