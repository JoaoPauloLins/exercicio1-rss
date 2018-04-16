package br.ufpe.cin.if1001.rss.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.ufpe.cin.if1001.rss.R;
import br.ufpe.cin.if1001.rss.domain.ItemRSS;

public class AdapterRSS extends ArrayAdapter<ItemRSS> {

    public AdapterRSS(Context context, List<ItemRSS> rssList) {
        super(context, 0, rssList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final ItemRSS itemRSS = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.itemlista, parent, false);
        }

        TextView itemTitulo = (TextView) convertView.findViewById(R.id.item_titulo);
        TextView itemData = (TextView) convertView.findViewById(R.id.item_data);

        itemTitulo.setText(itemRSS.getTitle());
        itemData.setText(itemRSS.getPubDate());

        return convertView;
    }

    public String getLink(int position){
        final ItemRSS itemRSS = getItem(position);

        return itemRSS.getLink();
    }
}
