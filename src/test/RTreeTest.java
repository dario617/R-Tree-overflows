package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rtree.RTree;

import static org.junit.jupiter.api.Assertions.*;

class RTreeTest {

    RTree theTree;
    private float[][] r1 = {{1,0},{1,1}};
    private float[][] r2 = {{4,1},{3,2}};
    private float[][] r3 = {{8,2},{3,1}};
    private float[][] r4 = {{3,4},{2,2}};
    private float[][] r5 = {{5,2},{-2,1}};
    private float[][] r6 = {{6,0},{1,2}};
    private float[][] r7 = {{5,2},{-2,1}};
    private float[][] r8 = {{6,0},{1,2}};
    private float[][] r9 = {{5,2},{-2,1}};
    private float[][] r10 = {{6,0},{1,2}};

    @BeforeEach
    void setUp() {
        theTree = new RTree(1,3, 2, RTree.OverflowMethod.LINEAR, 10);
        theTree.insertRect(r1);
        theTree.insertRect(r2);
        theTree.insertRect(r3);
    }

    @AfterEach
    void tearDown() {
        theTree = null;
    }

    @Test
    void insertRect() {
        assertEquals(3, theTree.rootSize());
        theTree.insertRect(r4);
        assertEquals(2, theTree.rootSize());
        theTree.insertRect(r5);
        theTree.insertRect(r6);
        theTree.insertRect(r7);
        assertEquals(3, theTree.rootSize());
    }

    @Test
    void search() {
        assertTrue(theTree.search(r1));
        assertTrue(theTree.search(r2));
        assertTrue(theTree.search(r3));
        assertFalse(theTree.search(r4));
    }
}