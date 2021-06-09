package cn.tivnan.firerabbit;

public class Bookmark {
    private int imgId;
    private String desc;

    public Bookmark(int imgId, String desc) {
        this.imgId = imgId;
        this.desc = desc;
    }

    public int getImgId() {
        return imgId;
    }

    public String getDesc() {
        return desc;
    }
}
