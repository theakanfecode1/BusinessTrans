package com.example.businesstrans;

import com.example.businesstrans.utils.Debtor;
import com.example.businesstrans.utils.Event;
import com.example.businesstrans.utils.Transaction;

public interface FragmentInflater {

    void inflateTransactionDetailsFragment(Transaction transaction);
    void inflateDebtorDetailsFragment(Debtor debtor);
    void inflateEventDetailsFragment(Event event);
    void inflateHomeFragment();
}
