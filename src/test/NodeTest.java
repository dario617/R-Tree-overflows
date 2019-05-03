package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rtree.Node;
import util.nRectangle;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    float[][] r0 = {{0,0}, {0,0}};
    float[][] r1 = {{0,0}, {1,1}};
    float[][] r2 = {{0,0}, {2,2}};
    long id0 = 0;
    long id1 = 1;
    long id2 = 2;
    private Node n;

    @BeforeEach
    void setUp(){
        this.n = new Node(true, r2, -1);
    }
    @AfterEach
    void tearDown(){
        this.n = null;
    }



    @Test
    void nodeCreation(){
        assertEquals(n.coords, r2);
        assert(n.isLeaf);
        assertEquals(n.parent, -1);

    }

    @Test
    void addChildIds() {
        ArrayList<Long> longs = new ArrayList<Long>();
        longs.add(id0);
        longs.add(id1);
        longs.add(id2);
        n.addChildIds(longs);
        assertEquals(3, n.childIds.size());
        assertEquals(longs, n.childIds);

    }

    @Test
    void addChildRectangles() {
        ArrayList<float[][]> rects = new ArrayList<>();
        rects.add(r0);
        rects.add(r1);
        rects.add(r2);
        n.addChildRectangles(rects);
        assertEquals(3, n.childRectangles.size());
        assertEquals(rects, n.childRectangles);
    }

    @Test
    void recalculateMBR() {
        assertEquals(r2, n.coords);
        n.childRectangles.add(r1);
        float[][] expected = nRectangle.MBR(r1, r2);
        assertNotEquals(expected, n.coords);
        n.recalculateMBR();
        //assertEquals(expected, n.coords);
    }

    @Test
    void setMyID() {
    }
}