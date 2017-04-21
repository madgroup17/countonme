package it.polito.mad.countonme.models;

import java.util.Date;
import java.util.List;

/**
 * The expense data model
 * Created by francescobruno on 03/04/17.
 */

public class Expense {
    private String mName;
    private String mDescription;
    private String mSharingActivityCurrency;
    private String mExpenseCurrenty;
    private Double mAmount;
    private Double mConvertedAmount;
    private String mImageUrl;
    private String mParentSharingActivityId;
    private Boolean mIsSurvey;
    private Date mDate;
    private User mPayer;
    private List<User> mInvolved;

    public Expense() {
        this( null, null, null, null, 0.0, 0.0, null, null, null, null, false );
    }


    public Expense(String mName, String mDescription, String mSharingActivityCurrency, String mExpenseCurrenty,
                   Double mAmount, Double mConvertedAmount, String mImageUrl, String mParentSharingActivityId, Date date, User payer, Boolean mIsSurvey) {
        this.mName = mName;
        this.mDescription = mDescription;
        this.mSharingActivityCurrency = mSharingActivityCurrency;
        this.mExpenseCurrenty = mExpenseCurrenty;
        this.mAmount = mAmount;
        this.mConvertedAmount = mConvertedAmount;
        this.mImageUrl = mImageUrl;
        this.mParentSharingActivityId = mParentSharingActivityId;
        this.mDate = date;
        this.mIsSurvey = mIsSurvey;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getSharingActivityCurrency() {
        return mSharingActivityCurrency;
    }

    public void setSharingActivityCurrency(String mSharingActivityCurrency) {
        this.mSharingActivityCurrency = mSharingActivityCurrency;
    }

    public String getExpenseCurrenty() {
        return mExpenseCurrenty;
    }

    public void setExpenseCurrenty(String mExpenseCurrenty) {
        this.mExpenseCurrenty = mExpenseCurrenty;
    }

    public Double getAmount() {
        return mAmount;
    }

    public void setAmount(Double mAmount) {
        this.mAmount = mAmount;
    }

    public Double getConvertedAmount() {
        return mConvertedAmount;
    }

    public void setConvertedAmount(Double mConvertedAmount) {
        this.mConvertedAmount = mConvertedAmount;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public Date getDate() { return mDate; }

    public void setDate( Date date ) { mDate = date; }

    public User getPayer() { return mPayer; }

    public void setPayer( User payer ) { mPayer = payer; }

    public List<User> getInvolved() { return mInvolved; }

    public void setInvolved( List<User> Involved ) { mInvolved = Involved; }

    public Boolean getIsSurvey() {
        return mIsSurvey;
    }

    public void setIsSurvey(Boolean mIsSurvey) {
        this.mIsSurvey = mIsSurvey;
    }

    public String getParentSharingActivityId() {
        return mParentSharingActivityId;
    }

    public void setParentSharingActivityId(String mParentSharingActivityId) {
        this.mParentSharingActivityId = mParentSharingActivityId;
    }
}

