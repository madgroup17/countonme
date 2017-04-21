package it.polito.mad.countonme.business;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Iterator;
import java.util.List;

import it.polito.mad.countonme.models.Expense;

/**
 * Created by Khatereh on 4/20/2017.
 */

public class Balance
{
    private List<Expense> ExpenseList;

    public void Balance(List<Expense> ExpenseList)
    {
        this.ExpenseList = ExpenseList;
    }

    public Double GetMySpend()
    {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        com.google.firebase.auth.FirebaseUser currentUser = mFirebaseAuth.getInstance().getCurrentUser();

        Double Amount = 0.0;

        for(Iterator<Expense> i = ExpenseList.iterator(); i.hasNext(); )
        {
            Expense item = i.next();

            if(item.getPayer().getId()== currentUser.getUid())
            {
                Amount+=item.getAmount();
            }
        }

        return Amount;
    }

    public Double GetOthersSpend()
    {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        com.google.firebase.auth.FirebaseUser currentUser = mFirebaseAuth.getInstance().getCurrentUser();

        Double Amount = 0.0;

        for(Iterator<Expense> i = ExpenseList.iterator(); i.hasNext(); )
        {
            Expense item = i.next();

            if(item.getPayer().getId()!= currentUser.getUid())
            {
                Amount+=item.getAmount();
            }
        }

        return Amount;
    }


    public Double GetMyDept()
    {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        com.google.firebase.auth.FirebaseUser currentUser = mFirebaseAuth.getInstance().getCurrentUser();

        Double Spend = GetMyDept();
        Double OthersSpend = GetOthersSpend();

        return OthersSpend - Spend;
    }

    public Double GetMyCredit()
    {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        com.google.firebase.auth.FirebaseUser currentUser = mFirebaseAuth.getInstance().getCurrentUser();

        Double Spend = GetMyDept();
        Double OthersSpend = GetOthersSpend();

        return  Spend-OthersSpend;
    }


}
