package com.example.shwetha.blockdata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class SmartContract extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_contract);





    }

    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox:
                if (checked)
                { Intent i=new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(i);}
            else{
                    Toast toast=Toast.makeText(getApplicationContext(),"Click on Checkbox",Toast.LENGTH_SHORT); toast.setMargin(50,50);
                }

                break;


        }
    }
}
