package ganny.vkmusic;

import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by Ganny on 02.12.2014.
 */
public class MyMediaPlayer extends MediaPlayer {

    protected int position;
    protected String request;

    public MyMediaPlayer() {
        super();
        position = -2;
    }

    public void prepareAsync(int pos) {
        super.prepareAsync();
        position = pos;
        //Log.d("log","MyMediaPayer::prepareAsync->pos:" + position);
    }

    public void start(int pos) {
        super.start();
        position = pos;
        //Log.d("log","MyMediaPayer::start->pos:" + position);
    }

    @Override
    public void reset() {
        super.reset();
        position = -2;
    }

    @Override
    public void pause() {
        super.pause();
        position = -2;
    }

    public int getPosition() {
        return position;
    }

    public String getRequest() {
        return request;
    }

    void setRequest(String pRequest) {
        request = pRequest;
    }
}
