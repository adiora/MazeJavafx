package com.adioracreations.lightningalgorithm;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Maze {

    private int height;

    private int width;

    private int cellSize;

    private Pane mazePane = new Pane();

    private Cell[][] cells;

    private boolean pathMode;

    Maze() {
        setUpHandlers();
    }

    public void create(int width, int height, int cellSize) {

        if (mazePane == null) mazePane = new Pane();
        pathMode = true;
        if(height == this.height && width == this.width && cellSize == this.cellSize) {
            int lines = 2 * height * width - height - width + 1;
            if (mazePane.getChildren().size() > lines)
                mazePane.getChildren().subList(lines, mazePane.getChildren().size()).clear();

            for (Node node : mazePane.getChildren())
                if (!node.isVisible()) node.setVisible(true);
        }
        else {
            this.height = height;
            this.width = width;
            this.cellSize = cellSize;

            mazePane.getChildren().clear();

            addLines(height, width);

            mazePane.getChildren().add(new Polyline(0, 0,
                    width * cellSize, 0,
                    width * cellSize, height * cellSize,
                    0, height * cellSize,
                    0, 0));

            Runtime.getRuntime().gc();
        }

        cells = generateMaze(height, width);
    }

    private void addLines(int height, int width) {
        for(int i = 1; i <= height; i++) {
            for (int j = 1; j <= width; j++) {
                if(i < height)
                    mazePane.getChildren().addAll(new Line((j-1)*cellSize, i*cellSize,
                            j*cellSize, i*cellSize));
                if(j < width)
                    mazePane.getChildren().add(new Line(j*cellSize, (i-1)*cellSize,
                            j*cellSize, i*cellSize));
            }
        }
    }

    private void setUpHandlers() {
        EventHandler<MouseEvent> mazeEnteredEvent = event -> {
            if(pathMode) {
                Rectangle rectangle = new Rectangle(cellSize - 3, cellSize - 3);
                rectangle.setFill(new Color(1, 1, 0, 0.5));
                mazePane.getChildren().add(rectangle);
            }
        };

        EventHandler<MouseEvent> mazeMovedEvent = event -> {
            if(pathMode) {
                int r = (int) (event.getY() / cellSize);
                int c = (int) (event.getX() / cellSize);
                if (c > width - 1 || r > height - 1) return;
                mazePane.getChildren().get(mazePane.getChildren().size() - 1).relocate(
                        c * cellSize + 1, r * cellSize + 1);
            }
        };

        EventHandler<MouseEvent> mazeExitedEvent = event -> {
            if (pathMode)
                mazePane.getChildren().remove(mazePane.getChildren().size()-1);
        };

        mazePane.addEventHandler(MouseEvent.MOUSE_ENTERED, mazeEnteredEvent);
        mazePane.addEventHandler(MouseEvent.MOUSE_MOVED, mazeMovedEvent);
        mazePane.addEventHandler(MouseEvent.MOUSE_EXITED, mazeExitedEvent);

        ArrayList<Cell> cellArrayList = new ArrayList<>(2);

        mazePane.addEventHandler(MouseEvent.MOUSE_CLICKED, event2 -> {
            if(pathMode) {
                if (cellArrayList.size() < 2) {
                    int r = (int) (event2.getY() / cellSize);
                    int c = (int) (event2.getX() / cellSize);
                    Rectangle rectangle = new Rectangle(cellSize - 3, cellSize - 3);
                    rectangle.setFill(new Color(1, 1, 0, 0.5));
                    rectangle.relocate(c * cellSize + 1, r * cellSize + 1);
                    mazePane.getChildren().add(rectangle);
                    cellArrayList.add(cells[r][c]);
                }
                if (cellArrayList.size() == 2) {
                    pathMode = false;
                    findPath(cellArrayList);
                    cellArrayList.clear();
                }
            }
        });
    }

    private void findPath(ArrayList<Maze.Cell> arrayList) {
        Maze.Cell d = arrayList.get(1);

        Stack<Maze.Cell> stack = new Stack<>();
        stack.push(arrayList.get(0));

        Stack<Maze.Cell> prevStack = new Stack<>();
        prevStack.push(arrayList.get(0));

        Stack<Integer> integers = new Stack<>();

        ArrayList<Maze.Cell> path = new ArrayList<>();

        Maze.Cell cell, prev;

        while (!stack.isEmpty()) {
            cell = stack.pop();
            prev = prevStack.pop();

            path.add(cell);

            if(d.row == cell.row && d.col == cell.col) break;

            int size = stack.size();

            if(cell.top != null && cell.top != prev)
                stack.push(cell.top);

            if(cell.right != null && cell.right != prev)
                stack.push(cell.right);

            if(cell.bottom != null && cell.bottom != prev)
                stack.push(cell.bottom);

            if(cell.left != null && cell.left != prev)
                stack.push(cell.left);

            for(int i = stack.size(); i > size; i--) {
                prevStack.push(cell);
            }

            if(stack.size() - size == 0) {
                for (int i = path.size() - 1, j = integers.pop(); i >= j; i--) {
                    path.remove(i);
                }
            }
            else if(stack.size() - size > 1) {
                for(int i = stack.size() -1; i > size; i--) {
                    integers.push(path.size());
                }
            }
        }

        for (int i = 0; i < path.size() - 1; i++) {
            Cell cell1 = path.get(i);
            Cell cell2 = path.get(i+1);
            Line line;
            if(i == 0) {
                line = new Line(
                        (cell1.col + cell2.col + 1) * 0.5 * cellSize,
                        (cell1.row + cell2.row + 1) * 0.5 * cellSize,
                        (cell2.col + 0.5) * cellSize,
                        (cell2.row + 0.5) * cellSize
                );
            }
            else if(i == path.size() - 2) {
                line = new Line(
                        (cell1.col + 0.5) * cellSize,
                        (cell1.row + 0.5) * cellSize,
                        (cell1.col + cell2.col + 1) * 0.5 * cellSize,
                        (cell1.row + cell2.row + 1) * 0.5 * cellSize
                );
            }
            else line = new Line(
                    (cell1.col + 0.5) * cellSize,
                    (cell1.row + 0.5) * cellSize,
                    (cell2.col + 0.5) * cellSize,
                    (cell2.row + 0.5) * cellSize
            );
            line.setStroke(Color.AQUA);
            line.setStrokeWidth(Math.max(2, cellSize/8F));
            mazePane.getChildren().add(line);
        }

    }


    public static class Cell {
        Cell top;
        Cell right;
        Cell bottom;
        Cell left;

        int row, col;
    }

    public Cell[][] generateMaze(int height, int width) {

        Cell[][] cells = new Cell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = new Cell();
                cell.row = i;
                cell.col = j;
                cells[i][j] = cell;
            }
        }

        boolean[][] visited = new boolean[height][width];

        Stack<Cell> cellStack = new Stack<>();

        Random random = new Random();

        Cell cell = cells[random.nextInt(height)][random.nextInt(width)];
        do {

            if (!visited[cell.row][cell.col]) {
                visited[cell.row][cell.col] = true;
                cellStack.push(cell);
            }

            boolean found = false;
            int direction = random.nextInt(4);

            while (!found) {
                switch (direction) {
                    //TOP
                    case -1:
                    case 0:
                        if(cell.row > 0 && !visited[cell.row-1][cell.col]) {
                            found = true;
                            mazePane.getChildren().get((cell.row-1) * (2*width-1) + 2*cell.col).setVisible(false);
                            cell.top = cells[cell.row-1][cell.col];
                            cell.top.bottom = cell;
                            cell = cell.top;
                            break;
                        }

                        //RIGHT
                    case 1:
                        if (cell.col < width-1 && !visited[cell.row][cell.col+1]) {
                            found = true;
                            mazePane.getChildren().get(cell.row * (2*width-1)
                                    + (cell.row == height-1? cell.col : 2*cell.col+1)).setVisible(false);
                            cell.right = cells[cell.row][cell.col+1];
                            cell.right.left = cell;
                            cell = cell.right;
                            break;
                        }

                        //BOTTOM
                    case 2:
                        if(cell.row < height-1 && !visited[cell.row+1][cell.col]) {
                            found = true;
                            mazePane.getChildren().get(cell.row * (2*width-1) + 2*cell.col).setVisible(false);
                            cell.bottom = cells[cell.row+1][cell.col];
                            cell.bottom.top = cell;
                            cell = cell.bottom;
                            break;
                        }

                        //LEFT
                    case 3:
                        if (cell.col > 0 && !visited[cell.row][cell.col-1]) {
                            found = true;
                            mazePane.getChildren().get(cell.row * (2*width-1)
                                    + (cell.row == height - 1? cell.col : 2*cell.col)-1).setVisible(false);
                            cell.left = cells[cell.row][cell.col-1];
                            cell.left.right = cell;
                            cell = cell.left;
                            break;
                        }
                }
                if (direction == -1) break;
                else direction = -1;
            }

            if (!found && !cellStack.isEmpty()) {
                cell = cellStack.pop();
            }

        } while (!cellStack.isEmpty());

        return cells;
    }

    public Pane getMazePane() {
        return mazePane;
    }
}
