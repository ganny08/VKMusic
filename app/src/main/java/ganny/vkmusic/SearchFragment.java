package ganny.vkmusic;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.vk.sdk.api.VKRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        Button find = (Button)v.findViewById(R.id.find);
        final EditText findStr = (EditText) v.findViewById(R.id.findString);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKRequest request;
                VKMusic vkMusic = (VKMusic) getActivity();
                request = new VKRequest("audio.search");
                request.addExtraParameter("q",findStr.getText().toString());
                request.executeWithListener(vkMusic.getRequestListener());
            }
        });

        return v;
    }


}
