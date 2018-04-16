package br.ufpe.cin.if1001.rss.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;
import br.ufpe.cin.if1001.rss.util.AdapterRSS;
import br.ufpe.cin.if1001.rss.util.ParserRSS;
import br.ufpe.cin.if1001.rss.R;
import br.ufpe.cin.if1001.rss.domain.ItemRSS;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ListView conteudoRSS;
    private List<ItemRSS> parsedResponse;
    private SQLiteRSSHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = SQLiteRSSHelper.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.rss_toolbar);
        setSupportActionBar(toolbar);

        conteudoRSS = (ListView) findViewById(R.id.conteudoRSS);

        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String url = sharedPreferences.getString(PreferenciasActivity.KEY_PREF_RSS_FEED, "");
        new CarregaRSStask().execute(url);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private class CarregaRSStask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... feeds) {
            boolean flag_problema = false;
            List<ItemRSS> items = null;
            try {
                String feed = getRssFeed(feeds[0]);
                items = ParserRSS.parse(feed);
                for (ItemRSS i : items) {
                    Log.d("DB", "Buscando no Banco por link: " + i.getLink());
                    ItemRSS item = db.getItemRSS(i.getLink());
                    if (item == null) {
                        Log.d("DB", "Encontrado pela primeira vez: " + i.getTitle());
                        db.insertItem(i);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                flag_problema = true;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                flag_problema = true;
            }
            return flag_problema;
        }

        @Override
        protected void onPostExecute(Boolean teveProblema) {
            if (teveProblema) {
                Toast.makeText(MainActivity.this, "Houve algum problema ao carregar o feed.", Toast.LENGTH_SHORT).show();
            } else {
                //dispara o task que exibe a lista
                new ExibirFeed().execute();
            }
        }
    }

    private class ExibirFeed extends AsyncTask<Void, Void, List<ItemRSS>> {

        @Override
        protected List<ItemRSS> doInBackground(Void... voids) {
            parsedResponse = db.getItems();
            return parsedResponse;
        }

        @Override
        protected void onPostExecute(List<ItemRSS> rssList) {
            if (rssList != null) {
                final AdapterRSS adapterRSS = new AdapterRSS(getApplicationContext(), rssList);
                conteudoRSS.setAdapter(adapterRSS);
                conteudoRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg) {
                        String url = adapterRSS.getLink(position);
                        if (db.markAsRead(url)){
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }

    public void changeFeed(View view){
        Intent intent = new Intent(this,PreferenciasActivity.class);
        startActivity(intent);
    }
}
