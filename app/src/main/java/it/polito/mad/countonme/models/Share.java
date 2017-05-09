package it.polito.mad.countonme.models;

/**
 * Created by francescobruno on 09/05/17.
 */

public class Share {

    private User mUser;
    private Double mAmount;

    public Share() {
        this( null, 0.0 );
    }

    public Share( User user, Double amount ) {
        mUser   = user;
        mAmount = amount;
    }

    public void setUser( User user ) { mUser = user; }

    public User getUser() { return mUser; }

    public void setAmount( Double amount ) { mAmount = amount; }

    public Double getAmount() { return mAmount; }

}
