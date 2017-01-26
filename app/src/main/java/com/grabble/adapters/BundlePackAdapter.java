package com.grabble.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.PaymentRequest;
import com.grabble.NavActivity;
import com.grabble.R;
import com.grabble.customclasses.BundleOffer;
import com.grabble.customclasses.GameState;

import java.util.ArrayList;

public class BundlePackAdapter extends RecyclerView.Adapter<BundlePackAdapter.BundleViewHolder>{

    private ArrayList<BundleOffer> bundleOffers;
    private Toast toast;
    private Context mContext;

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

    public BundlePackAdapter(ArrayList<BundleOffer> bundleOffers, Toast toast, Context context) {
        this.bundleOffers = bundleOffers;
        this.toast = toast;
        this.mContext = context;
    }

    @Override
    public BundleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bundle_pack_cv, viewGroup, false);
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
                else {
                    final int REQUEST_CODE = 1;
                    final String TOKEN = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJlNjEzMDIyM2NlMGYyYjhjYWRmM2ZiMWM0NWQ0MzI0YTU2OGJmMjAzZDFkMTRlYjBkOTkwZjIzYWQ2ODNhZmM2fGNyZWF0ZWRfYXQ9MjAxNy0wMS0yNFQwMjozNTo1NC41MDU3MzY4MzkrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=";

                    PaymentRequest paymentRequest = new PaymentRequest()
                            .clientToken(TOKEN);
                    ((Activity) mContext).startActivityForResult(paymentRequest.getIntent(v.getContext()
                            .getApplicationContext()), REQUEST_CODE);
                    TextView amount = (TextView)((Activity) mContext)
                            .findViewById(R.id.bundle_quantity);
                    int am = Integer.parseInt(amount.getText().toString());
                    state.setGems(state.getGems() + am);
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
                                if (state.getTokens() < currentBundleOffer.getTokenPrice()) {
                                    toast.setText("Not enough tokens to buy the selected pack.");
                                    toast.show();
                                }
                                else {
                                    state.buyBoosters(currentBundleOffer.getQuantity(),
                                            "los", "cash", currentBundleOffer.getTokenPrice());
                                    NavActivity.updateContent(state);
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
                                    NavActivity.updateContent(state);
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
                            NavActivity.updateContent(state);
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

