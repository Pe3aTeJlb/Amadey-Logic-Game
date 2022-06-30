/*
Copyright (C) Pplos Studio

    This file is a part of Amadey Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Amadey-Logic-Game

    CircuitJS1 was originally written by Paul Falstad.
	http://www.falstad.com/

	JavaScript conversion by Iain Sharp.
	http://lushprojects.com/

    Avrora Logic Game is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 1, 2 of the License, or
    (at your option) any later version.
    Avrora Logic Game is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with Avrora Logic Game.  If not, see <http://www.gnu.org/licenses/>.
*/

package AmadeyLogicGame;

import AmadeyLogicGame.circgen.CircuitSynthesizer;
import AmadeyLogicGame.util.IconsManager;
import AmadeyLogicGame.util.LC_gui;
import AmadeyLogicGame.util.Localizer;
import AmadeyLogicGame.circprocess.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Vector;

public class Main extends Application {

    //AnchorPane
    private AnchorPane root;

    //Menubar
    private MenuBar menuBar;
    private CheckMenuItem printableCheckItem;
    private CheckMenuItem alternativeColorCheckItem;
    private Menu infoMenu;

    //MainMenu container
    private VBox mainmenuVBox;

    //Canvas + canvas graphics
    private Canvas cv;
    private GraphicsContext cvcontext;
    private Graphics g;
    private double[] transform;
    private int width, height;
    private AnimationTimer updater;
    private double dragScreenX, dragScreenY;
    private CircuitElm mouseElm = null;
    private final int POSTGRABSQ=25;
    private final int MINPOSTGRABSIZE = 256;
    private Rectangle circuitArea;

    //Game vars
    private CirSim cirSim;
    private Vector<CircuitElm> elmList;
    private ArrayList<String> currOutput; //Хранят текущее состояние выходов функций
    private ArrayList<CircuitElm> FunctionsOutput;//Список выходных и входных элементов функций
    private ArrayList<SwitchElm> FunctionsInput;
    private int currOutputIndex = 0; // текущая позиция кристалла
    private int currCrystalPosY = 0;

    private boolean gamePause = true;

    public static int tickCounter = 0;

    private boolean refreshGameState = true;
    private int level = 1;
    private Gif crystal;
    private boolean lose = false;
    private boolean canToggle = true; //disable

    private boolean isTest;
    private double Score = 100;
    private final int testTime = 15; //minutes for test
    private double TimeSpend;
    private double penaltyPerFrame = 0;
    private final double failPenalty = 20;

    private final int maxLevelCount = 10;

    //Log
    private final String nl = System.getProperty("line.separator");
    private StringBuilder log = new StringBuilder();

    private final Localizer lc = LC_gui.getInstance();

    private boolean debug = true;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        root = new AnchorPane();

        primaryStage.titleProperty().bind(LC_gui.getInstance().createStringBinding("Title"));
        primaryStage.getIcons().add(IconsManager.AmadayLogicGame);
        primaryStage.setScene(new Scene(root, 800, 600));
        //primaryStage.setOnHidden(event -> updater.stop());
        primaryStage.show();

        cirSim = new CirSim();

        createMenuBar();
        createCanvas();
        createMainMenu();

        //initialize wire color
        CircuitElm.setColorScale(alternativeColorCheckItem.isSelected());
/*
        for(int i = 0; i < 1000; i++){
            System.out.println(i);
            startGame(false);
            stopGame();
        }

 */
        /*
        for(int i = 7; i < 11; i++){
            int m = i;
            level = m;
            for(int j = 0; j < 100; j++){
                System.out.println(i+" "+j);
                startGame(false);
                stopGame();
            }
        }

         */

    }

    private void createMainMenu() {

        mainmenuVBox = new VBox();

        root.getChildren().add(mainmenuVBox);
        AnchorPane.setLeftAnchor(mainmenuVBox, 0.);
        AnchorPane.setTopAnchor(mainmenuVBox, 0.);
        AnchorPane.setRightAnchor(mainmenuVBox, 0.);
        AnchorPane.setBottomAnchor(mainmenuVBox, 0.);

        mainmenuVBox.setAlignment(Pos.CENTER);
        mainmenuVBox.setSpacing(25);

        Button testButton = new Button();
        testButton.textProperty().bind(lc.createStringBinding("Test"));
        testButton.setPrefSize(150, 25);
        testButton.setOnAction(event -> startGame(true));

        Button trainingButton = new Button();
        trainingButton.textProperty().bind(lc.createStringBinding("Training"));
        trainingButton.setPrefSize(150, 25);
        trainingButton.setOnAction(event -> startGame(false));

        Button rulesButton = new Button();
        rulesButton.textProperty().bind(lc.createStringBinding("Rules"));
        rulesButton.setPrefSize(150, 25);
        rulesButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(lc.get("RulesTitle"));
            alert.setHeaderText(lc.get("Rules"));
            alert.setContentText(lc.get("RulesBody"));

            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.AmadayLogicGame);

            alert.showAndWait();
        });

        mainmenuVBox.getChildren().addAll(testButton, trainingButton, rulesButton);

    }

    private void createMenuBar() {

        menuBar = new MenuBar();

        root.getChildren().add(menuBar);
        AnchorPane.setLeftAnchor(menuBar, 0.);
        AnchorPane.setTopAnchor(menuBar, 0.);
        AnchorPane.setRightAnchor(menuBar, 0.);
        menuBar.setPrefHeight(25);



        Menu editMenu = new Menu();
        editMenu.textProperty().bind(lc.createStringBinding("Edit"));

        MenuItem centerCircItem = new MenuItem();
        centerCircItem.textProperty().bind(lc.createStringBinding("CenterCirc"));
        centerCircItem.setOnAction(event -> centreCircuit());

        MenuItem zoomItem = new MenuItem();
        zoomItem.textProperty().bind(lc.createStringBinding("Zoom100"));
        zoomItem.setOnAction(event -> setCircuitScale(0.5));

        MenuItem zoomInItem = new MenuItem();
        zoomInItem.textProperty().bind(lc.createStringBinding("ZoomIn"));
        zoomInItem.setOnAction(event -> zoomCircuit(20));

        MenuItem zoomOutItem = new MenuItem();
        zoomOutItem.textProperty().bind(lc.createStringBinding("ZoomOut"));
        zoomOutItem.setOnAction(event -> zoomCircuit(-20));

        editMenu.getItems().addAll(centerCircItem, zoomItem, zoomInItem, zoomOutItem);



        Menu optionsMenu = new Menu();
        optionsMenu.textProperty().bind(lc.createStringBinding("options"));

        printableCheckItem = new CheckMenuItem();
        printableCheckItem.textProperty().bind(lc.createStringBinding("WBack"));

        alternativeColorCheckItem = new CheckMenuItem();
        alternativeColorCheckItem.textProperty().bind(lc.createStringBinding("AltColor"));
        alternativeColorCheckItem.setOnAction(event -> CircuitElm.setColorScale(alternativeColorCheckItem.isSelected()));

        optionsMenu.getItems().addAll(printableCheckItem, alternativeColorCheckItem);



        Menu toolsMenu = new Menu();
        toolsMenu.textProperty().bind(lc.createStringBinding("Tools"));

        MenuItem regenCircItem = new MenuItem();
        regenCircItem.textProperty().bind(lc.createStringBinding("Regen"));
        regenCircItem.setOnAction(event -> generateCircuit());

        MenuItem lvlUpItem = new MenuItem();
        lvlUpItem.textProperty().bind(lc.createStringBinding("LevelUp"));
        lvlUpItem.setOnAction(event -> {
            level += 1;
            generateCircuit();
        });

        toolsMenu.getItems().addAll(regenCircItem, lvlUpItem);



        Menu aboutMenu = new Menu();
        aboutMenu.textProperty().bind(lc.createStringBinding("About"));

        MenuItem rulesItem = new MenuItem();
        rulesItem.textProperty().bind(lc.createStringBinding("Rules"));
        rulesItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(lc.get("RulesTitle"));
            alert.setHeaderText(lc.get("Rules"));
            alert.setContentText(lc.get("RulesBody"));

            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.AmadayLogicGame);

            alert.showAndWait();
        });

        MenuItem devItem = new MenuItem();
        devItem.textProperty().bind(lc.createStringBinding("Developers"));
        devItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(lc.get("DevelopersTitle"));
            alert.setHeaderText("A Pplos Studio Game");
            alert.setContentText(lc.get("DevelopersBody"));

            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.AmadayLogicGame);

            alert.showAndWait();
        });

        aboutMenu.getItems().addAll(rulesItem, devItem);



        Label backToMenuLable = new Label();
        backToMenuLable.textProperty().bind(lc.createStringBinding("ToMenu"));
        backToMenuLable.setOnMouseClicked(event -> stopGame());

        Menu backToMenu = new Menu();
        backToMenu.setGraphic(backToMenuLable);



        infoMenu = new Menu();
        infoMenu.setText(lc.get("Score")+ " " + (int)Score);



        menuBar.getMenus().addAll(editMenu, optionsMenu, toolsMenu, aboutMenu, backToMenu, infoMenu);
        if(!debug)menuBar.getMenus().remove(toolsMenu);

        menuBar.setDisable(true);
        menuBar.setVisible(false);

    }

    private void createCanvas() {

        transform = new double[6];

        cv = new Canvas();
        AnchorPane.setBottomAnchor(cv, 0.);
        root.getChildren().add(cv);

        root.widthProperty().addListener((observable, oldValue, newValue) ->{
           setCanvasSize();
           clearRect40K(transform[4],transform[5]);
        });
        root.heightProperty().addListener((observable, oldValue, newValue) ->{
            setCanvasSize();
            clearRect40K(transform[4],transform[5]);
        });

        cv.setOnMousePressed(event -> {

            CircuitElm newMouseElm=null;
            int sx = (int)event.getX();
            int sy = (int)event.getY();
            int gx = inverseTransformX(sx);
            int gy = inverseTransformY(sy);

            if(event.getButton() == MouseButton.PRIMARY) {

                if (mouseElm != null &&
                        mouseElm.getHandleGrabbedClose(gx, gy, POSTGRABSQ, MINPOSTGRABSIZE) >= 0) {

                    newMouseElm = mouseElm;

                } else {

                    int bestDist = 100000000;
                    int bestArea = 100000000;

                    for (int i = 0; i != elmList.size(); i++) {

                        CircuitElm ce = getElm(i);
                        if(ce!=null) {
                            if (ce.boundingBox.contains(gx, gy)) {
                                int j;
                                int area = ce.boundingBox.width * ce.boundingBox.height;
                                int jn = ce.getPostCount();
                                if (jn > 2)
                                    jn = 2;
                                for (j = 0; j != jn; j++) {
                                    Point pt = ce.getPost(j);
                                    int dist = Graphics.distanceSq(gx, gy, pt.x, pt.y);

                                    // if multiple elements have overlapping bounding boxes,
                                    // we prefer selecting elements that have posts close
                                    // to the mouse pointer and that have a small bounding
                                    // box area.
                                    if (dist <= bestDist && area <= bestArea) {
                                        bestDist = dist;
                                        bestArea = area;
                                        newMouseElm = ce;
                                    }
                                }
                                // prefer selecting elements that have small bounding box area (for
                                // elements with no posts)
                                if (ce.getPostCount() == 0 && area <= bestArea) {
                                    newMouseElm = ce;
                                    bestArea = area;
                                }
                            }
                        }
                    }

                }

                setMouseElm(newMouseElm);
            }

            dragScreenX = event.getX();
            dragScreenY = event.getY();


            if(debug) {

              //  System.out.println(inverseTransformX((int)event.getX()) + " " + inverseTransformY((int)event.getY()));

                if (event.getButton() == MouseButton.MIDDLE) {
                    generateCircuit();
                }

                if (event.getButton() == MouseButton.SECONDARY) {
                    if(isTest)level += 1;
                    generateCircuit();
                }

            }

        });

        cv.setOnMouseDragged(event -> {
            double dx = event.getX() - dragScreenX;
            double dy = event.getY() - dragScreenY;
            if (dx == 0 && dy == 0) {return;}
            clearRect40K(transform[4],transform[5]);

            transform[4] += dx;
            transform[5] += dy;
            dragScreenX = event.getX();
            dragScreenY = event.getY();

        });

        cv.setOnScroll(event -> {
            clearRect40K();
            zoomCircuit(event.getDeltaY());
        });

        cvcontext = cv.getGraphicsContext2D();
        g = new Graphics(cvcontext);

        setCanvasSize();

        centreCircuit();

        penaltyPerFrame = Score / (testTime * 60 * 60);

        updater = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if(isTest)Score -= penaltyPerFrame;
                TimeSpend += 0.015;
                infoMenu.setText(lc.get("Score") + " " + (int)Score);
                updateCircuit();

                if(isTest && Score < 0 && !lose){
                    Platform.runLater (() -> {
                        updater.stop();
                        gameOver();
                    });
                }

            }
        };

        clearRect40K();

    }

    /**Game init**/
    /**Circuit cunstrucrion**/
    public void startGame(boolean isTest) {

        gamePause = false;
        lose = false;
        level = 1;

        mainmenuVBox.setDisable(true);
        mainmenuVBox.setVisible(false);
        menuBar.setDisable(false);
        menuBar.setVisible(true);

        currOutputIndex = 0;
        currCrystalPosY = 0;

        transform[4] = 0;
        transform[5] = 0;

        this.isTest = isTest;

        if(isTest) {
            Score = 100;
        } else {
            Score = 0;
        }

        generateCircuit();

        if(debug) {
            System.out.println(log);
        }

        //Update circuit 10 times before game start to populate circ volts matrix
        for(int i = 0; i < 10; i++){
            cirSim.runCircuit();
            cirSim.analyzeCircuit();
        }

        updater.start();

    }

    public void stopGame() {

        updater.stop();

        menuBar.setDisable(true);
        menuBar.setVisible(false);

        gamePause = true;
        setCanvasSize();
        clearRect40K(transform[4],transform[5]);

        mainmenuVBox.setDisable(false);
        mainmenuVBox.setVisible(true);

    }

    private void generateCircuit() {

        if(level <= maxLevelCount) {

            CircuitSynthesizer v = new CircuitSynthesizer();

            if(isTest) {
                v.Synthesis(width, height, level);
            }else {
                Score = 0;
                v.Synthesis(width, height);
            }

            log = new StringBuilder("Log" + nl + "Log of level " + level + nl);
            log.append(v.getLog());

            elmList = v.elmList;
            cirSim.setElmList(elmList);
            FunctionsOutput = v.outElems;
            FunctionsInput = v.inElems;
            currOutput = new ArrayList<>();
            currCrystalPosY = FunctionsOutput.get(currOutputIndex).y - 80;

            crystal = new Gif("GIF", 1024, 1024, 128, 1);

        } else {
            Platform.runLater (() -> {
                updater.stop();
                Shrek();
            });
        }

    }

    /**VOID UPDATE**/

    private void updateCircuit() {

        setCanvasSize();
        cirSim.runCircuit();
        cirSim.analyzeCircuit();

        clearRect40K(transform[4], transform[5]); //clear current frame to avoid GIF fall trail

        if(refreshGameState)tickCounter++;

        CircuitElm.selectColor = Color.CYAN;

        if (printableCheckItem.isSelected()) {

            CircuitElm.whiteColor = Color.BLACK;
            CircuitElm.lightGrayColor = Color.BLACK;
            g.setColor(Color.WHITESMOKE);

        } else {

            CircuitElm.whiteColor = Color.WHITE;
            CircuitElm.lightGrayColor = Color.WHITE;
            g.setColor(Color.BLACK);

        }

        g.fillRect(0, 0, (int)g.context.getCanvas().getWidth(), (int)g.context.getCanvas().getHeight());

        cvcontext.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );


        //Отрисовываем схему с конца, т.к. fx не может в нормальные слои, а у меня тут бага, что не все жирные точки соединений попадают в
        //PostDrawList, поэтому пока так будет.

        //for (int i = 0; i != elmList.size(); i++) {
        for (int i = elmList.size()-1; i >= 0; i--) {

            if(printableCheckItem.isSelected()){
                g.setColor(Color.BLACK);
            }else{
                g.setColor(Color.WHITESMOKE);
            }

            try {
                getElm(i).draw(g);
            }catch(Exception ee) {
                ee.printStackTrace();
                log.append("exception while drawing ").append(ee).append(nl);
            }

        }

        Vector<Point> postDrawList = cirSim.getPostDrawList();
        for (int i = 0; i != postDrawList.size(); i++){
            CircuitElm.drawPost(g, postDrawList.get(i));
        }

        Vector<Point> badConnectionList = cirSim.getBadConnectionList();
        for (int i = 0; i != badConnectionList.size(); i++) {
            Point cn = badConnectionList.get(i);
            g.setColor(Color.RED);
            g.fillOval(cn.x-3, cn.y-3, 7, 7);
        }

        if(tickCounter > 6 && refreshGameState && currOutputIndex < FunctionsOutput.size()) {

            currOutput = new ArrayList<>();

            for (CircuitElm circuitElm : FunctionsOutput) {
                String s = circuitElm.volts[0] < 2.5 ? "0" : "1";
                currOutput.add(s);
            }

            log.append("curr out index ").append(currOutputIndex).append(nl);

            if(currOutputIndex < FunctionsOutput.size()) {

                log.append("currOutput ").append(currOutput).append(nl);

                //Условия поигрыша
                if(currOutputIndex != FunctionsOutput.size()-1 && currOutput.get(currOutputIndex).equals("0") && currOutput.get(currOutputIndex+1).equals("0")) {

                    log.append("Game Over").append(nl);
                    //Ищем, сколько платформ кристал должен пролететь прежде чем разбиться
                    for(int i = currOutputIndex; i < FunctionsOutput.size(); i++) {
                        if(currOutput.get(i).equals("0")) {
                            currOutputIndex += 1;
                        } else {
                            break;
                        }
                    }

                    lose = true;

                    if(isTest){
                        Score -= failPenalty;
                    }

                    crystal.RestartGif(30);

                }

                if(currOutputIndex != FunctionsOutput.size()) {
                    //Переход на след платформу
                    if (currOutputIndex != FunctionsOutput.size() - 1 && currOutput.get(currOutputIndex).equals("0") && currOutput.get(currOutputIndex + 1).equals("1")) {
                        currOutputIndex++;
                        log.append("new curr out index ").append(currOutputIndex);
                    }

                    //заглушка для последней платформы
                    if (currOutputIndex == FunctionsOutput.size() - 1 && currOutput.get(currOutputIndex).equals("0")) {
                        currOutputIndex++;
                        log.append("new curr out index ").append(currOutputIndex).append(nl);
                    }

                    //Переход на след уровень
                    if (currOutputIndex == FunctionsOutput.size()) {

                        currOutputIndex = 0;
                        log.append("You Won!").append(nl);
                        elmList.clear();
                        level++;

                        generateCircuit();
                    }
                }
            }

            refreshGameState = false;

        }

        //Отрисовка падения кристалла
        if(currOutputIndex == FunctionsOutput.size()) {

            canToggle = false;
            currCrystalPosY += 7;

            if(lose && currCrystalPosY > FunctionsOutput.get(3).y) {
                crystal.Play();
            }
            if(crystal.gifEnded && lose) {
                if(isTest && Score < 0){
                    Platform.runLater (() -> {
                        updater.stop();
                        gameOver();
                    });
                } else {
                    restartLevel();
                }
            }

            cvcontext.drawImage(crystal.img, crystal.currX, crystal.currY, crystal.frameWidth, crystal.frameWidth,FunctionsOutput.get(0).x+130,currCrystalPosY,50,50);

        } else if(currOutputIndex < FunctionsOutput.size() && currCrystalPosY < FunctionsOutput.get(currOutputIndex).y-67) {

            canToggle = false;
            currCrystalPosY += 5;

            cvcontext.drawImage(crystal.img, crystal.currX, crystal.currY,crystal.frameWidth,crystal.frameWidth,FunctionsOutput.get(0).x+130,currCrystalPosY,50,50);

        } else {

            //ожидание окончания гифки и перезапуск уровня. См класс Gif
            if(lose) {
                crystal.Play();
            } else {
                canToggle = true;
            }

            cvcontext.drawImage(crystal.img, crystal.currX, crystal.currY,crystal.frameWidth,crystal.frameWidth,FunctionsOutput.get(0).x+130,currCrystalPosY,50,50);

            if(crystal.gifEnded && lose) {
                if(isTest && Score < 0){
                    Platform.runLater (() -> {
                        updater.stop();
                        gameOver();
                    });
                } else {
                    restartLevel();
                }
            }

        }

    }

    private void setCanvasSize(){

        width = (int)root.getWidth();
        if(gamePause){
            height = (int)root.getHeight();
        } else {
            height = (int)(root.getHeight() - menuBar.getPrefHeight());
        }

        cv.setWidth(width);
        cv.setHeight(height);

        circuitArea = new Rectangle(0, 0, width, height);
    }

    private void gameOverTrigger() {
        refreshGameState = true;
        tickCounter = 0;
    }

    private void restartLevel() {

        currOutputIndex = 0;

        lose = false;

        for (SwitchElm switchElm : FunctionsInput) {
            switchElm.position = 0;
        }

        crystal.RestartGif(1);
        currOutputIndex = 0;
        currCrystalPosY = FunctionsOutput.get(currOutputIndex).y - 80;

        canToggle = true;

    }

    private void Shrek() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(lc.get("Title"));
        alert.setHeaderText(lc.get("GoodEnd"));
        alert.getDialogPane().setContent(IconsManager.getImageView("311.png"));
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.AmadayLogicGame);

        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent()) {
            // alert is exited, no button has been pressed.
        } else if(result.get() == ButtonType.OK){
            stopGame();
        }

        log.append("Exit").append(nl);

    }

    private void gameOver() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(lc.get("Title"));
        alert.setHeaderText(lc.get("BadEnd"));

        alert.getDialogPane().setContent(IconsManager.getImageView("Shrek.png"));
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.AmadayLogicGame);

        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent()) {
            // alert is exited, no button has been pressed.
        } else if(result.get() == ButtonType.OK){
            stopGame();
        }

        log.append("Exit").append(nl);

    }

    /**BEHAVIOUR**/

    private void zoomCircuit(double dy) {

        double newScale;
        double oldScale = transform[0];
        double val = dy*.005;
        newScale = Math.max(oldScale+val, .2);
        newScale = Math.min(newScale, 2.5);
        setCircuitScale(newScale);

    }

    private void setCircuitScale(double newScale) {

        int cx = inverseTransformX((double) circuitArea.width / 2);
        int cy = inverseTransformY((double) circuitArea.height / 2);

        transform[0] = newScale;
        transform[3] = newScale;

        // adjust translation to keep center of screen constant
        // inverse transform = (x-t4)/t0

        transform[4] = (double) circuitArea.width / 2 - cx * newScale;
        transform[5] = (double) circuitArea.height / 2 - cy * newScale;


    }

    private void centreCircuit() {

        transform[0] = transform[3] = 0.5;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

    }



    /**MOUSE EVENTS**/

    private void setMouseElm(CircuitElm ce) {

        if (ce != mouseElm) {
            if (mouseElm!=null)
                mouseElm.setMouseElm(false);
            if (ce!=null)
                ce.setMouseElm(true);
            mouseElm=ce;
        }

        if (mouseElm != null && (mouseElm instanceof SwitchElm)) {
            SwitchElm se = (SwitchElm) mouseElm;
            if(canToggle){
                se.toggle();
                gameOverTrigger();
            }
        }

    }



    /**TOOLS**/

    private void clearRect40K() {

        if (printableCheckItem.isSelected()) {
            cvcontext.setFill(Color.WHITESMOKE);
        } else {
            cvcontext.setFill(Color.BLACK);
        }
        cvcontext.fillRect(0, 0, (cv.getWidth() / transform[0]) * 2, (cv.getHeight() / transform[0]) * 2);

    }

    private void clearRect40K(double prevX, double prevY) {

        if (printableCheckItem.isSelected()) {
            cvcontext.setFill(Color.WHITESMOKE);
        } else {
            cvcontext.setFill(Color.BLACK);
        }

        cvcontext.fillRect(-prevX/transform[0],-prevY/transform[0],cv.getWidth()/transform[0],cv.getHeight()/transform[0]);

    }

    // convert screen coordinates to grid coordinates by inverting circuit transform
    private int inverseTransformX(double x) {
        return (int) ((x-transform[4])/transform[0]);
    }

    private int inverseTransformY(double y) {
        return (int) ((y-transform[5])/transform[3]);
    }

    public CircuitElm getElm(int n) {
        //if (n >= elmList.size()){return null;}
        return elmList.elementAt(n);
    }

}
