package it.polito.mad.countonme.business;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import it.polito.mad.countonme.models.Debt;
import it.polito.mad.countonme.models.DebtValue;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.User;

import static java.lang.Math.abs;

/**
 * Created by Khatereh on 4/20/2017.
 */

public class Balance {
    private List<Expense> ExpenseList;
    private Map<String, User> mUsers;

    public Balance(List<Expense> ExpenseList, Map<String, User> mUsers) {
        this.ExpenseList = ExpenseList;
        this.mUsers = mUsers;
    }

    /******************************************************************************************/

    /******************************************************************************************/
    // Methods
    public Double GetSpend(String UserId) {
        Double Amount = 0.0;

        for (Iterator<Expense> i = ExpenseList.iterator(); i.hasNext(); ) {
            Expense item = i.next();

            if (new String(item.getPayer().getId()).equals(UserId)) {
                Amount += item.getAmount();
            }
        }

        return Amount;
    }

    public Double GetOthersSpend(String UserId) {
        Double Amount = 0.0;

        for (Iterator<Expense> i = ExpenseList.iterator(); i.hasNext(); ) {
            Expense item = i.next();

            if (!new String(item.getPayer().getId()).equals(UserId)) {
                Amount += item.getAmount();
            }
        }

        return Amount;
    }

    public Double GetShare(String UserId) {
        Double Amount = 0.0;

        for (Iterator<Expense> i = ExpenseList.iterator(); i.hasNext(); ) {
            Expense item = i.next();

            /*if(isIinvolved(UserId,item))
            {
                Amount+=(item.getAmount()/ item.getInvolved().size());
            }*/

            Amount += (item.getAmount() / mUsers.size());
        }

        return Amount;
    }

    public Double GetCredit(String UserId) {
        Double Spend = GetSpend(UserId);
        Double Share = GetShare(UserId);

        return Spend - Share;
    }

    public Double GetDept(String UserId) {
        Double Credit = GetCredit(UserId);

        if (Credit > 0)
            return 0.0;
        else
            return Credit;
    }

    public boolean isIinvolved(String UserId, Expense model) {
        boolean IsExist = false;

        for (Iterator<User> i = model.getInvolved().iterator(); i.hasNext(); ) {
            User item = i.next();

            if (item.getId() == UserId) {
                return true;
            }
        }

        return IsExist;
    }


    /******************************************************************************************/

    /******************************************************************************************/
    // MyMethods
    public Double GetMySpend() {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        com.google.firebase.auth.FirebaseUser currentUser = mFirebaseAuth.getInstance().getCurrentUser();

        return GetSpend(currentUser.getUid());
    }

    public Double GetMyOthersSpend() {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        com.google.firebase.auth.FirebaseUser currentUser = mFirebaseAuth.getInstance().getCurrentUser();

        return GetOthersSpend(currentUser.getUid());
    }

    public Double GetMyDept() {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        com.google.firebase.auth.FirebaseUser currentUser = mFirebaseAuth.getInstance().getCurrentUser();

        return GetDept(currentUser.getUid());
    }

    public Double GetMyCredit() {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        com.google.firebase.auth.FirebaseUser currentUser = mFirebaseAuth.getInstance().getCurrentUser();

        return GetCredit(currentUser.getUid());
    }

    /******************************************************************************************/

    /******************************************************************************************/
    // Debts
    public List<Debt> getDebtList() {
        List<Debt> DebtList = setUserBalance();
        List<Debt> PositiveCredit = new ArrayList<Debt>();
        List<Debt> NegetiveCredit = new ArrayList<Debt>();

        GetPosNegCredit(DebtList, PositiveCredit, NegetiveCredit);

        for (Iterator<Debt> i = DebtList.iterator(); i.hasNext(); )
        {
            Debt NegetiveItem = i.next();
            if (NegetiveItem.getCredit() < 0)
            {
                int j = 0;
                int Size = PositiveCredit.size();
                while (j < Size && NegetiveItem.getTempCredit() < 0)
                {
                    if(PositiveCredit.get(j).getTempCredit()!= 0)
                    {
                        if (abs(NegetiveItem.getTempCredit()) <= PositiveCredit.get(j).getTempCredit())
                        {
                            Double NegCredit = NegetiveItem.getTempCredit();
                            Double PosCredit = PositiveCredit.get(j).getTempCredit();
                            Double Debt = abs(NegCredit);

                            DebtValue DValue = new DebtValue();
                            DValue.setDebterUser(NegetiveItem.getUser());
                            DValue.setAmount(Debt);
                            DValue.setUser(PositiveCredit.get(j).getUser());
                            NegetiveItem.addDebt(DValue);

                            NegetiveItem.setTempCredit(0.0);
                            PositiveCredit.get(j).setTempCredit(PosCredit - Debt);
                        }
                        else
                        {
                            Double NegCredit = NegetiveItem.getTempCredit();
                            Double PosCredit = PositiveCredit.get(j).getTempCredit();
                            Double Debt = PosCredit;

                            DebtValue DValue = new DebtValue();
                            DValue.setDebterUser(NegetiveItem.getUser());
                            DValue.setAmount(Debt);
                            DValue.setUser(PositiveCredit.get(j).getUser());
                            NegetiveItem.addDebt(DValue);

                            NegetiveItem.setTempCredit(NegCredit + PosCredit);
                            PositiveCredit.get(j).setTempCredit(0.0);
                        }
                    }
                    else
                    {
                        j++;
                    }
                }
            }
        }


        return DebtList;
    }

    public List<DebtValue> GetOwsList(List<Debt> DebtList)
    {
        List<DebtValue> DebtValueList = new ArrayList<DebtValue>();

        for (Iterator<Debt> i = DebtList.iterator(); i.hasNext(); ) {
            Debt NegetiveItem = i.next();

            if (NegetiveItem.getCredit() < 0)
            {

                for (Iterator<DebtValue> j = NegetiveItem.getDebts().iterator(); j.hasNext(); )
                {
                    DebtValue DebtValueItem = j.next();
                    DebtValueList.add(DebtValueItem);
                }
            }
        }

        return DebtValueList;
    }

    private List<Debt> setUserBalance() {
        List<Debt> DebtList = new ArrayList<Debt>();

        for (Map.Entry<String, User> entry : mUsers.entrySet()) {
            String key = entry.getKey();
            User value = entry.getValue();

            Debt DebtItem = new Debt();
            DebtItem.setUser(value);
            DebtItem.setSpend(GetSpend(key));
            DebtItem.setShare(GetShare(key));
            DebtItem.setCredit(GetCredit(key));
            DebtItem.setTempCredit(DebtItem.getCredit());

            DebtList.add(DebtItem);
        }

        return DebtList;
    }

    private void GetPosNegCredit(List<Debt> DebtList, List<Debt> PositiveCredit, List<Debt> NegetiveCredit) {
        for (Iterator<Debt> i = DebtList.iterator(); i.hasNext(); ) {
            Debt item = i.next();

            if (item.getCredit() > 0) {
                PositiveCredit.add(item);
                item.setDebts(null);
            } else
                NegetiveCredit.add(item);
        }
    }
}
