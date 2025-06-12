
public class Main{
    public static void main(String[] args){
        if(args.length < 1){
            System.out.println("Parameters has to be greater than 2");
            return;
        }

        Trie tree = Trie.loadObject(args[0]) != null ? Trie.loadObject(args[0]) : new Trie(args[0]);

        for(int i = 1; i < args.length; i++){
            String[] item = args[i].split(":");
            try{
                tree.add(item[0], item[1]);
            } catch (IndexOutOfBoundsException Exception){
                System.out.println(Exception);
            }
        }

        tree.saveObject();

        System.out.println(tree.toString());
    }
}