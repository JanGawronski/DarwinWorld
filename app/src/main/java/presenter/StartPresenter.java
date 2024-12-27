package presenter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.BiConsumer;
import org.json.JSONObject;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.FileChooser;
import javafx.beans.value.ChangeListener;

import simulation.SimulationConfig;

public class StartPresenter {
    private BiConsumer<SimulationConfig, Boolean> showSimulationWindowHandler;
    @FXML
    private Spinner<Integer> mapHeight;
    @FXML
    private Spinner<Integer> mapWidth;
    @FXML
    private Spinner<Integer> initialPlants;
    @FXML
    private Spinner<Integer> plantEnergy;
    @FXML
    private Spinner<Integer> dailyPlantGrowth;
    @FXML
    private RadioButton forestedEquators;
    @FXML
    private RadioButton creepingJungle;
    @FXML
    private Spinner<Integer> initialAnimals;
    @FXML
    private Spinner<Integer> initialAnimalEnergy;
    @FXML
    private Spinner<Integer> saturationEnergy;
    @FXML
    private Spinner<Integer> offspringEnergy;
    @FXML
    private Spinner<Integer> minMutations;
    @FXML
    private Spinner<Integer> maxMutations;
    @FXML
    private RadioButton fullPredestination;
    @FXML
    private RadioButton bitOfMadness;
    @FXML
    private Spinner<Integer> genomeLength;
    @FXML
    private CheckBox saveStats;
    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        genomeLength.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                maxMutations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, newValue, Math.min(maxMutations.getValue(), newValue)));
            }
        });
        maxMutations.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                minMutations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, newValue, Math.min(minMutations.getValue(), newValue)));
            }
        });
        minMutations.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                maxMutations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(newValue, genomeLength.getValue(), Math.max(maxMutations.getValue(), newValue)));
            }
        });
        mapHeight.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                initialPlants.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, newValue * mapWidth.getValue(), Math.min(initialPlants.getValue(), newValue * mapWidth.getValue())));
            }
        });
        mapWidth.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                initialPlants.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, newValue * mapHeight.getValue(), Math.min(initialPlants.getValue(), newValue * mapHeight.getValue())));
            }
        });
        saturationEnergy.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                offspringEnergy.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, newValue, Math.min(offspringEnergy.getValue(), newValue)));
            }
        });
    }

    public void setShowSimulationWindowHandler(BiConsumer<SimulationConfig, Boolean> handler) {
        this.showSimulationWindowHandler = handler;
    }

    @FXML
    private void startSimulation() throws IOException {
        SimulationConfig config = new SimulationConfig(mapHeight.getValue(), mapWidth.getValue(), 
                initialPlants.getValue(),
                plantEnergy.getValue(), forestedEquators.isSelected(), dailyPlantGrowth.getValue(),
                initialAnimals.getValue(),
                initialAnimalEnergy.getValue(), saturationEnergy.getValue(), offspringEnergy.getValue(),
                minMutations.getValue(),
                maxMutations.getValue(), fullPredestination.isSelected(), genomeLength.getValue());
        showSimulationWindowHandler.accept(config, saveStats.isSelected());
    }

    @FXML
    public void loadConfig() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Configuration");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                JSONObject config = new JSONObject(new String(java.nio.file.Files.readAllBytes(file.toPath())));
                mapHeight.getValueFactory().setValue(config.getInt("mapHeight"));
                mapWidth.getValueFactory().setValue(config.getInt("mapWidth"));
                initialPlants.getValueFactory().setValue(config.getInt("initialPlants"));
                plantEnergy.getValueFactory().setValue(config.getInt("plantEnergy"));
                dailyPlantGrowth.getValueFactory().setValue(config.getInt("dailyPlantGrowth"));
                forestedEquators.setSelected(config.getBoolean("defaultPlantGrowthVariant"));
                creepingJungle.setSelected(!config.getBoolean("defaultPlantGrowthVariant"));
                initialAnimals.getValueFactory().setValue(config.getInt("initialAnimals"));
                initialAnimalEnergy.getValueFactory().setValue(config.getInt("initialAnimalEnergy"));
                saturationEnergy.getValueFactory().setValue(config.getInt("saturationEnergy"));
                offspringEnergy.getValueFactory().setValue(config.getInt("offspringEnergy"));
                genomeLength.getValueFactory().setValue(config.getInt("genomeLength"));
                maxMutations.getValueFactory().setValue(config.getInt("maxMutations"));
                minMutations.getValueFactory().setValue(config.getInt("minMutations"));
                fullPredestination.setSelected(config.getBoolean("defaultBehaviourVariant"));
                bitOfMadness.setSelected(!config.getBoolean("defaultBehaviourVariant"));

                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
            } catch (IOException | org.json.JSONException e) {
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        }
    }

    @FXML
    public void saveConfig() {
        JSONObject config = new JSONObject();
        config.put("mapHeight", mapHeight.getValue());
        config.put("mapWidth", mapWidth.getValue());
        config.put("initialPlants", initialPlants.getValue());
        config.put("plantEnergy", plantEnergy.getValue());
        config.put("dailyPlantGrowth", dailyPlantGrowth.getValue());
        config.put("defaultPlantGrowthVariant", forestedEquators.isSelected());
        config.put("initialAnimals", initialAnimals.getValue());
        config.put("initialAnimalEnergy", initialAnimalEnergy.getValue());
        config.put("saturationEnergy", saturationEnergy.getValue());
        config.put("offspringEnergy", offspringEnergy.getValue());
        config.put("minMutations", minMutations.getValue());
        config.put("maxMutations", maxMutations.getValue());
        config.put("defaultBehaviourVariant", fullPredestination.isSelected());
        config.put("genomeLength", genomeLength.getValue());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Configuration");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setInitialFileName("config.json");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(config.toString(4));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}