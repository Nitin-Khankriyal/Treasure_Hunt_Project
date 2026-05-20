import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

class Node {
    int x, y;
    String name;
    int cap;

    Node(int x, int y, String name, int cap) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.cap = cap;
    }
}

class Edge {
    Node from, to;
    int w;

    Edge(Node f, Node t, int w) {
        from = f;
        to = t;
        this.w = w;
    }
}

class Item {
    String name;
    int weight, price;

    Item(String n, int w, int p) {
        name = n;
        weight = w;
        price = p;
    }
}

public class TreasureHuntFinal extends JFrame {

    java.util.List<Node> nodes = new ArrayList<>();
    java.util.List<Edge> edges = new ArrayList<>();
    JTextArea output = new JTextArea();
    int count = 0;

    public TreasureHuntFinal() {

        setTitle("Treasure Hunt - FULL LOGIC FINAL");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel canvas = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                for (Edge e : edges) {
                    g.drawLine(e.from.x, e.from.y, e.to.x, e.to.y);
                    g.drawString("" + e.w,
                            (e.from.x + e.to.x) / 2,
                            (e.from.y + e.to.y) / 2);
                }

                for (Node n : nodes) {
                    g.setColor(Color.ORANGE);
                    g.fillOval(n.x - 15, n.y - 15, 30, 30);
                    g.setColor(Color.BLACK);
                    g.drawString(n.name + "(" + n.cap + ")", n.x - 15, n.y - 20);
                }
            }
        };

        canvas.setBackground(Color.WHITE);

        canvas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                char name = (char) ('A' + count++);
                int cap = 20 + new Random().nextInt(6);
                nodes.add(new Node(e.getX(), e.getY(), "" + name, cap));
                repaint();
            }
        });

        JButton addEdge = new JButton("Add Edge");
        JButton run = new JButton("Run Simulation");

        addEdge.addActionListener(e -> addEdgeDialog());
        run.addActionListener(e -> simulate());

        JPanel top = new JPanel();
        top.add(addEdge);
        top.add(run);

        output.setEditable(false);
        output.setBackground(Color.BLACK);
        output.setForeground(Color.GREEN);

        add(top, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(new JScrollPane(output), BorderLayout.SOUTH);
    }

    // ================= EDGE =================
    void addEdgeDialog() {

        String[] names = nodes.stream().map(n -> n.name).toArray(String[]::new);

        JComboBox<String> from = new JComboBox<>(names);
        JComboBox<String> to = new JComboBox<>(names);
        JTextField w = new JTextField();

        JPanel p = new JPanel(new GridLayout(3,2));
        p.add(new JLabel("From")); p.add(from);
        p.add(new JLabel("To")); p.add(to);
        p.add(new JLabel("Distance")); p.add(w);

        int res = JOptionPane.showConfirmDialog(this, p, "Edge", JOptionPane.OK_CANCEL_OPTION);

        if(res == JOptionPane.OK_OPTION){
            Node f = nodes.get(from.getSelectedIndex());
            Node t = nodes.get(to.getSelectedIndex());
            edges.add(new Edge(f,t,Integer.parseInt(w.getText())));
            repaint();
        }
    }

    // ================= TREASURE =================
    List<Item> cave() {
        return Arrays.asList(
                new Item("A1",2,5), new Item("B1",3,7), new Item("C1",7,10),
                new Item("D1",8,14), new Item("E1",7,19)
        );
    }

    List<Item> mountain() {
        return Arrays.asList(
                new Item("A2",5,15), new Item("B2",4,9), new Item("C2",6,19),
                new Item("D2",9,34), new Item("E2",7,19)
        );
    }

    List<Item> sea() {
        return Arrays.asList(
                new Item("A3",3,8), new Item("B3",2,4), new Item("C3",2,3),
                new Item("D3",9,17), new Item("E3",11,28)
        );
    }

    // ================= DIJKSTRA =================
    int shortest(Node src, Node dest, Map<Node,Node> parent){

        Map<Node,Integer> dist = new HashMap<>();
        for(Node n: nodes) dist.put(n,Integer.MAX_VALUE);
        dist.put(src,0);

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        pq.add(src);

        while(!pq.isEmpty()){
            Node u = pq.poll();

            for(Edge e: edges){
                if(e.from==u){
                    Node v = e.to;
                    int nd = dist.get(u)+e.w;

                    if(nd < dist.get(v)){
                        dist.put(v,nd);
                        parent.put(v,u);
                        pq.add(v);
                    }
                }
            }
        }
        return dist.get(dest);
    }

    // ================= KNAPSACK =================
    int knap(List<Item> items, int cap){
        int n = items.size();
        int[][] dp = new int[n+1][cap+1];

        for(int i=1;i<=n;i++){
            Item it = items.get(i-1);
            for(int w=0;w<=cap;w++){
                if(it.weight<=w)
                    dp[i][w]=Math.max(dp[i-1][w],it.price+dp[i-1][w-it.weight]);
                else
                    dp[i][w]=dp[i-1][w];
            }
        }
        return dp[n][cap];
    }

    double fractional(List<Item> items,int cap){
        items.sort((a,b)->Double.compare((double)b.price/b.weight,
                (double)a.price/a.weight));

        double val=0;

        for(Item it:items){
            if(cap>=it.weight){
                cap-=it.weight;
                val+=it.price;
            }else{
                val+=((double)cap/it.weight)*it.price;
                break;
            }
        }
        return val;
    }

    // ================= SIMULATION =================
    void simulate(){

        if(nodes.size()<4){
            output.setText("Need 4 nodes");
            return;
        }

        Node src=nodes.get(0);
        src.name="East-Blue";

        Node c=nodes.get(1);
        Node m=nodes.get(2);
        Node s=nodes.get(3);

        c.name="Cave";
        m.name="Mountain";
        s.name="Sea-Floor";

        String[] h={"Luffy","Law","Blackbeard"};
        List<Node> d=Arrays.asList(c,m,s);
        Collections.shuffle(d);

        StringBuilder sb=new StringBuilder();
        double best=-1e9;
        String win="";

        for(int i=0;i<3;i++){

            Node dest=d.get(i);
            Map<Node,Node> parent=new HashMap<>();

            int dist=shortest(src,dest,parent);

            List<Node> path=new ArrayList<>();
            Node cur=dest;
            while(cur!=null){
                path.add(cur);
                cur=parent.get(cur);
            }
            Collections.reverse(path);

            int fuel=dist*40;
            int food=path.size()*100;

            int cap=20+new Random().nextInt(6);

            double treasure=0;

            if(dest==c) treasure=knap(cave(),cap);
            else if(dest==m) treasure=knap(mountain(),cap);
            else treasure=fractional(sea(),cap);

            double profit=treasure-(fuel+food);

            if(profit>best){
                best=profit;
                win=h[i];
            }

            sb.append("\n====================\n");
            sb.append(h[i]+" → "+dest.name+"\n");
            sb.append("Path: ");
            for(Node n:path) sb.append(n.name+" ");
            sb.append("\nTreasure: "+treasure);
            sb.append("\nFuel: "+fuel);
            sb.append("\nFood: "+food);
            sb.append("\nProfit: "+profit+"\n");
        }

        sb.append("\n🏆 WINNER: "+win);

        output.setText(sb.toString());
    }

    public static void main(String[] args){
        new TreasureHuntFinal().setVisible(true);
    }
}