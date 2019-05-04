package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.util.Pair;
import rtree.Node;
import util.nRectangle;
import rtree.LinearSplit;
class LinearSplitTest {
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
	private ArrayList<float[][]> rectList;
	private ArrayList<Long> childIdList;
	private Node nodo;
    @BeforeEach
    void setUp(){
    	rectList = new ArrayList<float[][]>();
    	childIdList = new ArrayList<Long>();
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
    	childIdList.add(new Long(0));
    	childIdList.add(new Long(1));
    	childIdList.add(new Long(2));
    	childIdList.add(new Long(3));
    	childIdList.add(new Long(4));
    	childIdList.add(new Long(5));
    	childIdList.add(new Long(6));
    	childIdList.add(new Long(7));
    	childIdList.add(new Long(8));
    	childIdList.add(new Long(9));
        nodo = new Node(false,nRectangle.MBR(rectList),0);
        nodo.addChildIds(childIdList);
        nodo.addChildRectangles(rectList);
    }
    @AfterEach
    void tearDown(){
        rectList = null;
        childIdList = null;
        nodo = null;
    }
    
    
	@Test
	void testPickSeedsLength() {
		assertEquals(LinearSplit.pickSeeds(nodo).size(),2);
		ArrayList<Pair<float[][],Long>> seeds = LinearSplit.pickSeeds(nodo);
		float[][] seed1 = seeds.get(0).getKey();
		Long id1 = seeds.get(0).getValue();
		float[][] seed2 = seeds.get(1).getKey();
		Long id2 = seeds.get(1).getValue();
		assertEquals(new Long(0), id1);
		assertEquals(new Long(2), id2);
		for(int i = 0;i<2; i++) {
			for(int j = 0; j<2;j++) {				
				assertEquals(r1[i][j],seed1[i][j]);	
				assertEquals(r3[i][j],seed2[i][j]);
				
			}
		}
	}
	
	@Test
	void testSplit() {
		Node[] nodos = LinearSplit.split(nodo, 2);
		System.out.println("Child Rects #");
		System.out.println(nodos[0].childRectangles.size());
		System.out.println(nodos[1].childRectangles.size());
		System.out.println("##########");
	}

}
