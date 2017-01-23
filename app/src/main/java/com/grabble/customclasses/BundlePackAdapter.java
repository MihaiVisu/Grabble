package com.grabble.customclasses;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grabble.R;

import java.util.ArrayList;

public class BundlePackAdapter extends RecyclerView.Adapter<BundlePackAdapter.BundleViewHolder>{

    private ArrayList<BundleOffer> bundleOffers;

    public static class BundleViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView bundleQuantity;
        TextView bundlePrice;
        ImageView bundleImg;

        BundleViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            bundleQuantity = (TextView)itemView.findViewById(R.id.bundle_quantity);
            bundlePrice = (TextView)itemView.findViewById(R.id.bundle_price);
            bundleImg = (ImageView)itemView.findViewById(R.id.bundle_img);
        }
    }

    public BundlePackAdapter(ArrayList<BundleOffer> bundleOffers) {
        this.bundleOffers = bundleOffers;
    }

    @Override
    public BundleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv, viewGroup, false);
        return new BundleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BundleViewHolder holder, int position) {

        int tokenPrice = bundleOffers.get(position).getTokenPrice();
        int gemPrice = bundleOffers.get(position).getGemPrice();
        double cashPrice = bundleOffers.get(position).getCashPrice();

        holder.bundleQuantity.setText(String.valueOf(
                "x" + bundleOffers.get(position).getQuantity()
        ));
        holder.bundleImg.setImageResource(bundleOffers.get(position).getImageId());

        if (tokenPrice != 0 && gemPrice != 0) {
            holder.bundlePrice.setText(String.valueOf(
                    "x" + tokenPrice + " tokens " +
                            " or  x" + gemPrice + " gems"
            ));
        }
        else if (tokenPrice == 0 && gemPrice != 0) {
            holder.bundlePrice.setText(String.valueOf(
                    "x" + gemPrice + " gems"
            ));
        }
        else {
            holder.bundlePrice.setText(String.valueOf(
                    "£" + cashPrice
            ));
        }

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: DO PAYMENTS STUFF
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return bundleOffers.size();
    }

}

