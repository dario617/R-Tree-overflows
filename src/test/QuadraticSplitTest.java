package test;

import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rtree.Node;
import rtree.QuadraticSplit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuadraticSplitTest {
    private float[][] r1 = {{1,1},{1,1}};
    private float[][] r2 = {{3,1},{2,3}};
    private float[][] r3 = {{1,5},{1,2}};
    private float[][] r4 = {{3,4},{2,2}};
    private float[][] r5 = {{5,2},{-2,1}};
    private float[][] r6 = {{6,0},{1,2}};
    private float[][] r7 = {{5,2},{-2,1}};
    private float[][] r8 = {{6,0},{1,2}};
    private float[][] r9 = {{5,2},{-2,1}};
    private float[][] r10 = {{6,0},{1,2}};
    private ArrayList<float[][]> rectList;
    private ArrayList<Long> childIdList;
    private Node n;
    @BeforeEach
    void setUp() {
        this.n = new Node(false, null, -1);
        rectList = new ArrayList<>();
        childIdList = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        this.n = null;
        childIdList = null;
        rectList = null;
    }

    @Test
    void pickSeeds() {
        rectList = new ArrayList<>();
        rectList.add(r1);
        rectList.add(r2);
        rectList.add(r3);
        n.childRectangles = rectList;
        childIdList.add(new Long(1));
        childIdList.add(new Long(2));
        childIdList.add(new Long(3));
        n.childIds = childIdList;
        List<Pair<float[][], Long>> seeds = QuadraticSplit.pickSeeds(n);
        assertEquals(3, seeds.get(0).getValue());
        assertEquals(2, seeds.get(1).getValue());


    }

    @Test
    void pickNext() {
    }

    @Test
    void split() {
        rectList.add(r1);
        rectList.add(r2);
        rectList.add(r3);
        rectList.add(r4);
        rectList.add(r5);
        rectList.add(r6);
        rectList.add(r7);
        rectList.add(r8);
        rectList.add(r9);
        rectList.add(r10);
        n.addChildRectangles(rectList);
        childIdList.add(new Long(1));
        childIdList.add(new Long(2));
        childIdList.add(new Long(3));
        childIdList.add(new Long(4));
        childIdList.add(new Long(5));
        childIdList.add(new Long(6));
        childIdList.add(new Long(7));
        childIdList.add(new Long(8));
        childIdList.add(new Long(9));
        childIdList.add(new Long(10));
        n.addChildIds(childIdList);
        Node [] nodes = QuadraticSplit.split(this.n, 4);
        Node n1 = nodes[0];
        Node n2 = nodes[1];
        System.out.println(n1.childRectangles.size());
        System.out.println(n2.childRectangles.size());
    }
}