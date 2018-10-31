package ch.hsr.appquest.coincollector;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CoinManager {

    private static final String kCoinRegionsKey = "CoinRegions";

    private List<CoinRegion> coinRegions = new ArrayList<>();

    private Context context;
    private SharedPreferences preferences;

    CoinManager(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        load();
    }

    public List<CoinRegion> getCoinRegions() { return coinRegions; }

    public List<Coin> getCoins() { return getFlattenedCoins(coinRegions); }

    private void load() {
        if (!(preferences.getString(kCoinRegionsKey, null) == null)) {
            coinRegions = loadCoins();
        } else {
            coinRegions = loadInitialCoins();
        }
    }

    private List<CoinRegion> loadCoins() {
        try {
            return fromJson(new JSONArray(preferences.getString(kCoinRegionsKey, null)));
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<CoinRegion> loadInitialCoins() {
        try {
            return fromJson(new JSONArray(readJsonFile()));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void save() {
        SharedPreferences.Editor editor = preferences.edit();
        try {
            if (coinRegions != null) {
                editor.putString(kCoinRegionsKey, toJson(coinRegions).toString());
                editor.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        coinRegions = loadInitialCoins();
        save();
    }

    public JSONObject logJson(List<CoinRegion> coinRegions) throws JSONException {
        JSONObject logEntry = new JSONObject();
        logEntry.put("task", "Muenzensammler");
        JSONArray jCoins = new JSONArray();
        for (Coin coin : getFlattenedCoins(coinRegions)) {
            JSONObject jCoin = new JSONObject();
            jCoin.put("major", coin.getMajor());
            jCoin.put("minor", coin.getMinor());
            jCoins.put(jCoin);
        }
        logEntry.put("coins", jCoins);
        return logEntry;
    }

    private static List<CoinRegion> fromJson(JSONArray jCoinRegions) throws JSONException {
        List<CoinRegion> coinRegionList = new ArrayList<>();
        for (int i = 0; i < jCoinRegions.length(); i++) {
            JSONObject jCoinRegion = jCoinRegions.getJSONObject(i);
            List<Coin> coinList = new ArrayList<>();
            JSONArray jCoins = jCoinRegion.getJSONArray("coinList");
            for (int j = 0; j < jCoins.length(); j++) {
                JSONObject jCoin = jCoins.getJSONObject(j);
                Coin coin = new Coin(jCoin.getInt("major"));
                coin.setMinor(jCoin.getInt("minor"));
                coinList.add(coin);
            }
            CoinRegion coinRegion = new CoinRegion(
                    CoinRegion.Identifier.valueOf(jCoinRegion.getString("identifier")),
                    coinList
            );
            coinRegionList.add(coinRegion);
        }
        return coinRegionList;
    }

    private static JSONArray toJson(List<CoinRegion> coinRegions) throws JSONException {
        JSONArray jCoinRegions = new JSONArray();
        for (int i = 0; i < coinRegions.size(); i++) {
            CoinRegion coinRegion = coinRegions.get(i);
            JSONObject jCoinRegion = new JSONObject();
            jCoinRegion.put("identifier", coinRegion.getIdentifier().toString());
            JSONArray jCoins = new JSONArray();
            for(Coin coin : coinRegion.getCoinList()) {
                JSONObject jCoin = new JSONObject();
                jCoin.put("major", coin.getMajor());
                jCoin.put("minor", coin.getMinor());
                jCoins.put(jCoin);
            }
            jCoinRegion.put("coinList", jCoins);
            jCoinRegions.put(jCoinRegion);
        }
        return jCoinRegions;
    }

    private String readJsonFile() throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.initial_coins);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, n);
        }
        is.close();
        return writer.toString();
    }

    private List<Coin> getFlattenedCoins(List<CoinRegion> coinRegions) {
        return coinRegions
                    .stream()
                    .flatMap((CoinRegion t) -> t.getCoinList().stream())
                    .collect(Collectors.toList());
    }

}
