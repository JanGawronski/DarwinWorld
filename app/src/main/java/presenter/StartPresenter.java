package presenter;

import java.io.IOException;
import java.util.function.BiConsumer;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import simulation.SimulationConfig;

public class StartPresenter {
    private BiConsumer<Stage, SimulationConfig> showSimulationWindowHandler;
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
    private RadioButton defaultPlantGrowthVariant;
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
    private RadioButton defaultBehaviourVariant;
    @FXML
    private Spinner<Integer> genomeLength;

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

    public void setShowSimulationWindowHandler(BiConsumer<Stage, SimulationConfig> handler) {
        this.showSimulationWindowHandler = handler;
    }

    @FXML
    private void startSimulation() throws IOException {
        SimulationConfig config = new SimulationConfig(mapHeight.getValue(), mapWidth.getValue(), 
                initialPlants.getValue(),
                plantEnergy.getValue(), defaultPlantGrowthVariant.isSelected(), dailyPlantGrowth.getValue(),
                initialAnimals.getValue(),
                initialAnimalEnergy.getValue(), saturationEnergy.getValue(), offspringEnergy.getValue(),
                minMutations.getValue(),
                maxMutations.getValue(), defaultBehaviourVariant.isSelected(), genomeLength.getValue());
        showSimulationWindowHandler.accept(new Stage(), config);
    }
}