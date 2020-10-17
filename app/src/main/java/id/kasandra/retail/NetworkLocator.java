package id.kasandra.retail;

/**
 * Created by Dios on 7/4/2015.
 */

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NetworkLocator extends Service implements LocationListener {
    private final Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    String provider;
    int lac = 0;
    int mcc = 0;
    int mnc = 0;
    int cellId = 0;
    String sNetName = "", sIMSI = "", sNetType = "";
    Long time;
    protected LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 6000; // miliseconds

    public NetworkLocator(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
            if (tm != null) {
                //GsmCellLocation location_tm = (GsmCellLocation) tm.getCellLocation();
                CellLocation cellLoc = tm.getCellLocation();
                if (cellLoc instanceof GsmCellLocation) {
                    GsmCellLocation location_tm = null;
                    location_tm = (GsmCellLocation) cellLoc;
                    lac = location_tm.getLac();
                    sNetName = tm.getNetworkOperator();

                    if (sNetName != null) {
                        mcc = Integer.parseInt(sNetName.substring(0, 3));
                        mnc = Integer.parseInt(sNetName.substring(3));
                    }

                    CellLocation locations = tm.getCellLocation();
                    GsmCellLocation gsmLocation = (GsmCellLocation) locations;
                    cellId = gsmLocation.getCid();
                    //sIMSI = findDeviceID();
                    // do work
                } else if (cellLoc instanceof CdmaCellLocation) {
                    CdmaCellLocation location_tm = null;
                    location_tm = (CdmaCellLocation) cellLoc;
                    lac = location_tm.getNetworkId();

                    sNetName = tm.getNetworkOperator();

                    if (sNetName != null) {
                        mcc = Integer.parseInt(sNetName.substring(0, 3));
                        mnc = Integer.parseInt(sNetName.substring(3));
                    }

                    CellLocation locations = tm.getCellLocation();
                    CdmaCellLocation cdmaLocation = (CdmaCellLocation) locations;
                    cellId = cdmaLocation.getBaseStationId();
                    //sIMSI = findDeviceID();
                }
            }


            ///

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                this.canGetLocation = false;
            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            provider= location.getProvider();
                            time  = location.getTime();

                        }
                    }
                } else {
                    // if GPS Enabled get lat/long using GPS Services
                    //if (isGPSEnabled && !isNetworkEnabled) {
                    //if (location != null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            //accuracy = location.getAccuracy();
                            //altitude= location.getAltitude();
                            //bearing = location.getBearing();
                            provider= location.getProvider();
                            time  = location.getTime();
                            //speed = location.getSpeed();
                        }
                    }
                    //}
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public String findDeviceID() {
        String deviceID = null;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        int deviceType = tm.getPhoneType();
        switch (deviceType) {
            case (TelephonyManager.PHONE_TYPE_GSM):
                sNetType = "GSM";
                break;
            case (TelephonyManager.PHONE_TYPE_CDMA):
                sNetType = "CDMA";
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                sNetType = "GSM/CDMA";
                break;
            default:
                break;
        }
        deviceID = tm.getDeviceId();

        if (deviceID != null) {
            return deviceID;
        } else {
            return android.os.Build.SERIAL;
            //return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        //return deviceID;
    }

    //getter functions
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getLAC(){
        return lac;
    }

    public int getMCC(){
        return mcc;
    }

    public int getMNC(){
        return mnc;
    }

    public int getCELLID(){
        return cellId;
    }

    public String getIMSI(){
        return sIMSI;
    }

    public String getAddress(double lon, double lat){
        Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {

                StringBuilder strReturnedAddress = new StringBuilder();
                for(int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(addresses.get(0).getAddressLine(i)).append(" ");
                }
                return strReturnedAddress.toString();
            } else {
                return "Address not found";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Address not found";
        }

    }
    /*
        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            this.location = location;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}