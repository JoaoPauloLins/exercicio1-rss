package br.ufpe.cin.if1001.rss;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ListView conteudoRSS;
    private List<ItemRSS> parsedResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    private class CarregaRSStask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "iniciando...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            String conteudo = "provavelmente deu erro...";
            try {
                conteudo = getRssFeed(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return conteudo;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

            try {
                parsedResponse = ParserRSS.parse(s);
                final AdapterRSS adapterRSS = new AdapterRSS(getApplicationContext(), parsedResponse);
                conteudoRSS.setAdapter(adapterRSS);
                conteudoRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg) {
                        String url = adapterRSS.getLink(position);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                });
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
