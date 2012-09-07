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

import com.tjeannin.provigen.sample.SampleContentProvider.SampleContract;
import com.tjeannin.provigen.sample.R;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, OnClickListener {

	private SimpleCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String[] columns = new String[] {
				SampleContract.COLUMN_ID,
				SampleContract.COLUMN_INT,
				SampleContract.COLUMN_REAL,
				SampleContract.COLUMN_STRING };

		int[] ids = {
				R.id.item_id,
				R.id.item_int,
				R.id.item_real,
				R.id.item_string };

		adapter = new SimpleCursorAdapter(this, R.layout.item, null, columns, ids, 0);

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
		return new CursorLoader(this, SampleContract.CONTENT_URI, null, "", null, "");
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
			values.put(SampleContract.COLUMN_INT, 5);
			values.put(SampleContract.COLUMN_STRING, "afhzpuf");
			values.put(SampleContract.COLUMN_REAL, 0.45896);
			getContentResolver().insert(SampleContract.CONTENT_URI, values);
			break;

		case R.id.delete:
			getContentResolver().delete(SampleContract.CONTENT_URI, "", null);
			break;
		default:

			break;
		}

	}
}
