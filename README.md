# ProviGen [![Build Status](https://travis-ci.org/TimotheeJeannin/ProviGen.png?branch=master)](https://travis-ci.org/TimotheeJeannin/ProviGen)

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

    @Override
    public Class[] contractClasses() {
            return new Class[]{ MyContract.class };
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

### Multiple contact classes

You can use ProviGen with several [ContractClass]es just by making the `contractClasses` method return an array of contract classes.
```java
public class MyContentProvider extends ProviGenProvider {

    @Override
    public Class[] contractClasses() {
            return new Class[]{ FirstContract.class, SecondContract.class };
    }
}
```
By default, ProviGen will create a table for each contract class.     
The table name will be the last path segment of the contract's content uri.

### Notifications and observers

ProviGen fully supports the uri notification mechanism.   
You can safely use it with [CursorLoader]s and [ContentObserver]s.

### Initial population and contract upgrades

ProviGen comes with a default implementation of the [SQLiteOpenHelper].
This default implementation will automatically create the needed tables on the first application launch.

Initial population and contract upgrades can be done providing your own implementation of the [SQLiteOpenHelper].
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

## License

This content is released under the MIT License.

[SQLiteOpenHelper]: https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html

[ContentObserver]: https://developer.android.com/reference/android/database/ContentObserver.html

[CursorLoader]: http://developer.android.com/reference/android/content/CursorLoader.html

[ContentProvider]: https://developer.android.com/reference/android/content/ContentProvider.html

[ContractClass]: http://developer.android.com/guide/topics/providers/content-provider-basics.html#ContractClasses

[ContentResolver]: https://developer.android.com/reference/android/content/ContentResolver.html
