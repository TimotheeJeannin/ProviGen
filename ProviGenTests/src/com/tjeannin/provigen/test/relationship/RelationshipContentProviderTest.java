package com.tjeannin.provigen.test.relationship;

import android.content.ContentValues;
import android.net.Uri;
import android.test.mock.MockContentResolver;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.relationship.RelationshipContentProvider.Artist;
import com.tjeannin.provigen.test.relationship.RelationshipContentProvider.Track;

public class RelationshipContentProviderTest extends ExtendedProviderTestCase<RelationshipContentProvider> {

	private MockContentResolver contentResolver;

	public RelationshipContentProviderTest() {
		super(RelationshipContentProvider.class, "com.test");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contentResolver = getMockContentResolver();

		insertArtist(1, "David Bowie");
		insertTracks(1, new String[] { "Under Pressure", "Space Oddity", "Station to Station", "Let’s Dance" });

		insertArtist(2, "David Bowie");
		insertTracks(2, new String[] { "Hit the road Jack!", "I've Got A Woman", "Drown In My Own Tears" });
	}

	public void insertArtist(int id, String name) {
		ContentValues artistValues = new ContentValues();
		artistValues.put(Artist._ID, id);
		artistValues.put(Artist.NAME, name);
		contentResolver.insert(Artist.CONTENT_URI, artistValues);
	}

	public void insertTracks(int artistId, String[] trackNames) {
		for (String track : trackNames) {
			ContentValues trackValues = new ContentValues();
			trackValues.put(Track.ARTIST_ID, artistId);
			trackValues.put(Track.NAME, track);
			contentResolver.insert(Track.CONTENT_URI, trackValues);
		}
	}

	public void testBasicQuery() {
		assertEquals(4, getRowCount(Uri.parse("content://com.test/artist/1/track/")));
		assertEquals(3, getRowCount(Uri.parse("content://com.test/artist/2/track/")));

		assertEquals(1, getRowCount(Uri.parse("content://com.test/artist/1/track/1")));
		assertEquals(1, getRowCount(Uri.parse("content://com.test/artist/1/track/2")));
	}
}
