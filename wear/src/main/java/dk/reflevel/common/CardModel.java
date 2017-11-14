package dk.reflevel.common;

/**
 * Created by lenovo on 4/26/2017.
 */

public class CardModel {

    public String getCardOptionName() {
        return cardOptionName;
    }

    public void setCardOptionName(String cardOptionName) {
        this.cardOptionName = cardOptionName;
    }

    private String cardOptionName;

    private String name;
    private String cardColor;
    private boolean cardSelected;
    private String timer;
    private String extraTimer;
    private String currentTimer;
    private String hlafTime;
    private String hlafName;

    public boolean isAddeddata() {
        return isAddeddata;
    }

    public void setAddeddata(boolean addeddata) {
        isAddeddata = addeddata;
    }

    private boolean isAddeddata;




    public String getHlafName() {
        return hlafName;
    }

    public void setHlafName(String hlafName) {
        this.hlafName = hlafName;
    }

    public String getHlafTime() {
        return hlafTime;
    }

    public void setHlafTime(String hlafTime) {
        this.hlafTime = hlafTime;
    }

    private boolean isTimerWork = false;

    public boolean isTimerWork() {
        return isTimerWork;
    }

    public void setTimerWork(boolean timerWork) {
        isTimerWork = timerWork;
    }

    public boolean isCardSelected() {
        return cardSelected;
    }

    public void setCardSelected(boolean cardSelected) {
        this.cardSelected = cardSelected;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardColor() {
        return cardColor;
    }

    public void setCardColor(String cardColor) {
        this.cardColor = cardColor;
    }

    public String getExtraTimer() {
        return extraTimer;
    }

    public void setExtraTimer(String extraTimer) {
        this.extraTimer = extraTimer;
    }

    public String getCurrentTimer() {
        return currentTimer;
    }

    public void setCurrentTimer(String currentTimer) {
        this.currentTimer = currentTimer;
    }
}