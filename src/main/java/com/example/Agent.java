package com.example;

import java.security.DrbgParameters.Reseed;
import java.util.Random;

import sim.engine.*;
import sim.field.grid.ObjectGrid2D;

public class Agent implements Steppable {
    
//TODO
//Wann wird geprüft ob die Constraints erfüllt werden?
//Wie entscheidet der Spieler wo was platziert wird? -> aus Expertenwissen bedienen

//Strategie 1
//Bei einem 25x25 Feld gibt es 2 hoch 625 Kombinationen, jede könnte probiert, validiert und benutzt oder verworfen werden. Wenn ich den gesamten Speicher und Rechenleistung des Universums hätte würde das auch funktionieren
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
        
        int num = smartStrategy(1000);
        System.out.println(num);
        //completeRandomStrategy();
    }

    public void setBulb(int x, int y){
        if(!checkConstraintViolation(x, y)){
        tempBoard.set(x, y, new Bulb());
        illuminate(x, y);

        if(!isOutOfBounds(x+1, y) && tempBoard.get(x+1, y).getClass() == Wall.class){
            Wall tempWall = (Wall) tempBoard.get(x+1, y);
            tempWall.numberLeftoverBulbs--;

        }
        if(!isOutOfBounds(x-1, y) && tempBoard.get(x-1, y).getClass() == Wall.class){
            Wall tempWall = (Wall) tempBoard.get(x-1, y);
            tempWall.numberLeftoverBulbs--;
            
        }
        if(!isOutOfBounds(x, y+1) && tempBoard.get(x, y+1).getClass() == Wall.class){
            Wall tempWall = (Wall) tempBoard.get(x, y+1);
            tempWall.numberLeftoverBulbs--;
            
        }
        if(!isOutOfBounds(x, y-1) && tempBoard.get(x, y-1).getClass() == Wall.class){
            Wall tempWall = (Wall) tempBoard.get(x, y-1);
            tempWall.numberLeftoverBulbs--;
            
        }
        //Wenn bulb platiert wird muss Leftoverbulbs von den Wall neighbors dekrementiert werden, in der Hauptmethode unten wird nähmlich sonst nur eine Mauer dekrementiert
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

    public void setLocationPlaceableNonTrivialBulbs(){

        for(int x = 0; x<tempBoard.width;x++){
            for(int y = 0; y<tempBoard.height;y++){
                if(isEmptyField(x, y)){
                    EmptyField tempField = (EmptyField) tempBoard.get(x, y);
                    if (!tempField.implaceable && !tempField.illuminated){
                        Integer[] temp = {x,y};
                        gameboard.locationPlaceableNonTrivialBulbs.add(temp);
                    }
                }
            }
        }
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

    public int numBulbs(){
        int numBulbs = 0;
        for(int x = 0; x<tempBoard.width;x++){
            for(int y = 0; y<tempBoard.height;y++){
                if(isBulb(x, y)){numBulbs++;}
            }
        }
        return numBulbs;
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

    public boolean validateNumBulbsOnWall(){
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

            if(bulbCounter > tempWall.numberAdjascentBulbs){constraintViolation = true;}
        }
        return !constraintViolation;
    }

    public boolean validateSolution(){
        if(numNotIlluminated() == 0 && validateNumBulbsOnWall()){return true;}
        else{return false;}
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
    
                //Wenn das Feld in der von neumann nachbarschaft leer ist, nicht beleuchetet wird und nicht als nicht platzierbar gekennzeichnet ist wird es als freier Nachbar gezählt
                if(isEmptyField(tempX+1,tempY) && !isIlluminated(tempX+1, tempY) && !isImplaceable(tempX+1, tempY)){tempFreeNeighbors++;}
                if(isEmptyField(tempX-1,tempY) && !isIlluminated(tempX-1, tempY) && !isImplaceable(tempX-1, tempY)){tempFreeNeighbors++;}
                if(isEmptyField(tempX,tempY+1) && !isIlluminated(tempX, tempY+1) && !isImplaceable(tempX, tempY+1)){tempFreeNeighbors++;}
                if(isEmptyField(tempX,tempY-1) && !isIlluminated(tempX, tempY-1) && !isImplaceable(tempX, tempY-1)){tempFreeNeighbors++;}

                if(tempWall.numberLeftoverBulbs == tempFreeNeighbors){
                    if(isEmptyField(tempX+1,tempY) && !isIlluminated(tempX+1, tempY) && !isImplaceable(tempX+1, tempY)){
                        setBulb(tempX+1, tempY);
                        if(smartmode==2){trivialTrigger = true;}
                    }
                    if(isEmptyField(tempX-1,tempY)  && !isIlluminated(tempX-1, tempY) && !isImplaceable(tempX-1, tempY)){
                        setBulb(tempX-1, tempY);
                        if(smartmode==2){trivialTrigger = true;}
                    }
                    if(isEmptyField(tempX,tempY+1) && !isIlluminated(tempX, tempY+1) && !isImplaceable(tempX, tempY+1)){
                        setBulb(tempX, tempY+1);
                        if(smartmode==2){trivialTrigger = true;}
                    }
                    if(isEmptyField(tempX,tempY-1) && !isIlluminated(tempX, tempY-1) && !isImplaceable(tempX, tempY-1)){
                        setBulb(tempX, tempY-1);
                        if(smartmode==2){trivialTrigger = true;}
                    }
                }
            }
        } while(trivialTrigger == true);
    }   

    public void removeNonTrivialBulbs(){
        int tempX;
        int tempY;

        for(int i = 0; i<gameboard.locationPlaceableNonTrivialBulbs.size();i++){
            tempX = gameboard.locationPlaceableNonTrivialBulbs.get(i)[0];
            tempY = gameboard.locationPlaceableNonTrivialBulbs.get(i)[1];
            tempBoard.set(tempX, tempY, new EmptyField());
        }
    }

    public int chaoticPlacement(boolean printEachIteration){
        Random dice = new Random();
        int counter = 0;
        int tempX;
        int tempY;
        while(numPlaceableBulbs()!=0){
            for(int i = 0; i<gameboard.locationPlaceableNonTrivialBulbs.size();i++){
                tempX = gameboard.locationPlaceableNonTrivialBulbs.get(i)[0];
                tempY = gameboard.locationPlaceableNonTrivialBulbs.get(i)[1];
                if(dice.nextInt(2) == 0){
                    setBulb(tempX, tempY);
                }

            
            }
            if(validateSolution()){
                System.out.println("Solution found");
                break;
            }
            if(numPlaceableBulbs() == 0){
                if(printEachIteration){System.out.print(".");}
                counter++;
                removeNonTrivialBulbs();
            }
               
        }

        return counter;
    }

    public void completeRandomStrategy(){
        setLocationPlaceableNonTrivialBulbs();
        int numIterations = chaoticPlacement(true);
        System.out.println("There are "+numNotIlluminated() + " fields which are not illuminated");
        System.out.println("There are "+numPlaceableBulbs() + " fields in which a bulb could be placed"); 
        System.out.println("Solution validated: " + validateSolution() + " after " + numIterations + " Iterations"); 

    }

    public int smartStrategy(int numSimulations){

        int totalNumIterations = 0;

        int outpouNumNotIlluminated = numNotIlluminated();
        int outputNumPlaceableBulbs = numPlaceableBulbs();

        System.out.println("There are "+numNotIlluminated() + " fields which are not illuminated");
        System.out.println("There are "+numPlaceableBulbs() + " fields in which a bulb could be placed\n");
        placeTrivialBulbs(2);
        int numBulbs = numBulbs();
        System.out.println(numBulbs + " trivial bulbs have been placed");

        outpouNumNotIlluminated -= numNotIlluminated();
        outputNumPlaceableBulbs -= numPlaceableBulbs();
        System.out.println("Number of placeable bulbs have been reduced by " + outputNumPlaceableBulbs);
        System.out.println("Number of fields to be illuminated have been reduced by " + outpouNumNotIlluminated);
        System.out.println("There are "+numNotIlluminated() + " fields which are not illuminated");
        System.out.println("There are "+numPlaceableBulbs() + " fields in which a bulb could be placed\n");        

        setLocationPlaceableNonTrivialBulbs();

        for(int i = 0; i<numSimulations; i++){
            removeNonTrivialBulbs();
            int numIterations = chaoticPlacement(true);
            System.out.println("Solution validated: " + validateSolution() + " after " + numIterations + " Iterations\n");
            totalNumIterations += numIterations;

        }

        return totalNumIterations/numSimulations;

    }
}

