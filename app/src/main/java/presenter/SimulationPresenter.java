package presenter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.Bindings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import model.Vector2d;
import model.elements.animal.Animal;
import model.elements.animal.AnimalStats;
import model.elements.animal.Genome;
import model.map.WorldMap;
import simulation.Simulation;
import simulation.SimulationChangeListener;
import simulation.SimulationConfig;
import simulation.SimulationStats;

public class SimulationPresenter implements SimulationChangeListener {
    private static final Color grassColor = Color.GREEN;
    private static final Color preferredColor = Color.DARKGREEN;
    private static final Color dominantGenomeColor = Color.BLUE;
    private static final Color followedAnimalColor = Color.DARKVIOLET;
    private static final Color currentMoveArrowColor = Color.RED;
    private static final Color[] energyColor = new Color[256];

    static {
        for (int i = 0; i < 256; i++) {
            energyColor[i] = Color.rgb(255 - i, i, 0);
        }
    }

    private Simulation simulation;
    private final HashMap<Vector2d, Circle> animalCircles = new HashMap<>();
    private final HashMap<Vector2d, Rectangle> grassSquares = new HashMap<>();
    private Animal followedAnimal;
    private boolean saveStats;
    private String statsFileName;
    @FXML
    private HBox simulationRoot;
    @FXML
    private VBox controlBox;
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
        drawGrid(config.map());
        this.saveStats = saveStats;
        this.statsFileName = "simulation_" + System.currentTimeMillis() + ".csv";
        if (saveStats) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(statsFileName))) {
                writer.write("day,animalsCount,grassCount,emptySquareCount,averageEnergy,averageLifespan,averageChildrenCount,topGenomes\n");
            } catch (IOException e) {
                this.saveStats = false;
                e.printStackTrace();
            }
        }
        this.simulation = new Simulation(config);
        simulation.addListener(this);
        simulationChanged(simulation);
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
        WorldMap map = simulation.getMap();
        for (Vector2d position : animalCircles.keySet())
            animalCircles.get(position).setOnMouseClicked(e -> {
                Set<Animal> animals = map.getAnimalsAt(position);
                followedAnimal = animals.isEmpty() ? null : Collections.max(animals);
                simulationChanged(simulation);
            });
    }

    private void drawGrid(WorldMap map) {
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

            
        DoubleBinding cellSizeBinding = Bindings.createDoubleBinding(() -> 
            Math.min((simulationRoot.getWidth() - controlBox.getWidth()) / map.getWidth(), simulationRoot.getHeight() / map.getHeight()),
            simulationRoot.widthProperty(), simulationRoot.heightProperty(), controlBox.widthProperty()
        );

        mapGrid.minWidthProperty().bind(cellSizeBinding.multiply(map.getWidth()));
        mapGrid.maxWidthProperty().bind(cellSizeBinding.multiply(map.getWidth()));
        mapGrid.minHeightProperty().bind(cellSizeBinding.multiply(map.getHeight()));
        mapGrid.maxHeightProperty().bind(cellSizeBinding.multiply(map.getHeight()));

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
                square.setFill(grassColor);
                square.setVisible(false);
                cell.setMinWidth(1);
                cell.setMinHeight(1);

                Circle circle = new Circle();
                circle.setFill(energyColor[0]);
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
    public void simulationChanged(Simulation simulation) {
        Platform.runLater(() -> {
            updateMapGrid(simulation.getMap());
            updateSimulationStats(simulation);
            updateAnimalStats();
        });
    }

    private void updateMapGrid(WorldMap map) {
        for (Vector2d position : animalCircles.keySet()) {
            grassSquares.get(position).setVisible(map.isGrassAt(position));
            grassSquares.get(position).setFill(grassColor);
            animalCircles.get(position).setVisible(map.isAnimalAt(position));
            try {
                if (map.isAnimalAt(position))
                    animalCircles.get(position).setFill(energyColor[Math.min(map.getTopAnimalAt(position).getEnergy(), 255)]);
            } catch (IllegalArgumentException e) {
                // no animal at position (happens because of multithreading)
            }
        }
        if (followedAnimal != null && followedAnimal.isAlive())
            animalCircles.get(followedAnimal.getPosition()).setFill(followedAnimalColor);
    }

    private void updateSimulationStats(Simulation simulation) {
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(statsFileName, true))) {
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
            saveStats = false;
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
            colored.setFill(currentMoveArrowColor);
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
        simulationChanged(simulation);
    }

    @FXML
    public void showDominantGenomeAnimals() {
        simulationChanged(simulation);
        Platform.runLater(() -> {
            simulation.getPopularAnimals().forEach(animal -> {
                animalCircles.get(animal.getPosition()).setFill(dominantGenomeColor);
            });
        });
    }

    @FXML
    public void showPreferredSquares() {
        simulationChanged(simulation);
        Platform.runLater(() -> {
            simulation.getPreferredPositions().forEach(position -> {
                grassSquares.get(position).setVisible(true);
                grassSquares.get(position).setFill(preferredColor);
            });
        });
    }

    @FXML
    public void initialize() {
        simulationSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            simulation.setSpeed(newValue);
        });
    }
}