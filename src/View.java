
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

// The View class creates and manages the GUI for the application.
// It doesn't know anything about the game itself, it just displays
// the current state of the Model, and handles user input
public class View implements EventHandler<KeyEvent>
{ 
	private Scene mainMenu, settings, leaderBoard, scene, endScore;
	private Stage window;
	
	public Slider sBatSpeed;
	public Slider sPUp;
	
    // variables for components of the user interface
    public int width;       // width of window
    public int height;      // height of window

    // user interface objects
    public Pane pane;       // basic layout pane
    public Canvas canvas;   // canvas to draw game on
    public Label infoText, infoTextLives, EndScoreScore, EndLeaderScore;  // info at top of screen
    public TextField scoreName;
    public Button submitScore;
    public ToggleGroup diffGroup;

    // The other parts of the model-view-controller setup
    public Controller controller;
    public Model model;

    public GameObj   bat;            // The bat
    public GameObj   ball;           // The ball
    public ArrayList<GameObj> balls; // Extra balls
    public ArrayList<GameObj> bricks;     // The bricks
    public ArrayList<PowerUp> PUps;  // Power ups
    public int       score =  0;     // The score
   
    // we don't really need a constructor method, but include one to print a 
    // debugging message if required
    public View(int w, int h)
    {
        Debug.trace("View::<constructor>");
        width = w;
        height = h;
    }

    // start is called from Main, to start the GUI up
    // Note that it is important not to create controls etc here and
    // not in the constructor (or as initialisations to instance variables),
    // because we need things to be initialised in the right order
    public void start(Stage myWindow) 
    {
        // breakout is basically one big drawing canvas, and all the objects are
        // drawn on it as rectangles, except for the text at the top - this
        // is a label which sits 'on top of' the canvas.
    	window = myWindow;
    	
    	setSceneMainMenu();
    	setSceneSettings();
    	setSceneLeaderBoard();
    	setSceneScene();
    	setSceneEndScore();
    	
        // put the scene in the winodw and display it
        window.setScene(mainMenu);
        window.getIcons().add(new Image("icon.png"));
        window.setTitle("Breakout");
        window.show();
    }
    
	public void setSceneMainMenu() {
	    GridPane gridMain = new GridPane();
	    gridMain.setId("Main");
	    gridMain.setAlignment(Pos.BOTTOM_CENTER);
	    gridMain.setVgap(25);
	    
	    double buttonWidth = width / 1.5;
	    double buttonHeight = height / 10;
	    
	    Button Bplay = new Button("Play");
	    Bplay.setOnAction(e -> startGame());
	    Bplay.setPrefSize(buttonWidth, buttonHeight);
	    GridPane.setRowIndex(Bplay, 0);
	    GridPane.setColumnIndex(Bplay, 0);
	    
	    Button Bsettings = new Button("Settings");
	    Bsettings.setOnAction(e -> window.setScene(settings));
	    Bsettings.setPrefSize(buttonWidth, buttonHeight);
	    GridPane.setRowIndex(Bsettings, 1);
	    GridPane.setColumnIndex(Bsettings, 0);
	    
	    Button BleaderBoard = new Button("Leaderboard");
	    BleaderBoard.setOnAction(e -> window.setScene(leaderBoard));
	    BleaderBoard.setPrefSize(buttonWidth, buttonHeight);
	    GridPane.setRowIndex(BleaderBoard, 2);
	    GridPane.setColumnIndex(BleaderBoard, 0);
	    
	    Button Bquit = new Button("Exit");
	    Bquit.setOnAction(e -> Platform.exit());
	    Bquit.setPrefSize(buttonWidth, buttonHeight);
	    GridPane.setRowIndex(Bquit, 3);
	    GridPane.setColumnIndex(Bquit, 0);
	    GridPane.setMargin(Bquit, new Insets(0, 0, (width - buttonWidth) / 2, 0));
	    
	    gridMain.getChildren().addAll(Bplay, Bsettings, BleaderBoard, Bquit);
	    mainMenu = new Scene(gridMain, width, height);
	    mainMenu.getStylesheets().add("breakout.css");
	}
	
	public void setSceneSettings() {
		GridPane gridSettings = new GridPane();
		gridSettings.setId("Settings");
		gridSettings.setAlignment(Pos.CENTER);
		
		VBox vbox = new VBox(20);
		
		Label settingsText = new Label("Settings");
		
		Label lDiff = new Label("Difficulty:");
		HBox hboxDiff = new HBox(20);
		diffGroup = new ToggleGroup();
		RadioButton rbEasy = new RadioButton("Easy");
		rbEasy.setToggleGroup(diffGroup);
	    rbEasy.setSelected(true);
		RadioButton rbMedium = new RadioButton("Medium");
		rbMedium.setToggleGroup(diffGroup);
		RadioButton rbHard = new RadioButton("Hard");
		rbHard.setToggleGroup(diffGroup);
		hboxDiff.getChildren().addAll(rbEasy, rbMedium, rbHard);
		
		
		Label lBatSpeed = new Label("Bat Movement Speed:");
		sBatSpeed = new Slider(1, 20, 5);
		sBatSpeed.setShowTickLabels(true);
		sBatSpeed.setShowTickMarks(true);
		sBatSpeed.setMajorTickUnit(1);
		sBatSpeed.setMinorTickCount(0);
		sBatSpeed.setSnapToTicks(true);
		Label lCurrentBatSpeed = new Label(Double.toString(sBatSpeed.getValue()));
		lCurrentBatSpeed.textProperty().bind(Bindings.format("%.0f", sBatSpeed.valueProperty()));
		
		Label lPUp = new Label ("Power Up Drop Chance:");
		sPUp = new Slider(0, 100, 10);
		sPUp.setShowTickLabels(true);
		sPUp.setMajorTickUnit(1);
		sPUp.setMinorTickCount(0);
		sPUp.setSnapToTicks(true);
		Label lCurrentPUp = new Label(Double.toString(sPUp.getValue()));
		lCurrentPUp.textProperty().bind(Bindings.format("%.0f%s", sPUp.valueProperty(), "%"));
		
		Button S2MM = new Button("Back to Main Menu");
	    S2MM.setOnAction(e -> window.setScene(mainMenu));
		
		vbox.getChildren().addAll(settingsText, lDiff, hboxDiff, lBatSpeed, sBatSpeed, lCurrentBatSpeed, lPUp, sPUp, lCurrentPUp, S2MM);
		
		
		gridSettings.getChildren().addAll(vbox);
		settings  = new Scene(gridSettings, width, height);
		settings.getStylesheets().add("breakout.css");
	}
	
	public void setSceneLeaderBoard() {
		int leaderBoardWidth = 1000;
		
		GridPane gridLeader = new GridPane();
		gridLeader.setId("Leader");
		gridLeader.setAlignment(Pos.CENTER);
		gridLeader.setVgap(10);
		
		Label leaderText = new Label("Leaderboard");
		leaderText.setAlignment(Pos.CENTER);
		GridPane.setRowIndex(leaderText, 0);
		GridPane.setColumnIndex(leaderText, 1);
		
		HBox scores = new HBox(0);
		GridPane.setRowIndex(scores, 1);
		GridPane.setColumnIndex(scores, 0);
		GridPane.setColumnSpan(scores, 3);
		
		VBox easyScoresContainer = new VBox();
		Label easyText = new Label("Easy:");
		ListView easyList = new ListView();
		easyScoresContainer.getChildren().addAll(easyText, easyList);
		easyList.getItems().add("Item1");
		easyList.getItems().add("Item2");
		easyList.getItems().add("Item3");
		
		VBox mediumScoresContainer = new VBox();
		Label mediumText = new Label("Medium:");
		ListView mediumList = new ListView();
		mediumScoresContainer.getChildren().addAll(mediumText, mediumList);
		
		VBox hardScoresContainer = new VBox();
		Label hardText = new Label("Hard:");
		ListView hardList = new ListView();
		hardScoresContainer.getChildren().addAll(hardText, hardList);
		
		scores.getChildren().addAll(easyScoresContainer, mediumScoresContainer, hardScoresContainer);
		
		Button LB2MM = new Button("Back to Main Menu");
	    LB2MM.setOnAction(e -> window.setScene(mainMenu));
		GridPane.setRowIndex(LB2MM, 2);
		GridPane.setColumnIndex(LB2MM, 0);
		GridPane.setColumnSpan(LB2MM, 3);
		
		gridLeader.getChildren().addAll(leaderText, scores, LB2MM);
		leaderBoard = new Scene(gridLeader, leaderBoardWidth, height);
		leaderBoard.getStylesheets().add("breakout.css");
	}
    
    public void setSceneScene() {
    	pane = new Pane();       // a simple layout pane
        pane.setId("Breakout");  // Id to use in CSS file to style the pane if needed
        
        // canvas object - we set the width and height here (from the constructor), 
        // and the pane and window set themselves up to be big enough
        canvas = new Canvas(width,height);  
        pane.getChildren().add(canvas);     // add the canvas to the pane
        
        // infoText box for the score - a label which we position on 
        //the canvas with translations in X and Y coordinates
        infoText = new Label("Score: " + model.getScore());
        infoText.setTranslateX(10);
        infoText.setTranslateY(10);
        pane.getChildren().add(infoText);  // add label to the pane
        
        infoTextLives = new Label("Lives: * * *");
        infoTextLives.setTranslateX(390);
        infoTextLives.setTranslateY(10);
        pane.getChildren().add(infoTextLives);

        // add the complete GUI to the scene
        scene = new Scene(pane);   
        scene.getStylesheets().add("breakout.css"); // tell the app to use our css file

        // Add an event handler for key presses. We use the View object itself
        // and provide a handle method to be called when a key is pressed.
        scene.setOnKeyPressed(this);
        scene.setOnKeyReleased(this);
    }
    
    public void setSceneEndScore() {
    	GridPane gridEndScore = new GridPane();
    	gridEndScore.setId("EndScore");
    	gridEndScore.setAlignment(Pos.CENTER);
    	gridEndScore.setVgap(10);
    	
    	VBox vboxEndScore = new VBox(10);
    	
    	EndScoreScore = new Label("Your final score is: " + model.getScore());
    	EndLeaderScore = new Label("...");
    	
    	HBox hboxEndScore = new HBox(10);
    	scoreName = new TextField();
    	submitScore = new Button("Submit");
    	submitScore.setDisable(true);
    	hboxEndScore.getChildren().addAll(scoreName, submitScore);
    	
    	Button ES2MM = new Button("Back to Main Menu");
    	ES2MM.setOnAction(e -> window.setScene(mainMenu));
    	
    	vboxEndScore.getChildren().addAll(EndScoreScore, EndLeaderScore, hboxEndScore, ES2MM);
    	gridEndScore.getChildren().add(vboxEndScore);
    	endScore = new Scene(gridEndScore, width, height);
		endScore.getStylesheets().add("breakout.css");    	
    	
    }

    // Event handler for key presses - it just passes the event to the controller
    public void handle(KeyEvent event)
    {
        // send the event to the controller
    	if (event.getEventType() == KeyEvent.KEY_PRESSED || event.getCode().isArrowKey()) {
    		controller.userKeyInteraction( event );
    	}
    }
    
    public void startGame() {
    	window.setScene(scene);
    	model.startGame();
    }
    
    // drawing the game
    public void drawPicture()
    {
        // the ball movement is runnng 'i the background' so we have
        // add the following line to make sure
        synchronized( Model.class )   // Make thread safe (because the bal
        {
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // clear the canvas to redraw
            gc.setFill( Color.BLACK );
            gc.fillRect( 0, 0, width, height );
            
            // update score
            infoText.setText("Score: " + score);

            // draw the bat and ball
            displayGameObj( gc, ball, "ball" );   // Display the Ball
            displayGameObj( gc, bat, "bat"  );   // Display the Bat
            
            for (int i = 0; i < balls.size(); i++) {
                displayGameObj (gc, balls.get(i), "ball");
            }
            
            for (int i = 0; i < PUps.size(); i++) {
                displayGameObj (gc, PUps.get(i), "rect");
            }    

            // *[2]****************************************************[2]*
            // * Display the bricks that make up the game                 *
            // * Fill in code to display bricks from the ArrayList        *
            // * Remember only a visible brick is to be displayed         *
            // ************************************************************
            for (int i = 0; i < bricks.size(); i++) {
                displayGameObj (gc, bricks.get(i), "rect");
            }     
        }
    }

    // Display a game object - it is just a rectangle on the canvas
    public void displayGameObj( GraphicsContext gc, GameObj go, String shape)
    {
        gc.setFill( go.colour );
        if (shape == "ball") {
        	gc.fillOval( go.topX, go.topY, go.width, go.height );
        }
        else if (shape == "bat") {
        	gc.fillRoundRect( go.topX, go.topY, go.width, go.height, 10, 10 );
        }
        else {
        	gc.fillRect( go.topX, go.topY, go.width, go.height );
        }
        
    }

    // This is how the Model talks to the View
    // This method gets called BY THE MODEL, whenever the model changes
    // It has to do whatever is required to update the GUI to show the new model status
    public void update()
    {
        // Get from the model the ball, bat, bricks & score
        ball    = model.getBall();              // Ball
        balls   = model.getBalls();
        bricks  = model.getBricks();            // Bricks
        PUps    = model.getPUps();
        bat     = model.getBat();               // Bat
        score   = model.getScore();             // Score
        //Debug.trace("Update");
        drawPicture();                     // Re draw game
    }
    
    public void endGame() {
    	window.setScene(endScore);
    }
}
