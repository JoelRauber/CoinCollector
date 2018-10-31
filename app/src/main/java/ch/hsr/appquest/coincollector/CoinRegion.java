package ch.hsr.appquest.coincollector;

import java.util.List;

public class CoinRegion {

    private Identifier identifier;
    private List<Coin> coinList;

    CoinRegion(Identifier identifier, List<Coin> coinList) {
        this.identifier = identifier;
        this.coinList = coinList;
    }

    public Identifier getIdentifier() { return identifier; }

    public List<Coin> getCoinList() { return coinList; }

    public enum Identifier {
        lakeside,
        island,
        cafeteria,
        bicyclestand,
        researchbuilding
    }

    public String getImageName() {
        switch (identifier) {
            case lakeside: return "lakeside_coin";
            case island: return "island_coin";
            case cafeteria: return "cafeteria_coin";
            case bicyclestand: return "bicyclestand_coin";
            case researchbuilding: return "researchbuilding_coin";
            default: return "";
        }
    }

    public String getRegionName() {
        // TODO: Gib anhand des Identifiers den richtigen Regions-Namen zurück. Dieser wird dann im passenden Section-Header der SectionedRecyclerView angezeigt.
        return String.valueOf(getIdentifier());
    }

}
