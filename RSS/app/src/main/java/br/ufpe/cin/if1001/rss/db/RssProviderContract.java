package br.ufpe.cin.if1001.rss.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class RssProviderContract {

    private RssProviderContract() {}

    public static class RssItems implements BaseColumns {

        public static final String TABLE_NAME = "items";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATE = "pubDate";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LINK = "guid";
        public static final String COLUMN_NAME_UNREAD = "unread";

        public final static String[] ALL_COLUMNS = {
                _ID, COLUMN_NAME_TITLE, COLUMN_NAME_DATE, COLUMN_NAME_DESCRIPTION, COLUMN_NAME_LINK, COLUMN_NAME_UNREAD};

        private static final Uri BASE_RSS_URI = Uri.parse("content://br.ufpe.cin.if1001.rss/");

        //URI para tabela
        public static final Uri ITEMS_LIST_URI = Uri.withAppendedPath(BASE_RSS_URI, TABLE_NAME);

        // Mime type para colecao de itens
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/RssProvider.data.text";

        // Mime type para um item especifico
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/RssProvider.data.text";
    }
}
