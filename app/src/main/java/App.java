import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import presenter.SimulationPresenter;
import presenter.StartPresenter;
import simulation.SimulationConfig;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("start.fxml"));
        VBox startRoot = loader.load();
        StartPresenter startPresenter = loader.getController();
        startPresenter.setShowSimulationWindowHandler((config, saveStats) -> {
            try {
                showSimulationWindow(config, saveStats);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        primaryStage.setScene(new Scene(startRoot));
        primaryStage.setTitle("Start Simulation");
        primaryStage.minWidthProperty().bind(startRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(startRoot.minHeightProperty());
        primaryStage.show();
    }

    public void showSimulationWindow(SimulationConfig config, boolean saveStats) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        HBox simulationRoot = loader.load();
        SimulationPresenter simulationPresenter = loader.getController();
        Stage primaryStage = new Stage();
        primaryStage.setOnCloseRequest(e -> simulationPresenter.stopSimulation());
        simulationPresenter.startSimulation(config, saveStats);
        primaryStage.setScene(new Scene(simulationRoot));
        primaryStage.setTitle("Simulation");
        primaryStage.minWidthProperty().bind(simulationRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(simulationRoot.minHeightProperty());
        primaryStage.show();
    }
}