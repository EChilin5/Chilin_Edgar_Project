import jdk.dynalink.NamedOperation;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class Project {

    private static ArrayList<String> aq = new ArrayList<>();
    private static PriorityBlockingQueue<Node> priorityQueue = new PriorityBlockingQueue<>();
    private static Hashtable<Character, String> find = new Hashtable<>();
    private static Node rootShare;
    // private static String complete="";
    private static HuffmanEncoder code;
    private static StringBuilder end;

    private static int Alphabetsize = 600;
    private static int[] type1 = new int[Alphabetsize];

    ////////////////////////////////////////////////////////////////////////////////////
    static class HuffmanEncoder {
        Node root;
        String encodeTedxt;

        public HuffmanEncoder(String encodeTedxt, Node root) {
            this.root = root;
            this.encodeTedxt = encodeTedxt;
        }

        public Node getRoot() {
            return this.root;
        }

        public String getEncodeText() {
            return this.encodeTedxt;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////

    static class Node implements Comparable<Node> {
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

        public boolean isLeaf() {
            return this.left == null && this.right == null;
        }

        @Override
        public int compareTo(Node o) {

            int CompareValues = Integer.compare(this.type, o.type);
            if (CompareValues != 0) {
                return CompareValues;
            }
            return Integer.compare(this.character, o.character);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////

    public static void buildTable(final String text) {
        for (char character : text.toCharArray()) {
            type1[character]++;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////

    public static void compress(ArrayList<String> aq) throws IOException {
        for (int i = 0; i < aq.size(); i++) {
            buildTable(aq.get(i));
            int[] te = type1;
            Node root = HuffmanTree(te);
            rootShare = root;
            Search();
            code = new HuffmanEncoder(encodeText(aq.get(i), find), root);
            //System.out.println(code.encodeTedxt);
            // System.out.print(code.getEncodeText() + " \n");
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter("src/output.txt", true)  //Set true for append mode
            );
           // byte[] bytesEncoded = Base64.getEncoder().encode(code.getEncodeText().getBytes());
           // System.out.println("encoded value is " + new String(bytesEncoded));
           // byte[] str = code.getEncodeText().getBytes(Charset.forName("US-ASCII"));
            System.out.println(code.getEncodeText());
            writer.write(code.getEncodeText().hashCode());
            writer.newLine();
            writer.close();

          //  System.out.println(str);
        }
    }

    public static Node HuffmanTree(int[] type) {
        for (char i = 0; i < Alphabetsize; i++) {
            if (type[i] > 0) {
                priorityQueue.add(new Node(i, type[i], null, null));
            }
        }
        if (priorityQueue.size() == 1) {
            priorityQueue.add(new Node('\0', 1, null, null));
        }
        while (priorityQueue.size() > 1) {
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node parent = new Node('\0', left.type + right.type, left, right);
            priorityQueue.add(parent);
        }
        return priorityQueue.poll();
    }

    ////////////////////////////////////////////////////////////////////////////////////

    private static String encodeText(String text, Hashtable<Character, String> find) {
        StringBuilder create = new StringBuilder();
        for (char character : text.toCharArray()) {
            create.append(find.get(character).hashCode());
        }
        return create.toString();
    }
    ////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////

    private static void Search() {
        find = new Hashtable<>();
        SearchTree(rootShare, "", find);
    }

    private static void SearchTree(Node root, String entry, Hashtable<Character, String> find) {
        if (!root.isLeaf()) {
            SearchTree(root.left, entry + '0', find);
            SearchTree(root.right, entry + '1', find);
        } else {
            find.put(root.character, entry);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////

    public static String decompress(HuffmanEncoder text) {
        StringBuilder result = new StringBuilder();
        Node current = text.getRoot();
        int i = 0;
        while (i < text.getEncodeText().length()) {
            while (!current.isLeaf()) {
                char bit = text.getEncodeText().charAt(i);
                if (bit == '1') {
                    current = current.right;
                } else if (bit == '0') {
                    current = current.left;

                }
                i++;
            }
            result.append(current.character);
            current = text.getRoot();
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

            String entry = "";
            while (s2.hasNext()) {
                String s = s2.next();

                entry += s + " ";
            }
            aq.add(entry);
            aq.add("\n");

        }
        //aq.add("hello");
        //complete = text;
    }


    public static void main(String[] args) throws IOException {
        ReadFile();
        //String tryOne = complete;
        compress(aq);
        //  System.out.println(code.encodeTedxt);

        //  System.out.println(decompress(code));

    }


}
