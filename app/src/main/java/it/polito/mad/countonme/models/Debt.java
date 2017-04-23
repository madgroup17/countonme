package it.polito.mad.countonme.models;

import java.util.List;
import java.util.Map;

/**
 * Created by Khatereh on 4/21/2017.
 */

public class Debt
{
    private Double mSpend;
    private Double mShare;
    private Double mCredit;
    private User mUser;
    private List<DebtValue> mDebts;

    public Double getSpend() {
        return mSpend;
    }

    public void setSpend(Double Spend) {
        this.mSpend = Spend;
    }

    public Double getShare() {
        return mShare;
    }

    public void setShare(Double Share) {
        this.mShare = Share;
    }

    public Double getCredit() {
        return mCredit;
    }

    public void setCredit(Double Credit) {
        this.mCredit = Credit;
    }

    public User getUser() {return mUser;}

    public void setUser(User User) {
        this.mUser = User;
    }

    public List<DebtValue> getDebts() { return mDebts; }

    public void setDebts( List<DebtValue> Debts ) { mDebts = Debts; }

    public void addDebt( DebtValue Debt ) {
        if( Debt == null ) return;
        mDebts.add(Debt);
    }


}
