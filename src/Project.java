import jdk.dynalink.NamedOperation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.PriorityBlockingQueue;

public class Project {

    private static PriorityBlockingQueue<Node> priorityQueue = new PriorityBlockingQueue<>();
    private static  Hashtable<Character, String > find = new Hashtable<>();
    private static Node rootShare;
    private static String complete="";
    private static HuffmanEncoder code;
    private static StringBuilder end;

    private static int Alphabetsize = 256;
    private static  int[] type1 = new int[Alphabetsize];
  ////////////////////////////////////////////////////////////////////////////////////
    static class HuffmanEncoder{
        Node root;
        String encodeTedxt;

        public HuffmanEncoder( String encodeTedxt, Node root) {
            this.root = root;
            this.encodeTedxt = encodeTedxt;
        }

        public Node getRoot(){
            return this.root;
        }
        public String getEncodeText(){
            return this.encodeTedxt;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////

    static class Node implements Comparable<Node>{
        private char character;
        private int type;
        private Node right;
        private Node left;

        public Node(char character, int type, Node right, Node left) {
            this.character = character;
            this.type = type;
            this.right = right;
            this.left = left;
        }
        public  boolean isLeaf(){
            return this.left == null && this.right ==null;
        }

        @Override
        public int compareTo(Node o) {

            int CompareValues = Integer.compare(this.type, o.type);
            if(CompareValues != 0){
                return  CompareValues;
            }
            return Integer.compare(this.character, o.character);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////

    public static void buildTable(final String text){
        for(char character: text.toCharArray()){
            type1[character]++;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////

    public static void compress(final String text){
       buildTable(text);
        int[] te = type1;
        Node root = HuffmanTree(te);
        rootShare = root;
         Search();
        code = new HuffmanEncoder(encodeText(text, find),root);
    }
    public static Node HuffmanTree(int[] type){
        for(char i=0; i< Alphabetsize; i++){
            if(type[i] > 0){
                priorityQueue.add(new Node(i, type[i], null, null));
            }
        }
        if(priorityQueue.size() == 1){
            priorityQueue.add(new Node('\0',1, null, null));
        }
        while(priorityQueue.size()>1){
            Node left =priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node parent = new Node('\0', left.type +right.type, left, right);
            priorityQueue.add(parent);
        }
        return priorityQueue.poll();
    }

    ////////////////////////////////////////////////////////////////////////////////////

    private static String encodeText(String text, Hashtable<Character, String> find) {
        StringBuilder create = new StringBuilder();
        for(char character: text.toCharArray()){
            create.append(find.get(character));
        }
        return create.toString();
    }
    ////////////////////////////////////////////////////////////////////////////////////



      ////////////////////////////////////////////////////////////////////////////////////

    private static void   Search(){
       find  = new Hashtable<>();
        SearchTree(rootShare, "",find);
    }

    private static void SearchTree(Node root, String entry, Hashtable<Character, String> find) {
        if(!root.isLeaf()){
            SearchTree(root.left, entry + '0', find);
            SearchTree(root.right, entry + '1', find);
        }else{
            find.put(root.character, entry);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////

    public static String  decompress(final HuffmanEncoder text){
        StringBuilder result  = new StringBuilder();
        Node cuerrent = text.getRoot();
        int i = 0;
        while(i <text.getEncodeText().length()){
            while(!cuerrent.isLeaf()){
                char bit = text.getEncodeText().charAt(i);
                if(bit =='1'){
                    cuerrent =cuerrent.right;
                }else if(bit== '0') {
                    cuerrent = cuerrent.left;

                }
                i++;
            }
            result.append(cuerrent.character );
            cuerrent = text.getRoot();
        }
        return result.toString();
    }

    private static void ReadFile() {
        int count = 0;
        Scanner sc2 = null;
        String text = "";
        try {
            sc2 = new Scanner(new File("src/USConstitution.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc2.hasNextLine()) {
            Scanner s2 = new Scanner(sc2.nextLine());
            count = 1;
            while (s2.hasNext()) {
                String s = s2.next();
                text += s + " ";
                count =0;
            }
            text +="\n";
        }
      complete = text;
    }



    public static void main(String[] args){
        ReadFile();
        String tryOne = complete;
        compress(tryOne);
        System.out.println(code.encodeTedxt);

        System.out.println(decompress(code));

    }


}
