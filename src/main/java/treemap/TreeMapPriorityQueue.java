package treemap;

import java.util.LinkedList;
import java.util.Queue;
import model.PriorityQueue;

/**
 * Implementação de uma fila de prioridade mínima usando uma Árvore Preto-Vermelha (Red-Black Tree).
 * Implementa a interface PriorityQueue, permitindo a inserção de elementos e a remoção
 * do elemento de menor valor com garantia de complexidade logarítmica O(log n).
 * 
 * Chaves duplicadas são armazenadas em uma fila (Queue) dentro do mesmo nó para manter
 * o balanceamento e evitar nós redundantes.
 */
public class TreeMapPriorityQueue implements PriorityQueue{
    
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    /**
     * Estrutura interna que representa um nó da Árvore Preto-Vermelha.
     */
    private class Node {
        int key;
        Queue<Integer> values = new LinkedList<>();
        Node left, right, parent;

        /**
         * Cor do nó: RED (true) ou BLACK (false). Por padrão, novos nós são inseridos como RED.
         */
        boolean color = RED;

        /**
         * Cria um novo nó com a chave informada.
         *
         * @param key valor da chave do nó
         */
        Node(int key) {
            this.key = key;
        }
    }

    private final Node NIL;
    private Node root;
    private int size;

    /**
     * Cria uma instância de TreeMapPriorityQueue vazia, inicializando o nó sentinela (NIL)
     * e definindo a raiz como NIL.
     */
    public TreeMapPriorityQueue() {
        NIL = new Node(-1);
        NIL.color = BLACK;
        NIL.left = NIL.right = NIL.parent = NIL;
        this.root = NIL;
        this.size = 0;
    }

    /**
     * Insere um novo valor na fila de prioridade, mantendo as propriedades de balanceamento
     * da Árvore Preto-Vermelha.
     * 
     * Se o valor já existir, ele é adicionado à fila interna do nó existente.
     *
     * @param value valor a ser inserido na fila de prioridade
     */ 
    @Override
    public void add(int value) {
        Node exists = searchNode(value);
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

    /**
     * Remove e retorna o menor elemento da fila de prioridade (o nó mais à esquerda).
     * 
     * Se o nó contiver múltiplos valores idênticos, remove apenas um elemento da fila do nó.
     * Caso a fila do nó fique vazia, o nó é removido da árvore.
     *
     * @return o menor elemento removido
     * @throws RuntimeException se a fila de prioridade estiver vazia
     */
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

    /**
     * Busca uma chave específica na árvore e retorna o valor correspondente.
     *
     * @param key chave a ser buscada na árvore
     * @return a chave encontrada ou -1 caso não pertença à árvore
     */
    @Override
    public int search(int key) {
        Node res = searchNode(key);
        return res != NIL ? res.key : -1;
    }

    /**
     * Realiza a busca binária por um nó que contenha a chave especificada.
     *
     * @param key chave procurada
     * @return o nó encontrado ou o sentinela NIL caso não seja encontrado
     */
    private Node searchNode(int key) {
        Node node = root;
        while (node != NIL && key != node.key) {
            if (key < node.key) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node;
    }

    /**
     * Verifica se a fila de prioridade está vazia.
     *
     * @return true se não houver elementos armazenados, false caso contrário
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Reorganiza e recolore os nós após uma inserção para garantir as propriedades
     * da Árvore Preto-Vermelha.
     *
     * @param node nó recém-inserido a partir do qual o ajuste será iniciado
     */
    private void fixInsert(Node node) {
        while (node.parent.color == RED) {
            // Para o caso em que o nó pai é filho esquerdo do avô
            if (node.parent == node.parent.parent.left) {
                Node uncle = node.parent.parent.right;
                // Caso em que o nó tio é vermelho, apenas recolore os nós
                if (uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                // Caso em que o nó tio é preto, realiza rotações para balancear a árvore
                } else {
                    // Caso o nó esteja em "zig-zag", realiza uma rotação para alinhar
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }

                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            // Para o caso em que o nó pai é filho direito do avô
            } else {
                Node uncle = node.parent.parent.left;
                // Caso em que o nó tio é vermelho, apenas recolore os nós
                if (uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                // Caso em que o nó tio é preto, realiza rotações para balancear a árvore
                } else {
                    // Caso o nó esteja em "zig-zag", realiza uma rotação para alinhar
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

    /**
     * Realiza uma rotação para a esquerda em torno do nó especificado.
     *
     * @param x nó sobre o qual a rotação à esquerda será executada
     */
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

    /**
     * Realiza uma rotação para a direita em torno do nó especificado.
     *
     * @param x nó sobre o qual a rotação à direita será executada
     */
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

    /**
     * Remove um nó da Árvore Preto-Vermelha e reajusta a estrutura conforme necessário.
     *
     * @param z nó a ser removido da árvore
     */
    private void rbDelete(Node z) {
        Node y = z;
        boolean yOriginalColor = y.color;
        Node x;
        // Caso o nó tenha 0 ou 1 filho, o próprio nó é removido 
        if (z.left == NIL) {
            x = z.right;
            rbTransplant(z, z.right);
        } else if (z.right == NIL) {
            x = z.left;
            rbTransplant(z, z.left);
        // Caso o nó tenha 2 filhos, encontra o sucessor mínimo e substitui o nó a ser removido, e quem será fisicamente removido é o sucessor mínimo   
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

    /**
     * Substitui a subárvore enraizada no nó u pela subárvore enraizada no nó v.
     *
     * @param u nó a ser substituído
     * @param v nó que ocupará a posição de u
     */
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

    /**
     * Encontra o nó com a menor chave a partir do nó fornecido.
     *
     * @param node nó raiz da subárvore
     * @return o nó contendo a menor chave da subárvore
     */
    private Node minimum(Node node) {
        while (node.left != NIL) {
            node = node.left;
        }

        return node;
    }

    /**
     * Reorganiza e recolore os nós após uma remoção para restaurar as propriedades da Árvore Preto-Vermelha quando um nó preto é removido.
     *
     * @param x nó a partir do qual a correção da remoção será efetuada
     */
    private void fixDelete(Node x) {
        while (x != root && x.color == BLACK) {
            // Caso em que o nó x é filho esquerdo do seu pai
            if (x == x.parent.left) {
                Node s = x.parent.right;
                // Caso em que o nó irmão é vermelho
                if (s.color == RED) {
                    s.color = BLACK;
                    x.parent.color = RED;
                    rotateLeft(x.parent);
                    s = x.parent.right;
                }
                // Caso em que o nó irmão é preto e ambos os filhos do nó irmão são pretos
                if (s.left.color == BLACK && s.right.color == BLACK) {
                    s.color = RED;
                    x = x.parent;
                } else {
                    // Caso em que o nó irmão é preto, filho esquerdo do nó irmão é vermelho e filho direito do nó irmão é preto
                    if (s.right.color == BLACK) {
                        s.left.color = BLACK;
                        s.color = RED;
                        rotateRight(s);
                        s = x.parent.right;
                    }
                    // Caso em que o nó irmão é preto e filho direito do nó irmão é vermelho
                    s.color = x.parent.color;
                    x.parent.color = BLACK;
                    s.right.color = BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            // Caso em que o nó x é filho direito do seu pai, o processo é simétrico ao caso anterior
            } else { 
                Node s = x.parent.left;
                // Caso em que o nó irmão é vermelho
                if (s.color == RED) {
                    s.color = BLACK;
                    x.parent.color = RED;
                    rotateRight(x.parent);
                    s = x.parent.left;
                }
                // Caso em que o nó irmão é preto e ambos os filhos do nó irmão são pretos
                if (s.right.color == BLACK && s.left.color == BLACK) {
                    s.color = RED;
                    x = x.parent;
                } else {
                    // Caso em que o nó irmão é preto, filho direito do nó irmão é vermelho e filho esquerdo do nó irmão é preto
                    if (s.left.color == BLACK) {
                        s.right.color = BLACK;
                        s.color = RED;
                        rotateLeft(s);
                        s = x.parent.left;
                    }
                    // Caso em que o nó irmão é preto e filho esquerdo do nó irmão é vermelho
                    s.color = x.parent.color;
                    x.parent.color = BLACK;
                    s.left.color = BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        // Garante que a raiz da árvore seja sempre preta
        x.color = BLACK;
    }
}
