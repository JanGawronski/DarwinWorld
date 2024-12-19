package model.map;

import model.MapDirection;
import model.Pair;
import model.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorldMapTest {

    @Test
    void convert() {
        WorldMap map37 = new WorldMap(3, 7);
        WorldMap map21 = new WorldMap(2, 1);

        Vector2d p1 = new Vector2d(3, 3);
        MapDirection o1 = MapDirection.NE;
        Vector2d p2 = new Vector2d(0, -1);
        MapDirection o2 = MapDirection.S;
        Vector2d p3 = new Vector2d(8, 1);
        MapDirection o3 = MapDirection.E;

        Vector2d p4 = new Vector2d(0, 1);
        MapDirection o4 = MapDirection.N;
        Vector2d p5 = new Vector2d(-1, 2);
        MapDirection o5 = MapDirection.NW;

        Pair<Vector2d, MapDirection> r1 = map37.convert(p1, o1);
        Pair<Vector2d, MapDirection> r2 = map37.convert(p2, o2);
        Pair<Vector2d, MapDirection> r3 = map37.convert(p3, o3);

        Pair<Vector2d, MapDirection> r4 = map21.convert(p4, o4);
        Pair<Vector2d, MapDirection> r5 = map21.convert(p5, o5);

        assertEquals(new Pair<>(new Vector2d(2, 2), MapDirection.SW), r1);
        assertEquals(new Pair<>(new Vector2d(0, 0), MapDirection.N), r2);
        assertEquals(new Pair<>(new Vector2d(0, 1), MapDirection.E), r3);

        assertEquals(new Pair<>(new Vector2d(0, 1), MapDirection.N), r4);
        assertEquals(new Pair<>(new Vector2d(0, 1), MapDirection.SE), r5);
    }
}