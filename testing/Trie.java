package testing;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

    private Node<Character, LinkedList<String>> getNode(Node<Character, LinkedList<String>> node, char alpha ){
        if(node.next == null){
            return null;
        }
        
        for (Node<Character, LinkedList<String>> current : node.next) {
            if (alpha == current.key) {
                return current;
            }
        }

        return null;
    }

    public LinkedList<String> get(String item){
        char[] namespace = item.toCharArray();
        Node<Character, LinkedList<String> > current = this.root;
        for (char elem : namespace) {
            if(current == null){
                return null;
            }
            current = this.getNode(current, elem);
        }
        return current == null ? null: current.value;
    }

    private Node<Character, LinkedList<String>> removeNode(Node<Character, LinkedList<String>> node, char alpha, String ID ){
        if(node.next == null){
            return null;
        }

        for (Node<Character, LinkedList<String>> current : node.next) {
            if (alpha == current.key) {
                current.value.remove(ID);
                return current;
            }
        }

        var IDlist = new LinkedList<String>();
        IDlist.add(ID);
        var newNode = new Node<>(alpha, IDlist);
        node.next.add(newNode);

        return newNode;
    }
 
    public boolean remove(String item, String ID){
        char[] namespace = item.toCharArray();
        Node<Character, LinkedList<String>> current = this.root;
        for (char elem : namespace) {
            if(current == null){
                return false;
            }
            current = this.removeNode(current, elem, ID);
        }

        return true;
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


