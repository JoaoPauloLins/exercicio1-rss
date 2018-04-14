package br.ufpe.cin.if1001.rss;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class PreferenciasActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_RSS_FEED = "feed_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new RssPreferenceFragment())
                .commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_RSS_FEED)) {
            Preference feedPreference = findPreference(key);
            feedPreference.setSummary(sharedPreferences.getString(key, ""));
        }
    }

    public static class RssPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferencias);
        }
    }
}