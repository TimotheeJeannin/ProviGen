package com.tjeannin.provigen.sample;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, OnClickListener {

    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] columns = new String[]{
                SampleContentProvider.Person.AGE,
                SampleContentProvider.Person.NAME};

        int[] ids = {
                R.id.person_age,
                R.id.person_name};

        adapter = new SimpleCursorAdapter(this, R.layout.person_item, null, columns, ids, 0);

        ((ListView) findViewById(R.id.list_view)).setAdapter(adapter);

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, SampleContentProvider.Person.CONTENT_URI, null, "", null, "");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.add:
                ContentValues values = new ContentValues();
                values.put(SampleContentProvider.Person.AGE, 20);
                values.put(SampleContentProvider.Person.NAME, "Some Name");
                getContentResolver().insert(SampleContentProvider.Person.CONTENT_URI, values);
                break;

            case R.id.delete:
                getContentResolver().delete(SampleContentProvider.Person.CONTENT_URI, "", null);
                break;
            default:

                break;
        }

    }
}
