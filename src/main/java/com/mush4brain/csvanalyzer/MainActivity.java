package com.mush4brain.csvanalyzer;

//import android.view.View;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends Activity {
  private String TAG = "MAIN ACTIVITY";
  //detects swipes
  float x1,x2;
  float y1, y2;
  final String mURL="https://raw.githubusercontent.com/tillnagel/unfolding/master/data/data/countries-population-density.csv";
  TextView mTextView = (TextView)findViewById(R.id.textMain);
  //mTextView.setMovementMethod(LinkMovementMethod.getInstance());



  //fragment stuff
  FragmentTransaction ft = null;
  PlaceholderFragment fragMain = new PlaceholderFragment();
  PlaceholderFragmentRight fragRight = new PlaceholderFragmentRight();
  PlaceholderFragmentLeft fragLeft = new PlaceholderFragmentLeft();

  final int FRAGMENT_LEFT = 0;
  final int FRAGMENT_MAIN = 1;
  final int FRAGMENT_RIGHT = 2;
  int position = FRAGMENT_MAIN; //indicates current fragment


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.d(TAG, mURL);

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

    //sets main fragment
    ft = getFragmentManager().beginTransaction();
    ft.replace(R.id.container, fragMain);
    ft.commit();
  }

  public boolean onTouchEvent(MotionEvent touchevent){
    switch (touchevent.getAction())               {
      // when user first touches the screen we get x and y coordinate
      case MotionEvent.ACTION_DOWN:
      {
        x1 = touchevent.getX();
        y1 = touchevent.getY();
        break;
      }
      case MotionEvent.ACTION_UP:
      {
        x2 = touchevent.getX();
        y2 = touchevent.getY();

        //if left to right sweep event on screen
        if (x1 < x2){
          position++;
          if(position > 2)
            position = 2;

          if (position == FRAGMENT_MAIN){
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragMain);
            ft.addToBackStack(null);
            ft.commit();
          }
          else if (position == FRAGMENT_RIGHT){
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragRight);
            ft.addToBackStack(null);
            ft.commit();
          }
        }

        // if right to left sweep event on screen
        if (x1 > x2){
          position--;
          if(position < 0)
            position = 0;

          if (position == FRAGMENT_MAIN){
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragMain);
            ft.addToBackStack(null);
            ft.commit();
          }
          else if (position == FRAGMENT_LEFT){
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragLeft);
            ft.addToBackStack(null);
            ft.commit();
          }
        }
        break;
      }
    }
    return false;
  }

  //Main fragment
  public static class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater
              .inflate(R.layout.fragment_main, container, false);

      Toast.makeText(this.getActivity(), "Loading CSV File", Toast.LENGTH_LONG).show();



      return rootView;
    }



  }

  //Right fragment
  public static class PlaceholderFragmentRight extends Fragment {

    public PlaceholderFragmentRight() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater
              .inflate(R.layout.fragment_right, container, false);
      return rootView;
    }
  }

  //Left fragment
  public static class PlaceholderFragmentLeft extends Fragment {

    public PlaceholderFragmentLeft() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater
              .inflate(R.layout.fragment_left, container, false);
      return rootView;
    }
  }


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
        Log.d(TAG,total.toString());
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

