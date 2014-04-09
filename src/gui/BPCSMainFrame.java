package gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * This is the Main frame for the application.
 * The frame is using Java FX components.
 * @author karthik
 *
 */


/*
 * To initialize the JFX Application, the class should extends
 * javafx.application.Application.
 */
public class BPCSMainFrame extends Application{

	ObservableList<String> actionList = FXCollections.observableArrayList(
			"Hide", "Retrive");
	GridPane mainPane = new GridPane();

	Label				actionLabel 	= new Label("Select Action");
	ComboBox<String> 	actionComboBox	= new ComboBox<>(actionList);

	Label		inImgLabel 		= new Label("Input Image");
	Label		inDataLabel 	= new Label("Secert Data");
	TextField	inImgTextField 	= new TextField();
	TextField	inDataTextField = new TextField();
	Button		inImgSelButton 	= new Button("Browse");
	Button		inDataSelButton = new Button("Browse");

	Button	settingsButton 		= new Button("Edit Settings");
	Button	viewInputButton 	= new Button("View Input");
	Button	viewOutputButton 	= new Button("View Output");
	
	Label	tempLabel = new Label("Not Selected");

	public static void main(String[] args) {
		launch(args);
	}

	/*
	 * The start method is entry point for launching.
	 */
	public void start(Stage primaryStage) throws Exception {

		actionComboBox.valueProperty().addListener(new ChangeListener<String>() {

			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				tempLabel.setText(arg2);
			}
		});

		mainPane.add(actionLabel, 0, 0);
		mainPane.add(actionComboBox, 1, 0);
		mainPane.add(tempLabel, 0, 1);

		mainPane.setPadding(new Insets(20.0, 0.0, 0.0, 40.0));
		mainPane.setHgap(5.0);
		mainPane.setVgap(50.0);

		GridPane inputPane = new GridPane();
		inputPane.setVgap(5);
		inputPane.setHgap(10);
		inputPane.setPadding(new Insets(15, 0, 0, 10));
		inputPane.add(inImgLabel, 0, 0);
		inputPane.add(inImgTextField, 1, 0);
		inputPane.add(inImgSelButton, 2, 0);

		inputPane.add(inDataLabel, 0, 1);
		inputPane.add(inDataTextField, 1, 1);
		inputPane.add(inDataSelButton, 2, 1);

		MyGroupWithBorder inputGroup = new MyGroupWithBorder("Select Inputs ", 300, 75, inputPane) ;
		mainPane.add(inputGroup, 0, 2, 2, 1);

		HBox buttonBox = new HBox();
		buttonBox.setPadding(new Insets(15, 12, 15, 12));
		buttonBox.setSpacing(10);
		
		buttonBox.getChildren().add(settingsButton);
		buttonBox.getChildren().add(viewInputButton);
		buttonBox.getChildren().add(viewOutputButton);
		
		mainPane.add(buttonBox, 0, 3, 2, 1);
		
		primaryStage.setScene(new Scene(mainPane, 500, 400));
		primaryStage.show();
	}

	private class MyGroupWithBorder extends Group {

		public MyGroupWithBorder(String titleIn, int width, int height, Node componentIn) {
			Rectangle		_shape			= new Rectangle(width, height);
			_shape.setStyle("-fx-fill: WHITE; -fx-stroke: BLACK;");

			Label	title = new Label("Select Inputs ");
			title.setTranslateX(10.0);
			title.setTranslateY(-7.0);
			title.setStyle("-fx-background-color:WHITE;");

			getChildren().addAll(_shape, title, componentIn);
		}
	}
}
