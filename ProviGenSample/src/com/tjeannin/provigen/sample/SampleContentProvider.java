package com.tjeannin.provigen.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;


public class SampleContentProvider extends ProviGenProvider {

    public static final String AUTHORITY = "com.tjeannin.provigen.sample";

    public static final String DB_NAME = "ProviGenDatabase";
    public static final int DB_VERSION = 1;

    public static final String SECOND_DB_NAME = "ProviGenDatabaseSecond";
    public static final int SECOND_DB_VERSION = 1;

    private static final Class[] CONTRACTS = new Class[] {
            SampleContract.Person.class,
            SampleContract.Specialty.class,
            SampleContract.Passport.class
    };

    private static final Class[] CONTRACTS_SECOND = new Class[] {
            SampleContract.PersonSecondDb.class
    };

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return null; // you can return null
    }

    @Override
    public SQLiteOpenHelper[] openHelpers(Context context) {
        return new SQLiteOpenHelper[] {
                new ProviGenOpenHelper(getContext(), DB_NAME, null, DB_VERSION, CONTRACTS), // first db
                new ProviGenOpenHelper(getContext(), SECOND_DB_NAME, null, SECOND_DB_VERSION, CONTRACTS_SECOND) // second db
        };
    }

    @Override
    public Class[] contractClasses() {
        return null; // you can return null
    }

    @Override
    public Class[][] contractClassesMultipleDb() {
        return new Class[][] {CONTRACTS, CONTRACTS_SECOND};
    }

    @Override
    public int conflictAlgorithm() {
        return SQLiteDatabase.CONFLICT_REPLACE;
    }
}
