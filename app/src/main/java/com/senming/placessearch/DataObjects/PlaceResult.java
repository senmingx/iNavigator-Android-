package com.senming.placessearch.DataObjects;

import java.io.Serializable;
import java.util.Objects;

public class PlaceResult implements Serializable {

    private String name;
    private String address;
    private String iconUrl;
    private String place_id;
    private String lat;
    private String lng;

    public PlaceResult() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getPlaceId() {
        return place_id;
    }

    public void setPlaceId(String place_id) {
        this.place_id = place_id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceResult place = (PlaceResult) o;
        return Objects.equals(place_id, place.place_id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(place_id);
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", place_id='" + place_id + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
