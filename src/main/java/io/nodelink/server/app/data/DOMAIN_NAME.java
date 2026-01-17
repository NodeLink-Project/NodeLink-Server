package io.nodelink.server.app.data;

public enum DOMAIN_NAME {
    LOCALHOST("http://localhost:8080/"),
    ;

    private final String url;

    DOMAIN_NAME(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
