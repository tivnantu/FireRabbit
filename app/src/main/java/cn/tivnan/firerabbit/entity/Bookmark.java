package cn.tivnan.firerabbit.entity;

public class Bookmark {
    private int id;
    private String name;//书签的名字
    private String url;//书签的url

    public Bookmark(int id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
