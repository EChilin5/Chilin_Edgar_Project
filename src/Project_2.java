
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class Project_2 {
    private static PriorityBlockingQueue<Node> priorityQueue = new PriorityBlockingQueue<>();
    private static Hashtable<Character, String> find = new Hashtable<>();
    private static Node rootShare;
    private static String complete = "";
    private static HuffmanEncoder code;
    private static String output;


    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();
    private static final Object lock3 = new Object();


    private static int Alphabetsize = 256;
    private static int[] type1 = new int[Alphabetsize];

    ///Huffman Encoder Class /////////////////////////////////////////////
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

    static class ReadFile implements Runnable {

        @Override
        public void run() {
            try {
                Read();
            } catch (Exception e) {

            }
        }

        private static void Read() {
            synchronized (lock1) {
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
                        count = 0;
                    }
                    text += "\n";
                }
                complete = text;
                System.out.println("done printing text");

                lock1.notifyAll();
            }
        }
    }

    static class Compress implements Runnable {

        @Override
        public void run() {
            try {
                compress();
            } catch (Exception e) {

            }
        }

        private void Awake() {
            synchronized (lock2) {
                lock2.notifyAll();
            }
        }
        private void Awake3() {
            synchronized (lock3) {
                lock3.notifyAll();
            }
        }

        public void compress() throws InterruptedException {
            synchronized (lock1) {
                buildTable(complete);
                int[] te = type1;
                Node root = HuffmanTree(te);
                rootShare = root;
                Awake();
                lock1.wait();
                /// add notify than wait
                final HuffmanEncoder data = new HuffmanEncoder(encodeText(complete, find), root);
                code = data;
                System.out.println("done with compress");
                Awake3();

            }
        }

        private static String encodeText(String text, Hashtable<Character, String> find) {
            StringBuilder create = new StringBuilder();
            for (char character : text.toCharArray()) {
                create.append(find.get(character));
            }
            return create.toString();
        }

        public static void buildTable(final String text) {
            for (char character : text.toCharArray()) {
                type1[character]++;
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

    }

    static class SearchTable implements Runnable {
        public void run() {
            try {
                Search();
            } catch (Exception E) {

            }
        }

        private void Awake() {
            synchronized (lock1) {
                lock1.notifyAll();
            }
        }

        private void Search() throws InterruptedException {
            synchronized (lock2) {
                lock2.wait();
                System.out.println("done searching");

                find = new Hashtable<>();
                SearchTree(rootShare, "", find);
                Awake();
            }
        }

        private static void SearchTree(Node root, String entry, Hashtable<Character, String> find) {
            if (!root.isLeaf()) {
                SearchTree(root.left, entry + '0', find);
                SearchTree(root.right, entry + '1', find);
            } else {
                find.put(root.character, entry);
            }
        }
    }

        static class Decode implements Runnable {

            @Override
            public void run() {
                try {
                    System.out.println(decompress(code));
                } catch (Exception e) {

                }
            }


            public static String decompress(final HuffmanEncoder text) throws InterruptedException {
                synchronized (lock3) {
                    System.out.println("rej");
                    lock3.wait();
                    System.out.println("Awake 3 ");
                    StringBuilder result = new StringBuilder();
                    System.out.println(code.getEncodeText().length());

                    Node cuerrent = code.getRoot();
                    int i = 0;
                    System.out.println(code.getEncodeText().length() +" "+ 9);

                    while (i < 10) {
                        System.out.println("Awake 3 ");

                        while (!cuerrent.isLeaf()) {
                            System.out.println("Awake 3 ");

                            char bit = code.getEncodeText().charAt(i);
                            if (bit == '1') {
                                cuerrent = cuerrent.right;
                            } else if (bit == '0') {
                                cuerrent = cuerrent.left;
                            }
                            i++;
                        }
                        result.append(cuerrent.character);
                        cuerrent = code.getRoot();
                    }
                    output = result.toString();
                 return output;
                }
            }

        }


    public static void main(String[] args) {
        int coreCount = Runtime.getRuntime().availableProcessors(); // count of cores computer has gets
        ExecutorService service = Executors.newFixedThreadPool(coreCount);

        service.execute(new SearchTable());
        service.execute(new Compress());
        service.execute(new Decode());
        service.execute(new ReadFile());
        //service.execute(new Reverse(storage, reverseCopy));
        //service.execute(new Print(reverseCopy));
        service.shutdown();
    }
}
