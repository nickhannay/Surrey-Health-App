package com.example.poject_01.model;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class RestaurantCluster implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private BitmapDescriptor image;

    public RestaurantCluster(LatLng position, String title, String snippet, BitmapDescriptor image) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.image = image;
    }

    public BitmapDescriptor getImage() {
        return image;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}

