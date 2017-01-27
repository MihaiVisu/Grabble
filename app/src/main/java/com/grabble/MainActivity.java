package com.grabble;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.grabble.customclasses.GameState;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button btn1, btn2;
    private LoginButton fbLogin;
    private EditText editText;
    private Context context;

    private CallbackManager callbackManager;

    GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);

        state = (GameState)context;
        // initialize the achievements in the main activity
        state.initializeAchievements();

        btn1 = (Button) findViewById(R.id.button1);
        btn1.setOnClickListener(this);

        btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        fbLogin = (LoginButton) findViewById(R.id.login_button);

        fbLogin.setReadPermissions(Arrays.asList(
                "public_profile", "email"));

        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    state.setUsername(object.get("name").toString());
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                request.executeAsync();


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("log in failed");
            }
        });

        // add change listener to the editText view
        editText = (EditText) findViewById(R.id.editText);
        if (state.getUsername() != null) {
            editText.setText(state.getUsername());
            // enable the button by default if we have a username already
            btn1.setEnabled(true);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && !btn1.isEnabled()) {
                    btn1.setEnabled(true);
                }
                else if (s.length() == 0 && btn1.isEnabled()) {
                    btn1.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()) {
            case R.id.button1:
                // set username of the game state
                state.setUsername(editText.getText().toString());
                i = new Intent(context, NavActivity.class);
                startActivity(i);
                break;
            case R.id.button2:
                i = new Intent(context, InstructionsActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        state.activityStopped();
        super.onStop();
    }
}
