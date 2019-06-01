package com.example.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myplaces.data.MyPlace;
import com.example.myplaces.data.MyPlacesData;

public class EditMyPlaceActivity extends AppCompatActivity implements View.OnClickListener {
    Button saveButton, cancelButton, getLocationButton;
    EditText nameEditText, descEditText, latEditText, longEditText;
    boolean editMode = true;
    int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        nameEditText = findViewById(R.id.et_name_editmyplace);
        descEditText = findViewById(R.id.et_desc_editmyplace);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_place);

        try {
            Intent listIntent = getIntent();
            Bundle indexBundle = listIntent.getExtras();
            if (indexBundle != null)
                index = indexBundle.getInt("index");
            else
                editMode = false;
        } catch (Exception e) {
            editMode = false;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        saveButton = findViewById(R.id.btn_save_editmyplace);
        saveButton.setOnClickListener(this);
        cancelButton = findViewById(R.id.btn_cancel_editmyplace);
        cancelButton.setOnClickListener(this);
        getLocationButton = findViewById(R.id.btn_location_editmyplace);
        getLocationButton.setOnClickListener(this);

        if (!editMode) {
            // adding new place
            saveButton.setEnabled(false);
            saveButton.setText("Add");
        } else if (index >= 0) { // editing existing place
            nameEditText = findViewById(R.id.et_name_editmyplace);
            descEditText = findViewById(R.id.et_desc_editmyplace);
            latEditText = findViewById(R.id.et_lat_editmyplace);
            longEditText = findViewById(R.id.et_long_editmyplace);

            saveButton.setText("Save");
            MyPlace place = MyPlacesData.getInstance().getPlace(index);
            nameEditText.setText(place.name);
            descEditText.setText(place.description);
            latEditText.setText(place.latitude);
            longEditText.setText(place.longitude);
        }

        // set button save to enable only if there is text input
        nameEditText = findViewById(R.id.et_name_editmyplace);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(s.length() > 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        nameEditText = findViewById(R.id.et_name_editmyplace);
        descEditText = findViewById(R.id.et_desc_editmyplace);

        switch(v.getId()) {
            case R.id.btn_cancel_editmyplace: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }
            case R.id.btn_save_editmyplace: {
                String name = nameEditText.getText().toString();
                String description = descEditText.getText().toString();
                String latitude = latEditText.getText().toString();
                String longitude = longEditText.getText().toString();

                if (!editMode) { // adding new place
                    MyPlace place = new MyPlace(name, description);
                    place.latitude = latitude;
                    place.longitude = longitude;
                    MyPlacesData.getInstance().addNewPlace(place);
                } else { // edit existing place
                    MyPlacesData.getInstance().updatePlace(index, name, description, latitude, longitude);
                }
                setResult(Activity.RESULT_OK);
                finish();
                break;
            }
            case R.id.btn_location_editmyplace: {
                Intent i = new Intent(this, MapsActivity.class);
                i.putExtra("state", MapsActivity.SELECT_COORDINATES);
                startActivityForResult(i, 1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.show_map_item) {
           Intent i = new Intent(this, MapsActivity.class);
            i.putExtra("state", MapsActivity.SHOW_MAP);
           startActivity(i);
        } else if (id == R.id.about_item) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.my_places_list_item) {
            Intent i = new Intent(this, MyPlacesListActivity.class);
            startActivity(i);
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                String latitude = data.getExtras().getString("latitude");
                String longitude = data.getExtras().getString("longitude");

                latEditText = findViewById(R.id.et_lat_editmyplace);
                longEditText = findViewById(R.id.et_long_editmyplace);
                latEditText.setText(latitude);
                longEditText.setText(longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
