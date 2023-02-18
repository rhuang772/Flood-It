import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;
import javalib.impworld.World;
import javalib.impworld.WorldScene;
import java.awt.Color;
import javalib.worldimages.*;


// Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;
  
  Cell(int x, int y, Color color, boolean flooded, Cell left, Cell top, Cell right, Cell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }
  
  Cell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  // draws an individual cell
  public WorldImage drawCell() {
    WorldImage cell = new RectangleImage(18, 18, OutlineMode.SOLID, this.color);
    return cell;
  }
  
  // checks if a cell has the same color as a given cell
  public boolean sameColor(Color that) {
    return this.color == that;
  } 
  
  // gets the neighbors of a cell
  public ArrayList<Cell> getCellNeighbors() {
    ArrayList<Cell> cellList = new ArrayList<Cell>();
    
    if (left != null) {
      cellList.add(left);
    }
    if (right != null) {
      cellList.add(right);
    }
    if (top != null) {
      cellList.add(top);
    }
    if (bottom != null) {
      cellList.add(bottom);
    }
    return cellList;
  }
}
 
// represents the FloodItWorld world state with the game
class FloodItWorld extends World {
  
  // list of colors we c
  // choose from to create the board
  static ArrayList<Color> ARR_OF_COLORS = 
      new ArrayList<Color>(Arrays.asList(Color.red, Color.gray, Color.blue));
  
  static int BOARD_SIZE = 24;

  static int SQUARE_SIZE = 18;
  
  int score = 0;

  ArrayList<ArrayList<Cell>> board;
  
  Random rand;
  
  Color color;
  
  ArrayDeque<Cell> worklist;
  
  ArrayDeque<Cell> visited;
  
  FloodItWorld(ArrayList<ArrayList<Cell>> board, Random rand) {
    this.board = board;
    this.rand = rand;
  }
  
  FloodItWorld(Random rand) {
    this.rand = rand;
    this.board = generateBoard();
  }
  
  FloodItWorld(Color color) {
    this.color = color;
    this.board = generateBoard();
  }
  
  FloodItWorld(ArrayList<ArrayList<Cell>> board, Random rand, Color color) {
    this.board = board;
    this.rand = rand;
    this.color = color;
  }
  
  FloodItWorld(ArrayDeque<Cell> worklist, ArrayDeque<Cell> visited, Random rand, Color color) { 
    this.worklist = worklist;
    this.visited = visited;
    this.rand = rand;
    this.color = color;
    this.board = generateBoard();
    
    this.board.get(0).get(0).flooded = true;

  }
  
 

  
  // generates a single list of cells with randomized colors and a specified row
  ArrayList<Cell> generateCellList(int row) {
    ArrayList<Cell> cellList = new ArrayList<Cell>();
    
    for (int i = 0; i < FloodItWorld.BOARD_SIZE ; i++) {
      int addedIdx = this.rand.nextInt(ARR_OF_COLORS.size());
      cellList.add(new Cell(row, i, ARR_OF_COLORS.get(addedIdx), false));
    }
    return cellList;
  }
  
  // generates the entire board with a row of cellLists
  // and links cells together
  ArrayList<ArrayList<Cell>> generateBoard() {
    ArrayList<ArrayList<Cell>> boardOfCells = new ArrayList<ArrayList<Cell>>();
    
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      boardOfCells.add(generateCellList(i));

    }

    for (int y = 0; y < FloodItWorld.BOARD_SIZE; y++) {
      for (int x = 0; x < FloodItWorld.BOARD_SIZE; x++) {
        Cell c1 = boardOfCells.get(y).get(x); 
        
        if (x - 1 >= 0) {
          Cell leftCell = boardOfCells.get(y).get(x - 1);
          c1.left = leftCell;
        }
        
        if (x + 1 <= FloodItWorld.BOARD_SIZE - 1) {
          Cell rightCell = boardOfCells.get(y).get(x + 1);
          c1.right = rightCell;
        }
        if (y - 1 >= 0) {
          Cell topCell = boardOfCells.get(y - 1).get(x);
          c1.top = topCell;
        }
        if (y + 1 <= FloodItWorld.BOARD_SIZE - 1) {
          Cell bottomCell = boardOfCells.get(y + 1).get(x);
          c1.bottom = bottomCell;
        } 
      }
    }
    return boardOfCells;
  }
   
  // renders the board with images of cells
  public WorldImage drawCells() {
    
    WorldImage e = new EmptyImage();
   
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      WorldImage savedImage = new EmptyImage();
      for (int k = 0; k < FloodItWorld.BOARD_SIZE; k++) {
        Cell a = board.get(i).get(k);
        savedImage = new BesideImage(savedImage, a.drawCell());
      }
      e = new AboveImage(e, savedImage);
    }
    return e;
    
  }

  // renders the image of the board onto the scene
  public WorldScene makeScene() {
    
    WorldImage scoreText = new TextImage(Integer.toString(score) + "/" + 
        Integer.toString(((int)(FloodItWorld.BOARD_SIZE * 0.79))), 23, Color.black);
    
    WorldImage loseText = new TextImage("You Lose!", 15, Color.black);
    
    WorldScene s = new WorldScene(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE );
    
    s.placeImageXY(this.drawCells(), (FloodItWorld.BOARD_SIZE * FloodItWorld.SQUARE_SIZE) / 2, 
        (FloodItWorld.BOARD_SIZE * FloodItWorld.SQUARE_SIZE) / 2);
    
    s.placeImageXY(scoreText, FloodItWorld.BOARD_SIZE * FloodItWorld.SQUARE_SIZE / 2,
        (int) (FloodItWorld.BOARD_SIZE  * FloodItWorld.SQUARE_SIZE * 1.2));
        
    
    if (score >= (int)(FloodItWorld.BOARD_SIZE * 0.79)) {
     
      s.placeImageXY(loseText, FloodItWorld.SQUARE_SIZE * FloodItWorld.BOARD_SIZE / 2, 
          (int) (FloodItWorld.SQUARE_SIZE * FloodItWorld.BOARD_SIZE * 1.3));  
    }
    
    return s;
  } 
  
  // when the user presses "r", create a new board and reset score to 0.
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.board = generateBoard();
      score = 0;
      makeScene();
    }
  }
  
  // go through the cell list to determine which
  // cells need to be flooded.
  public void onTick() {
    // As long as the worklist isn't empty...
    // while (!this.worklist.isEmpty()) {

    if (!this.worklist.isEmpty()) {
      Cell next = worklist.removeFirst();

      if (this.visited.contains(next)) {
        // do nothing: we've already seen this one
      }
      else {
        // do something - flood the cell
        if (this.color == next.color || next.flooded) {
          next.color = this.color;
          next.flooded = true;

          // add all the neighbors of next to the worklist for further processing
          for (Cell toBeFlooded : next.getCellNeighbors()) {
            worklist.addLast(toBeFlooded);
          }
        }
        // add next to alreadySeen, since we're done with it
        this.visited.addLast(next);
      }
    }
  }
  
  // when user clicks on a coordinate in a cell
  // that is within 0.9*(SQUARE_SIZE) of x and
  // y position of cell, generate flooded list
  // AND start incrememting the number of clicks made
  public void onMouseClicked(Posn p) {
    
    score++;
    
    int cellXPos = p.x / FloodItWorld.SQUARE_SIZE;
    int cellYPos = p.y / FloodItWorld.SQUARE_SIZE;
        
    this.color = this.board.get(cellYPos).get(cellXPos).color;

    this.worklist = new ArrayDeque<Cell>();
    this.visited = new ArrayDeque<Cell>();
    
    worklist.add(this.board.get(0).get(0));

  }
}

class ExamplesBoard {
  
  Cell c1 = new Cell(0, 1, Color.GRAY, false);
  Cell c2 = new Cell(2, 1, Color.red, false);
  Cell c3 = new Cell(1, 2, Color.BLUE, false);
  Cell c5 = new Cell(1, 1, Color.BLACK, false, this.c1, null, this.c2, this.c3);
  Cell c6 = new Cell(1, 3, Color.BLUE, false, null, null, this.c2, this.c3);
  
  FloodItWorld f1 = new FloodItWorld(new Random(10));
  FloodItWorld f2 = new FloodItWorld(new Random(2));
  FloodItWorld f3 = new FloodItWorld(new Random(4));
  FloodItWorld f4 = new FloodItWorld(new ArrayDeque<Cell>(), 
      new ArrayDeque<Cell>(), new Random(), Color.BLUE);
      
  
  // tests the drawCell method
  boolean testdrawCell(Tester t) {
    return t.checkExpect(c1.drawCell(), new RectangleImage(18, 18, OutlineMode.SOLID, Color.GRAY))
        && t.checkExpect(c3.drawCell(), new RectangleImage(18, 18, OutlineMode.SOLID, Color.BLUE))
        && t.checkExpect(c5.drawCell(), new RectangleImage(18, 18, OutlineMode.SOLID, Color.BLACK));
  }
  
  // tests the generateBoard method
  boolean testgenerateBoard(Tester t) {
    ArrayList<ArrayList<Cell>> temp11 = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> temp12 = new ArrayList<Cell>();
    temp12.add(new Cell(0, 0, Color.red, false, null, null, null, null));
    temp11.add(temp12);
    ArrayList<ArrayList<Cell>> temp13 = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> temp14 = new ArrayList<Cell>();
    temp14.add(new Cell(0, 0, Color.blue, false, null, null, null, null));
    temp13.add(temp14);
    ArrayList<ArrayList<Cell>> temp16 = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> temp17 = new ArrayList<Cell>();
    temp17.add(new Cell(0, 0, Color.white, false, null, null, null, null));
    temp16.add(temp17);
    return t.checkExpect(f1.generateBoard(),temp11)
        && t.checkExpect(f2.generateBoard(), temp13)
        && t.checkExpect(f3.generateBoard(), temp16);
  }
  
  // tests the generateCellList method
  boolean testgenerateCellList(Tester t) {
    ArrayList<Cell> temp4 = new ArrayList<Cell>();
    temp4.add(new Cell(0, 0, Color.red, false, null, null, null, null));
    ArrayList<Cell> temp5 = new ArrayList<Cell>();
    temp5.add(new Cell(1, 0, Color.blue, false, null, null, null, null));
    ArrayList<Cell> temp6 = new ArrayList<Cell>();
    temp6.add(new Cell(4, 0, Color.white, false, null, null, null, null));
    return t.checkExpect(f1.generateCellList(0), temp4)
        //&& t.checkExpect(f2.generateCellList(1), temp5)
        && t.checkExpect(f3.generateCellList(4), temp6);
  }
  
  // tests the drawCells method
  boolean testdrawCells(Tester t) {
    return t.checkExpect(f1.drawCells(), new AboveImage(new EmptyImage()
        , new BesideImage(new EmptyImage(), 
            new RectangleImage(18, 18, OutlineMode.SOLID, Color.red))))
        && t.checkExpect(f2.drawCells(), new AboveImage(new EmptyImage()
            , new BesideImage(new EmptyImage(), 
                new RectangleImage(18, 18, OutlineMode.SOLID, Color.white))))
        && t.checkExpect(f3.drawCells(), new AboveImage(new EmptyImage()
            , new BesideImage(new EmptyImage(), 
                new RectangleImage(18, 18, OutlineMode.SOLID, Color.blue))));
    
  }
  
  // tests the makeScene method 
  boolean testmakeScene(Tester t) {
    
    WorldScene s1 = new WorldScene(1, 1);
    s1.placeImageXY(new RectangleImage(1, 1, OutlineMode.OUTLINE, Color.black), 0, 0);
    s1.placeImageXY(new AboveImage(
        new EmptyImage(), new BesideImage(
            new EmptyImage(), new RectangleImage(18, 18, OutlineMode.SOLID, Color.RED))), 9, 9);
    
    WorldScene s2 = new WorldScene(1, 1);
    s2.placeImageXY(new RectangleImage(1, 1, OutlineMode.OUTLINE, Color.black), 0, 0);
    s2.placeImageXY(new AboveImage(
        new EmptyImage(), new BesideImage(
            new EmptyImage(), new RectangleImage(18, 18, OutlineMode.SOLID, Color.white))), 9, 9);
    
    WorldScene s3 = new WorldScene(1, 1);
    s3.placeImageXY(new RectangleImage(1, 1, OutlineMode.OUTLINE, Color.black), 0, 0);
    s3.placeImageXY(new AboveImage(
        new EmptyImage(), new BesideImage(
            new EmptyImage(), new RectangleImage(18, 18, OutlineMode.SOLID, Color.blue))), 9, 9);
    
    return t.checkExpect(f1.makeScene(), s1)
        && t.checkExpect(f2.makeScene(), s2)
        && t.checkExpect(f3.makeScene(), s3);
  }
  
  boolean testSameColor(Tester t) {
    return t.checkExpect(c5.sameColor(Color.DARK_GRAY), false)
        && t.checkExpect(c1.sameColor(Color.white), false)
        && t.checkExpect(c1.sameColor(Color.gray), true)
        && t.checkExpect(c3.sameColor(Color.red), false);
  }
  
  // tests the getCellNeighbors method
  boolean testgetCellNeighbors(Tester t) {
    ArrayList<Cell> cellNeighborsc5 = new ArrayList<Cell>();
    cellNeighborsc5.add(c1);
    cellNeighborsc5.add(c2);
    cellNeighborsc5.add(c3);
    
    ArrayList<Cell> cellNeighborsc1 = new ArrayList<Cell>();
    ArrayList<Cell> cellNeighborsc2 = new ArrayList<Cell>();
    
    ArrayList<Cell> cellNeighborsc6 = new ArrayList<Cell>();
    cellNeighborsc6.add(c2);
    cellNeighborsc6.add(c3);
    
    return t.checkExpect(c1.getCellNeighbors(), cellNeighborsc1)
        && t.checkExpect(c2.getCellNeighbors(), cellNeighborsc2)
        && t.checkExpect(c5.getCellNeighbors(), cellNeighborsc5)
        && t.checkExpect(c6.getCellNeighbors(), cellNeighborsc6);
  }
  
  // tests the onKeyEvent method
  void testOnKeyEvent(Tester t) {
    t.checkExpect(f1, f1);
    f1.onKeyEvent("x");
    t.checkExpect(f1, f1);
    
    t.checkExpect(f2, f2);
    f2.onKeyEvent("r");
    t.checkExpect(f2, f2);
    
    t.checkExpect(f3, f3);
    f3.onKeyEvent("R");
    t.checkExpect(f3, f3);
    
    t.checkExpect(f4, f4);
    f4.onKeyEvent("r");
    t.checkExpect(f4, f4);
        
  }
  
  // tests the onMouseClicked method
  void testOnMouseClicked(Tester t) {
    
    // for testing purposes, we've
    // reduced the size of the board to
    // make it easier to pinpoint
    // a valid clicked location.
    t.checkExpect(f1, f1);
    f1.onMouseClicked(new Posn(17, 17));
    t.checkExpect(f1, f1);
    
    t.checkExpect(f2, f2);
    f2.onMouseClicked(new Posn(19, 20));
    t.checkExpect(f2, f2);
    
    t.checkExpect(f3, f3);
    f3.onMouseClicked(new Posn(26, 34));
    t.checkExpect(f3, f3);
    
    t.checkExpect(f4, f4);
    f4.onMouseClicked(new Posn(16, 14));
    t.checkExpect(f4, f4);
    
    
  }
  
  // tests the onTick method.
  void testOnTick(Tester t) {
    
    t.checkExpect(f4, f4);
    f4.onTick();
    t.checkExpect(f4, f4);
    
    t.checkExpect(f3, f3);
    f3.onTick();
    t.checkExpect(f3, f3);
    
    t.checkExpect(f2, f2);
    f2.onTick();
    t.checkExpect(f2, f2);
    
    t.checkExpect(f1, f1);
    f1.onTick();
    t.checkExpect(f1, f1);
    
    
  }
  
  
  // tests the draw method
  void testDraw(Tester t) {
    f4.bigBang(FloodItWorld.BOARD_SIZE * FloodItWorld.SQUARE_SIZE ,
        (int) (FloodItWorld.BOARD_SIZE * FloodItWorld.SQUARE_SIZE * 1.5), 0.000000001);
  } 
}
