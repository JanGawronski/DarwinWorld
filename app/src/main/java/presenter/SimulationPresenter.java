package presenter;

import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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
import javafx.scene.text.TextFlow;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.text.Text;

import model.Vector2d;
import model.map.MapChangeListener;
import model.map.WorldMap;
import simulation.Simulation;
import simulation.SimulationConfig;
import simulation.SimulationStats;
import model.elements.animal.Animal;
import model.elements.animal.AnimalStats;
import model.elements.animal.Genome;

public class SimulationPresenter implements MapChangeListener {
    private Simulation simulation;
    private WorldMap map;
    private HashMap<Vector2d, Circle> animalCircles = new HashMap<>();
    private HashMap<Vector2d, Rectangle> grassSquares = new HashMap<>();
    private static final Color[] energyColor = new Color[256];
    static {
        for (int i = 0; i < 256; i++) {
            energyColor[i] = Color.rgb(255 - i, i, 0);
        }
    }
    private Animal followedAnimal;
    private boolean saveStats;
    private BufferedWriter writer;
    @FXML
    private GridPane mapGrid;
    @FXML
    private Button resumeButton;
    @FXML
    private Button stopButton;
    @FXML
    private Spinner<Integer> simulationSpeed;
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
    private Label topGenomes;
    @FXML
    private Button showDominantGenomeAnimalsButton;
    @FXML
    private Button showPreferredSquaresButton;
    @FXML
    private Label notFollowingAnimal;
    @FXML
    private VBox followedAnimalBox;
    @FXML
    private Button stopFollowingButton;
    @FXML
    private Label followedAnimalGenome;
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

    public void startSimulation(SimulationConfig config, boolean saveStats) {
        this.map = config.map();
        map.addListener(this);
        drawGrid(map);
        this.saveStats = saveStats;
        if (saveStats) {
            try {
                writer = new BufferedWriter(new FileWriter("simulation_" + System.currentTimeMillis() + ".csv"));
                writer.write("day,animalsCount,grassCount,emptySquareCount,averageEnergy,averageLifespan,averageChildrenCount,topGenomes\n");
            } catch (IOException e) {
                saveStats = false;
                e.printStackTrace();
            }
        }
        this.simulation = new Simulation(config);
        simulation.start();
    }

    @FXML
    public void resumeSimulation() {
        simulation.start();
        resumeButton.setVisible(false);
        resumeButton.setManaged(false);
        stopButton.setVisible(true);
        stopButton.setManaged(true);
        showDominantGenomeAnimalsButton.setVisible(false);
        showDominantGenomeAnimalsButton.setManaged(false);
        showPreferredSquaresButton.setVisible(false);
        showPreferredSquaresButton.setManaged(false);
        for (Circle circle : animalCircles.values())
            circle.setOnMouseClicked(null);
    }

    @FXML
    public void stopSimulation() {
        simulation.stop();
        stopButton.setVisible(false);
        stopButton.setManaged(false);
        resumeButton.setVisible(true);
        resumeButton.setManaged(true);
        showDominantGenomeAnimalsButton.setVisible(true);
        showDominantGenomeAnimalsButton.setManaged(true);
        showPreferredSquaresButton.setVisible(true);
        showPreferredSquaresButton.setManaged(true);
        for (Vector2d position : animalCircles.keySet())
            animalCircles.get(position).setOnMouseClicked(e -> {
                Set<Animal> animals = map.getAnimalsAt(position);
                followedAnimal = animals.isEmpty() ? null : Collections.max(animals);
                mapChanged(map);
            });
    }

    private void drawGrid(WorldMap map) {
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

        for (int i = 0; i < map.getWidth(); i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / map.getWidth());
            colConst.setHgrow(Priority.ALWAYS);
            colConst.setMinWidth(1);
            mapGrid.getColumnConstraints().add(colConst);
        }

        for (int j = 0; j < map.getHeight(); j++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / map.getHeight());
            rowConst.setVgrow(Priority.ALWAYS);
            rowConst.setMinHeight(1);
            mapGrid.getRowConstraints().add(rowConst);
        }

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                StackPane cell = new StackPane();
                Rectangle square = new Rectangle();
                square.setFill(Color.GREEN);
                square.setVisible(false);
                cell.setMinWidth(1);
                cell.setMinHeight(1);

                Circle circle = new Circle();
                circle.setFill(Color.BLACK);
                circle.setVisible(false);

                square.widthProperty().bind(cell.widthProperty());
                square.heightProperty().bind(cell.heightProperty());
                circle.radiusProperty().bind(cell.widthProperty().divide(2.5));

                cell.getChildren().addAll(square, circle);
                mapGrid.add(cell, i, j);
                Vector2d position = new Vector2d(i, j);
                animalCircles.put(position, circle);
                grassSquares.put(position, square);

            }
        }
    }

    @Override
    public void mapChanged(WorldMap map) {
        Platform.runLater(() -> {
            for (Vector2d position : animalCircles.keySet()) {
                grassSquares.get(position).setVisible(map.isGrassOn(position));
                grassSquares.get(position).setFill(Color.GREEN);
                Set<Animal> animals = map.getAnimalsAt(position);
                animalCircles.get(position).setVisible(!animals.isEmpty());
                if (!animals.isEmpty()) {
                    Animal animal = Collections.max(animals);
                    animalCircles.get(position).setFill(energyColor[Math.min(animal.getEnergy(), 255)]);
                }
            }
            if (followedAnimal != null && followedAnimal.isAlive())
                animalCircles.get(followedAnimal.getPosition()).setFill(Color.DARKVIOLET);
            updateSimulationStats();
            updateAnimalStats();
        });
    }

    private void updateSimulationStats() {
        SimulationStats simulationStats = simulation.getStats();
        day.setText(String.valueOf(simulationStats.day()));
        animalsCount.setText(String.valueOf(simulationStats.animalsCount()));
        grassCount.setText(String.valueOf(simulationStats.grassCount()));
        emptySquareCount.setText(String.valueOf(simulationStats.emptySquareCount()));
        averageEnergy.setText(String.format("%.2f", simulationStats.averageEnergy()));
        averageLifespan.setText(String.format("%.2f", simulationStats.averageLifeSpan()));
        averageChildrenCount.setText(String.format("%.2f", simulationStats.averageChildrenCount()));
        topGenomes.setText(simulationStats.genomePopularity().entrySet().stream()
                .sorted(Map.Entry.<Genome, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n")));
        if (saveStats)
            saveStats(simulationStats);
    }

    private void saveStats(SimulationStats simulationStats) {
        try {
            writer.write(simulationStats.day() + ",");
            writer.write(simulationStats.animalsCount() + ",");
            writer.write(simulationStats.grassCount() + ",");
            writer.write(simulationStats.emptySquareCount() + ",");
            writer.write(String.format("%.2f", simulationStats.averageEnergy()) + ",");
            writer.write(String.format("%.2f", simulationStats.averageLifeSpan()) + ",");
            writer.write(String.format("%.2f", simulationStats.averageChildrenCount()) + ",");
            writer.write(simulationStats.genomePopularity().entrySet().stream()
                    .sorted(Map.Entry.<Genome, Integer>comparingByValue().reversed())
                    .limit(10)
                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                    .collect(Collectors.joining(" ")) + "\n"
            );
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private void updateAnimalStats() {
        if (followedAnimal == null) {
            followedAnimalBox.setVisible(false);
            followedAnimalBox.setManaged(false);
            notFollowingAnimal.setVisible(true);
            notFollowingAnimal.setManaged(true);
        } else {
            followedAnimalBox.setVisible(true);
            followedAnimalBox.setManaged(true);
            notFollowingAnimal.setVisible(false);
            notFollowingAnimal.setManaged(false);

            AnimalStats animalStats = followedAnimal.getStats();
            String text = animalStats.genome().toString();
            Text before = new Text(text.substring(0, 2 * animalStats.activeGene()));
            Text colored = new Text(String.valueOf(text.charAt(2 * animalStats.activeGene())));
            colored.setFill(Color.RED);
            Text after = new Text(text.substring(2 * animalStats.activeGene() + 1));
            TextFlow textFlow = new TextFlow();
            textFlow.getChildren().addAll(before, colored, after);
            followedAnimalGenome.setGraphic(textFlow);

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
    }

    @FXML
    public void stopFollowingAnimal() {
        followedAnimal = null;
        mapChanged(map);
    }

    @FXML
    public void showDominantGenomeAnimals() {
        mapChanged(map);
        Platform.runLater(() -> {
            simulation.getPopularAnimals().forEach(animal -> {
                animalCircles.get(animal.getPosition()).setFill(Color.BLUE);
            });
        });
    }

    @FXML
    public void showPreferredSquares() {
        mapChanged(map);
        Platform.runLater(() -> {
            simulation.getPreferredPositions().forEach(position -> {
                grassSquares.get(position).setVisible(true);
                grassSquares.get(position).setFill(Color.DARKGREEN);
            });
        });
    }

    @FXML
    public void initialize() {
        simulationSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            simulation.setSpeed(newValue.intValue());
        });
    }
}