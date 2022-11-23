package com.adioracreations.lightningalgorithm;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.function.UnaryOperator;

public class Main extends Application {

    Maze maze = new Maze();
    Pane mazePane = maze.getMazePane();

    @Override
    public void start(Stage stage) {

        Robot robot = new Robot();

        Rectangle2D rectangle2D =  Screen.getPrimary().getBounds();

        long time = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++)
            robot.getScreenCapture(null, rectangle2D);
        time = System.currentTimeMillis() - time;
        System.out.println(time);

        Label maze_width = new Label("Width");
        Slider mazeGridWidth = new Slider(1, 150, 10);
        TextField mazeWidthField = new TextField("10");

        Label maze_height = new Label("Height");
        Slider mazeGridHeight = new Slider(1, 150, 10);
        TextField mazeHeightField = new TextField("10");

        Label maze_cell_size = new Label("Cell Size");
        Slider mazeCellSize = new Slider(5, 50, 20);
        TextField mazeCellSizeField = new TextField("20");

        Button generate = new Button("Generate");
        generate.setFocusTraversable(false);

        HBox hBox = new HBox(maze_width, mazeGridWidth, mazeWidthField,
                maze_height, mazeGridHeight, mazeHeightField,
                maze_cell_size, mazeCellSize, mazeCellSizeField,
                generate);
        hBox.setSpacing(4);
        hBox.setAlignment(Pos.CENTER);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            if(change.getControlNewText().matches("([1-9][0-9]*)?"))
                return change;
            return null;
        };

        for (int i = 2; i < 10; i+=3) {
            TextField field = (TextField) hBox.getChildren().get(i);
            field.setPrefColumnCount(3);
            field.setTextFormatter(new TextFormatter<>(filter));
            HBox.setMargin(field, new Insets(0, 24, 0, 0));

            Slider slider = ((Slider) hBox.getChildren().get(i-1));
            slider.valueProperty().addListener((observable, oldValue, newValue) ->
                    field.setText(String.valueOf(newValue.intValue())));

            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue.length() == 0) return;
                int value = Integer.parseInt(newValue);
                if(value >= slider.getMin() && value <= slider.getMax())
                    slider.setValue(value);
            });
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFocusTraversable(false);
        mazePane.widthProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue() < scrollPane.getWidth()) mazePane.setTranslateX((scrollPane.getWidth() - mazePane.getWidth())/2);
            else mazePane.setTranslateX(0);
        });
        scrollPane.widthProperty().addListener((observable) -> mazePane.setTranslateX((scrollPane.getWidth() - mazePane.getWidth())/2));

        VBox vBox = new VBox(hBox, scrollPane);
        vBox.setSpacing(8);
        vBox.setPadding(new Insets(8));

        generate.setOnAction(event -> {
            maze.create((int) mazeGridWidth.getValue(), (int) mazeGridHeight.getValue(), (int) mazeCellSize.getValue());
            scrollPane.setContent(mazePane);
        });

        stage.setScene(new Scene(vBox));
        stage.setTitle("Maze Algorithm");
        stage.show();
    }
    public static void main(String[] args) {

        launch();

    }
}