# ProviGen [![Build Status](https://travis-ci.org/TimotheeJeannin/ProviGen.png?branch=master)](https://travis-ci.org/TimotheeJeannin/ProviGen) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.provigen/ProviGen-lib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.provigen/ProviGen-lib/)

Easily make a [ContentProvider] from a [ContractClass].    

## Setup

* Follow the [installation guide](https://github.com/TimotheeJeannin/ProviGen/wiki/Installation-Guide).

* Annotate your ContractClass.

```java
public interface MyContract extends ProviGenBaseContract {

	@Column(Type.INTEGER)
	public static final String MY_INT_COLUMN = "int";

	@Column(Type.TEXT)
	public static final String MY_STRING_COLUMN = "string";

	@ContentUri
	public static final Uri CONTENT_URI = Uri.parse("content://com.myapp/table_name");
}
```

* Extend the ProviGenProvider.

```java
public class MyContentProvider extends ProviGenProvider {

    private static Class[] contracts = new Class[]{MyContract.class};

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(), "dbName", null, 1, contracts);
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }
}
```

* Add your provider in your manifest.

```xml
<provider
    android:name="com.myapp.MyContentProvider"
    android:authorities="com.myapp" >
</provider>
```

* You're done.

## Usage

You can make the usual insert, update, delete and query using a [ContentResolver].    
For example querying a single row boils down to:
```java
getContentResolver().query(	
	Uri.withAppendedPath(MyContract.CONTENT_URI, myId),
	null, "", null, "");
```
or 
```java
getContentResolver().query(
	MyContract.CONTENT_URI, null, 
	MyContract._ID + " = ? ", new String[]{ myId }, "");
```

## Features

### Table creation and contract upgrades

ProviGen comes with an implementation of the [SQLiteOpenHelper] called `ProviGenOpenHelper`.
This default implementation will

* automatically create the needed tables on the first application launch
* automatically add missing columns every time the database version increases

### Notifications and observers

ProviGen fully supports the uri notification mechanism.   
You can safely use it with [CursorLoader]s and [ContentObserver]s.

### Custom SQLiteOpenHelper

You can provide your own implementation of the [SQLiteOpenHelper] for initial population, complex contract upgrades
or anything else database related you want to achieve.

```java
public class MyContentProvider extends ProviGenProvider {

    @Override
    public Class[] contractClasses() {
        return new Class[]{MyContract.class};
    }

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
    
        return new SQLiteOpenHelper(getContext(), "databaseName", null, 1) {
        
            @Override
            public void onCreate(SQLiteDatabase database) {
                // Automatically creates table and needed columns.
                new TableBuilder(MyContract.class).createTable(database);

                // Do initial population here.
            }

            @Override
            public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
                // Automatically adds new columns.
                TableUpdater.addMissingColumns(database, MyContract.class);

                // Anything else related to database upgrade should be done here.
            }
        };
    }
}
```

### Data constraint

You can apply a `UNIQUE` or a `NOT_NULL` constraint to a column using the appropriate `TableBuilder` methods.

```java
new TableBuilder(MyContract.class)
        .addConstraint(MyContract.MY_INT, Constraint.UNIQUE, OnConflict.ABORT)
        .addConstraint(MyContract.MY_STRING, Constraint.NOT_NULL, OnConflict.IGNORE)
        .createTable(database);
```

### Primary key and foreign key

Autoincrement or non-autoincrement primary key.

```java
@Id(autoincrement = true)
@Column(Column.Type.INTEGER)
public static final String ID = "id";
```

Composite primary key support.

```java
public class Passport {

    public static final String TABLE_NAME = "passport";
    
    @Id
    @Column(Column.Type.TEXT)
    public static final String SERIES = "series";
    
    @Id
    @Column(Column.Type.INTEGER)
    public static final String NUMBER = "number";
    
    @ContentUri
    public static final Uri CONTENT_URI = ProviGenUriBuilder.contentUri(SampleContentProvider.AUTHORITY, TABLE_NAME);
        
}
```

Foreign key support.

```java
@ForeignKey(table = Specialty.TABLE_NAME, column = Specialty.ID)
@Column(Column.Type.INTEGER)
public static final String SPECIALTY_ID = "specialty_id";
```

### Multiple databases support

Add a name of the second database in your ContractClass.

```java
public class PersonSecondDb implements ProviGenBaseContract {

    public static final String TABLE_NAME = "persons_second";

    @Column(Column.Type.INTEGER)
    public static final String AGE = "age";

    @Column(Column.Type.TEXT)
    public static final String NAME = "name";

    @ContentUri
    public static final Uri CONTENT_URI = ProviGenUriBuilder.contentUri(
            SampleContentProvider.AUTHORITY, 
            TABLE_NAME, 
            SampleContentProvider.SECOND_DB_NAME); // name of second database
}
```

Override openHelpers(Context context) and contractClassesMultipleDb() methods in your content provider.

```java
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
}
```

### Joins support

Inner join, left outer join and cross join support.
Your ContractClasses.

```java
public static class Person implements ProviGenBaseContract {

    public static final String TABLE_NAME = "person";

    @Column(Column.Type.INTEGER)
    public static final String AGE = "age";

    @Column(Column.Type.TEXT)
    public static final String NAME = "name";

    @Column(Column.Type.INTEGER)
    public static final String SPECIALTY_ID = "specialty_id";

    @ContentUri
    public static final Uri CONTENT_URI = ProviGenUriBuilder.contentUri(SampleContentProvider.AUTHORITY, TABLE_NAME);

    public static final String[] DEFAULT_PROJECTION = new String[] { _ID, AGE, NAME };

    // projection for join-query
    public static final String[] JOIN_PROJECTION = new String[] {
            _ID,
            AGE,
            // ContractUtil.fullName() used to avoid ambiguous column name
            ContractUtil.fullName(TABLE_NAME, NAME),
            ContractUtil.fullName(Specialty.TABLE_NAME, Specialty.NAME)
    };
}

public static class Specialty {

    public static final String TABLE_NAME = "specialty";

    @Id
    @Column(Column.Type.INTEGER)
    public static final String ID = "id";

    @Column(Column.Type.TEXT)
    public static final String NAME = "name";

    @ContentUri
    public static final Uri CONTENT_URI = ProviGenUriBuilder.contentUri(SampleContentProvider.AUTHORITY, TABLE_NAME);

    public static final String[] DEFAULT_PROJECTION = new String[] { ID, NAME };
}
```

Use the method ContractUtil.joinName() for correct obtaining of the columns.

```java
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

SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.person_item, null, columns, ids, 0);
```

Create special join URI using the method ProviGenUriBuilder.joinUri() and execute the query.

```java
@Override
public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Uri innerJoinUri = ProviGenUriBuilder.joinUri(
            ProviGenUriBuilder.JoinType.INNER_JOIN, // inner, left outer or cross join
            SampleContract.Person.CONTENT_URI,
            new JoinEntity(SampleContract.Specialty.CONTENT_URI, SampleContract.Person.SPECIALTY_ID , SampleContract.Specialty.ID));

    return new CursorLoader(this, innerJoinUri, SampleContract.Person.JOIN_PROJECTION, null, null, null);
}
```

## License

This content is released under the MIT License.

[SQLiteOpenHelper]: https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html

[ContentObserver]: https://developer.android.com/reference/android/database/ContentObserver.html

[CursorLoader]: http://developer.android.com/reference/android/content/CursorLoader.html

[ContentProvider]: https://developer.android.com/reference/android/content/ContentProvider.html

[ContractClass]: http://developer.android.com/guide/topics/providers/content-provider-basics.html#ContractClasses

[ContentResolver]: https://developer.android.com/reference/android/content/ContentResolver.html
