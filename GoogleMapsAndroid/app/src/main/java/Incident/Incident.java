package Incident;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Incident {

    public String calldatetime;
    public String callnumber;
    public String description;
    public String district;
    public String incidentlocation;
    public String location;
    public String location_address;
    public String location_city;
    public String location_state;
    public String location_zip;
    public String priority;

    public String getCallnumber() {
        return callnumber;
    }

    public void setCallnumber(String callnumber) {
        this.callnumber = callnumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getIncidentlocation() {
        return incidentlocation;
    }

    public void setIncidentlocation(String incidentlocation) {
        this.incidentlocation = incidentlocation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation_address() {
        return location_address;
    }

    public void setLocation_address(String location_address) {
        this.location_address = location_address;
    }

    public String getLocation_city() {
        return location_city;
    }

    public void setLocation_city(String location_city) {
        this.location_city = location_city;
    }

    public String getLocation_state() {
        return location_state;
    }

    public void setLocation_state(String location_state) {
        this.location_state = location_state;
    }

    public String getLocation_zip() {
        return location_zip;
    }

    public void setLocation_zip(String location_zip) {
        this.location_zip = location_zip;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRecordid() {
        return recordid;
    }

    public void setRecordid(String recordid) {
        this.recordid = recordid;
    }

    public String recordid;

    public String getCalldatetime() {
        return calldatetime;
    }

    public void setCalldatetime(String calldatetime) {
        this.calldatetime = calldatetime;
    }

    public Incident(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, String k, String l) {
        calldatetime = a;
        callnumber = b;
        description = c;
        district = d;
        incidentlocation = e;
        location = f;
        location_address = g;
        location_city = h;
        location_state = i;
        location_zip = j;
        priority = k;
        recordid = l;
    }

    public void fillLocation() {
        if (location.equals("NODATA")) {
            if (!location_address.equals("NODATA")) {
                URL url;
                String key = "AIzaSyDGmABFEBhgLGOeGLrB9wIo7czDdBhI2GI";
                String link = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location_address + location_city + location_state + location_zip + "&key=" + key;
                try {
                    url = new URL(link);
                    URLConnection con = url.openConnection();
                    InputStream is = con.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (line.contains("\"location\"")) {
                            String lat = br.readLine();
                            String lng = br.readLine();

                            lat = lat.replace("\"", "");
                            lat = lat.replace("lat", "");
                            lat = lat.replace("lng", "");
                            lat = lat.replace(":", "");
                            lat = lat.replace(",", "");
                            lat = lat.replace(" ", "");
                            lat = lat.trim();

                            lng = lng.replace("\"", "");
                            lng = lng.replace("lat", "");
                            lng = lng.replace("lng", "");
                            lng = lng.replace(":", "");
                            lng = lng.replace(",", "");
                            lng = lng.replace(" ", "");
                            lng = lng.trim();
                            String dest = lng + "," + lat;
                            //double newLat = Double.parseDouble(lat);
                            //double newLong = Double.parseDouble(lng);
                            //LatLng dest = new LatLng(newLat, newLong);
                            //System.out.println(dest);
                            location = dest;
                        }
                    }
                } catch (MalformedURLException e) {
                    System.err.println("Error reading from link");
                } catch (IOException e) {
                    System.err.println("Not data found");
                }
            }
        }
    }
}