package stc2.mcts;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import stc2.BitBoard;
import stc2.BitBoardTest;

public class NodeTest {
  @Test
  public void expandNodes() throws Exception {
    BitBoard board = new BitBoard();
    Node root = new Node(board);
    
    root.expand(board);
    
    assertThat(root.unvisitedChildren.size(), CoreMatchers.is(22));
    assertThat(root.children.size(), CoreMatchers.is(0));
  }
  
  @Test
  public void expandNodes_only2possibilities() throws Exception {
    BitBoard board = new BitBoard();
    BitBoardTest.prepareBoard(board, 
        "......",
        "....2.",
        "....3.",
        "....4.",
        "....5.",
        "....1.",
        "....3.",
        "....4.",
        "....4.",
        "...45.",
        "...43.",
        "...33."       );
    Node root = new Node(board);
    
    root.expand(board);
    
    assertThat(root.unvisitedChildren.size(), CoreMatchers.is(20));
    assertThat(root.children.size(), CoreMatchers.is(0));
  }

  @Test
  public void makeMove() throws Exception {
    BitBoard board = new BitBoard();
    Node root = new Node(board);
    Node child = new Node(board);
    child.parent = root;
    child.makeMove(1, 2);
    
    assertThat(child.board.layers[1].isEmpty(), is(false));
    assertThat(child.board.layers[2].isEmpty(), is(false));
    assertThat(child.board.layers[3].isEmpty(), is(true));
    assertThat(child.points, is(0));
    assertThat(child.score, is(-81.0));
  }
  
  @Test
  public void makeMove_withPoints() throws Exception {
    BitBoard board = new BitBoard();
    Node root = new Node(board);
    Node child = new Node(board);
    child.parent = root;
    child.makeMove(1, 1);

    Node cchild = new Node(board);
    cchild.parent = child;
    cchild.makeMove(1, 1);
    
    assertThat(cchild.board.layers[1].isEmpty(), is(true));
    assertThat(cchild.board.layers[2].isEmpty(), is(true));
    assertThat(cchild.board.layers[3].isEmpty(), is(true));
    assertThat(cchild.points, is(40));
    assertThat(cchild.score, is(40.0));
  }

  @Test
  public void makeMove_withScore() throws Exception {
    BitBoard board = new BitBoard();
    Node root = new Node(board);
    Node child = new Node(board);
    child.parent = root;
    child.makeMove(1, 1);

    Node cchild = new Node(board);
    cchild.parent = child;
    cchild.makeMove(1, 2);
    
    assertThat(cchild.board.layers[1].isEmpty(), is(false));
    assertThat(cchild.board.layers[2].isEmpty(), is(false));
    assertThat(cchild.board.layers[3].isEmpty(), is(true));
    assertThat(cchild.points, is(0));
    assertThat(cchild.score, is(-2.0));
  }
  
  @Test
  public void backPropagate_withLowerScore() throws Exception {
    BitBoard board = new BitBoard();
    Node root = new Node(board);
    Node child = new Node(board);
    child.score = -2.0;
    child.parent = root;

    root.backPropagate(child, child.score);
    
    assertThat(root.points, is(0));
    assertThat(root.score, is(0.0));
  }

  @Test
  public void backPropagate_withGreaterScore() throws Exception {
    BitBoard board = new BitBoard();
    Node root = new Node(board);
    root.bestChildScore = 10.0;
    Node child = new Node(board);
    child.score = 11.0;
    child.parent = root;

    root.backPropagate(child, child.score);
    
    assertThat(root.points, is(0));
    assertThat(root.bestChildScore, is(11.0));
    assertThat(root.bestChild, is (child));
  }
}
