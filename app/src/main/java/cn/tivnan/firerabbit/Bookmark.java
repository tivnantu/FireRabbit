package cn.tivnan.firerabbit;

public class Bookmark {
    private int imgId;//img地址
    private String desc;//书签的名字

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
