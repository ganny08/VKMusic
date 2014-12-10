package ganny.vkmusic;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Ganny on 21.11.2014.
 */
public class JSONArrayAdapter extends SimpleAdapter {
    public JSONArrayAdapter(Context context, JSONArray jsonArray, int resource,
                            String[] from, int[] to, MyMediaPlayer pMp) {
        super(context, getListFromJsonArray(jsonArray), resource, from, to);
        mp = pMp;
        this.context = context;
    }

    protected Context context;
    protected MyMediaPlayer mp;
    // method converts JSONArray to List of Maps
    protected static List<Map<String, String>> getListFromJsonArray(
            JSONArray jsonArray) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map;
        // fill the list
        for (int i = 0; i < jsonArray.length(); i++) {
            map = new HashMap<String, String>();
            try {
                JSONObject jo = (JSONObject) jsonArray.get(i);
                // fill map
                Iterator iter = jo.keys();
                while (iter.hasNext()) {
                    String currentKey = (String) iter.next();
                    map.put(currentKey, jo.getString(currentKey));
                }
                // add map to list
                list.add(map);
            } catch (JSONException e) {
                Log.e("JSON", e.getLocalizedMessage());
            }

        }
        return list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position,convertView,parent);
        //Log.d("log","adapter->getView->pos: " + position);
        ImageView imV = (ImageView) view.findViewById(R.id.playPause);
        VKMusic vkMusic = (VKMusic)context;
        String request = vkMusic.getRequest();
        if(position == mp.getPosition() && request.equalsIgnoreCase(mp.getRequest())) imV.setImageResource(R.drawable.pause);
        else imV.setImageResource(R.drawable.play);
        vkMusic = null;
        return view;
    }

}
