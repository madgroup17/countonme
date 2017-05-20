package it.polito.mad.countonme.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The expense data model
 * Created by francescobruno on 03/04/17.
 */

@IgnoreExtraProperties
public class Expense {
    private String mKey;
    private String mName;
    private String mDescription;
    private String mSharingActivityCurrency;
    private String mExpenseCurrency;
    private Double mAmount;
    private Double mConvertedAmount;
    private String mImageUrl;
    private String mParentSharingActivityId;
    private Boolean mIsSurvey;
    private Boolean mIsSharedEvenly;
    private Boolean mIsMoneyTransfer;
    private User mCreatedBy;
    private Date mDate;
    private User mPayer;
    private Map<String, Share> mShares = new HashMap<>();

    public Expense() {
        this( null, null, null, null, 0.0, 0.0, null, null, null, null, false, false );
    }


    public Expense(String mName, String mDescription, String mSharingActivityCurrency, String mExpenseCurrency,
                   Double mAmount, Double mConvertedAmount, String mImageUrl, String mParentSharingActivityId, Date date, User payer, Boolean mIsSurvey, Boolean mIsSharedEvenly ) {
        this.mName = mName;
        this.mDescription = mDescription;
        this.mSharingActivityCurrency = mSharingActivityCurrency;
        this.mExpenseCurrency = mExpenseCurrency;
        this.mAmount = mAmount;
        this.mConvertedAmount = mConvertedAmount;
        this.mImageUrl = mImageUrl;
        this.mParentSharingActivityId = mParentSharingActivityId;
        this.mDate = date;
        this.mIsSurvey = mIsSurvey;
        this.mIsSharedEvenly = mIsSharedEvenly;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey( String key ) {
        mKey = key;
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

    public String getExpenseCurrency() {
        return mExpenseCurrency;
    }

    public void setExpenseCurrency( String mExpenseCurrency ) {
        this.mExpenseCurrency = mExpenseCurrency;
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

    public Boolean getIsSurvey() {
        return mIsSurvey;
    }

    public void setIsSurvey(Boolean isSurvey) {
        mIsSurvey = isSurvey;
    }

    public Boolean getIsSharedEvenly() {
        return mIsSharedEvenly;
    }

    public void setIsSharedEvenly( Boolean isSharedEvenly ) {
        mIsSharedEvenly = isSharedEvenly;
    }

    public Boolean getIsMoneyTransfer(  ) { return mIsMoneyTransfer; }

    public void setIsMoneyTransfer(Boolean isMoneyTransfer) { mIsMoneyTransfer = isMoneyTransfer; }

    public User getCreatedBy() { return mCreatedBy; }

    public void setCreatedBy( User createdBy ) { mCreatedBy = createdBy; }

    public String getParentSharingActivityId() {
        return mParentSharingActivityId;
    }

    public void setParentSharingActivityId(String mParentSharingActivityId) {
        this.mParentSharingActivityId = mParentSharingActivityId;
    }

    public void setShares( Map<String, Share> shares ) {
        mShares = shares;
    }

    public Map<String, Share> getShares() { return mShares; }

    @Exclude
    public void clearShare() {
        mShares.clear();
    }

    @Exclude
    public void addShare( String id, Share share ) {
        if( id == null || share == null ) return;
        mShares.put( id, share );
    }

}

