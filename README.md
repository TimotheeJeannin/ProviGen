ProviGen
========

ProviGen allows you to easily generate a [ContentProvider] from a given [ContractClass].

[ContentProvider]: https://developer.android.com/reference/android/content/ContentProvider.html

[ContractClass]: http://developer.android.com/guide/topics/providers/content-provider-basics.html#ContractClasses

How to install and use
----------------------

* Put the ProviGen [jar] in your `libs` folder or add ProviGen as a library project.

[jar]: https://github.com/TimotheeJeannin/ProviGen/downloads

* Annotate your ContractClass as follows:

```java
@Contract(authority = "com.myapp", databaseName = "myDataBaseName")
public static class MyContract {

	@Table
	public static final String TABLE_NAME = "myTableName";

	@Id
	@Column(type = Type.INTEGER)
	public static final String COLUMN_ID = "_id";

	@Column(type = Type.INTEGER)
	public static final String MY_INT_COLUMN = "int";

	@Column(type = Type.TEXT)
	public static final String MY_STRING_COLUMN = "string";

	public static final Uri CONTENT_URI = Uri.parse("content://com.myapp/" + TABLE_NAME);
}
```

* Extend the ProviGenProvider as follows:

```java
public class MyContentProvider extends ProviGenProvider {

	public MyContentProvider() throws InvalidContractException {
		super(MyContract.class);
	}
}
```

* Add your provider in your manifest:

```xml
<provider
    android:name="com.myapp.MyContentProvider"
    android:authorities="com.myapp" >
</provider>
```