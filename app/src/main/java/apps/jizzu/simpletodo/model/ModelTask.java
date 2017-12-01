package apps.jizzu.simpletodo.model;

/**
 * Class for representing a specific task.
 */
public class ModelTask {

    private long id;
    private String title;
    private long date;
    private int position;

    public ModelTask() {

    }

    public ModelTask(long id, String title, long date, int position) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.position = position;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
