package com.example.poject_01.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class RestaurantClusterRenderer extends DefaultClusterRenderer<RestaurantCluster> {
   private LatLng coordinates;

    public RestaurantClusterRenderer(Context context, GoogleMap map, ClusterManager<RestaurantCluster> clusterManager, LatLng coordinates) {
        super(context, map, clusterManager);
        clusterManager.setRenderer(this);
        this.coordinates = coordinates;
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull RestaurantCluster item, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        if(item.getImage() != null) {
            markerOptions.icon(item.getImage());
            markerOptions.snippet(item.getSnippet());
            markerOptions.title(item.getTitle());
        }
        markerOptions.visible(true);
    }

    @Override
    protected void onClusterItemRendered(@NonNull RestaurantCluster clusterItem, @NonNull Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        if(clusterItem.getPosition().equals(coordinates))
            marker.showInfoWindow();
    }
}
