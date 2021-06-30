package cn.tivnan.firerabbit.entity;


public class BookmarkVO {
    private int id;
    private String title;//书签的名字
    private String url;//书签的url

    public BookmarkVO(int id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
