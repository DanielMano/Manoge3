package com.example.daniel.manoge3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Daniel on 5/10/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> names;
    private ArrayList<Integer> counts;
    private ArrayList<Integer> routineIDs;
    private Context mContext;
    private Bundle incomingBundle;
    boolean fAE;

    public RecyclerViewAdapter(Context context, ArrayList<String> names, ArrayList<Integer> counts, ArrayList<Integer> routineIDs, Bundle incomingBundle) {
        this.names = names;
        this.counts = counts;
        this.mContext = context;
        this.routineIDs = routineIDs;
        this.incomingBundle = incomingBundle;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.begin_workout_recycler_view_listitem, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        String name = names.get(position);
        int count = counts.get(position);

        holder.routineTitle.setText(name);
        holder.exerciseLiteral.setText(count + " Exercises");
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: CardView clicked: " + position);

                fAE = false;
                if (incomingBundle != null)
                    fAE = incomingBundle.getBoolean("fromAddExercise");

                Intent intent = new Intent(mContext, RoutineExercisesActivity.class);
                Bundle sendBundle = new Bundle();
                sendBundle.putInt("routineID", routineIDs.get(position));
                sendBundle.putString("routineName", names.get(position));
                sendBundle.putBoolean("fromAddExercise", fAE);
                intent.putExtras(sendBundle);
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView routineTitle;
        TextView exerciseLiteral;
        public CardView mCardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            routineTitle = itemView.findViewById(R.id.recyc_item_name);
            exerciseLiteral = itemView.findViewById(R.id.recyc_item_num_exercises);
            mCardView = itemView.findViewById(R.id.card_view);
        }
    }
}
