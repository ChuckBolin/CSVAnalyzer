package com.mush4brain.csvanalyzer;

//import android.view.View;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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

//http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
public class MainActivity extends Activity{
  private String TAG = "MAIN ACTIVITY";
  final String mURL="https://raw.githubusercontent.com/tillnagel/unfolding/master/data/data/countries-population-density.csv";
  private TextView mTextView;
  private Context mContext;
  private View mRootView;
  Button mButtonFindFile;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mTextView = (TextView)findViewById(R.id.textView);
    mContext = getApplicationContext();

    //set action bar
    ActionBar actionBar = getActionBar();
    actionBar.setTitle("CSV Analyzer");
    actionBar.setSubtitle(R.string.app_version);
    actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
  }

  private class DownloadAsyncTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
      String response = "";

      try{
        URL url = new URL(mURL);
        InputStreamReader isr =  new InputStreamReader(url.openStream());
        BufferedReader in = new BufferedReader(isr);
        StringBuilder total = new StringBuilder();
        String line;
        while((line = in.readLine()) != null){
          total.append(line + "\n");
        }

        in.close();
        return total.toString();
      }catch(MalformedURLException e){
        Log.d(TAG,"Malformed");
      }catch(IOException e){
        Log.d(TAG,"IOException");
      }catch(NetworkOnMainThreadException e){
        Log.d(TAG,"NetworkProblem");
      }

      return response;
    }

    @Override
    protected void onPostExecute(String result) {
      //Toast.makeText(mContext, "onPostExecute", Toast.LENGTH_LONG).show();
      mTextView.setText(result);
    }
  }

  public void onButtonClick(View v){
    mTextView.setText("");
    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
    DownloadAsyncTask task = new DownloadAsyncTask();
    task.execute();
  }
}

