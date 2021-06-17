package cn.tivnan.firerabbit.entity;

public class History {
    private String id;//重复url的历史记录，只删除选中的那一条
    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public History(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }
}
