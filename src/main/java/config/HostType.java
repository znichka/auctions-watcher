package config;

public enum HostType {
    MESHOK("meshok.net"),
    AYBY("ay.by"),
    KUFAR("kufar.by"),
    AVITO("avito.ru"),
    EBAYCOM("ebay.com"),
    EBAYDE("ebay.de"),
    ETSY("etsy.com"),
    UNKNOWN("unknown");

    public final String host;

    HostType(String host) {
        this.host = host;
    }

    public static HostType getHostType(String url) {
        for (HostType e : HostType.values())
            if (url.contains(e.host)) return e;
        return UNKNOWN;
    }
}
