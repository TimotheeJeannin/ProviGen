ProviGen
========

ProviGen allows you to easily generate a [ContentProvider] from a given [ContractClass].

[ContentProvider]: https://developer.android.com/reference/android/content/ContentProvider.html

[ContractClass]: http://developer.android.com/guide/topics/providers/content-provider-basics.html#ContractClasses

How to install and use
----------------------

1. Put the ProviGen [jar] in your `libs` folder or add ProviGen as a library project.

[jar]: https://github.com/TimotheeJeannin/ProviGen/downloads

2. Annotate your ContractClass as follows:

```java
@Contract(authority = "com.myapp", databaseName = "myDataBaseName")
  public static class SimpleContract {

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