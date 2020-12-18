package com.example.businesstrans.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.businesstrans.FragmentInflater;
import com.example.businesstrans.R;
import com.example.businesstrans.utils.Debtor;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DebtorAdapter extends RecyclerView.Adapter<DebtorAdapter.DebtorViewHolder> {

    private Context mContext;
    private ArrayList<Debtor> mDebtors;
    private FragmentInflater mFragmentInflater;


    public DebtorAdapter(Context context, ArrayList<Debtor> debtors) {
        mContext = context;
        mDebtors = debtors;
    }

    @NonNull
    @Override
    public DebtorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.debtor_list_item,parent,false);
        DebtorViewHolder debtorViewHolder = new DebtorViewHolder(view);
        return debtorViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DebtorViewHolder holder, final int position) {

        holder.companyName.setText(mDebtors.get(position).getDebtor_company_name());
        holder.typeOfTransaction.setText(mDebtors.get(position).getDebtor_type_of_transaction());
        holder.valueOfTransaction.setText(mDebtors.get(position).getDebtor_value_of_transaction());
        holder.locationOfTransaction.setText(mDebtors.get(position).getDebtor_location_of_transaction());
        holder.dateOfTransaction.setText(mDebtors.get(position).getDebtor_date_of_transaction());
        holder.debtorBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Debtor Item");
                mFragmentInflater.inflateDebtorDetailsFragment(mDebtors.get(position));

            }
        });

    }

    @Override
    public int getItemCount() {
        return mDebtors.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mFragmentInflater = (FragmentInflater)mContext;
    }

    class DebtorViewHolder extends RecyclerView.ViewHolder{

        CardView debtorBoard;
        TextView companyName;
        TextView typeOfTransaction;
        TextView valueOfTransaction;
        TextView locationOfTransaction;
        TextView dateOfTransaction;


        public DebtorViewHolder(@NonNull View itemView) {
            super(itemView);
            debtorBoard = itemView.findViewById(R.id.debtor_item);
            companyName = itemView.findViewById(R.id.debtor_company_name);
            typeOfTransaction = itemView.findViewById(R.id.debtor_type_of_transaction);
            valueOfTransaction = itemView.findViewById(R.id.debtor_value_of_transaction);
            locationOfTransaction = itemView.findViewById(R.id.debtor_location);
            dateOfTransaction = itemView.findViewById(R.id.debtor_date);

        }
    }
}
