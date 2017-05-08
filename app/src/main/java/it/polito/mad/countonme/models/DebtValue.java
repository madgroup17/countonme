package it.polito.mad.countonme.models;

/**
 * Created by Khatereh on 4/22/2017.
 */

public class DebtValue
{
    private User mDebterUser;
    private Double mAmount;
    private User mUser;

    public User getDebterUser() {return mDebterUser;}

    public void setDebterUser(User User) {
        this.mDebterUser = User;
    }

    public Double getAmount() {
        return mAmount;
    }

    public void setAmount(Double Amount) {
        this.mAmount = Amount;
    }

    public User getUser() {return mUser;}

    public void setUser(User User) {
        this.mUser = User;
    }
}
