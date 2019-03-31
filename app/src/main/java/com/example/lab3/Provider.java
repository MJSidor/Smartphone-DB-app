package com.example.lab3;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class Provider extends ContentProvider {

    private DBHelper dbHelper;
    //identyfikator (ang. authority) dostawcy
    private static final String IDENTIFIER = "com.example.smartphones.Provider";

    //stała – aby nie trzeba było wpisywać tekstu samodzielnie
    public static final Uri URI_ZAWARTOSCI = Uri.parse("content://" + IDENTIFIER + "/" + DBHelper.TABLE_NAME);

    //stałe pozwalające zidentyfikować rodzaj rozpoznanego URI
    private static final int WHOLE_TABLE = 1;
    private static final int SELECTED_ROW = 2;

    //UriMacher z pustym korzeniem drzewa URI (NO_MATCH)
    private static final UriMatcher uriMatch = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //dodanie rozpoznawanych URI
        uriMatch.addURI(IDENTIFIER, DBHelper.TABLE_NAME,
                WHOLE_TABLE);
        uriMatch.addURI(IDENTIFIER, DBHelper.TABLE_NAME +
                "/#", SELECTED_ROW);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //czy wiersz czy cała tabela i otworzenie bazy
        int typUri = uriMatch.match(uri);
        DBHelper dbHelper=new DBHelper(getContext());
        SQLiteDatabase DB=dbHelper.getWritableDatabase();

        long idDodanego = 0;
        switch (typUri) {
            case WHOLE_TABLE:
                //zapisanie do magazynu – np. insert do bazy...
                idDodanego=DB.insert(dbHelper.TABLE_NAME, //tabela
                        null, //nullColumnHack
                        values); //wartości
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " +
                        uri);
        }
        //powiadomienie o zmianie danych (->np. odświeżenie listy)
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(DBHelper.TABLE_NAME + "/" + idDodanego);
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int typUri = uriMatch.match(uri);
        DBHelper dbHelper=new DBHelper(getContext());
        SQLiteDatabase DB=dbHelper.getWritableDatabase();

        Cursor kursor = null;
        switch (typUri) {
            case WHOLE_TABLE:
                kursor = DB.query(true, //distinct
                        dbHelper.TABLE_NAME, //tabela
                        projection, //kolumny
                        selection, //where
                        selectionArgs, //whereArgs - argumenty zastępujące "?" w where
                        null, //group by
                        null, //having
                        sortOrder, //order by
                        null); //limit
                break;
            case SELECTED_ROW:
                kursor = DB.query(true, //distinct
                        dbHelper.TABLE_NAME, //tabela
                        projection, //kolumny
                        selection, //where
                        selectionArgs, //whereArgs - argumenty zastępujące "?" w where
                        null, //group by
                        null, //having
                        sortOrder, //order by
                        null); //limit
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        //URI może być monitorowane pod kątem zmiany danych – tu jest rejestrowane. Obserwator (którego trzeba zarejestrować będzie powiadamiany o zmianie danych)
        kursor.setNotificationUri(getContext().getContentResolver(), uri);
        return kursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int typUri = uriMatch.match(uri);
        DBHelper dbHelper=new DBHelper(getContext());
        SQLiteDatabase DB=dbHelper.getWritableDatabase();

        int deletedCount = 0;
        switch (typUri) {
            case WHOLE_TABLE:
                deletedCount = DB.delete(DBHelper.TABLE_NAME,
                        selection, //WHERE
                        selectionArgs); //usuwanie rekordów
                break;
            case SELECTED_ROW:
                deletedCount = DB.delete(DBHelper.TABLE_NAME,
                        selection, //WHERE
                        selectionArgs);//usuwanie rekordu (może się nie udać)
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " +
                        uri);
        }
        //powiadomienie o zmianie danych
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int typUri = uriMatch.match(uri);
        DBHelper dbHelper=new DBHelper(getContext());
        SQLiteDatabase DB=dbHelper.getWritableDatabase();

        int liczbaZaktualizowanych = 0;
        switch (typUri) {
            case WHOLE_TABLE:
                liczbaZaktualizowanych = DB.update(DBHelper.TABLE_NAME, //tabela
                        values, //wartości
                        selection, //where
                        selectionArgs); //whereArgs (zastępują "?" w parametrze where)//aktualizacja...
                break;
            case SELECTED_ROW:
                liczbaZaktualizowanych = DB.update(DBHelper.TABLE_NAME, //tabela
                        values, //wartości
                        selection, //where
                        selectionArgs); //whereArgs (zastępują "?" w parametrze where)//aktualizacja...
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " +
                        uri);
        } //powiadomienie o zmianie danych
        getContext().getContentResolver().notifyChange(uri, null);
        return liczbaZaktualizowanych;
    }


}
