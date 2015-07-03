package com.mush4brain.csvanalyzer;

//import android.view.View;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.provider.OpenableColumns;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    mTextView = (TextView)findViewById(R.id.textView);
    mTextView.setMovementMethod(new ScrollingMovementMethod());
    mContext = getApplicationContext();

    //set action bar
    ActionBar actionBar = getActionBar();
    actionBar.setTitle("CSV Analyzer");
    actionBar.setSubtitle(R.string.app_version);
    actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));


  }

  //download mURL to mTextView
  public void onButtonClick(View v){
    mTextView.setText("");
    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
    DownloadAsyncTask task = new DownloadAsyncTask();
    task.execute();
  }

  //download URL AsyncTask
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

  //Button to find file on device is clicked,
  //then calls onActivityResults with filepath
  public void onButtonFindClick(View v){
    mTextView.setText("");
    Toast.makeText(getApplicationContext(), "Finding...", Toast.LENGTH_SHORT).show();

    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("text/csv");//*/*");
    //intent.setDataAndType(Uri, "text/csv");
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    try{
      startActivityForResult(
              Intent.createChooser(intent, "Select a file to load"),
              0);
    }catch(ActivityNotFoundException ex){
      Toast.makeText(this, "Install a file manager.",Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data){
    switch(requestCode){
      case 0:
        if(resultCode == RESULT_OK){
          Uri uri = data.getData();
          ReadFileAsyncTask task = new ReadFileAsyncTask();
          task.execute(uri);
        }
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  //read file AsyncTask
  //3 params are <params, Progress, Result>
  private class ReadFileAsyncTask extends AsyncTask<Uri, Void, String> {

    @Override
    protected String doInBackground(Uri... uri) {
      String response = "";
      String storageState = Environment.getExternalStorageState();
      Log.d(TAG,"uri: " + uri.toString());
      Log.d(TAG,"T1: " + Environment.getExternalStorageDirectory());
      Log.d(TAG,"T2: " + getExternalFilesDir(null));
      Log.d(TAG,"T3: " + uri[0].getPath().toString());// getExternalFilesDir(null));
      if(storageState.equals(Environment.MEDIA_MOUNTED)){
        //Log.d(TAG, "Media_Mounted");
        File file = new File(Environment.getExternalStorageDirectory(),getFileName(uri[0]));//"wifiscan.csv");
        //File file = new File(getExternalFilesDir(null),getFileName(uri[0]));//"wifiscan.csv");
        Log.d(TAG, "file: " + file.toString());
        //Log.d(TAG, "uri.getPath: " + getFileName(uri[0]));// uri.getPath());
        try {
          BufferedReader inputReader2 = new BufferedReader(
                  new InputStreamReader(new FileInputStream(file)));// getFileName(uri.toString()))));// uri.getPath())));
          String inputString2;
          StringBuffer stringBuffer2 = new StringBuffer();
          while ((inputString2 = inputReader2.readLine()) != null) {
            stringBuffer2.append(inputString2 + "\n");
            //Log.d(TAG, inputString2);
          }
          return stringBuffer2.toString();

          //mTextView.setText(stringBuffer2.toString());
        }catch(FileNotFoundException ex){
          Log.d(TAG,"Filenotfound");
        }catch(IOException ex){
          Log.d(TAG,"IOException");
        }
      }

      return response;
    }

    @Override
    protected void onPostExecute(String result) {
       mTextView.setText(result);
    }
  }

  //Thanks to
  //http://stackoverflow.com/questions/5568874/how-to-extract-the-file-name-from-uri-returned-from-intent-action-get-content
  public String getFileName(Uri uri) {
    String result = null;
    if (uri.getScheme().equals("content")) {
      Cursor cursor = getContentResolver().query(uri, null, null, null, null);
      try {
        if (cursor != null && cursor.moveToFirst()) {
          result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }
      } finally {
        cursor.close();
      }
    }
    if (result == null) {
      result = uri.getPath();
      int cut = result.lastIndexOf('/');
      if (cut != -1) {
        result = result.substring(cut + 1);
      }
    }
    return result;
  }




}




//  public String readTextFile(Uri uri){
//
////    Log.d(TAG,"1:" + uri.getPath());    //storage/emulated/0/wifiscan.csv
////    Log.d(TAG,"2:" + getFileName(uri)); //wifiscan.csv
//    String storageState = Environment.getExternalStorageState();
//    if(storageState.equals(Environment.MEDIA_MOUNTED)){
//      Log.d(TAG, "Media_Mounted");
//      File file = new File(getExternalFilesDir(null),"wifiscan.csv");
//      Log.d(TAG, "File: " + file.toString());
//      Log.d(TAG, "uri.getPath: " + uri.getPath());
//      try {
//        BufferedReader inputReader2 = new BufferedReader(
//                new InputStreamReader(new FileInputStream(uri.getPath())));
//        String inputString2;
//        StringBuffer stringBuffer2 = new StringBuffer();
//        while ((inputString2 = inputReader2.readLine()) != null) {
//          stringBuffer2.append(inputString2 + "\n");
//          Log.d(TAG, inputString2);
//        }
//        mTextView.setText(stringBuffer2.toString());
//      }catch(FileNotFoundException ex){
//        Log.d(TAG,"Filenotfound");
//      }catch(IOException ex){
//        Log.d(TAG,"IOException");
//      }
//    }
//
//    return "";
//  }