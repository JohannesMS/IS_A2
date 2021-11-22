package com.example;

import sim.engine.*;
import sim.field.grid.ObjectGrid2D;

public class Agent implements Steppable {
    
//TODO
//Wann wird geprüft ob die Constraints erfüllt werden?
//Wie entscheidet der Spieler wo was platziert wird? -> aus Expertenwissen bedienen

    static ObjectGrid2D tempBoard = null;
    public int stepcounter;
    public String strategy;

    public Agent(){
        super();
        this.stepcounter = 0;
    }

    public Agent(String strategy){
        super();
        this.stepcounter = 0;
        this.strategy = strategy;
    }


    public void step(SimState state){
        GameBoard gameboard = (GameBoard)state;
        tempBoard = gameboard.field;
        if(sizeNotBeingShinedOn() == 0){gameboard.finish();}
        System.out.println(sizeNotBeingShinedOn());
        setBulb(0, 0);
        System.out.println(sizeNotBeingShinedOn());

        
    }

    public void setBulb(int x, int y){
        if(!checkConstraintViolation(x, y)){
        tempBoard.set(x, y, new Bulb());
        illuminate(x, y);
        }
    }

    public static boolean checkConstraintViolation(int x, int y){
        //Prüfung ob die numbered wall constraints erfüllt sind ist etwas schwerer, erstmal das einfache
        //Beispiel: Nur setten wenn die adjacent numbered wall>0 ist, dann setzen und die nummer decrementen
        if(!isWall(x, y) && !isBulb(x, y) && isEmptyField(x, y) && !isIlluminated(x, y)){
            return false;
        }
        return true;
    }

    public static boolean isWall(int x, int y){
        if(x>tempBoard.width-1 || x<0 || y>tempBoard.height-1|| y<0){return true;}
        if(tempBoard.get(x, y).getClass() == Wall.class){return true;}
        else{return false;}

    }

    public static boolean isEmptyField(int x, int y){
        if(x>tempBoard.width-1 || x<0 || y>tempBoard.height-1|| y<0){return false;}
        if(tempBoard.get(x, y).getClass() == EmptyField.class){return true;}
        else{return false;}
    }

    public static boolean isIlluminated(int x, int y){
       EmptyField tempField = (EmptyField) tempBoard.get(x, y);
       if(tempField.illuminated){return true;}
       else{return false;}
    }

    public static boolean isBulb(int x, int y){
        if(tempBoard.get(x, y).getClass() == Bulb.class){return true;}
        else{return false;}
    }

    public static int sizeNotBeingShinedOn(){
        int counter = 0;
        for(int i = 0; i<tempBoard.height; i++){
            for(int y = 0; y<tempBoard.width; y++){
                if(isEmptyField(i,y) && !isIlluminated(i, y)){counter++;}
            }
        }
        return counter;
    }

    public void illuminate(int x, int y){
        int tempX = x;
        int tempY = y;
        while(isEmptyField(tempX+1, tempY)){
            EmptyField tempField = (EmptyField) tempBoard.get(tempX+1, tempY);
            tempField.illuminated = true;
            tempX++;
        }

        tempX = x;
        tempY = y;
        while(isEmptyField(tempX-1, tempY)){
            EmptyField tempField = (EmptyField) tempBoard.get(tempX-1, tempY);
            tempField.illuminated = true;
            tempX--;
        }

        tempX = x;
        tempY = y;
        while(isEmptyField(tempX, tempY+1)){
            EmptyField tempField = (EmptyField) tempBoard.get(tempX, tempY+1);
            tempField.illuminated = true;
            tempY++;
        }

        tempX = x;
        tempY = y;
        while(isEmptyField(tempX, tempY-1)){
            EmptyField tempField = (EmptyField) tempBoard.get(tempX, tempY-1);
            tempField.illuminated = true;
            tempY--;
        }
    }

}
