package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {

    @Test
    void equals() {
        int x1 = 3;
        int x2 = -1;
        int y1 = -5;
        int y2 = 2;
        Vector2d main = new Vector2d(x1, y1);
        Vector2d sameAsMain = new Vector2d(x1, y1);
        Vector2d differentX = new Vector2d(x2, y1);
        Vector2d differentY = new Vector2d(x1, y2);
        Object object = new Object();

        assertNotEquals(null, main);
        assertNotEquals(main, object);
        assertNotEquals(main, differentX);
        assertNotEquals(sameAsMain, differentY);
        assertEquals(main, sameAsMain);
    }

    @Test
    void toStringTest() {
        Vector2d vector = new Vector2d(1, -3);
        Vector2d vector2 = new Vector2d(-4, 5);

        String toStringResult = vector.toString();
        String toStringResult2 = vector2.toString();

        assertEquals("(1, -3)", toStringResult);
        assertEquals("(-4, 5)", toStringResult2);
    }

    @Test
    void precedes() {
        final Vector2d small = new Vector2d(2, 2);
        final Vector2d big = new Vector2d(3, 3);
        final Vector2d sideGrade = new Vector2d(3, 1);

        assertTrue(small.precedes(small));
        assertTrue(small.precedes(big));
        assertFalse(small.precedes(sideGrade));
        assertFalse(big.precedes(small));
    }

    @Test
    void follows() {
        Vector2d small = new Vector2d(2, 2);
        Vector2d big = new Vector2d(3, 3);
        Vector2d sideGrade = new Vector2d(1, 3);

        assertTrue(small.follows(small));
        assertTrue(big.follows(small));
        assertFalse(small.follows(sideGrade));
        assertFalse(small.follows(big));
    }

    @Test
    void upperRight() {
        Vector2d small = new Vector2d(0, 0);
        Vector2d big = new Vector2d(5, 5);
        Vector2d leftUp = new Vector2d(0, 5);
        Vector2d rightDown = new Vector2d(5, 0);

        assertEquals(small, small.upperRight(small));
        assertEquals(big, small.upperRight(big));
        assertEquals(big, leftUp.upperRight(rightDown));
        assertEquals(leftUp, small.upperRight(leftUp));
    }

    @Test
    void lowerLeft() {
        Vector2d small = new Vector2d(0, 0);
        Vector2d big = new Vector2d(5, 5);
        Vector2d leftUp = new Vector2d(0, 5);
        Vector2d rightDown = new Vector2d(5, 0);

        assertEquals(small, small.lowerLeft(small));
        assertEquals(small, small.lowerLeft(big));
        assertEquals(small, leftUp.lowerLeft(rightDown));
        assertEquals(small, small.lowerLeft(leftUp));
    }

    @Test
    void add() {
        int x1 = 3;
        int x2 = -1;
        int y1 = -5;
        int y2 = 2;
        Vector2d v1 = new Vector2d(x1, y1);
        Vector2d v2 = new Vector2d(x2, y2);

        Vector2d sum = v1.add(v2);
        Vector2d sum2 = v1.add(v1);

        assertEquals(new Vector2d(x1 + x2, y1 + y2), sum);
        assertEquals(new Vector2d(x1 + x1, y1 + y1), sum2);
    }

    @Test
    void subtract() {
        int x1 = 3;
        int x2 = -1;
        int y1 = -5;
        int y2 = 2;
        Vector2d v1 = new Vector2d(x1, y1);
        Vector2d v2 = new Vector2d(x2, y2);

        Vector2d dif = v1.subtract(v2);
        Vector2d dif2 = v2.subtract(v1);

        assertEquals(new Vector2d(x1 - x2, y1 - y2), dif);
        assertEquals(new Vector2d(x2 - x1, y2 - y1), dif2);
    }

    @Test
    void opposite() {
        int x = 3;
        int y = -6;
        Vector2d normal = new Vector2d(x, y);
        Vector2d zero = new Vector2d(0, 0);

        Vector2d normalOpp = normal.opposite();
        Vector2d zeroOpp = zero.opposite();

        assertEquals(new Vector2d(-x, -y), normalOpp);
        assertEquals(new Vector2d(0, 0), zeroOpp);
    }
}
