package com.grabble;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.widget.Toast;

import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.grabble.customclasses.BundleOffer;
import com.grabble.adapters.BundlePackAdapter;
import com.grabble.customclasses.GameState;

import java.util.ArrayList;

/**
 * Shop activity class
 */
public class ShopActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // get the state variable
        state = (GameState) getApplicationContext();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // get the tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    // handle the onactivity result when getting a result from Braintree server
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(
                        BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                // if we made a payment request with a certain amount of money, then
                // add the specified amount
                if (state.getPaymentRequest() != 0 && state.getPaymentPrice() != 0) {
                    state.setGems(state.getGems() + state.getPaymentRequest());
                    // update nav activity content
                    NavActivity.updateContent(state);
                    // reset payment request to 0 in state
                    state.setPaymentRequest(0, 0);
                }
            }
        }
    }

    @Override
    public void onStop() {
        state.activityStopped();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a base shop fragment
     * which changes its values based on the section we are in
     */
    public static class BaseShopFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        // initialize the recycler view and the bundle offers list
        private RecyclerView rv;
        private static ArrayList<BundleOffer> bundleOffers;

        // method to initialize bundle offers in terms of the section number
        // we are in
        public static void initializeBundleOffers(int sectionNum) {
            bundleOffers = new ArrayList<>();
            switch (sectionNum) {
                // if we are in section one
                case 0:
                    bundleOffers.add(new BundleOffer(1, R.drawable.los, 200, 5, 0));
                    bundleOffers.add(new BundleOffer(2, R.drawable.los, 390, 9, 0));
                    bundleOffers.add(new BundleOffer(4, R.drawable.los, 770, 17, 0));
                    bundleOffers.add(new BundleOffer(8, R.drawable.los, 1530, 33, 0));
                    break;
                // if we are in section 2
                case 1:
                    bundleOffers.add(new BundleOffer(1, R.drawable.helper, 0, 8, 0));
                    bundleOffers.add(new BundleOffer(2, R.drawable.helper, 0, 15, 0));
                    bundleOffers.add(new BundleOffer(4, R.drawable.helper, 0, 29, 0));
                    bundleOffers.add(new BundleOffer(8, R.drawable.helper, 0, 57, 0));
                    break;
                // if we are in section 3
                case 2:
                    bundleOffers.add(new BundleOffer(5, R.drawable.gem, 0, 0, 0.99));
                    bundleOffers.add(new BundleOffer(10, R.drawable.gem, 0, 0, 1.89));
                    bundleOffers.add(new BundleOffer(20, R.drawable.gem, 0, 0, 2.99));
                    bundleOffers.add(new BundleOffer(40, R.drawable.gem, 0, 0, 4.99));
                    break;
                default:
                    break;
            }
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static BaseShopFragment newInstance(int sectionNumber) {
            BaseShopFragment fragment = new BaseShopFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment; // return fragment in terms of section number
        }



        // what happens when fragment is created
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_shop, container, false);

            // initialize the recycler view
            rv = (RecyclerView) rootView.findViewById(R.id.rv);

            rv.setHasFixedSize(true); // fixed size to improve performance
            LinearLayoutManager llm = new LinearLayoutManager(getActivity()
                    .getApplicationContext());
            rv.setLayoutManager(llm);

            // initialize the bundle offers before setting the adapter
            initializeBundleOffers(getArguments().getInt(ARG_SECTION_NUMBER)-1);

            // set adapter with requested parameters
            rv.setAdapter(new BundlePackAdapter(bundleOffers,
                    Toast.makeText(getActivity().getApplicationContext(), "message",
                            Toast.LENGTH_SHORT), getActivity()));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // function which gets the right fragment based on the section number / position
        // given as parameter
        @Override
        public Fragment getItem(int position) {
            return BaseShopFragment.newInstance(position + 1);
        }

        // get total number of fragments
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        // get titles of fragments
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LOS Boosters";
                case 1:
                    return "Word Helpers";
                case 2:
                    return "Gems";
            }
            return null;
        }
    }
}
