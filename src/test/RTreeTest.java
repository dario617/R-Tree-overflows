package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rtree.RTree;

import static org.junit.jupiter.api.Assertions.*;

class RTreeTest {
    RTree theTree;
    float[][] r1;
    float[][] r2;
    float[][] r3;
    float[][] r4;
    float[][] r5;
    float[][] r6;
    float[][] r7;
    float[][] r8;
    float[][] r9;
    float[][] r10;

    @BeforeEach
    void setUp() {
        theTree = new RTree(1, 3, 2, RTree.OverflowMethod.LINEAR, 15);
        float[][] r1 = {{1,0},{1,1}};
        float[][] r2 = {{4,1},{3,2}};
        float[][] r3 = {{8,2},{3,1}};
        float[][] r4 = {{3,4},{2,2}};
        float[][] r5 = {{5,2},{-2,1}};
        float[][] r6 = {{6,0},{1,2}};
        float[][] r7 = {{5,2},{-2,1}};
        float[][] r8 = {{6,0},{1,2}};
        float[][] r9 = {{5,2},{-2,1}};
        float[][] r10 = {{6,0},{1,2}};
        theTree.insertRect(r1);
        theTree.insertRect(r2);
        theTree.insertRect(r3);
        System.out.println(theTree.sizeOfRoot());

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void insertRect() {
    }

    @Test
    void search() {
        assertTrue(theTree.search(r3));
        assertTrue(theTree.search(r2));
        assertTrue(theTree.search(r1));
    }
}