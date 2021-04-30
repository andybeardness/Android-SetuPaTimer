package com.beardness.setupatimer.Codez.Factoriez;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.beardness.setupatimer.Codez.Databasez.JokeDB;
import com.beardness.setupatimer.Codez.Helperz.RecyclerJokesAdapter;
import com.beardness.setupatimer.FragmentChoosenJoke;
import com.beardness.setupatimer.R;

public class ListenerFactory {
  
  private ListenerFactory() {}
  
  public static View.OnClickListener getJokeItemListener(RecyclerJokesAdapter.JokeViewHolder viewHolder,
                                                         RecyclerJokesAdapter adapter) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        int position = viewHolder.getAdapterPosition();
        
        JokeDB.cursor().moveToPosition(position);
        int itemID = JokeDB.cursor().getInt(JokeDB.cursor().getColumnIndex(JokeDB.COL_ID));
        
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
}