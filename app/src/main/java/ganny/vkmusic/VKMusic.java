package ganny.vkmusic;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;


public class VKMusic extends Activity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

    private static String LOG_TAG = "log";
    private static String sTokenKey = "ACCESS_TOKEN";

    private FragmentTransaction mFragmentTransaction;
    private int mIdMenuItemSelect;
    private int mIdAudioItemSelect;
    private JSONArrayAdapter mAdapter;
    private ListView mListMusic;
    private MyMediaPlayer mMediaPlayer;
    private TextView mMyAudio;
    private TextView mLogout;
    private TextView mRecommendations;
    private String mCurrentRequest;
    private Fragment mCurrentFragment;
    //private View mAudioViewSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(LOG_TAG,"VKMusic->onCreate");
        setContentView(R.layout.activity_vkmusic);
        AudioManager mAudioManager;
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mMyAudio = (TextView) findViewById(R.id.myAudio);
        mMyAudio.setOnClickListener(menuClickListener);
        mLogout = (TextView) findViewById(R.id.logout);
        mLogout.setOnClickListener(menuClickListener);
        mRecommendations = (TextView) findViewById(R.id.recommendations);
        mRecommendations.setOnClickListener(menuClickListener);
        initRequest();
    }

    public void initRequest() {
        mIdAudioItemSelect = -1;
        mIdMenuItemSelect = mMyAudio.getId();
        menuClickListener.onClick(mMyAudio);
        mMediaPlayer = new MyMediaPlayer();
    }

    public void logout() {
        Log.d(LOG_TAG,"VKMusic->mLogout");
        VKSdk.logout();
        VKAccessToken.removeTokenAtKey(VKMusic.this,sTokenKey);
        this.finish();
        Intent i = new Intent(VKMusic.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d(LOG_TAG,"VKMusic->onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
        //Log.d(LOG_TAG,"VKMusic->onDestroy");
        try {
            mMediaPlayer.release();
        } catch (Exception e) {
            Log.d(LOG_TAG,"" + e);
        }
    }

    public String getRequest() {
        return mCurrentRequest;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(LOG_TAG, "VKMusic->onCompletion");
        mp.reset();
        LayoutInflater ltInflater = getLayoutInflater();
        View view = mAdapter.getView(++mIdAudioItemSelect,
                ltInflater.inflate(R.layout.itemmusic,null,false),mListMusic);
        TextView tV = (TextView) view.findViewById(R.id.url);
        play("" + tV.getText(),mIdAudioItemSelect);
        Parcelable state = mListMusic.onSaveInstanceState();
        mAdapter.notifyDataSetChanged();
        mListMusic.onRestoreInstanceState(state);
    }

    public void play(String url, int position) {
        //url = url.replace("https","http");
        try {
            mMediaPlayer.reset();
            Log.d(LOG_TAG, "list->onClick: stop");
        } catch (Exception e) {
            Log.d(LOG_TAG, "release: " + e);
        }
        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(VKMusic.this);
            mMediaPlayer.prepareAsync(position);
            Log.d(LOG_TAG, "list->onClick: play");
        } catch (Exception e) {
            Log.d(LOG_TAG, "" + e);
        }
        mMediaPlayer.setRequest(mCurrentRequest);
        mMediaPlayer.setOnCompletionListener(VKMusic.this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(LOG_TAG,"VKMusic->onPrepared");
        mp.start();
    }

    private VKRequest.VKRequestListener mRequestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            JSONArray mResult;
            //Do complete stuff
            try {
                mResult = response.json.getJSONObject("response").getJSONArray("items");
                //Log.d(LOG_TAG,mResult.getJSONObject(0).getString("mArtist"));
                //Log.d(LOG_TAG,response.json.toString(0));
                mAdapter = new JSONArrayAdapter(VKMusic.this, mResult, R.layout.itemmusic,
                        new String[]{"artist", "title", "url"},
                        new int[]{R.id.artist, R.id.title, R.id.url},mMediaPlayer);
                mListMusic = (ListView) findViewById(R.id.listMusic);
                mListMusic.setAdapter(mAdapter);
                mListMusic.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //ImageView imV = (ImageView)view.findViewById(R.id.playPause);

                        if(position == mIdAudioItemSelect) {
                            if(mMediaPlayer.isPlaying()) {
                                mMediaPlayer.pause();
                                Log.d(LOG_TAG, "list->onClick: pause");
                            }
                            else {
                                mMediaPlayer.start(position);
                            }
                        }
                        else {
                            TextView url = (TextView) view.findViewById(R.id.url);
                            String strUrl = url.getText().toString();
                            play(strUrl,position);
                            mIdAudioItemSelect = position;
                            //mAudioViewSelected = view;
                        }
                        Parcelable state = mListMusic.onSaveInstanceState();
                        mAdapter.notifyDataSetChanged();
                        mListMusic.onRestoreInstanceState(state);
                    }
                });
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.toString());
            }
        }
        @Override
        public void onError(VKError error) {
            //Do error stuff
            Log.d(LOG_TAG, "VKMusic->error");
        }

        @Override
        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
            //I don't really believe in progress
            Log.d(LOG_TAG, "VKMusic->Failed");
        }
    };

    private View.OnClickListener menuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VKRequest mRequest;
            findViewById(mIdMenuItemSelect).setBackgroundResource(R.drawable.item_menu_color);
            TextView tv = (TextView)findViewById(mIdMenuItemSelect);
            tv.setTextColor(getResources().getColor(R.color.vk_my_color_text));
            mIdMenuItemSelect = v.getId();
            v.setBackgroundResource(R.color.vk_my_color_text);
            tv = (TextView) findViewById(mIdMenuItemSelect);
            tv.setTextColor(getResources().getColor(R.color.vk_white));
            switch (v.getId()) {
                case R.id.myAudio: {
                    try {
                        mCurrentFragment = new MyAudioFragment();
                        mRequest = new VKRequest("audio.get");
                        mRequest.executeWithListener(mRequestListener);
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "request: " + e);
                    }
                    Log.d(LOG_TAG, "myAudio");
                    break;
                }
                case R.id.recommendations: {
                    try {
                        mRequest = new VKRequest("audio.getRecommendations");
                        mRequest.executeWithListener(mRequestListener);
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "request: " + e);
                    }
                    Log.d(LOG_TAG,"recommendations");
                    break;
                }
                case R.id.logout: {
                    logout();
                    Log.d(LOG_TAG, "logout");
                    break;
                }
            }
            mFragmentTransaction = getFragmentManager().beginTransaction();
            mFragmentTransaction.add(R.id.fragment,mCurrentFragment);
            mFragmentTransaction.commit();
            mCurrentRequest = tv.getText().toString();
            Log.d(LOG_TAG,"mCurrentRequest = " + mCurrentRequest);
        }
    };
}
