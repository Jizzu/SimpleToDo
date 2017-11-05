package apps.jizzu.simpletodo.model;


/**
 * Class for representing a specific task.
 */
public class ModelTask {

    private String title;
    private long date;
    private int id;

    public ModelTask() {

    }

    public ModelTask(String title, long date, int id) {
        this.title = title;
        this.date = date;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
