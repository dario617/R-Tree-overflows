package test;

import util.nRectangle;

import static org.junit.jupiter.api.Assertions.*;

class nRectangleTest {

    float [][] r0 = {{0,0}, {0,0}};
    float [][] r1 = {{0,0}, {1,1}};
    float [][] r2 = {{0,0}, {2,2}};
    float [][] r3 = {{1,1}, {2,2}};
    float [][] r4 = {{4,0}, {3,3}};

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
    }

    @org.junit.jupiter.api.Test
    void MBR() {
    }

    @org.junit.jupiter.api.Test
    void MBR1() {
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