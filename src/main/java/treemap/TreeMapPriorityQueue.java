package treemap;

import java.util.LinkedList;
import java.util.Queue;
import model.PriorityQueue;

public class TreeMapPriorityQueue implements PriorityQueue{
    
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node {
        int key;
        Queue<Integer> values = new LinkedList<>();
        Node left, right, parent;
        boolean color = RED;

        Node(int key) {
            this.key = key;
        }
    }

    private final Node NIL;
    private Node root;
    private int size;

    public TreeMapPriorityQueue() {
        NIL = new Node(-1);
        NIL.color = BLACK;
        NIL.left = NIL.right = NIL.parent = NIL;
        root = NIL;
        this.size = 0;
    }

    @Override
    public void add(int value) {
        Node exists = searchNode(root, value);
        if (exists != NIL) {
            exists.values.add(value);
            this.size++;
            return;
        }

        Node node = new Node(value);
        node.values.add(value);
        node.left = NIL;
        node.right = NIL;

        Node y = NIL;
        Node x = root;

        while (x != NIL) {
            y = x;
            if (node.key < x.key) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        node.parent = y;
        if (y == NIL) {
            root = node;
        } else if (node.key < y.key) {
            y.left = node;
        } else {
            y.right = node;
        }

        fixInsert(node);
        this.size++;
    }

    @Override
    public int removeMin() {
        if (root == NIL) {
            throw new RuntimeException("Empty");
        }

        Node minNode = minimum(root);

        int element = minNode.values.poll();
        this.size--;

        if (minNode.values.isEmpty()) {
            rbDelete(minNode);
        }

        return element;
    }

    public int remove(int key) {
        if (root == NIL) {
            throw new RuntimeException("Empty");
        }

        Node minNode = root;
        while (minNode.left != NIL) {
            minNode = minNode.left;
        }

        int element = minNode.values.poll();
        this.size--;

        if (minNode.values.isEmpty()) {
            rbDelete(minNode);
        }

        return element;
    }

    @Override
    public int search(int key) {
        Node res = searchNode(root, key);
        return res != NIL ? res.key : -1;
    }

    private Node searchNode(Node node, int key) {
        while (node != NIL && key != node.key) {
            if (key < node.key) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    private void fixInsert(Node node) {
        while (node.parent.color == RED) {
            if (node.parent == node.parent.parent.left) {
                Node uncle = node.parent.parent.right;
                if (uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }

                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                Node uncle = node.parent.parent.left;
                if (uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != NIL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NIL) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != NIL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NIL) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    private void rbDelete(Node z) {
        Node y = z;
        boolean yOriginalColor = y.color;
        Node x;
        if (z.left == NIL) {
            x = z.right;
            rbTransplant(z, z.right);
        } else if (z.right == NIL) {
            x = z.left;
            rbTransplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
        rbTransplant(z, y);
        y.left = z.left;
        y.left.parent = y;
        y.color = z.color;
        }
        if (yOriginalColor == BLACK) {
            fixDelete(x);
        }
    }

    private void rbTransplant(Node u, Node v) {
        if (u.parent == NIL) {
            root = v;
        } else if(u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    private Node minimum(Node node) {
        while (node.left != NIL) {
            node = node.left;
        }

        return node;
    }

   private void fixDelete(Node x) {
        while (x != root && x.color == BLACK) {
            if (x == x.parent.left) {
                Node s = x.parent.right;
                if (s.color == RED) {
                    s.color = BLACK;
                    x.parent.color = RED;
                    rotateLeft(x.parent);
                    s = x.parent.right;
                }
                if (s.left.color == BLACK && s.right.color == BLACK) {
                    s.color = RED;
                    x = x.parent;
                } else {
                    if (s.right.color == BLACK) {
                        s.left.color = BLACK;
                        s.color = RED;
                        rotateRight(s);
                        s = x.parent.right;
                    }
                    s.color = x.parent.color;
                    x.parent.color = BLACK;
                    s.right.color = BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            } else { // Lado espelhado
                Node s = x.parent.left;
                if (s.color == RED) {
                    s.color = BLACK;
                    x.parent.color = RED;
                    rotateRight(x.parent);
                    s = x.parent.left;
                }
                if (s.right.color == BLACK && s.left.color == BLACK) {
                    s.color = RED;
                    x = x.parent;
                } else {
                    if (s.left.color == BLACK) {
                        s.right.color = BLACK;
                        s.color = RED;
                        rotateLeft(s);
                        s = x.parent.left;
                    }
                    s.color = x.parent.color;
                    x.parent.color = BLACK;
                    s.left.color = BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }
}
