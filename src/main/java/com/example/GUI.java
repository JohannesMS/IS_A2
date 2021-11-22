package com.example;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.FieldPortrayal2D;
import sim.portrayal.grid.*;
import java.awt.*;
import javax.swing.*;
import sim.portrayal.simple.*;

public class GUI extends GUIState {
    
    public Display2D display;
    public JFrame displayFrame;
    ObjectGridPortrayal2D fieldPortrayal = new ObjectGridPortrayal2D();
    public GUI(){
        super(new GameBoard(System.currentTimeMillis(),0));
    }

    public void start(){
        super.start();
        setupPortrayals();
    }

    public void load(){

    }

    public void setupPortrayals(){
        GameBoard board = (GameBoard) state;
        fieldPortrayal.setField(board.field);
        fieldPortrayal.setPortrayalForAll(new RectanglePortrayal2D());

        display.reset();
        display.setBackdrop(Color.white);

        display.repaint();
    }

    public void init(Controller c){
        super.init(c);
        display = new Display2D(30, 30, this);
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Lights Logic Puzzle");
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display.attach(fieldPortrayal, "Field");

    }

    public void quit(){
        super.quit();
        if(displayFrame!=null){displayFrame.dispose();}
        displayFrame = null;
        display = null;
    }


    public GUI(SimState state){
        super(state);   
    }

    public static String getName(){
        return "Lights Logic Puzzle";
    }

    public static void main (String[] args){
        GUI vid = new GUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }
}
