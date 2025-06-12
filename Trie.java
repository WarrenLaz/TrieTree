import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class Trie implements Serializable{

    private class Node<K, V> implements Serializable{
        public K key;
        public V value;
        public LinkedList<Node<K,V>> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }

    }

    private static final long serialVersionUID = 1L;

    private final Node<Character, LinkedList<String>> root;
    private final String OBJECTUID;

    {
        this.root = new Node<>(null, null);
    }

    public Trie(){
        this.OBJECTUID = null;
    }

    public Trie(String objectUID){
        this.OBJECTUID = objectUID;
    }
    
    private Node<Character, LinkedList<String>> updateTree(Node<Character, LinkedList<String>> node, char alpha, String ID){
        if(node.next == null){
            node.next = new LinkedList<>();
        }

        for (Node<Character, LinkedList<String>> current : node.next) {
            if (alpha == current.key) {
                current.value.add(ID);
                return current;
            }
        }

        var IDlist = new LinkedList<String>();
        IDlist.add(ID);
        var newNode = new Node<>(alpha, IDlist);
        node.next.add(newNode);

        return newNode;
    }


    public void add(String item, String ID){
        char[] namespace = item.toCharArray();
        System.out.println(item + ID);
        Node<Character, LinkedList<String> > current = this.root;
        for (char elem : namespace) {
            current = this.updateTree(current, elem, ID);
        }
    }

    @Override
    public String toString(){
        Queue<Node<Character, LinkedList<String>>> queue = new LinkedList<>();
        Node<Character, LinkedList<String>> current;
        String output = "";

        for (Node<Character, LinkedList<String>> item : this.root.next) {
            queue.add(item);
        }

        while(!queue.isEmpty()){
            current = queue.poll();
            output += " " + current.key;
            if(current.next == null)
                continue;

            for (Node<Character, LinkedList<String>> item : current.next) {
                queue.add(item);
            }
        }

        return output;
    }

    public boolean remove(String item){
        return true;
    }

    public String[] get(String item){
        return null;
    }

    public void saveObject(){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.OBJECTUID + ".ser"))) {
            oos.writeObject(this);
            System.out.println("Object saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Trie loadObject(String objectUID) {
        Trie obj = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objectUID + ".ser"))) {
            obj = (Trie) ois.readObject();
            System.out.println("Object loaded.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.print("Object Not Found");
        }
        return obj;
    }
}


