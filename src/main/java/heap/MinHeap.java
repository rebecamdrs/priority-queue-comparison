package heap;

import java.util.Arrays;

import model.PriorityQueue;

/**
* Implementação de uma fila de prioridade mínima (Min-Heap) usando um array
 * como estrutura de dados. Nessa estrutura, o menor elemento está sempre na raiz
 * (índice 0), e cada nó pai é menor que seus filhos.
 */
public class MinHeap implements PriorityQueue {
    private int[] heap;
    private int tail;

    /**
     * Cria um Min-Heap vazio com uma capacidade inicial definida.
     *
     * @param capacity capacidade inicial do array interno
     */
    public MinHeap(int capacity) {
        this.heap = new int[capacity];
        this.tail = -1;
    }

    /**
     * Cria um Min-Heap a partir de um array já existente, reorganizando seus
     * elementos para satisfazer a propriedade de heap mínimo.
     *
     * @param array array de valores a partir do qual o heap será construído
     */
    public MinHeap(int[] array) {
        this.heap = array;
        this.tail = this.heap.length-1;
        this.buildHeap();
    }

    /**
     * Verifica se o heap está vazio.
     *
     * @return true se não houver elementos, false caso contrário
     */
    public boolean isEmpty() {
        return this.tail == -1;
    }

    /**
     * Calcula o índice do filho à esquerda de um nó.
     *
     * @param index índice do nó pai
     * @return índice do filho esquerdo
     */
    public int left(int index) {
        return 2 * index + 1;
    }

    /**
     * Calcula o índice do filho à esquerda de um nó.
     *
     * @param index índice do nó pai
     * @return índice do filho esquerdo
     */
    public int right(int index) {
        return 2 * index + 2;
    }

    /**
     * Calcula o índice do nó pai de um nó.
     *
     * @param index índice do nó filho
     * @return índice do nó pai
     */
    public int parent(int index) {
        return (index - 1) / 2;
    }

    /**
     * Insere um novo valor no heap, mantendo a propriedade de heap mínimo.
     * 
     * O elemento é inserido na última posição e "sobe" trocando de posição 
     * com seu pai enquanto for menor que ele.
     *
     * @param value valor a ser inserido no heap
     */
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

    /**
     * Remove e retorna o menor elemento do heap (a raiz).
     * 
     * O último elemento do heap é movido para a raiz e depois faz um heapify
     * para seguir a invariante do heap. 
     *
     * @return o menor elemento removido
     * @throws RuntimeException se o heap estiver vazio
     */
    @Override
    public int remove(int key) {
        if (isEmpty()) throw new RuntimeException("Empty");

        int element = this.heap[0];
        this.heap[0] = this.heap[this.tail];
        this.tail -= 1;

        this.heapify(0);
        
        return element;
    }

    /**
     * Retorna, sem remover, o menor elemento do heap.
     *
     * @return o valor armazenado na raiz do heap
     * @throws IllegalStateException se o heap estiver vazio
     */
    public int peek() {
        if (isEmpty()) throw new IllegalStateException("Empty");
        return this.heap[0];
    }

    /**
     * Retorna a quantidade de elementos atualmente armazenados no heap.
     *
     * @return o número de elementos do heap
     */
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