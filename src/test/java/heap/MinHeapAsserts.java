package heap;
import java.util.Arrays;

public class MinHeapAsserts {

    public static void testLeft() {
        // 100, 90, 85, 30, 45, 60, 70, 20
        MinHeap heap = new MinHeap(15);
        heap.add(100);
        heap.add(90);
        heap.add(85);
        heap.add(30);
        heap.add(45);
        heap.add(60);
        heap.add(70);
        heap.add(20);

        assert heap.left(0) == 1 : "esperado 1, obtido " + heap.left(0);
        assert heap.left(2) == 5 : "esperado 5, obtido " + heap.left(2);
        assert heap.left(1) == 3 : "esperado 3, obtido " + heap.left(1);

        System.out.println("testLeft OK");
    }

    public static void testRight() {
        // 100, 90, 85, 30, 45, 60, 70, 20
        MinHeap heap = new MinHeap(15);
        heap.add(100);
        heap.add(90);
        heap.add(85);
        heap.add(30);
        heap.add(45);
        heap.add(60);
        heap.add(70);
        heap.add(20);
        heap.add(113);

        assert heap.right(0) == 2 : "esperado 2, obtido " + heap.right(0);
        assert heap.right(2) == 6 : "esperado 6, obtido " + heap.right(2);
        assert heap.right(1) == 4 : "esperado 4, obtido " + heap.right(1);

        System.out.println("testRight OK");
    }

    public static void testParent() {
        // 100, 90, 85, 30, 45, 60, 70, 20
        MinHeap heap = new MinHeap(15);
        heap.add(100);
        heap.add(90);
        heap.add(85);
        heap.add(30);
        heap.add(45);
        heap.add(60);
        heap.add(70);
        heap.add(20);

        assert heap.parent(5) == 2 : "esperado 2, obtido " + heap.parent(5);
        assert heap.parent(3) == 1 : "esperado 1, obtido " + heap.parent(3);
        assert heap.parent(4) == 1 : "esperado 1, obtido " + heap.parent(4);

        System.out.println("testParent OK");
    }

    public static void testRemove() {
        int[] expected = new int[]{82, 65, 62, 45, 56, 52, 43, 30, 33, 38,
                0, 0, 0, 0, 0};

        MinHeap heap = new MinHeap(15);
        for (int i = 0; i <= 9; i++)
            heap.add(expected[i]);
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        int removed = heap.remove();
        assert removed == 82 : "esperado 82, obtido " + removed;
        expected = new int[]{65, 56, 62, 45, 38, 52, 43, 30, 33, 38, 0, 0, 0, 0, 0};
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        removed = heap.remove();
        assert removed == 65 : "esperado 65, obtido " + removed;
        expected = new int[]{62, 56, 52, 45, 38, 33, 43, 30, 33, 38, 0, 0, 0, 0, 0};
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        removed = heap.remove();
        assert removed == 62 : "esperado 62, obtido " + removed;
        expected = new int[]{56, 45, 52, 30, 38, 33, 43, 30, 33, 38, 0, 0, 0, 0, 0};
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        removed = heap.remove();
        assert removed == 56 : "esperado 56, obtido " + removed;
        expected = new int[]{52, 45, 43, 30, 38, 33, 43, 30, 33, 38, 0, 0, 0, 0, 0};
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        removed = heap.remove();
        assert removed == 52 : "esperado 52, obtido " + removed;
        expected = new int[]{45, 38, 43, 30, 33, 33, 43, 30, 33, 38, 0, 0, 0, 0, 0};
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        removed = heap.remove();
        assert removed == 45 : "esperado 45, obtido " + removed;
        expected = new int[]{43, 38, 33, 30, 33, 33, 43, 30, 33, 38, 0, 0, 0, 0, 0};
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        removed = heap.remove();
        assert removed == 43 : "esperado 43, obtido " + removed;
        expected = new int[]{38, 30, 33, 30, 33, 33, 43, 30, 33, 38, 0, 0, 0, 0, 0};
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        removed = heap.remove();
        assert removed == 38 : "esperado 38, obtido " + removed;
        expected = new int[]{33, 30, 33, 30, 33, 33, 43, 30, 33, 38, 0, 0, 0, 0, 0};
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        removed = heap.remove();
        assert removed == 33 : "esperado 33, obtido " + removed;
        expected = new int[]{30, 30, 33, 30, 33, 33, 43, 30, 33, 38, 0, 0, 0, 0, 0};
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        removed = heap.remove();
        assert removed == 30 : "esperado 30, obtido " + removed;
        expected = new int[]{30, 30, 33, 30, 33, 33, 43, 30, 33, 38, 0, 0, 0, 0, 0};
        assert Arrays.toString(expected).equals(heap.toString())
                : "esperado " + Arrays.toString(expected) + ", obtido " + heap.toString();

        System.out.println("testRemove OK");
    }

    public static void main(String[] args) {
        testLeft();
        testRight();
        testParent();
        testRemove();
        System.out.println("Todos os testes passaram!");
    }
}