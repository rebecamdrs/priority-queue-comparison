package heap;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {

    private MinHeap heap;

    @BeforeEach
    public void setUp() {
        // Inicializa uma heap vazia antes de cada teste
        heap = new MinHeap(10);
    }

    @Test
    public void testInsertAndPeek() {
        heap.insert(15);
        heap.insert(10);
        heap.insert(20);
        heap.insert(5);

        // O menor elemento (5) deve estar no topo
        assertEquals(5, heap.peek(), "O menor elemento deve estar no topo");
        assertEquals(4, heap.size(), "O tamanho da heap deve ser 4");
    }

    @Test
    public void testRemoveOrder() {
        heap.insert(30);
        heap.insert(10);
        heap.insert(20);
        heap.insert(5);

        // A remoção deve garantir a ordem crescente (propriedade da MinHeap)
        assertEquals(5, heap.remove());
        assertEquals(10, heap.remove());
        assertEquals(20, heap.remove());
        assertEquals(30, heap.remove());
        assertTrue(heap.isEmpty(), "A heap deveria estar vazia");
    }

    @Test
    public void testConstructorWithArray() {
        int[] array = {40, 10, 30, 5, 20};
        MinHeap heapFromArray = new MinHeap(array);

        // O construtor deve rodar o buildHeap() e colocar o menor no topo
        assertEquals(5, heapFromArray.peek());
        assertEquals(5, heapFromArray.size());
    }

    @Test
    public void testResize() {
        MinHeap smallHeap = new MinHeap(2);
        smallHeap.insert(10);
        smallHeap.insert(20);
        
        // Este terceiro insert deve disparar o método resize() sem quebrar
        assertDoesNotThrow(() -> smallHeap.insert(5));
        assertEquals(3, smallHeap.size());
        assertEquals(5, smallHeap.peek());
    }

    @Test
    public void testPeekOnEmptyHeapThrowsException() {
        // Garante que espiar uma heap vazia lança a exceção correta
        assertThrows(IllegalStateException.class, () -> {
            heap.peek();
        });
    }

    @Test
    public void testRemoveOnEmptyHeapThrowsException() {
        // Garante que remover de uma heap vazia lança a exceção correta
        assertThrows(RuntimeException.class, () -> {
            heap.remove();
        });
    }
}
