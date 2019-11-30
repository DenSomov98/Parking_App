package sample.updaters;

import javafx.scene.control.Label;
import java.util.Calendar;

public class UpdaterTime implements Runnable{
    private Calendar calendar;
    private Label clock;

    public UpdaterTime(Calendar calendar, Label clock){
        this.calendar = calendar;
        this.clock = clock;
    }
    @Override
    public void run() {
        if (calendar.get(Calendar.MINUTE) < 10 && calendar.get(Calendar.SECOND) < 10) {
            clock.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":0" + calendar.get(Calendar.MINUTE) + ":0" + calendar.get(Calendar.SECOND));
        } else if (calendar.get(Calendar.MINUTE) < 10 && calendar.get(Calendar.SECOND) >= 10) {
            clock.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":0" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
        } else if (calendar.get(Calendar.MINUTE) > 10 && calendar.get(Calendar.SECOND) < 10) {
            clock.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":0" + calendar.get(Calendar.SECOND));
        } else {
            clock.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
        }
    }
}
