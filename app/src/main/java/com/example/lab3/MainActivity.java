package com.example.lab3;


import android.app.ActionBar;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements AbsListView.MultiChoiceModeListener {

    private DBHelper DBHelper;
    private SQLiteDatabase DB;
    private long rowID;
    private Cursor kursor;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper = new DBHelper(this);
        DB = DBHelper.getWritableDatabase();
        list = (ListView) findViewById(android.R.id.list);

        getSupportActionBar().setTitle("Smartphone database");
        setUpContextualMenu();
        setUpOnClickListener();

        showDB();


    }

    public void showDB() {
        kursor = DB.query(true, //distinct
                DBHelper.TABLE_NAME,  //tabela
                new String[]{DBHelper.ID, DBHelper.COLUMN1, DBHelper.COLUMN2}, //kolumny
                null,  //where
                null,  //whereArgs - argumenty zastępujące "?" w where
                null,  //group by
                null,  //having
                null,  //order by
                null); //limit
        startManagingCursor(kursor);

        kursor.moveToFirst();

        while (!kursor.isAfterLast()) {
            int indeksKolumny = kursor.getColumnIndexOrThrow(DBHelper.COLUMN1);
            String wartosc = kursor.getString(indeksKolumny);
            kursor.moveToNext();
        }

        String[] mapujZ = new String[]{
                DBHelper.COLUMN1, DBHelper.COLUMN2
        };
        int[] mapujDo = new int[]{
                R.id.textView_brand, R.id.textView_model
        };

        SimpleCursorAdapter adapterBazy = new SimpleCursorAdapter(this, R.layout.list_item, kursor, mapujZ, mapujDo);

        list.setAdapter(adapterBazy);


    }

    public void insertToDB(String brand, String model) {

        ContentValues wartosci = new ContentValues();
        wartosci.put(DBHelper.COLUMN1, brand);
        wartosci.put(DBHelper.COLUMN2, model);

        this.rowID = DB.insert(DBHelper.TABLE_NAME, null, wartosci);


    }

    public void updateDBEntry(String brand, String model, int position) {

        ContentValues wartosci = new ContentValues();
        wartosci.put(DBHelper.COLUMN1, brand);
        wartosci.put(DBHelper.COLUMN2, model);


        DB.update(DBHelper.TABLE_NAME, wartosci, DBHelper.ID + "=" + position, null);

    }

    public void setUpContextualMenu() {
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean
            onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void
            onDestroyActionMode(ActionMode mode) {
            }

            @Override
            public boolean
            onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.contextual_menu, menu);
                return true;
            }

            @Override
            public boolean
            onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.deleteSmartphones:
                        deleteSelected();
                        showToast("Deleting selected items...");
                        return true;
                }
                return false;
            }

            @Override
            public void
            onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            }
        });
    }

    public void setUpOnClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AddSmartphoneData.class);
                intent.putExtra("operationType", "update");
                intent.putExtra("position", position+1);
                
                startActivityForResult(intent, new Integer(0));
            }
        });
    }

    private void deleteSelected() {
        long checked[] = list.getCheckedItemIds();
        for (int i = 0; i < checked.length; ++i) {
            //getContentResolver().delete(ContentUris.withAppendedId(MojProvider.URI_ZAWARTOSCI, checked[i]), null, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.addSmartphone) {
            Intent intent = new Intent(this, AddSmartphoneData.class);
            intent.putExtra("operationType", "insert");
            startActivityForResult(intent, new Integer(0));
            return true;
        } else return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int kodZadania, int kodWyniku, Intent data) {
        super.onActivityResult(kodZadania, kodWyniku, data);
        if (kodWyniku == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String brand = bundle.getString("brand");
            String model = bundle.getString("model");
            String operationType = bundle.getString("operationType");
            if (operationType.startsWith("insert"))
            {
                insertToDB(brand, model);
                showToast("New entry added");
            }
            if (operationType.startsWith("update"))
            {
                int position=bundle.getInt("position");
                updateDBEntry(brand, model,position);
                showToast("Entry updated");
            }
        }
    }

    @Override
    protected void onDestroy() {
        DB.close();
        super.onDestroy();

    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

    }
}
