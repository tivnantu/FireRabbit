package cn.tivnan.firerabbit.entity;

public class History {
    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public History(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
