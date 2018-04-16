package br.ufpe.cin.if1001.rss.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if1001.rss.domain.ItemRSS;

public class SQLiteRSSHelper extends SQLiteOpenHelper  {
    //Nome do Banco de Dados
    private static final String DATABASE_NAME = "rss";
    //Nome da tabela do Banco a ser usada
    private static final String DATABASE_TABLE = RssProviderContract.RssItems.TABLE_NAME;
    //Versão atual do banco
    private static final int DB_VERSION = 1;

    //alternativa
    Context c;

    private SQLiteRSSHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        c = context;
    }

    private static SQLiteRSSHelper db;

    //Definindo Singleton
    public static SQLiteRSSHelper getInstance(Context c) {
        if (db==null) {
            db = new SQLiteRSSHelper(c.getApplicationContext());
        }
        return db;
    }

    //Definindo constantes que representam os campos do banco de dados
    private static final String ITEM_ROWID = RssProviderContract.RssItems._ID;
    private static final String ITEM_TITLE = RssProviderContract.RssItems.COLUMN_NAME_TITLE;
    private static final String ITEM_DATE = RssProviderContract.RssItems.COLUMN_NAME_DATE;
    private static final String ITEM_DESC = RssProviderContract.RssItems.COLUMN_NAME_DESCRIPTION;
    private static final String ITEM_LINK = RssProviderContract.RssItems.COLUMN_NAME_LINK;
    private static final String ITEM_UNREAD = RssProviderContract.RssItems.COLUMN_NAME_UNREAD;

    //Definindo constante que representa um array com todos os campos
    public final static String[] columns = { ITEM_ROWID, ITEM_TITLE, ITEM_DATE, ITEM_DESC, ITEM_LINK, ITEM_UNREAD};

    //Definindo constante que representa o comando de criação da tabela no banco de dados
    private static final String CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
            ITEM_ROWID +" integer primary key autoincrement, "+
            ITEM_TITLE + " text not null, " +
            ITEM_DATE + " text not null, " +
            ITEM_DESC + " text not null, " +
            ITEM_LINK + " text not null, " +
            ITEM_UNREAD + " boolean not null);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Executa o comando de criação de tabela
        db.execSQL(CREATE_DB_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //estamos ignorando esta possibilidade no momento
        throw new RuntimeException("nao se aplica");
    }

    //IMPLEMENTAR ABAIXO
    //Implemente a manipulação de dados nos métodos auxiliares para não ficar criando consultas manualmente
    public long insertItem(ItemRSS item) {
        return insertItem(item.getTitle(),item.getPubDate(),item.getDescription(),item.getLink());
    }
    private long insertItem(String title, String pubDate, String description, String link) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_TITLE, title);
        contentValues.put(ITEM_DATE, pubDate);
        contentValues.put(ITEM_DESC, description);
        contentValues.put(ITEM_LINK, link);
        contentValues.put(ITEM_UNREAD, 1);

        return db.insert(DATABASE_TABLE, null, contentValues);
    }
    public ItemRSS getItemRSS(String link) throws SQLException {

        SQLiteDatabase db = getReadableDatabase();

        String selection = ITEM_LINK + " = ?";
        String selectionArgs[] = {link};

        Cursor cursor = db.query(
            DATABASE_TABLE,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null
        );

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return new ItemRSS(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ITEM_LINK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ITEM_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ITEM_DESC)));
        }
        return null;
    }
    public List<ItemRSS> getItems() throws SQLException {

        SQLiteDatabase db = getReadableDatabase();

        String selection = ITEM_UNREAD + " = ?";
        String selectionArgs[] = {"1"};

        Cursor cursor = db.query(
                DATABASE_TABLE,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List<ItemRSS> rssList = new ArrayList<>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_TITLE));
                String link = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_LINK));
                String pubDate = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_DATE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_DESC));
                ItemRSS itemRSS = new ItemRSS(title,link,pubDate,description);
                rssList.add(itemRSS);
            } while (cursor.moveToNext());

            return rssList;
        }
        return null;
    }
    public boolean markAsUnread(String link) {

        SQLiteDatabase db = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_UNREAD, 1);

        String selection = ITEM_LINK + " = ?";
        String selectionArgs[] = { link };

        int count = db.update(
                DATABASE_TABLE,
                contentValues,
                selection,
                selectionArgs
        );

        return count > 0;
    }

    public boolean markAsRead(String link) {

        SQLiteDatabase db = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_UNREAD, 0);

        String selection = ITEM_LINK + " = ?";
        String selectionArgs[] = { link };

        int count = db.update(
            DATABASE_TABLE,
            contentValues,
            selection,
            selectionArgs
        );

        return count > 0;
    }
}
