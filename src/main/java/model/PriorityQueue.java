package model;

public interface PriorityQueue {
    public void add(int value);
    public int remove(int key); // heap sempre remove o 0
    public int search(int key);
}