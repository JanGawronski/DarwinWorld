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
        startPresenter.setShowSimulationWindowHandler((stage, config) -> {
            try {
                showSimulationWindow(stage, config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        primaryStage.setScene(new Scene(startRoot));
        primaryStage.setTitle("Start Simulation");
        primaryStage.show();
    }

    public void showSimulationWindow(Stage primaryStage, SimulationConfig config) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        HBox simulationRoot = loader.load();
        SimulationPresenter simulationPresenter = loader.getController();
        primaryStage.setOnCloseRequest(e -> simulationPresenter.stopSimulation());
        simulationPresenter.startSimulation(config);
        configureStage(primaryStage, simulationRoot);
        primaryStage.show();
    }

    private void configureStage(Stage primaryStage, HBox viewRoot) {
        var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation app");
        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }
}