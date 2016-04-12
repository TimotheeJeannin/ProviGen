package com.tjeannin.provigen.sample;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
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

import com.tjeannin.provigen.helper.ContractUtil;
import com.tjeannin.provigen.helper.ProviGenUriBuilder;
import com.tjeannin.provigen.model.JoinEntity;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, OnClickListener {

    private static final String[] NAMES = {"David", "Stephanie", "John", "Anna", "Thomas", "Natalie", "Andrew", "Sofia", "Richard", "Alexandra", "Matthew", "Grace", "Christian", "Mary"};
    private static final String[] SPECIALTIES = {"Android Developer", "iOS Developer", "Backend Developer", "Frontend Developer", "Web Developer", "Software Tester", "System Administrator", "Graphic Designer", "Project Manager", "Team Lead", "CEO"};
    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] columns = new String[]{
                SampleContract.Person.AGE,
                // important for correct obtaining of the columns
                ContractUtil.joinName(SampleContract.Person.TABLE_NAME, SampleContract.Person.NAME),
                ContractUtil.joinName(SampleContract.Specialty.TABLE_NAME, SampleContract.Specialty.NAME)
        };

        int[] ids = {
                R.id.person_age,
                R.id.person_name,
                R.id.person_spec
        };

        adapter = new SimpleCursorAdapter(this, R.layout.person_item, null, columns, ids, 0);

        ((ListView) findViewById(R.id.list_view)).setAdapter(adapter);

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);

        initSpecialties();
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void initSpecialties() {
        ContentValues[] valuesArray = new ContentValues[SPECIALTIES.length];
        for(int i = 0; i < SPECIALTIES.length; i++) {
            ContentValues values = new ContentValues();
            values.put(SampleContract.Specialty.ID, i);
            values.put(SampleContract.Specialty.NAME, SPECIALTIES[i]);
            valuesArray[i] = values;
        }
        getContentResolver().bulkInsert(SampleContract.Specialty.CONTENT_URI, valuesArray);
    }

    private void addPerson() {
        ContentValues values = new ContentValues();
        values.put(SampleContract.Person.AGE, (int) (Math.random() * 40 + 20));
        values.put(SampleContract.Person.NAME, NAMES[(int) (Math.random() * NAMES.length)]);
        values.put(SampleContract.Person.SPECIALTY_ID, (int) (Math.random() * SPECIALTIES.length));
        getContentResolver().insert(SampleContract.Person.CONTENT_URI, values);
    }

    private void clearPerson() {
        getContentResolver().delete(SampleContract.Person.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create special join URI and execute the query
        Uri innerJoinUri = ProviGenUriBuilder.joinUri(
                ProviGenUriBuilder.JoinType.INNER_JOIN,
                SampleContract.Person.CONTENT_URI,
                new JoinEntity(SampleContract.Specialty.CONTENT_URI, SampleContract.Person.SPECIALTY_ID , SampleContract.Specialty.ID)
        );

        return new CursorLoader(this, innerJoinUri, SampleContract.Person.JOIN_PROJECTION, null, null, null);
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
                addPerson();
                break;

            case R.id.delete:
                clearPerson();
                break;

            default:
                break;
        }

    }
}
