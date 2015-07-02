package com.mush4brain.csvanalyzer;

//import android.view.View;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends Activity {
  private String TAG = "MainActivity";
  //detects swipes
  float x1,x2;
  float y1, y2;

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


  public static class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater
              .inflate(R.layout.fragment_main, container, false);

      return rootView;
    }
  }

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


}

