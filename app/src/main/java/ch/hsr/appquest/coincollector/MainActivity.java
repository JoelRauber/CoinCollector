package ch.hsr.appquest.coincollector;

import android.Manifest;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String appUuid = "52495334-5696-4DAE-BEC7-98D44A30FFDA";
    private static final String beaconLayout = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String TAG = "MyActivity";
    private static final int REQUEST_COARSE_LOCATION = 1;

    private BeaconManager beaconManager;
    private CoinManager coinManager;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private NotificationUtil notificationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBeaconManager();
        setupCoinManager();
        setupSectionedRecyclerView();
        setupNotificationUtil();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                beaconManager.bind(this);
            }
        }
    }

    private void setupBeaconManager() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(beaconLayout));
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        } else {
            beaconManager.bind(this);
        }

        beaconManager.bind(this);

        Beacon beacon = new Beacon.Builder()
                .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
                .setId2("1")
                .setId3("2")
                .setManufacturer(0x0118) // Radius Networks.  Change this for other beacon layouts
                .setTxPower(-59)
                .setDataFields(Arrays.asList(new Long[] {0l})) // Remove this for beacon layouts without d: fields
                .build();

        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout(beaconLayout);
        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {

            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Advertisement start failed with code: "+ errorCode);
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i(TAG, "Advertisement start succeeded.");
            }
        });

    }

    private void setupCoinManager() { coinManager = new CoinManager(this); }

    private void setupSectionedRecyclerView() {
        sectionAdapter = new SectionedRecyclerViewAdapter();
        addCoinRegionSections();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (sectionAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(sectionAdapter);
    }

    private void setupNotificationUtil() {
        notificationUtil = new NotificationUtil(this);
        notificationUtil.createNotificationChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add("Reset");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menuItem.setOnMenuItemClickListener(item -> {
            sectionAdapter.removeAllSections();
            coinManager.reset();
            addCoinRegionSections();
            sectionAdapter.notifyDataSetChanged();
            return true;
        });
        menuItem = menu.add("Log");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menuItem.setOnMenuItemClickListener(item -> {
            onLogAction();
            return false;
        });
        return true;
    }

    public CoinManager sendActiveManager(){
        return this.coinManager;
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "TEST");
        beaconManager.addRangeNotifier((beacons, region) -> {
            if (beacons.size() > 0) {
                // TODO: Hier musst du über alle Beacons iterieren und diese collecten

                for(Beacon beacon : beacons){
                    collectBeacon(beacon);
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region(appUuid, null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void onLogAction() {
        // TODO: Vorbereitung und Absenden des Logbuch-Eintrags gemäss der vorgegebenen Formatierung. Verwende dazu die logJson() Methode der Klasse CoinManager.
    }

    private void addCoinRegionSections() {
        for (CoinRegion coinRegion : coinManager.getCoinRegions()) {
            sectionAdapter.addSection(new CoinRegionSection(coinRegion, this, this.coinManager));
        }
    }

    private void collectBeacon(Beacon beacon) {
        int major = beacon.getId2().toInt();
        int minor = beacon.getId3().toInt();
        updateCoin(major, minor);
    }

    private void updateCoin(int major, int minor) {

        // TODO: Setze die Minor Nummer der gefundenen Münze und speichere das Ergebnis. Danach muss man auch noch die SectionedRecyclerView neu laden.
        // TODO: --> coinManager verwenden und über alle Coins, welche dieser zurückgibt, iterieren
        // TODO: --> prüfen, ob coin.major == major und minor == 0
        // TODO: --> coin minor setzen
        // TODO: Änderung saven
        // TODO: Den sectionAdapter notifyen
        // TODO (optional): Zeige dem User eine lokale Notification. Dazu kannst Du die Klasse NotificationUtil verwenden.

        List<Coin> coins = coinManager.getCoins();

        for (Coin coin: coins){
            if(coin.getMajor() == major){
                Log.i(TAG, "Minor = " + minor);
                coin.setMinor(minor);
                coinManager.save();
            }
        }
        sendActiveManager();
        sectionAdapter.notify();
    }

}
