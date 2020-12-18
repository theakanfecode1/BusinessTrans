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
import com.example.businesstrans.utils.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{
    private Context mContext;
    private ArrayList<Transaction> mTransactions;
    private FragmentInflater mFragmentInflater;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        mContext = context;
        mTransactions = transactions;
    }




    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.transaction_list_item,parent,false);
        TransactionViewHolder transactionViewHolder = new TransactionViewHolder(view);
        return transactionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, final int position) {

        holder.companyName.setText(mTransactions.get(position).getCompany_name());
        holder.typeOfTransaction.setText(mTransactions.get(position).getType_of_transaction());
        DecimalFormat decimalFormat = new DecimalFormat();
        String formattedamount = decimalFormat.format(Long.parseLong(mTransactions.get(position).getValue_of_transaction()));
        holder.valueOfTransaction.setText(formattedamount);
        holder.locationOfTransaction.setText(mTransactions.get(position).getLocation_of_transaction());
        holder.dateOfTransaction.setText(mTransactions.get(position).getDate_of_tansaction());
        holder.statusOfTransaction.setText(mTransactions.get(position).getStatus_of_transaction());
        holder.transactionBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Transaction Item");
                mFragmentInflater.inflateTransactionDetailsFragment(mTransactions.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mFragmentInflater = (FragmentInflater)mContext;
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder{
        CardView transactionBoard;
        TextView companyName;
        TextView typeOfTransaction;
        TextView valueOfTransaction;
        TextView locationOfTransaction;
        TextView dateOfTransaction;
        TextView statusOfTransaction;


        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionBoard = itemView.findViewById(R.id.transaction_item);
            companyName = itemView.findViewById(R.id.company_name);
            typeOfTransaction = itemView.findViewById(R.id.type_of_transaction);
            valueOfTransaction = itemView.findViewById(R.id.value_of_transaction);
            locationOfTransaction = itemView.findViewById(R.id.transaction_location);
            dateOfTransaction = itemView.findViewById(R.id.transaction_date);
            statusOfTransaction = itemView.findViewById(R.id.transaction_status);
        }
    }
}
