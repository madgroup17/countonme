package it.polito.mad.countonme.models;

/**
 * Created by Khatereh on 4/22/2017.
 */

public class DebtValue
{
    private Double mAmount;
    private User mUser;

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
