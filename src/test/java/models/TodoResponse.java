package models;

public class TodoResponse {

    private String meta;
    private CreateTodoData data;

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public CreateTodoData getData() {
        return data;
    }

    public void setData(CreateTodoData data) {
        this.data = data;
    }
}
