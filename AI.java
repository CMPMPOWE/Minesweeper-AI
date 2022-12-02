package uk.ac.ljmu.fet.cs.csw.CompetitiveMinesweeper.base.solvers;

import java.util.Random;

import uk.ac.ljmu.fet.cs.csw.CompetitiveMinesweeper.base.ExploredSpot;
import uk.ac.ljmu.fet.cs.csw.CompetitiveMinesweeper.base.MineMap;
import uk.ac.ljmu.fet.cs.csw.CompetitiveMinesweeper.base.MineMap.MapCopyException;
import uk.ac.ljmu.fet.cs.csw.CompetitiveMinesweeper.base.Spot;

public class AI extends AbstractSolver {
	int cols, rows, bombs, flaggedBombs;
	ExploredSpot pos;
	MineMap myMap;
	MineMap map;
    boolean loss;
    boolean moveMade;
    int value;
    int[][] flaggedBoard;
    int[][] hiddenBoard;
    int[][] solvedBoard;
    int[][] surroundingFlags;
    int[][] surroundingsUnknown;
    int[][] probableFlags;
    double[][] probabilities;
    static int neighbours[][] = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};

    int cells;
    int unknownCount;
    int flagCount;
    
    int[][] surroundingsSafe;
    
    int[][] surroundingUnexplored;
    int i, j;

    int totalbombs;
	
	@Override
	public void run() {
		
		super.run();
		
		myMap = getMyMap();
		cells = myMap.fieldSize;
		
		rows = myMap.rows;
		cols = myMap.cols;
		unknownCount = cells;
		flagCount = 0;
		moveMade = false;

		flaggedBoard = new int[rows][cols];
		probabilities = new double[rows][cols];
		probableFlags = new int[rows][cols];
 
        i=0;
        j=0;

		bombs = myMap.mineCount;
		
		flaggedBombs = 0;
		for (int rc = 0; rc < rows; rc++) {

            for (int cc = 0; cc < cols; cc++) {
            	probabilities[rc][cc] = ((double)bombs)/cells;
            }
		} 

		while(!myMap.isEnded()) {
		makeMove();
		}
		updateProbabilities();
		
		
	}
	
	public void makeMove() {
		

		
		hiddenBoard = new int[rows][cols];
		
		solvedBoard = new int[rows][cols];

        surroundingFlags = new int[rows][cols];
        
        surroundingsUnknown = new int[rows][cols];
        
        surroundingsSafe = new int[rows][cols];
        
        surroundingUnexplored = new int[rows][cols];
        
        j=i;
        
        if(unknownCount==cells) {
        Random random = new Random();

        int x, y;
        
        x = Math.abs(random.nextInt()) % cols;
        y = Math.abs(random.nextInt()) % rows;
        myMap.pickASpot(x, y);
        }
        updateBoards();
        updateNeighbours();
        
        for (int rc = 0; rc < rows; rc++) {

            for (int cc = 0; cc < cols; cc++) {
            	
            	if (solvedBoard[rc][cc] == 1) {
            		pos = myMap.getPos(rc, cc);
        			
        			value = pos.nearMineCount;
            		
        			System.out.println("solving coord : { "+(rc+1)+" , "+(cc+1)+" } value: "+value);
        			
            		System.out.println("flag neighbours : "+surroundingFlags[rc][cc]);
            		
            		System.out.println("safe neighbours : "+surroundingsSafe[rc][cc]);
            	
            		System.out.println("unknown neighbours: "+surroundingsUnknown[rc][cc]);

        			if(surroundingFlags[rc][cc] == value ) {	
	        			for (int r = -1; r < 2; r++) {
	
	                        for (int c = -1; c < 2; c++) {
	                        	
	                        	if (cc + c >= 0 && rc + r >= 0 && cc + c < cols && rc + r < rows && hiddenBoard[rc+r][cc+c]==1  && !(c==0 && r==0)) {
	                        		
	                        			if(myMap.getPos(rc+r, cc+c).type.equals(Spot.UNEXPLORED)) {
	                       
		                        			myMap.pickASpot(rc+r, cc+c);
		                        			surroundingsSafe[rc][cc]++;
	
		                            		hiddenBoard[rc+r][cc+c] = 0;  			
		                            		solvedBoard[rc+r][cc+c] = 1;
		                            		unknownCount--;

		                            		i++;
	                        			}
	                        		}

	                        	}
	                        }
	        		}
            	
            	
            		if (value == (surroundingsUnknown[rc][cc]+surroundingFlags[rc][cc])) {
            			
            			for (int r = -1; r < 2; r++) {
            				
	                        for (int c = -1; c < 2; c++) {
	                        	
	                        	if (cc + c >= 0 && rc + r >= 0 && cc + c < cols && rc + r < rows && flaggedBoard[rc+r][cc+c]==0 && hiddenBoard[rc+r][cc+c]== 1 && !(c==0 && r==0)) {
	                        		
	                        		if(myMap.getPos(rc+r, cc+c).type.equals(Spot.UNEXPLORED)) {
	                        			
	                        			flaggedBoard[rc+r][cc+c] = 1;
	                        			
	                        			myMap.flagASpot(rc+r, cc+c);
	                            		
	                            		hiddenBoard[rc+r][cc+c] = 0;
	                        			
	                            		solvedBoard[rc+r][cc+c] = 0;
	                            		flagCount++;

	                            		bombs--;
	                            		unknownCount--;
                        		
	                            		i++;
	                        		}
	                        		}

	                        	}
	                        }
            		}
            	}
            }
        }
        
        //pickRandom();
        pickProbability();  
            
        
	}
	void updateBoards() {
		for (int rc = 0; rc < rows; rc++) {

            for (int cc = 0; cc < cols; cc++) {


                pos = myMap.getPos(rc, cc);
                
                value = pos.nearMineCount;
                
                if(value != 0) {
                	if(value > 0) {
                    	
                    	solvedBoard[rc][cc] = 1;
                   
                    	
                    }
                    else if (value ==-1 && flaggedBoard[rc][cc]==0) {
                    	
                    	hiddenBoard[rc][cc] = 1;
                    	unknownCount++;
                    }
                    if(flaggedBoard[rc][cc]==1) {
                    	flagCount++;
                    }
             	
                }
                

                
            }

        }
		
	}
	void updateNeighbours() {
		for (int rc = 0; rc < rows; rc++) {

            for (int cc = 0; cc < cols; cc++) {
            	
            	if (solvedBoard[rc][cc] == 1) {
            		
            		for (int r = -1; r < 2; r++) {

                        for (int c = -1; c < 2; c++) {
                        	
                        	if (cc + c >= 0 && rc + r >= 0 && cc + c < cols && rc + r < rows) {
                        		
                        		if(!(c==0 && r==0)) {
                        			
                        			pos = myMap.getPos(rc + r, cc + c);
                        			
                        			value = pos.nearMineCount;
                        			
                        			if (value != 0) {
                        				
                        				if (value > 0) {
                        					
                        					surroundingsSafe[rc][cc]++;
                        					
                        				}
                        				else if (value == -1) {
                        					
                        					if (flaggedBoard[rc + r][cc + c] == 1) {
                                				
        	                                    surroundingFlags[rc][cc]++;
        	
        	                                }
                        					else {
                        						
                        						surroundingsUnknown[rc][cc]++;
                        						
                        					
                        					}
                        				}
                        				else {
                        					surroundingUnexplored[rc][cc]++;
                        				}
                        					
                        			}

                            	}
                        		
                        	}
                        	
                        }
                        
            		}
            	}
            
            }
		}
	}
	void updateProbabilities() {
		unknownCount=0;
		for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                surroundingsUnknown[i][j] = 0;
                surroundingFlags[i][j] = 0;
                probableFlags[i][j] = 0;
            }
        }
		updateNeighbours();
		for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (hiddenBoard[i][j] == 1) {
                    probabilities[i][j] = calculateProbability(i, j);
                    for (int[] neighbour : neighbours) {
                        int x = neighbour[0] + i;
                        int y = neighbour[1] + j;
                        if (x < 0 || x >= rows || y < 0 || y >= cols) {
                            continue;
                        }
                        probableFlags[x][y] += (probabilities[i][j] > 0.8) ? 1 : 0;
                    }
                }
            }
        }
		doBayes();
	}
	private void doBayes(){
        double newProbs[][] = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (hiddenBoard[i][j]==1) {
                    if (probabilities[i][j] == 0 || probabilities[i][j] == 1 || surroundingsUnknown[i][j] == countNeighbours(i,j)){
                        newProbs[i][j] = probabilities[i][j];
                        continue;
                    }
                    double minfac = 1;
                    double maxfac = 0;
                    for (int[] neighbour : AI.neighbours) {
                        int x = i + neighbour[0];
                        int y = j + neighbour[1];
                        if (x < 0 || x >= rows || y < 0 || y >= cols) {
                            continue;
                        }
                        double minpab = 1;
                        double maxpab = 0;
                        if (hiddenBoard[i][j]==1) {
                            for (int[] neighbour2 : AI.neighbours) {
                                int r = x + neighbour2[0];
                                int s = y + neighbour2[1];
                                if (r < 0 || r >= rows || s < 0 || s >= cols) {
                                    continue;
                                }
                                if (r == i && s == j){
                                    continue;
                                }
                                if (solvedBoard[i][j]==1){
                                    if ((Math.abs(i - r) <= 1 && Math.abs(j - s) <= 1)){
                                    	pos = myMap.getPos(i, j);
                            			
                            			value = pos.nearMineCount;	
                                        double pabn = ((double) value - surroundingFlags[i][j] - 1) / surroundingsUnknown[i][j];
                                        minpab = Math.min(minpab, pabn);
                                        maxpab = Math.max(maxpab, pabn);
                                    }
                                }
                            }
                            minfac = Math.min(minfac, minpab / probabilities[i][j]);
                            maxfac = Math.max(maxfac, maxpab / probabilities[i][j]);
                        }
                    }
                    if (minfac * probabilities[i][j] < 1 - maxfac * probabilities[i][j]){
                        newProbs[i][j] = Math.max(0,minfac * probabilities[i][j]);
                    } else {
                        newProbs[i][j] = Math.min(1,maxfac * probabilities[i][j]);
                    }
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++){
                probabilities[i][j] = newProbs[i][j];
            }
        }
    }
	double calculateProbability(int x, int y){
		
		if (surroundingsUnknown[x][y] + surroundingFlags[x][y] == countNeighbours(x,y)){
            return ((double) bombs) / unknownCount;
        }
        double minp = 1;
        double maxp = 0;
        
		for (int r = -1; r < 2; r++) {

            for (int c = -1; c < 2; c++) {
            	
            	if (y + c >= 0 && x + r >= 0 && y + c < cols && x + r < rows) {
            		
            		if(!(c==0 && r==0)) {
            			
            			if (solvedBoard[x+r][y+c]==1) {
            				
            				pos = myMap.getPos(x + r, y + c);
                			
                			value = pos.nearMineCount;
                			
                			double p = ((double) value - surroundingFlags[x+r][y+c] - probableFlags[i][j]) / surroundingsUnknown[x+r][y+c];
                			minp = Math.max(0,Math.min(p, minp));
                            maxp = Math.min(1,Math.max(p, maxp));
                			
            			}
            			
            		}

            	}
            }
		}
		
        
        if (minp < 1 - maxp){
            return minp;
        }
        return maxp;
    }
	int countNeighbours(int x, int y){
        if (x == 0 || x == rows - 1) {
            if (y == 0 || y == cols - 1){
                return 3;
            }
            return 5;
        }
        if (y == 0 || y == cols - 1 ){
            return 5;
        }
        return 8;
    }
	public double[][] getProbs(){
        return probabilities;
    }
	public void pickProbability(){
		if(i==j) {
        double minProb = 1;
        int minRow = -1;
        int minCol = -1;
        double maxProb = 0;
        int maxRow = -1;
        int maxCol = -1;

        double[][] probs = getProbs();

        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j ++) {
                if (hiddenBoard[i][j]==1 && probs[i][j] < minProb) {
                    minProb = probs[i][j];
                    minRow = i;
                    minCol = j;
                }
                if (hiddenBoard[i][j]==1 && probs[i][j] > maxProb) {
                    maxProb = probs[i][j];
                    maxRow = i;
                    maxCol = j;
                }
            }
        }
        if (minCol == -1 && maxCol == -1){
            return;
        }
        if (maxProb >= 0.99){
            myMap.flagASpot(maxRow, maxCol);
            flaggedBoard[maxRow][maxCol] = 1;
            flagCount++;
           
        } else {
    
            myMap.pickASpot(minRow, minCol);
            System.out.println((minRow+1)+"  ---  "+(minCol+1));
        }
        unknownCount--;
    }
	}

	
}
            		

        

	



