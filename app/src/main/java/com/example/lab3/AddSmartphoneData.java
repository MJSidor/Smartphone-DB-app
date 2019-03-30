package com.example.lab3;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddSmartphoneData extends AppCompatActivity {

    private EditText brand;
    private EditText version;
    private EditText model;
    private EditText www;
    String operationType;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_smartphone_data);

        brand = (EditText) findViewById(R.id.editText_brand);
        version = (EditText) findViewById(R.id.editText_version);
        model = (EditText) findViewById(R.id.editText_model);
        www = (EditText) findViewById(R.id.editText_www);

        handleOperationType();




    }

    public void handleOperationType()
    {
        Bundle bundleIn = getIntent().getExtras();
        operationType = bundleIn.getString("operationType");
        if (operationType.startsWith("insert")) getSupportActionBar().setTitle("Add a smartphone to the DB");
        if (operationType.startsWith("update"))
        {
            getSupportActionBar().setTitle("Update DB entry");
            position=bundleIn.getInt("position");
        }
    }

    public boolean validate() {
        if (!isDataOK()) {
            showToast("Wprowadz poprawne dane");
            return false;
        } else return true;
    }

    public boolean isDataOK() {
        if (brand.getText().toString().length() >= 3 && version.getText().toString().length() >= 3 && model.getText().toString().length() >= 3) {
            return true;
        } else return false;
    }


    public void cancel(View view) {
        showToast("Anuluję...");
        finish();
    }

    public void save(View view) {
        if (validate()) {

            Bundle bundleOut = new Bundle();
            bundleOut.putString("brand", brand.getText().toString());
            bundleOut.putString("model", model.getText().toString());
            bundleOut.putString("operationType", operationType);
            if (operationType.startsWith("update")) bundleOut.putInt("position",position);
            Intent intentOut = new Intent();
            intentOut.putExtras(bundleOut);
            setResult(RESULT_OK, intentOut);
            finish();
        }
    }

    public void www(View view) {
        if (www.getText().toString().contains(".") && www.getText().toString().length() >= 5) {
            Uri webpage;
            if (!www.getText().toString().startsWith("http://"))
                webpage = Uri.parse("http://" + www.getText().toString());
            else webpage = Uri.parse(www.getText().toString());
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(webIntent);
        } else showToast("Wprowadź poprawny adres!");
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }


}
