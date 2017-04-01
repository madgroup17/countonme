package it.polito.mad.countonme;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Khatereh on 4/1/2017.
 */

public class SharingListHolder extends RecyclerView.ViewHolder {
    private TextView textView;
    private LinearLayout linearLayout;
    private Activity activity;
    public SharingListHolder(View itemView,Activity activity) {
        super(itemView);
        textView= (TextView) itemView.findViewById(R.id.title_tv);
        linearLayout= (LinearLayout) itemView.findViewById(R.id.my_layout);
        this.activity= activity;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
