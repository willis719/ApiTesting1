package models;

public class PostResponse {
    private String meta;
    private CreatePostData postData;

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public CreatePostData getData() {
        return postData;
    }

    public void setData(CreatePostData data) {
        this.postData = data;
    }
}
