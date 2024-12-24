package presenter;

import java.util.HashMap;
import java.util.Collections;
import java.util.Set;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import model.AnimalConfigData;
import model.Vector2d;
import model.map.MapChangeListener;
import model.map.WorldMap;
import model.elements.grass.generators.ForestedEquators;
import simulation.Simulation;
import simulation.SimulationStats;
import model.elements.animal.Animal;
import model.elements.animal.AnimalStats;

public class SimulationPresenter implements MapChangeListener {
    private Simulation simulation;
    private WorldMap map;
    private Animal followedAnimal;
    @FXML
    private GridPane mapGrid;
    private HashMap<Vector2d, Circle> circles = new HashMap<>();
    private HashMap<Vector2d, Rectangle> squares = new HashMap<>();

    @FXML
    private Button resumeButton;

    @FXML
    private Button stopButton;

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
    @FXML
    private VBox followedAnimalBox;
    @FXML
    private Label followedAnimalGenome;
    @FXML
    private Label followedAnimalActiveGene;
    @FXML
    private Label followedAnimalEnergy;
    @FXML
    private Label followedAnimalGrassEaten;
    @FXML
    private Label followedAnimalChildren;
    @FXML
    private Label followedAnimalDescendants;
    @FXML
    private Label followedAnimalDaysAlive;
    @FXML
    private HBox followedAnimalDayOfDeathBox;
    @FXML
    private Label followedAnimalDayOfDeath;


    private void drawGrid(WorldMap map) {
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
                Vector2d position = new Vector2d(i, j);
                circles.put(position, circle);
                squares.put(position, square);

            }
        }
    }

    @Override
    public void mapChanged(WorldMap map) {
        Platform.runLater(() -> {
            for (Vector2d position : circles.keySet()) {
                squares.get(position).setVisible(map.isGrassOn(position));
                circles.get(position).setVisible(map.getAnimalsAt(position).size() > 0);
            }

            SimulationStats simulationStats = simulation.getStats();
            day.setText(String.valueOf(simulationStats.day()));
            animalsCount.setText(String.valueOf(simulationStats.animalsCount()));
            grassCount.setText(String.valueOf(simulationStats.grassCount()));
            emptySquareCount.setText(String.valueOf(simulationStats.emptySquareCount()));
            averageEnergy.setText(String.format("%.2f", simulationStats.averageEnergy()));
            averageLifespan.setText(String.format("%.2f", simulationStats.averageLifeSpan()));
            averageChildrenCount.setText(String.format("%.2f", simulationStats.averageChildrenCount()));

            if (followedAnimal == null) {
                followedAnimalBox.setVisible(false);
                followedAnimalBox.setManaged(false);
            } else {
                followedAnimalBox.setVisible(true);
                followedAnimalBox.setManaged(true);
                AnimalStats animalStats = followedAnimal.getStats();
                followedAnimalGenome.setText(animalStats.genome().toString());
                followedAnimalActiveGene.setText(String.valueOf(animalStats.activeGene()));
                followedAnimalEnergy.setText(String.valueOf(animalStats.energy()));
                followedAnimalGrassEaten.setText(String.valueOf(animalStats.grassEaten()));
                followedAnimalChildren.setText(String.valueOf(animalStats.children()));
                followedAnimalDescendants.setText(String.valueOf(animalStats.descendants()));
                followedAnimalDaysAlive.setText(String.valueOf(animalStats.lifeSpan()));
                if (followedAnimal.isAlive()) {
                    followedAnimalDayOfDeathBox.setVisible(false);
                    followedAnimalDayOfDeathBox.setManaged(false);
                } else {
                    followedAnimalDayOfDeathBox.setVisible(true);
                    followedAnimalDayOfDeathBox.setManaged(true);
                    followedAnimalDayOfDeath.setText(String.valueOf(animalStats.deathDay().get()));
                }
            }
        });
    }    

    public void startSimulation() {
        WorldMap map = new WorldMap(100, 100);
        this.map = map;
        map.addListener(this);
        drawGrid(map);
        AnimalConfigData animalConfigData = new AnimalConfigData(10, 5, 5, true, 10, 1, 10);
        Simulation simulation = new Simulation(map, animalConfigData, new ForestedEquators(map), 100, 100, 100, 10);
        this.simulation = simulation;
        simulation.start();
        simulation.setSpeed(10);
    }

    @FXML
    public void resumeSimulation() {
        simulation.start();
        resumeButton.setVisible(false);
        resumeButton.setManaged(false);
        stopButton.setVisible(true);
        stopButton.setManaged(true);
        for (Circle circle : circles.values())
            circle.setOnMouseClicked(null);
    }

    @FXML
    public void stopSimulation() {
        simulation.stop();
        stopButton.setVisible(false);
        stopButton.setManaged(false);
        resumeButton.setVisible(true);
        resumeButton.setManaged(true);
        for (Vector2d position : circles.keySet())
            circles.get(position).setOnMouseClicked(e -> {
                Set<Animal> animals = map.getAnimalsAt(position);
                followedAnimal = animals.isEmpty() ? null : Collections.max(animals);
                mapChanged(map);
            });
    }

}