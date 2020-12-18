package com.example.businesstrans.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.businesstrans.FragmentInflater;
import com.example.businesstrans.R;
import com.example.businesstrans.utils.Event;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context mContext;
    private ArrayList<Event> mEvents;
    private FragmentInflater mFragmentInflater;


    public EventAdapter(Context context, ArrayList<Event> events) {
        mContext = context;
        mEvents = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.event_list_item,parent,false);
        EventViewHolder eventViewHolder = new EventViewHolder(view);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, final int position) {
        holder.eventDetails.setText(mEvents.get(position).getName_of_event());
        holder.eventBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFragmentInflater.inflateEventDetailsFragment(mEvents.get(position));

            }
        });
        String toBeBrokenDown = mEvents.get(position).getDate_of_event();
        String[] parts = toBeBrokenDown.split(" ");
        holder.bigDate.setText(parts[0]);
        String monthYear = parts[1]+" "+parts[2];
        holder.monthYear.setText(monthYear);


    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mFragmentInflater = (FragmentInflater)mContext;
    }

    class EventViewHolder extends RecyclerView.ViewHolder{
        CardView eventBoard;
        TextView bigDate;
        TextView monthYear;
        TextView eventDetails;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventBoard = itemView.findViewById(R.id.event_item);
            bigDate = itemView.findViewById(R.id.big_date_event);
            monthYear = itemView.findViewById(R.id.month_year_event);
            eventDetails = itemView.findViewById(R.id.details_of_event);
        }
    }
}
