package org.example.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClockManager {
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

