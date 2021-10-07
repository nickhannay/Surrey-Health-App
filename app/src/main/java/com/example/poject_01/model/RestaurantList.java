 package com.example.poject_01.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Iterator;
import java.util.List;

 /**
  * This class holds each restaurant, sorted by (#-a-z).
  * The list is sorted every time a restaurant is added.
  */
 public class RestaurantList implements Iterable<Restaurant>{
    private List<Restaurant> restaurants ;
    private int restaurantListSize ;
    private static RestaurantList instance;

     public RestaurantList(List<Restaurant> restaurants) {
         this.restaurants = restaurants;
         this.restaurantListSize = 0;
     }

     public RestaurantList() {
         this.restaurants = new ArrayList<>();
         this.restaurantListSize = 0;
     }

     public void addRestaurant(Restaurant r) {
        restaurants.add(r);
        restaurantListSize++;
        Collections.sort(restaurants);
    }

   public void clear(){
        restaurants.clear();
        restaurantListSize = 0;
    }


     public int getRestaurantListSize() {
         return restaurantListSize;
     }

     public List<Restaurant> getList(){
        return(this.restaurants);
    }


    public Restaurant getRestaurantIndex(int i){
        return restaurants.get(i);
    }


    public static RestaurantList getInstance(){
        if (instance == null){
            instance = new RestaurantList();
        }
        return instance;
    }


    @Override
    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }

 }

