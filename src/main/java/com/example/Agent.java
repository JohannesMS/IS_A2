package com.example;

import sim.engine.*;
import sim.field.grid.ObjectGrid2D;

public class Agent implements Steppable {
    
//TODO
//Wann wird geprüft ob die Constraints erfüllt werden?
//Wie entscheidet der Spieler wo was platziert wird? -> aus Expertenwissen bedienen

//Strategie 1
//Bei einem 25x25 Feld gibt es 2 hoch 25 Kombinationen, jede könnte probiert, validiert und benutzt oder verworfen werden. Laufzeittechnisch beschissen aber gut zum Vergleich
//Strategie 2
//Alle sicher platzierbaren Birnen platzieren, dann wie Strategie 1 verfahren. Reduziert den Suchbaum
//Strategie 3
//Smarteste Stragie, aber welche?


    public ObjectGrid2D tempBoard = null;
    public GameBoard gameboard = null;
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
        this.gameboard = (GameBoard)state;
        this.tempBoard = this.gameboard.field;
        //if(numNotIlluminated() == 0){gameboard.finish();}
        //System.out.println(numNotIlluminated());
        //setBulb(0, 0);
        System.out.println("There are "+numNotIlluminated() + " fields which are not illuminated");
        System.out.println("There are "+numPlaceableBulbs() + " fields in which a bulb could be placed");
        placeTrivialBulbs(2);
        System.out.println("There are "+numNotIlluminated() + " fields which are not illuminated");
        System.out.println("There are "+numPlaceableBulbs() + " fields in which a bulb could be placed");
        System.out.print(validateSolution());

        

    }

    public void setBulb(int x, int y){
        if(!checkConstraintViolation(x, y)){
        tempBoard.set(x, y, new Bulb());
        illuminate(x, y);
        }
    }


    public int numPlaceableBulbs(){
        int placeable= 0;
        for(int x = 0; x<tempBoard.width;x++){
            for(int y = 0; y<tempBoard.height;y++){
                if(isEmptyField(x, y)){
                    EmptyField tempField = (EmptyField) tempBoard.get(x, y);
                    if (!tempField.implaceable && !tempField.illuminated){placeable++;}
                }
            }
        }
        return placeable;

    }

    public int numNotIlluminated(){
        int notIlluminated = 0;
        for(int x = 0; x<tempBoard.width;x++){
            for(int y = 0; y<tempBoard.height;y++){
                if(isEmptyField(x, y) && !isIlluminated(x, y)){notIlluminated++;}
            }
        }
        return notIlluminated;
    }

    public boolean checkConstraintViolation(int x, int y){
        //Prüfung ob die numbered wall constraints erfüllt sind ist etwas schwerer, erstmal das einfache
        //Beispiel: Nur setten wenn die adjacent numbered wall>0 ist, dann setzen und die nummer decrementen
        if(!isWall(x, y) && !isBulb(x, y) && isEmptyField(x, y) && !isIlluminated(x, y) && !isImplaceable(x, y)){
            return false;
        }
        return true;
    }

    public boolean isWall(int x, int y){
        if(x>tempBoard.width-1 || x<0 || y>tempBoard.height-1|| y<0){return true;}
        if(tempBoard.get(x, y).getClass() == Wall.class){return true;}
        else{return false;}

    }

    public boolean isEmptyField(int x, int y){
        if(x>tempBoard.width-1 || x<0 || y>tempBoard.height-1|| y<0){return false;}
        if(tempBoard.get(x, y).getClass() == EmptyField.class){return true;}
        else{return false;}
    }

    public boolean isImplaceable(int x, int y){
        EmptyField tempField = (EmptyField) tempBoard.get(x, y);
        return tempField.implaceable;
    }

    public boolean isIlluminated(int x, int y){
       EmptyField tempField = (EmptyField) tempBoard.get(x, y);
       return tempField.illuminated;
    }

    public boolean isOutOfBounds(int x, int y){
        if(x>tempBoard.width-1 || x<0 || y>tempBoard.height-1|| y<0){return true;}
        else{return false;}
    }

    public boolean isBulb(int x, int y){
        if(isOutOfBounds(x, y)){return false;}
        if(tempBoard.get(x, y).getClass() == Bulb.class){return true;}
        else{return false;}
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

    public boolean validateSolution(){
        //1. Die numbered Walls prüfen
        //Ob birnen sich gegenseitig beleuchten wird während dem Platzieren geprüft
        //Sind alle Felder beleuchetet?
        //if(numNotIlluminated() == 0 && )
        int tempX;
        int tempY;
        int bulbCounter;
        boolean constraintViolation = false;

        for(int i = 0; i<gameboard.numberedWallLocations.size();i++){
            bulbCounter = 0;
            tempX = gameboard.numberedWallLocations.get(i)[0];
            tempY = gameboard.numberedWallLocations.get(i)[1];
            Wall tempWall = (Wall) tempBoard.get(tempX, tempY);

            if(isBulb(tempX+1,tempY)){bulbCounter++;}
            if(isBulb(tempX-1,tempY)){bulbCounter++;}
            if(isBulb(tempX,tempY+1)){bulbCounter++;}
            if(isBulb(tempX,tempY-1)){bulbCounter++;}

            System.out.println("Wall number "+i);
            System.out.println(bulbCounter);
            System.out.println(tempWall.numberAdjascentBulbs);

            if(bulbCounter > tempWall.numberAdjascentBulbs){constraintViolation = true;}
        }

        return constraintViolation;
        //if(numNotIlluminated() == 0 && !constraintViolation) {return true;}
        //else{return false;}
    }

    public void placeTrivialBulbs(int degreeOfSmartmode){
        //first degree smartmode
        //place trivial bulbs (including surrounding walls)
        //only one iteration

        //second degree smartmode
        //place trivial bulbs iteratively (including surrounding walls)


        //check for implaceable fields in a x++ and y++ loop


        int smartmode = degreeOfSmartmode;
        boolean trivialTrigger;
        int tempX;
        int tempY;
        int tempFreeNeighbors;

        for(int i = 0;i<gameboard.emptyFieldLocations.size();i++){
            int wallCounter = 0;
            tempX = gameboard.emptyFieldLocations.get(i)[0];
            tempY = gameboard.emptyFieldLocations.get(i)[1];
            if(isWall(tempX+1,tempY)){wallCounter++;}
            if(isWall(tempX-1,tempY)){wallCounter++;}
            if(isWall(tempX,tempY+1)){wallCounter++;}
            if(isWall(tempX,tempY-1)){wallCounter++;}
            if(wallCounter == 4){setBulb(tempX, tempY);}
        }

        do{
            trivialTrigger = false;
            for(int i=0;i<gameboard.numberedWallLocations.size();i++){
                tempFreeNeighbors = 0;
                tempX = gameboard.numberedWallLocations.get(i)[0];
                tempY = gameboard.numberedWallLocations.get(i)[1];
                Wall tempWall = (Wall) tempBoard.get(tempX, tempY);
    
                //Wenn die Mauer eine Null anzeigt wird die von neumann nachbarschaft als nicht platzierbar gekennezeichnet
                if(tempWall.numberLeftoverBulbs == 0){
                    if(isEmptyField(tempX+1, tempY)){
                        EmptyField tempField = (EmptyField) tempBoard.get(tempX+1, tempY);
                        if(!tempField.implaceable && smartmode==2){trivialTrigger=true;}
                        tempField.implaceable = true;
                        
                    }
                    if(isEmptyField(tempX-1, tempY)){
                        EmptyField tempField = (EmptyField) tempBoard.get(tempX-1, tempY);
                        if(!tempField.implaceable && smartmode==2){trivialTrigger=true;}
                        tempField.implaceable = true;
                        
                    }
                    if(isEmptyField(tempX, tempY+1)){
                        EmptyField tempField = (EmptyField) tempBoard.get(tempX, tempY+1);
                        if(!tempField.implaceable && smartmode==2){trivialTrigger=true;}
                        tempField.implaceable = true;
                        
                    }
                    if(isEmptyField(tempX, tempY-1)){
                        EmptyField tempField = (EmptyField) tempBoard.get(tempX, tempY-1);
                        if(!tempField.implaceable && smartmode==2){trivialTrigger=true;}
                        tempField.implaceable = true;
                    }
                    continue;
                }
    
                if(isEmptyField(tempX+1,tempY) && !isIlluminated(tempX+1, tempY) && !isImplaceable(tempX+1, tempY)){tempFreeNeighbors++;}
                if(isEmptyField(tempX-1,tempY) && !isIlluminated(tempX-1, tempY) && !isImplaceable(tempX-1, tempY)){tempFreeNeighbors++;}
                if(isEmptyField(tempX,tempY+1) && !isIlluminated(tempX, tempY+1) && !isImplaceable(tempX, tempY+1)){tempFreeNeighbors++;}
                if(isEmptyField(tempX,tempY-1) && !isIlluminated(tempX, tempY-1) && !isImplaceable(tempX, tempY-1)){tempFreeNeighbors++;}
                
                if(tempWall.numberLeftoverBulbs == tempFreeNeighbors){
                    if(isEmptyField(tempX+1,tempY) && !isIlluminated(tempX+1, tempY) && !isImplaceable(tempX+1, tempY)){
                        setBulb(tempX+1, tempY);
                        tempWall.numberLeftoverBulbs--;
                        if(smartmode==2){trivialTrigger = true;}
                    }
                    if(isEmptyField(tempX-1,tempY)  && !isIlluminated(tempX-1, tempY) && !isImplaceable(tempX-1, tempY)){
                        setBulb(tempX-1, tempY);
                        tempWall.numberLeftoverBulbs--;
                        if(smartmode==2){trivialTrigger = true;}
                    }
                    if(isEmptyField(tempX,tempY+1) && !isIlluminated(tempX, tempY+1) && !isImplaceable(tempX, tempY+1)){
                        setBulb(tempX, tempY+1);
                        tempWall.numberLeftoverBulbs--;
                        if(smartmode==2){trivialTrigger = true;}
                    }
                    if(isEmptyField(tempX,tempY-1) && !isIlluminated(tempX, tempY-1) && !isImplaceable(tempX, tempY-1)){
                        setBulb(tempX, tempY-1);
                        tempWall.numberLeftoverBulbs--;
                        if(smartmode==2){trivialTrigger = true;}
                    }
                }
            }
        } while(trivialTrigger == true);

        }
        
        //Entweder alle Felder durchgehen oder
        //Die Locations der Mauern speichern und hier aufrufen


        //Wenn nummer der Wand == freie Emptyfields sind dann platzieren
        //Wenn nummer 0 ist dann Felder als implaceable markieren
        //Wenn ein Feld von Mauern umzingelt ist dann eine Birne platzieren
        
        //Alle Mauern durchgehen
        
    

    public void placeTrivialBulbsIteration2(){
        //When the first iteration of trivial bulbs have been places there might be new numbered wall where the number of notIlluminated has been reduced
    }
}
