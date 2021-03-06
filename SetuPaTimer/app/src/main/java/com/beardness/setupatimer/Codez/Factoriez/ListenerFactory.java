package com.beardness.setupatimer.Codez.Factoriez;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.beardness.setupatimer.Codez.Databasez.JokeDB;
import com.beardness.setupatimer.Codez.Helperz.RecyclerJokesAdapter;
import com.beardness.setupatimer.FragmentAppTitle;
import com.beardness.setupatimer.FragmentChoosenJoke;
import com.beardness.setupatimer.FragmentInfo;
import com.beardness.setupatimer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Factory fo Listeners
public class ListenerFactory {
  
  private ListenerFactory() {}
  
  // Listener for item in list in Recycler
  public static View.OnClickListener getJokeItemListener(RecyclerJokesAdapter.JokeViewHolder viewHolder,
                                                         RecyclerJokesAdapter adapter) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Prepare
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        int position = viewHolder.getAdapterPosition();
        
        JokeDB.cursor().moveToPosition(position);
        int itemID = JokeDB.cursor().getInt(JokeDB.cursor().getColumnIndex(JokeDB.COL_ID));
        
        // Change choosen joke fragment with bundle
        Bundle bundle = new Bundle();
        bundle.putInt(
          FragmentChoosenJoke.BUNDLE_JOKE_ID,
          JokeDB.cursor().getInt(JokeDB.cursor().getColumnIndex(JokeDB.COL_ID)));
        bundle.putInt(
          FragmentChoosenJoke.BUNDLE_JOKE_POSITION,
          position);
  
        Fragment choosenJoke = new FragmentChoosenJoke(adapter);
        choosenJoke.setArguments(bundle);
        FTFactory.setJoke(activity.getSupportFragmentManager(), choosenJoke);
  
        // Status working
        int status = JokeDB.cursor().getInt(JokeDB.cursor().getColumnIndex(JokeDB.COL_STATUS));
  
        if (status == 0) {
          ContentValues content = new ContentValues();
          content.put(JokeDB.COL_STATUS, JokeDB.STATUS_SET_UP_WATCHED);
  
          JokeDB.updateDB(
            JokeDB.DB_NAME,
            content,
            JokeDB.COL_ID + " = ?",
            new String[]{String.valueOf(itemID)});
  
          adapter.updateCursor(activity);
          adapter.notifyDataSetChanged();
        }
      }
    };
  }
  
  // Listener for send punchline button
  public static View.OnClickListener getSendPunchlineListener(FragmentChoosenJoke fragment,
                                                              RecyclerJokesAdapter adapter,
                                                              int position) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ToastFactory.makeToast(fragment.getActivity(), fragment.punchline);
        
        ContentValues content = new ContentValues();
        content.put(JokeDB.COL_STATUS, JokeDB.STATUS_PUNCHLINE_WATCHED);
  
        JokeDB.updateDB(
                JokeDB.DB_NAME,
                content,
                JokeDB.COL_ID + " = ?",
                new String[]{String.valueOf(JokeDB.posToID(position))}
        );
        
        adapter.updateCursor(fragment.getActivity());
        adapter.notifyDataSetChanged();
      }
    };
  }
  
  // Listener for make favorite button
  public static View.OnClickListener getMakeFavoriteListener(FragmentChoosenJoke fragment,
                                                             RecyclerJokesAdapter adapter,
                                                             int position) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (fragment.isFavorite == 0) {
          fragment.makeFavorite.setImageResource(R.drawable.ic_favorite_true);
          
          ContentValues content = new ContentValues();
          content.put(JokeDB.COL_IS_FAVORITE, 1);
  
          JokeDB.updateDB(
                  JokeDB.DB_NAME,
                  content,
                  JokeDB.COL_ID + " = ?",
                  new String[]{String.valueOf(JokeDB.posToID(position))}
          );
        }
        else {
          fragment.makeFavorite.setImageResource(R.drawable.ic_favorite_false);
  
          ContentValues content = new ContentValues();
          content.put(JokeDB.COL_IS_FAVORITE, 0);
  
          JokeDB.db().update(
                  JokeDB.DB_NAME,
                  content,
                  JokeDB.COL_ID + " = ?",
                  new String[]{String.valueOf(JokeDB.posToID(position))}
          );
        }
  
        adapter.updateCursor(fragment.getActivity());
        adapter.notifyDataSetChanged();
        
        fragment.reverseIsFavorite();
      }
    };
  }
  
  // Listener for navigation bar
  public static BottomNavigationView.OnNavigationItemSelectedListener getBottomNavigationListener(FragmentManager manager) {
    return new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
          
          case R.id.bottom_bar_btn_all:
            Log.d("BOTTOM_BAR", "ALL");
            FTFactory.setAppTitle(manager);
            FTFactory.setJokesList(manager, JokeDB.CURSOR_TYPE_ALL);
            return true;
          
          case R.id.bottom_bar_btn_unwatched:
            Log.d("BOTTOM_BAR", "UNWATCHED");
            FTFactory.setAppTitle(manager);
            FTFactory.setJokesList(manager, JokeDB.CURSOR_TYPE_UNWATCHED);
            return true;
          
          case R.id.bottom_bar_btn_favorite:
            Log.d("BOTTOM_BAR", "FAVORITES");
            FTFactory.setAppTitle(manager);
            FTFactory.setJokesList(manager, JokeDB.CURSOR_TYPE_FAVORITE);
            return true;
          
          case R.id.bottom_bar_btn_watched:
            Log.d("BOTTOM_BAR", "WATCHED");
            FTFactory.setAppTitle(manager);
            FTFactory.setJokesList(manager, JokeDB.CURSOR_TYPE_WATCHED);
            return true;
          
          case R.id.bottom_bar_btn_info:
            Log.d("BOTTOM_BAR", "INFO");
            FTFactory.setAppTitle(manager, FragmentAppTitle.TITLE_TYPE_INFO);
            FTFactory.setInfoContent(manager);
            return true;
          
          default:
            Log.d("BOTTOM_BAR", "DEFAULT");
            FTFactory.setAppTitle(manager);
            FTFactory.setJokesList(manager, JokeDB.CURSOR_TYPE_ALL);
            return false;
        }
        
      }
    };
  }
  
  // Listener for GitHub link
  public static View.OnClickListener getSourceCodeListener(Context context) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Uri uri = Uri.parse(FragmentInfo.URL_SOURCE);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
      }
    };
  }
  
  // Listener for Instagram link
  public static View.OnClickListener getInstagramListener(Context context) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Uri uri = Uri.parse(FragmentInfo.URL_INSTAGRAM);
  
        try {
          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
          intent.setPackage(FragmentInfo.INSTAGRAM_PACKAGE);
          context.startActivity(intent);
        }
        catch (Exception e) {
          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
          context.startActivity(intent);
        }
      }
    };
  }
  
  // Listener for Telegram link
  public static View.OnClickListener getTelegramListener(Context context) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Uri uri = Uri.parse(FragmentInfo.URL_TELEGRAM);
      
        try {
          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
          intent.setPackage(FragmentInfo.TELEGRAM_PACKAGE);
          context.startActivity(intent);
        }
        catch (Exception e) {
          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
          context.startActivity(intent);
        }
      }
    };
  }
  
}
