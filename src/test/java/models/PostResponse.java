package models;

public class PostResponse {

    private String meta;
    private CreatePostData data;

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public CreatePostData getData() {
        return data;
    }

    public void setData(CreatePostData data) {
        this.data = data;
    }
}
