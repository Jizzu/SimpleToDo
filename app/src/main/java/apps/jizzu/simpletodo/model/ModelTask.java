package apps.jizzu.simpletodo.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for representing a specific task.
 */
public class ModelTask implements Serializable {

    private long id;
    private String title;
    private long date;
    private int position;
    private long timeStamp;

    public ModelTask() {
        this.timeStamp = new Date().getTime();
    }

    public ModelTask(long id, String title, long date, int position, long timeStamp) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.position = position;
        this.timeStamp = timeStamp;
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
