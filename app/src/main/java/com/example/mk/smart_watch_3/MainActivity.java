package com.example.mk.smart_watch_3;


// Speech to Text Imports
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Locale;


// HTTP Imports
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


// Main file
public class MainActivity extends AppCompatActivity
{
    //Speech to Text
    EditText editText;
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;

    // Http
    private TextView mTextViewResult;

    // Speech Text and Http
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        // edit text method
        editText = findViewById(R.id.editText);



        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // mSpeechRecognizerIntent
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        // Http
        mTextViewResult = findViewById(R.id.text_view_result);
        OkHttpClient client = new OkHttpClient();
        String url = "http://209.2.212.87/esp8266/message&value=himohammad";
        Request request = new Request.Builder()
                .url(url)
                .build();


        // creating the method for Speech to Text
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener()
        {
            @Override
            public void onReadyForSpeech(Bundle bundle)
            {
            }

            @Override
            public void onBeginningOfSpeech()
            {
            }

            @Override
            public void onRmsChanged(float v)
            {
            }

            @Override
            public void onBufferReceived(byte[] bytes)
            {
            }

            @Override
            public void onEndOfSpeech()
            {
            }

            @Override
            public void onError(int i)
            {
            }

            @Override
            public void onResults(Bundle bundle)
            {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matches != null)
                    editText.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle)
            {
            }

            @Override
            public void onEvent(int i, Bundle bundle)
            {
            }
        });

        // Block of Code for HTTP
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                // Extra things that we don't need
                if (response.isSuccessful())
                {
                    final String myResponse = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mTextViewResult.setText(myResponse);
                        }
                    });
                }
            }
        });




        // Getting Speech to Text implemented
        findViewById(R.id.button).setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                switch(motionEvent.getAction())
                {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        editText.setHint("You will see the input here");
                        break;

                    case MotionEvent.ACTION_DOWN:
                        editText.setText("");
                        editText.setHint("Listening...");
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        break;

                }
                return false;
            }
        });

    }
    // Check to method on current version of Android
    private void checkPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(!(ContextCompat
                    .checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED))
            {

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();

            }
        }
    }
}
