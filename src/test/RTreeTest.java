package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rtree.RTree;

import static org.junit.jupiter.api.Assertions.*;

class RTreeTest {

    RTree qTree;
    RTree lTree;
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
        qTree = new RTree(1,3, 2, RTree.OverflowMethod.QUADRATIC, 10);
        qTree.insertRect(r1);
        qTree.insertRect(r2);
        qTree.insertRect(r3);
        lTree = new RTree(1,3, 2, RTree.OverflowMethod.LINEAR, 10);
        lTree.insertRect(r1);
        lTree.insertRect(r2);
        lTree.insertRect(r3);
    }

    @AfterEach
    void tearDown() {
        qTree = null;
        lTree = null;
    }

    @Test
    void insertRectLinear() {
        assertEquals(3, lTree.rootSize());
        lTree.insertRect(r4);
        assertEquals(2, lTree.rootSize());
        lTree.insertRect(r5);
        lTree.insertRect(r6);
        lTree.insertRect(r7);
        assertEquals(3, lTree.rootSize());
    }

    @Test
    void searchLinear() {
        assertTrue(lTree.search(r1));
        assertTrue(lTree.search(r2));
        assertTrue(lTree.search(r3));
        assertFalse(lTree.search(r4));
        lTree.insertRect(r4);
        lTree.insertRect(r5);
        lTree.insertRect(r6);
        assertTrue(lTree.search(r1));
        System.out.println("SEARCH R4 CON 6 NODOS");
        assertTrue(lTree.search(r4));
        System.out.println("SEARCH R6 CON 6 NODOS");
        assertTrue(lTree.search(r6));
        assertFalse(lTree.search(r7));
    }

    @Test
    void insertRectQuadratic() {
        assertEquals(3, qTree.rootSize());
        qTree.insertRect(r4);
        assertEquals(2, qTree.rootSize());
        qTree.insertRect(r5);
        qTree.insertRect(r6);
        qTree.insertRect(r7);
        assertEquals(3, qTree.rootSize());
    }

    @Test
    void searchQuadratic() {
        assertTrue(qTree.search(r1));
        assertTrue(qTree.search(r2));
        assertTrue(qTree.search(r3));
        assertFalse(qTree.search(r4));
        qTree.insertRect(r4);
        qTree.insertRect(r5);
        qTree.insertRect(r6);
        assertTrue(qTree.search(r1));
        System.out.println("SEARCH R4 CON 6 NODOS");
        assertTrue(qTree.search(r4));
        System.out.println("SEARCH R6 CON 6 NODOS");
        assertTrue(qTree.search(r6));
        assertFalse(qTree.search(r7));
    }
}