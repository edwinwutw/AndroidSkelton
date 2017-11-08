package com.edwin.androidskelton.ui;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.edwin.androidskelton.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    @BindView(R.id.image)
    ImageView placePicture;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.place_detail)
    TextView placeDetail;
    @BindView(R.id.place_location)
    TextView placeLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //// butterknife
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        // Set title of Detail page
        // collapsingToolbar.setTitle(getString(R.string.item_title));

        int postion = getIntent().getIntExtra(EXTRA_POSITION, 0);
        Resources resources = getResources();
        String[] places = resources.getStringArray(R.array.places);
        collapsingToolbar.setTitle(places[postion % places.length]);

        String[] placeDetails = resources.getStringArray(R.array.place_details);
        placeDetail.setText(placeDetails[postion % placeDetails.length]);

        String[] placeLocations = resources.getStringArray(R.array.place_locations);
        placeLocation.setText(placeLocations[postion % placeLocations.length]);

        TypedArray placePictures = resources.obtainTypedArray(R.array.places_picture);
        placePicture.setImageDrawable(placePictures.getDrawable(postion % placePictures.length()));

        placePictures.recycle();
    }
}
