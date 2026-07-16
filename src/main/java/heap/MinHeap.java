package heap;

import java.util.Arrays;

import model.InterfaceEstrutura;

public class MinHeap implements InterfaceEstrutura {
    private int[] heap;
    private int tail;

    public MinHeap(int capacity) {
        this.heap = new int[capacity];
        this.tail = -1;
    }

    public MinHeap(int[] array) {
        this.heap = array;
        this.tail = this.heap.length-1;
        this.buildHeap();
    }

    public boolean isEmpty() {
        return this.tail == -1;
    }

    public int left(int index) {
        return 2 * index + 1;
    }

    public int right(int index) {
        return 2 * index + 2;
    }

    public int parent(int index) {
        return (index - 1) / 2;
    }

    @Override
    public void add(int value) {
        if (this.tail >= (this.heap.length - 1))
            resize();
    
        this.tail += 1;
        this.heap[this.tail] = value;
        
        int i = this.tail;
        while (i > 0 && this.heap[parent(i)] > this.heap[i]) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    @Override
    public int remove(int key) {
        if (isEmpty()) throw new RuntimeException("Empty");

        int element = this.heap[0];
        this.heap[0] = this.heap[this.tail];
        this.tail -= 1;

        this.heapify(0);
        
        return element;
    }

    public int peek() {
        if (isEmpty()) throw new IllegalStateException("Dmpty");
        return this.heap[0];
    }

    public int size() {
        return this.tail + 1;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOfRange(this.heap, 0, this.tail + 1));
    }

    private void buildHeap() {
        for (int i = parent(this.tail); i >= 0; i--)
            heapify(i);
    }

    private void heapify(int index) {
        if (isLeaf(index) || !isValidIndex(index)) return;
        
        int indexMin = indexMin(index, left(index), right(index));

        if (indexMin != index) {
            swap(index, indexMin);
            heapify(indexMin);
        }
    } 

    private int indexMin(int index, int left, int right) {
        int min = index;

        if (this.heap[left] < this.heap[min]) min = left;
        if (isValidIndex(right) && this.heap[right] < this.heap[min]) min = right;

        return min;
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index <= this.tail;
    }
    
    private boolean isLeaf(int index) {
        return index > parent(this.tail) && index <= this.tail; 
    } 

    private void swap(int i, int j) {
        int temp = this.heap[i];
        this.heap[i] = this.heap[j];
        this.heap[j] = temp;
    }

    private void resize() {
        int[] newHeap = new int[this.heap.length * 2];
        for (int i = 0; i <= this.tail; i++)
            newHeap[i] = this.heap[i];
        
        this.heap = newHeap;
    }

    @Override
    public int search(int key) {
        for (int i = 0; i <= this.tail; i++) {
            if (this.heap[i] == key) return i;
        }
        return -1;
    }

}