package cn.tivnan.firerabbit.entity;

public class Bookmark {
    private String name;//书签的名字
    private String url;//书签的url

    public Bookmark(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
