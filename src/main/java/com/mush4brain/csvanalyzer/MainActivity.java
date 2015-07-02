package com.mush4brain.csvanalyzer;

//import android.view.View;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends Activity{//} implements View.OnClickListener {
  private String TAG = "MAIN ACTIVITY";
  final String mURL="https://raw.githubusercontent.com/tillnagel/unfolding/master/data/data/countries-population-density.csv";
  TextView mTextView;// =
  Button mButtonFindFile;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mTextView = (TextView)findViewById(R.id.textView);
    mButtonFindFile = (Button)findViewById(R.id.buttonFindFile);
    mButtonFindFile.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        //findFileClick();
        Toast.makeText(getApplicationContext(), "Clicked2", Toast.LENGTH_LONG).show();
      }
    });

//    Log.d(TAG, mURL);

    //start thread
    Thread thread = new Thread(new Task());
    thread.start();

    //wait for thread to finish
    try {
      thread.join();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    //set action bar
    ActionBar actionBar = getActionBar();
    actionBar.setTitle("CSV Analyzer");
    actionBar.setSubtitle(R.string.app_version);
    actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
  }

//  public void onClick(View v){
//    Toast.makeText(this,"Clicked2", Toast.LENGTH_LONG).show();
//    if(v.getId() == R.id.buttonFindFile) {
//     Toast.makeText(this,"Clicked", Toast.LENGTH_LONG).show();
//
//    }
//  }

//  public void findFileClick(View v){
//
//  }

  class Task implements Runnable {

    @Override
    public void run() {

      try{
        URL url = new URL(mURL);
        Log.d(TAG,"URL: " + url.toString());
        InputStreamReader isr =  new InputStreamReader(url.openStream());
        BufferedReader in = new BufferedReader(isr);
        StringBuilder total = new StringBuilder();
        String line;
        while((line = in.readLine()) != null){
          total.append(line + "\n");
        }

        in.close();
        Log.d(TAG, total.toString());
        mTextView.setText(total.toString());
      }catch(MalformedURLException e){
        Log.d(TAG,"Malformed");
      }catch(IOException e){
        Log.d(TAG,"IOException");
      }catch(NetworkOnMainThreadException e){
        Log.d(TAG,"NetworkProblem");
      }
    }
  }
}

