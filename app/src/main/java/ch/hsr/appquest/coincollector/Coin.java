package ch.hsr.appquest.coincollector;

public class Coin {

    private int major;
    private int minor = 0;

    public Coin(int major) { this.major = major; }

    public int getMajor() { return major; }

    public int getMinor() { return minor; }

    public void setMinor(int minor) { this.minor = minor; }

}
