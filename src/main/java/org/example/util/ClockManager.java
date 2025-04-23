package org.example.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ClockManager is responsible for managing and displaying the current time on a given Label.
 * It updates the time every second.
 */
public class ClockManager {

    /**
     * Starts a clock that updates the given Label every second with the current date and time.
     * The time is formatted in the pattern "yyyy-MM-dd HH:mm:ss".
     *
     * @param clockLabel the Label to display the current time
     */
    public static void startClock(Label clockLabel) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            clockLabel.setText(now.format(formatter));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
