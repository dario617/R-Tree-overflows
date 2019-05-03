package test;

import util.nRectangle;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class nRectangleTest {

    float [][] r0 = {{0,0}, {0,0}};
    float [][] r1 = {{0,0}, {1,1}};
    float [][] r2 = {{0,0}, {2,2}};
    float [][] r3 = {{1,1}, {2,2}};
    float [][] r4 = {{4,0}, {3,3}};
    float [][] mbr34 = {{1,0}, {6,3}};
    float [][] mbr012 = {{0,0}, {2,2}};

    @org.junit.jupiter.api.BeforeEach
    void setUp() {

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void overlaps() {
        assertTrue(nRectangle.overlaps(r2, r3));
        assertFalse(nRectangle.overlaps(r4, r3));
        assertTrue(nRectangle.overlaps(r0, r1));
        assertTrue(nRectangle.overlaps(r1, r2));
    }

    @org.junit.jupiter.api.Test
    void MBR() {
        float[][] mbr = nRectangle.MBR(r4,r3);
        for(int i = 0; i<2; i++){
            for(int j = 0; j<2; j++){
                assertEquals(mbr[i][j], mbr34[i][j]);
            }
        }

    }

    @org.junit.jupiter.api.Test
    void MBR1() {
        ArrayList<float[][]> rects0 = new ArrayList<>();
        rects0.add(r3);
        rects0.add(r4);
        float[][] mbr0 = nRectangle.MBR(rects0);
        for(int i = 0; i<2; i++){
            for(int j = 0; j<2; j++){
                System.out.println(i+j);
                assertEquals(mbr34[i][j], mbr0[i][j]);
            }
        }
        ArrayList<float[][]> rects = new ArrayList<>();
        rects.add(r0);
        rects.add(r1);
        rects.add(r2);
        float[][] mbr = nRectangle.MBR(rects);
        for(int i = 0; i<2; i++){
            for(int j = 0; j<2; j++){
                assertEquals(mbr012[i][j], mbr[i][j]);
            }
        }

    }

    @org.junit.jupiter.api.Test
    void area() {
    }

    @org.junit.jupiter.api.Test
    void checkEnlargement() {
    }

    @org.junit.jupiter.api.Test
    void enlargementArea() {
    }
}