package com.example.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myplaces.data.MyPlace;
import com.example.myplaces.data.MyPlacesData;

public class DeletePlace extends Activity implements View.OnClickListener {
    Button deleteButton, cancelButton;
    int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_place);

        cancelButton = findViewById(R.id.btn_cancel_deleteplace);
        cancelButton.setOnClickListener(this);
        deleteButton = findViewById(R.id.btn_delete_deleteplace);
        deleteButton.setOnClickListener(this);

        try {
            Intent listIntent = getIntent();
            Bundle indexBundle = listIntent.getExtras();
            index = indexBundle.getInt("index");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_deleteplace: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }
            case R.id.btn_delete_deleteplace: {
                MyPlacesData.getInstance().removePlace(index);
                setResult(Activity.RESULT_OK);
                finish();
                break;
            }
        }
    }
}
