// ALL UPDATES COMPLETED
package com.example.foodtasker.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foodtasker.Fragments.CustomerHistoryFragment;
import com.example.foodtasker.Utils.CircleTransform;
import com.example.foodtasker.Fragments.OrderFragment;
import com.example.foodtasker.R;
import com.example.foodtasker.Fragments.RestaurantListFragment;
import com.example.foodtasker.Fragments.TrayFragment;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.example.foodtasker.BuildConfig;

public class CustomerMainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private SharedPreferences sharedPref;

    public String CLIENT_ID = BuildConfig.CLIENT_ID;
    public String CLIENT_SECRET = BuildConfig.CLIENT_SECRET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        final Drawable menuBurger = getResources().getDrawable(R.drawable.icon_menu_24dp);
        getSupportActionBar().setHomeAsUpIndicator(menuBurger);
        menuBurger.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        int id = menuItem.getItemId();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        if (id == R.id.nav_restaurant) {
                            transaction.replace(R.id.content_frame, new RestaurantListFragment()).commit();
                        } else if (id == R.id.nav_tray) {
                            transaction.replace(R.id.content_frame, new TrayFragment()).commit();
                        } else if (id == R.id.nav_order) {
                            transaction.replace(R.id.content_frame, new OrderFragment()).commit();
                        } else if (id == R.id.nav_customer_order_history) {
                            transaction.replace(R.id.content_frame, new CustomerHistoryFragment()).commit();
                        } else if (id == R.id.nav_logout) {
                            logoutToServer(sharedPref.getString("token", ""));
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove("token");
                            editor.apply();

                            finishAffinity();
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent);
                        }

                        return true;
                    }
                });

        Intent intent = getIntent();
        String screen = intent.getStringExtra("screen");

        if (Objects.equals(screen, "tray")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new TrayFragment()).commit();
        } else if (Objects.equals(screen, "order")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new OrderFragment()).commit();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new RestaurantListFragment()).commit();
        }

        // Get the User's info
        sharedPref = getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);

        View header = navigationView.getHeaderView(0);
        ImageView customer_avatar = (ImageView) header.findViewById(R.id.customer_avatar);
        TextView customer_name = (TextView) header.findViewById(R.id.customer_name);

        customer_name.setText(sharedPref.getString("name", ""));
        Picasso.with(this).load(sharedPref.getString("avatar", "")).transform(new CircleTransform()).into(customer_avatar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    private void logoutToServer(final String token) {
        String url = getString(R.string.API_URL) + "/social/revoke-token";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // Execute code
                        Log.d("RESPONSE FROM SERVER", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("client_id", CLIENT_ID);
                params.put("client_secret", CLIENT_SECRET);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }
}