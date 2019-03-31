package com.example.lab3;


import android.app.ActionBar;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements AbsListView.MultiChoiceModeListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private DBHelper DBHelper;
    private SQLiteDatabase DB;
    private long rowID;
    private Cursor kursor;
    private ListView list;
    private SimpleCursorAdapter adapterBazy;
    private Provider dbProvider;

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

        //showDB();

        uruchomLoader();


    }

    public void showDB() {

        String[] mapujZ = new String[]{
                DBHelper.COLUMN1, DBHelper.COLUMN2
        };
        int[] mapujDo = new int[]{
                R.id.textView_brand, R.id.textView_model
        };

        kursor = getContentResolver().query(Provider.URI_ZAWARTOSCI, new String[]{DBHelper.ID, DBHelper.COLUMN1, DBHelper.COLUMN2}, null, null, null);

        startManagingCursor(kursor);

        kursor.moveToFirst();

        while (!kursor.isAfterLast()) {
            int indeksKolumny = kursor.getColumnIndexOrThrow(DBHelper.COLUMN1);
            String wartosc = kursor.getString(indeksKolumny);
            kursor.moveToNext();
        }

        SimpleCursorAdapter adapterBazy = new SimpleCursorAdapter(this, R.layout.list_item, kursor, mapujZ, mapujDo);

        list.setAdapter(adapterBazy);

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
            int checkedCount = 0;
            private ActionMenuItemView deleteCounter = findViewById(R.id.menu_counter);

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
                checkedCount=0;
                return true;
            }

            @Override
            public boolean
            onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.deleteSmartphones:
                        deleteSelected();
                        showDB();
                        checkedCount = 0;
                        deleteCounter = findViewById(R.id.menu_counter);
                        deleteCounter.setText(Integer.toString(checkedCount));
                        showToast("Deleting selected items...");

                        return true;
                }
                return false;
            }


            @Override
            public void
            onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) checkedCount++;
                if (!checked) checkedCount--;
                deleteCounter = findViewById(R.id.menu_counter);
                deleteCounter.setText(Integer.toString(checkedCount));
            }
        });
    }

    public void setUpOnClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AddSmartphoneData.class);
                intent.putExtra("operationType", "update");
                intent.putExtra("id", id);

                startActivityForResult(intent, new Integer(0));
            }
        });
    }

    private void deleteSelected() {
        long checked[] = list.getCheckedItemIds();
        for (int i = 0; i < checked.length; ++i) {
            getContentResolver().delete(ContentUris.withAppendedId(dbProvider.URI_ZAWARTOSCI, checked[i]), DBHelper.ID + " = " + Long.toString(checked[i]), null);
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
            if (operationType.startsWith("insert")) {
                //insertToDB(brand, model);
                showToast("New entry added");
            }
            if (operationType.startsWith("update")) {
                int position = bundle.getInt("position");
                //updateDBEntry(brand, model, position);
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

    private void uruchomLoader() {
        getLoaderManager().initLoader(0, //identyfikator loadera
                null, //argumenty (Bundle)
                 this); //klasa implementująca LoaderCallbacks

        String[] mapujZ = new String[]{
                DBHelper.COLUMN1, DBHelper.COLUMN2
        };
        int[] mapujDo = new int[]{
                R.id.textView_brand, R.id.textView_model
        };

        SimpleCursorAdapter DBadapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_item, kursor, mapujZ, mapujDo);
        list.setAdapter(DBadapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // adapter wymaga aby wyniku zapytania znajdowała się kolumna _id
        String[] projection = {DBHelper.ID, DBHelper.COLUMN1, DBHelper.COLUMN2}; // inne „kolumny” do wyświetlenia
        CursorLoader cLoader = new CursorLoader(this,
                Provider.URI_ZAWARTOSCI, projection, null, null, null);
        //return cLoader;
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        //ustawienie danych w adapterze
        adapterBazy.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        adapterBazy.swapCursor(null);
    }

    /*
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor dane) {
        //ustawienie danych w adapterze
        adapterBazy.swapCursor(dane);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        adapterBazy.swapCursor(null);
    }
*/

}
