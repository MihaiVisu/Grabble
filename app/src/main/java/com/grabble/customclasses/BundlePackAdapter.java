package com.grabble.customclasses;


import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grabble.R;

import java.util.ArrayList;

public class BundlePackAdapter extends RecyclerView.Adapter<BundlePackAdapter.BundleViewHolder>{

    private ArrayList<BundleOffer> bundleOffers;
    private Toast toast;

    public static class BundleViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView bundleQuantity;
        TextView bundlePrice;
        ImageView bundleImg;
        GameState state;

        BundleViewHolder(View itemView, GameState state) {
            super(itemView);
            this.state = state;
            cv = (CardView)itemView.findViewById(R.id.cv);
            bundleQuantity = (TextView)itemView.findViewById(R.id.bundle_quantity);
            bundlePrice = (TextView)itemView.findViewById(R.id.bundle_price);
            bundleImg = (ImageView)itemView.findViewById(R.id.bundle_img);
        }
    }

    public BundlePackAdapter(ArrayList<BundleOffer> bundleOffers, Toast toast) {
        this.bundleOffers = bundleOffers;
        this.toast = toast;
    }

    @Override
    public BundleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv, viewGroup, false);
        return new BundleViewHolder(v, (GameState) v.getContext().getApplicationContext());
    }

    @Override
    public void onBindViewHolder(BundleViewHolder holder, int position) {

        final BundleOffer currentBundleOffer = bundleOffers.get(position);
        final GameState state = holder.state;

        int tokenPrice = currentBundleOffer.getTokenPrice();
        int gemPrice = currentBundleOffer.getGemPrice();
        double cashPrice = currentBundleOffer.getCashPrice();


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
                if (currentBundleOffer.getTokenPrice() != 0 &&
                        currentBundleOffer.getGemPrice() != 0) {
                    createChoiceAlert(v, state, currentBundleOffer);
                }
                else if (currentBundleOffer.getTokenPrice() == 0 &&
                        currentBundleOffer.getGemPrice() != 0) {
                    createConfirmationAlert(v, state, currentBundleOffer);
                }
            }
        });
    }

    // create a choice alert so that user has to choose the payment method
    // for los boosters
    private void createChoiceAlert(View v, final GameState state,
                                  final BundleOffer currentBundleOffer) {
        new AlertDialog.Builder(v.getContext())
                .setTitle("Choose Currency")
                .setMessage("Buy Boosters with:")
                .setNeutralButton("x" + currentBundleOffer.getTokenPrice() + " tokens",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (state.getCash() < currentBundleOffer.getTokenPrice()) {
                                    toast.setText("Not enough tokens to buy the selected pack.");
                                    toast.show();
                                }
                                else {
                                    state.buyBoosters(currentBundleOffer.getQuantity(),
                                            "los", "cash", currentBundleOffer.getTokenPrice());
                                }
                            }
                        })
                .setNegativeButton("x" + currentBundleOffer.getGemPrice() + " gems",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (state.getGems() < currentBundleOffer.getGemPrice()) {
                                    toast.setText("Not enough gems to buy the selected pack.");
                                    toast.show();
                                }
                                else {
                                    state.buyBoosters(currentBundleOffer.getQuantity(),
                                            "los", "gems", currentBundleOffer.getGemPrice());
                                }
                            }
                        })
                .setIcon(R.drawable.grabble_logo_main).show();

    }

    // create a confirmation alert so that user has to confirm before
    // purchasing a bundle pack
    private void createConfirmationAlert(View v, final GameState state,
                                         final BundleOffer currentBundleOffer) {
        new AlertDialog.Builder(v.getContext())
                .setTitle("Perform Purchase")
                .setMessage("Are you sure you want to perform this purchase?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (state.getGems() < currentBundleOffer.getGemPrice()) {
                            toast.setText("Not enough gems to buy the selected pack.");
                            toast.show();
                        }
                        else {
                            state.buyBoosters(currentBundleOffer.getQuantity(),
                                    "helper", "gems", currentBundleOffer.getGemPrice());
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.grabble_logo_main).show();
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

