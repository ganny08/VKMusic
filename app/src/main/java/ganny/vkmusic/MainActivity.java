package ganny.vkmusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;



public class MainActivity extends Activity {

    private static final String LOG_TAG = "MainActivity";
    private static String sTokenKey = "ACCESS_TOKEN";
    private static String[] sMyScope = new String[]{VKScope.FRIENDS,VKScope.AUDIO};

    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG,"MainActivity->onCreate");
        VKSdk.initialize(sdkListener, "4643080",VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
        setContentView(R.layout.activity_main);
        mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.authorize(sMyScope,false,true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
        Log.d(LOG_TAG,"MainActivity->onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
        Log.d(LOG_TAG,"MainActivity->onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG,"MainActivity->onPause");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
    }

    private VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            Log.d(LOG_TAG, "MainActivity->onCaptchaError");
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            Log.d(LOG_TAG, "MainActivity->onTokenExpired");
            VKSdk.authorize(sMyScope,false,true);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(authorizationError.errorMessage)
                    .show();
            Log.d(LOG_TAG,"MainActivity->onAccessDenied");
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            newToken.saveTokenToSharedPreferences(MainActivity.this, sTokenKey);
            Log.d(LOG_TAG,"MainActivity->onReceiveNewToken");
            Intent i = new Intent(MainActivity.this, VKMusic.class);
            startActivity(i);
            MainActivity.this.finish();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.d(LOG_TAG,"MainActivity->onAcceptUserToken");
            Intent i = new Intent(MainActivity.this, VKMusic.class);
            startActivity(i);
            MainActivity.this.finish();
        }
    };
}
