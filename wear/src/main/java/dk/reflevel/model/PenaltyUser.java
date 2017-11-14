package dk.reflevel.model;

/**
 * Created by Tecocraft-Hiral on 30/09/2017.
 */

public class PenaltyUser {

    private String title;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
