package it.polito.mad.countonme;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Khatereh on 4/1/2017.
 */

public class SharingListAdapter extends RecyclerView.Adapter {
    private ArrayList<SharingListHolder> arrayList;
    private ArrayList<String> input;
    private Activity activity;
    public SharingListAdapter(ArrayList<String> list,Activity activity) {
        this.input=list;
        arrayList= new ArrayList<>();
        this.activity = activity;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_sharing_list,parent,false);
        SharingListHolder slh = new SharingListHolder(view,activity);
        return slh;
    }

    @Override
    /**
     * Here put the logit, ex. Assign text, assgin images, etc.
     */
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SharingListHolder)holder).getTextView().setText(input.get(position));
        ((SharingListHolder)holder).getLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SharingActivity)activity).showAppFragment(SharingActivity.AppFragment.DETAIL);
            }
        });
        arrayList.add((SharingListHolder) holder);

    }

    @Override
    public int getItemCount() {
        return input.size();
    }
}
