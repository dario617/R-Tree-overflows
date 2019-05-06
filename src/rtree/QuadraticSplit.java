package rtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.util.Pair;
import util.nRectangle;

import static java.lang.Math.abs;


public class QuadraticSplit {
    /**
     * Selecciona los dos primeros nodos para el quadratic split. Los entrega como pares rectangulo, id
     * @param n Nodo a splittear
     * @return Lista de dos pares rectangulo, id
     */
    public static List<Pair<float[][], Long>> pickSeeds(Node n){
        float[][] rect1 = null;
        Long id1 = null;
        float[][] rect2 = null;
        Long id2 = null;
        float d;
        float max = -1;
        for(int i = 0; i < n.childRectangles.size(); i++){
            float[][] rectA = n.childRectangles.get(i);
            for(int j = 0; j < n.childRectangles.size(); j++){
                float[][] rectB = n.childRectangles.get(j);
                if(i <= j){
                    continue;
                }
                float areaA = nRectangle.area(rectA);
                float areaB = nRectangle.area(rectB);
                float areaC = nRectangle.area(nRectangle.MBR(rectA, rectB));
                d = areaC - areaA - areaB;
                if (d<0) {
                	d = 0;
                }
                if(d > max){
                    rect1 = rectA;
                    rect2 = rectB;
                    id1 = n.childIds.get(i);
                    id2 = n.childIds.get(j);
                    max = d;
                }
            }
        }
        Pair<float[][], Long> pair1 = new Pair<>(rect1, id1);
        Pair<float[][], Long> pair2 = new Pair<>(rect2, id2);
        List<Pair<float[][], Long>> pairarray = new ArrayList<Pair<float[][], Long>>();
        pairarray.add(pair1);
        pairarray.add(pair2);
        return pairarray;
    }

    public static Pair<float[][], Long> pickNext(Node n, ArrayList<float[][]> g1, ArrayList<float[][]> g2){
        float[][] theRect = null;
        Long theId = null;
        float max = -1;
        for(float[][] rect : n.childRectangles){
            float d1 = nRectangle.enlargementArea(rect, g1);
            float d2 = nRectangle.enlargementArea(rect, g2);
            float diff = abs(d1-d2);
            if(diff > max){
             theRect = rect;
             theId = n.childIds.get(n.childRectangles.indexOf(rect));
             max = diff;
            }
        }
        Pair<float[][], Long> thePair = new Pair<float[][], Long>(theRect, theId);
        return thePair;

    }

    public static Node [] split(Node n, int m){
        assert(n != null) : "Null node passed to quadratic split!";
        List<Pair<float[][], Long>> seeds = pickSeeds(n);
        ArrayList<float[][]> g1Rects = new ArrayList<>();
        ArrayList<float[][]> g2Rects = new ArrayList<>();
        ArrayList<Long> g1Ids = new ArrayList<>();
        ArrayList<Long> g2Ids = new ArrayList<>();
        g1Rects.add(seeds.get(0).getKey());
        g1Ids.add(seeds.get(0).getValue());
        g2Rects.add(seeds.get(1).getKey());
        g2Ids.add(seeds.get(1).getValue());
        n.childRectangles.remove(n.childIds.indexOf(seeds.get(0).getValue()));
        n.childRectangles.remove(n.childIds.indexOf(seeds.get(1).getValue()));
        n.childIds.remove(seeds.get(0).getValue());
        n.childIds.remove(seeds.get(1).getValue());
        while(n.childIds.size() > 0){
            if(g1Ids.size() + n.childIds.size() == m){
                while(n.childIds.size() > 0){
                    g1Ids.add(n.childIds.get(0));
                    g1Rects.add(n.childRectangles.get(0));
                    n.childIds.remove(0);
                    n.childRectangles.remove(0);
                }
                break;
            }
            else if(g2Ids.size() + n.childIds.size() == m){
                while(n.childIds.size() > 0){
                    g2Ids.add(n.childIds.get(0));
                    g2Rects.add(n.childRectangles.get(0));
                    n.childIds.remove(0);
                    n.childRectangles.remove(0);
                }
                break;

            }
            else {
                Pair<float[][], Long> next = pickNext(n, g1Rects, g2Rects);
                n.childRectangles.remove(n.childIds.indexOf(next.getValue()));
                n.childIds.remove(next.getValue());
                float area1 = nRectangle.area(nRectangle.MBR(g1Rects));
                float area2 = nRectangle.area(nRectangle.MBR(g2Rects));
                float enlargeArea1 = nRectangle.enlargementArea(next.getKey(), g1Rects);
                float enlargeArea2 = nRectangle.enlargementArea(next.getKey(), g2Rects);
                float diff1 = enlargeArea1 - area1;
                float diff2 = enlargeArea2 - area2;
                if (diff1 > diff2) {
                    g2Rects.add(next.getKey());
                    g2Ids.add(next.getValue());
                } else {
                    g1Rects.add(next.getKey());
                    g1Ids.add(next.getValue());
                }
            }
        }
        float[][] voidCoords = {{0,0}, {0,0}};
        Node newNode = new Node(n.isLeaf, voidCoords, n.parent);
        newNode.childRectangles = g2Rects;
        newNode.childIds = g2Ids;
        newNode.recalculateMBR();
        n.childRectangles = g1Rects;
        n.childIds = g1Ids;
        n.recalculateMBR();
        Node[] nodes = {n, newNode};
        return nodes;

    }
}
