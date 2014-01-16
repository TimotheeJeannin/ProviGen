package com.tjeannin.provigen.test.relationship;

import android.net.Uri;
import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.ForeignKey;

public class RelationshipContentProvider extends ProviGenProvider {

	public RelationshipContentProvider() throws InvalidContractException {
		super(new Class[] { Artist.class, Track.class });
	}

	@Contract(version = 1)
	public static interface Artist extends ProviGenBaseContract {

		@Column(Type.TEXT)
		public static final String NAME = "name";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test/artist");

	}

	@Contract(version = 1)
	public static interface Track extends ProviGenBaseContract {

		@ForeignKey(Artist._ID)
		@Column(Type.INTEGER)
		public static final String ARTIST_ID = "artist_id";

		@Column(Type.TEXT)
		public static final String NAME = "name";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test/track");

	}
}
