package com.tjeannin.provigen.test.entity;

import android.database.Cursor;
import android.net.Uri;

import com.tjeannin.provigen.ProviGenEntity;
import com.tjeannin.provigen.annotation.Entity;
import com.tjeannin.provigen.annotation.Persist;

@Entity(contentUri = "content://com.test.entity/table_entity")
public class EntityClass extends ProviGenEntity {

	@Persist(columnName = EntityContentProvider.EntityContract.MY_INT)
	private int myInt;

	@Persist(columnName = EntityContentProvider.EntityContract.MY_STRING)
	private String myString;

	@Persist(columnName = EntityContentProvider.EntityContract.MY_DOUBLE)
	private double myDouble;

	@Persist(columnName = EntityContentProvider.EntityContract.MY_BOOLEAN)
	private boolean myBoolean;

	@Persist(columnName = EntityContentProvider.EntityContract.MY_URI)
	private Uri myUri;

	public EntityClass() {
		super();
	}

	public EntityClass(Cursor cursor) {
		super(cursor);
	}

	public int getMyInt() {
		return myInt;
	}

	public void setMyInt(int myInt) {
		this.myInt = myInt;
	}

	public String getMyString() {
		return myString;
	}

	public void setMyString(String myString) {
		this.myString = myString;
	}

	public double getMyDouble() {
		return myDouble;
	}

	public void setMyDouble(double myDouble) {
		this.myDouble = myDouble;
	}

	public boolean getMyBoolean() {
		return myBoolean;
	}

	public void setMyBoolean(boolean myBoolean) {
		this.myBoolean = myBoolean;
	}

	public Uri getMyUri() {
		return myUri;
	}

	public void setMyUri(Uri myUri) {
		this.myUri = myUri;
	}
}
