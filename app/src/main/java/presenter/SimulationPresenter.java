package presenter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import model.AnimalConfigData;
import model.Vector2d;
import model.map.MapChangeListener;
import model.map.WorldMap;
import model.elements.grass.generators.ForestedEquators;
import simulation.Simulation;
import simulation.SimulationStats;

public class SimulationPresenter implements MapChangeListener {
    private Simulation simulation;
    @FXML
    private GridPane mapGrid;
    private Circle[][] circles;
    private Rectangle[][] squares;

    @FXML
    private Label day;
    @FXML
    private Label animalsCount;
    @FXML
    private Label grassCount;
    @FXML
    private Label emptySquareCount;
    @FXML
    private Label averageEnergy;
    @FXML
    private Label averageLifespan;
    @FXML
    private Label averageChildrenCount;


    private void drawGrid(WorldMap map) {
        circles = new Circle[map.getWidth()][map.getHeight()];
        squares = new Rectangle[map.getWidth()][map.getHeight()];

        
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

        for (int i = 0; i < map.getWidth(); i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / map.getWidth());
            colConst.setHgrow(Priority.ALWAYS);
            colConst.setMinWidth(10);
            mapGrid.getColumnConstraints().add(colConst);
        }

        for (int j = 0; j < map.getHeight(); j++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / map.getHeight());
            rowConst.setVgrow(Priority.ALWAYS);
            rowConst.setMinHeight(10);
            mapGrid.getRowConstraints().add(rowConst);
        }


        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                StackPane cell = new StackPane();
                Rectangle square = new Rectangle();
                square.setFill(Color.GREEN);
                square.setVisible(false);
                cell.setMinWidth(10);
                cell.setMinHeight(10);
                
                Circle circle = new Circle();
                circle.setFill(Color.BLACK);
                circle.setVisible(false);

                square.widthProperty().bind(cell.widthProperty());
                square.heightProperty().bind(cell.heightProperty());
                circle.radiusProperty().bind(cell.widthProperty().divide(2.5));

                cell.getChildren().addAll(square, circle);
                mapGrid.add(cell, i, j);
                circles[i][j] = circle;
                squares[i][j] = square;

            }
        }
    }

    @Override
    public void mapChanged(WorldMap map, Vector2d position) {
        Platform.runLater(() -> {
            squares[position.x()][position.y()].setVisible(map.isGrassOn(position));
            circles[position.x()][position.y()].setVisible(map.getAnimalsAt(position).size() > 0);

            SimulationStats simulationStats = simulation.getStats();
            day.setText(String.valueOf(simulationStats.day()));
            animalsCount.setText(String.valueOf(simulationStats.animalsCount()));
            grassCount.setText(String.valueOf(simulationStats.grassCount()));
            emptySquareCount.setText(String.valueOf(simulationStats.emptySquareCount()));
            averageEnergy.setText(String.format("%.2f", simulationStats.averageEnergy()));
            averageLifespan.setText(String.format("%.2f", simulationStats.averageLifeSpan()));
            averageChildrenCount.setText(String.format("%.2f", simulationStats.averageChildrenCount()));
        });
    }    

    public void startSimulation() {
        WorldMap map = new WorldMap(100, 100);
        map.addListener(this);
        drawGrid(map);
        AnimalConfigData animalConfigData = new AnimalConfigData(10, 5, 5, true, 10, 1, 10);
        Simulation simulation = new Simulation(map, animalConfigData, new ForestedEquators(map), 100, 100, 100, 10);
        this.simulation = simulation;
        simulation.start();
        simulation.setSpeed(10);
    }

    public void stopSimulation() {
        simulation.stop();
    }

}