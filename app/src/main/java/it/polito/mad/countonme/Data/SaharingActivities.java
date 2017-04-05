package it.polito.mad.countonme.Data;


import android.icu.util.Currency;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Khatereh on 4/5/2017.
 */

public abstract class SaharingActivities
{
    private String name;
    private String description;
    private Currency currency;
    private String image;
    private String link;


    //Keys for storing and retrieving the data in any dictionary
    public static final String NAME = "NAME";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String CURRENCY = "CURRENCY";
    public static final String IMAGE = "IMAGE";
    public static final String LINK = "LINK";


    //Default values
    //public static final Currency DEFAULT_CURRENCY = Currency.getInstance()


    public SaharingActivities(String name, String description,Currency currency , String image,String link)
    {
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.image = image;
        this.link = link;
    }

    /**
     * Default constructor
     */
    public SaharingActivities()
    {
        this.name = "";
        this.description = "";
        //this.currency = new Currency();
        this.image = "";
        this.link = "";
    }

    public SaharingActivities(String SaharingActivity)
    {
        try
        {
            JSONObject oj = new JSONObject(SaharingActivity);
            this.name = new String(String.valueOf(oj.get("name")));
            this.description = new String(String.valueOf(oj.get("description")));
            //this.currency = new Currency(String.valueOf(oj.get("currency")));
            this.image = new String(String.valueOf(oj.get("image")));
            this.link = new String(String.valueOf(oj.get("link")));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public String toString()
    {
        JSONObject oj = new JSONObject();
        try
        {
            oj.put("name",this.name);
            oj.put("description",this.description);
            oj.put("currency",this.currency);
            oj.put("image",this.image);
            oj.put("link",this.link);

            return oj.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDes(String description) {
        this.description = description;
    }

    public String getDes() {
        return this.description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getLink()
    {
       return this.link;
    }

    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    public Currency getCurrency()
    {
        return this.currency;
    }
}

