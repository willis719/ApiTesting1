package models;

public class CommentResponse {

    private String meta;
    private CreateCommentData data;

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public CreateCommentData getData() {
        return data;
    }

    public void setData(CreateCommentData data) {
        this.data = data;
    }

}
