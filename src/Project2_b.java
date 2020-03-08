import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;

public class Project2_b {
    private static List<String> aq = Collections.synchronizedList(new ArrayList<String>());
    private static PriorityBlockingQueue<Node> priorityQueue = new PriorityBlockingQueue<>();
    private static Hashtable<Character, String > find = new Hashtable<>();
    private static Node rootShare;
    // private static String complete="";
    private static HuffmanEncoder code;
    private static StringBuilder end;

    private static int Alphabetsize = 300;
    private static  int[] type1 = new int[Alphabetsize];

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
            String entry = "";
            while (s2.hasNext()) {
                String s = s2.next();

                entry += s + " ";
                aq.add(s);
                count =0;
            }

            aq.add("\n");

        }
    }


    ////////////////////////////////////////////////////////////////////////////////////
    public static String  decompress( HuffmanEncoder text){
        StringBuilder result  = new StringBuilder();
        Node current = text.getRoot();
        int i = 0;
        while(i <text.getEncodeText().length()){
            while(!current.isLeaf()){
                char bit = text.getEncodeText().charAt(i);
                if(bit =='1'){
                    current =current.right;
                }else if(bit== '0') {
                    current = current.left;

                }
                i++;
            }
            result.append(current.character );
            current = text.getRoot();
        }
        return result.toString();
    }


    public static void buildTable(final String text){
        for(char character: text.toCharArray()){
            type1[character]++;
        }
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

private static String encodeText(String text, Hashtable<Character, String> find) {
        StringBuilder create = new StringBuilder();
        for(char character: text.toCharArray()){
            create.append(find.get(character));
        }
        return create.toString();
    }

     public static void pmax( List<String> data ,int numThreads) {
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        try {
            List<Future<HuffmanEncoder>> list =Collections.synchronizedList(new ArrayList<>());;
            for (int i = 0; i < data.size(); i++) {
               final List<String> subArr = data;

                int finalI = i;
                int finalI1 = i;
                list.add(pool.submit(new Callable<HuffmanEncoder>() {
                    @Override
                    public HuffmanEncoder call() throws Exception {

                                buildTable(aq.get(finalI1));
                                int[] te = type1;
                                Node root = HuffmanTree(te);
                                rootShare = root;
                                Search();
                                code = new HuffmanEncoder(encodeText(aq.get(finalI1), find), root);
                                //System.out.println(code.encodeTedxt);
                               // System.out.print(code.getEncodeText() + " \n");

                        return code;
                    }
                }));
            }
                int max = 0;
                int count =0;
                String winner ="";
                for (Future<HuffmanEncoder> future : list) {

                    if (future.get().getEncodeText() != null) {
                        byte[] ascii = future.get().getEncodeText().getBytes();
                        System.out.println("contents of byte array in default encoding: "
                                + Arrays.toString(ascii));

                    }
                }
            } catch(Exception e){
                System.out.println("hello " + e.getMessage());
            } finally{
                pool.shutdown();
            }
        }





    public static void main(String[] args){
        ReadFile();
        int coreCount = Runtime.getRuntime().availableProcessors(); // count of cores computer has gets
       pmax(aq, coreCount);


    }

}
