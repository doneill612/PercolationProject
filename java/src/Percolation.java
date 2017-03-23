import java.io.*;
import java.util.*;

/**
 * @author David O'Neill
 * @since 2/3/2017
 */
public class Percolation {

    public static void main(String[] args) throws IOException {

        long start = System.nanoTime();

        float prob = 0.0F;

        // Output to be written to a csv file ...
        BufferedWriter bw = new BufferedWriter(new FileWriter("results.csv"));

        // Testing occupation probabilities from 0.0 -> 1.0
        while(prob < 1.0F) test(prob += 0.02F, bw);

        // Finished writing, close buffer.
        bw.close();

        long end = System.nanoTime();
        System.out.println("Run-time: " + (Math.abs(start - end) / 1000000000.0) + "sec.");
    }

    /**
     * Finds "roots" in the lattice, AKA starting locations for the DFS to find clusters.
     * Traverses the top row and left rank for occupied nodes.
     * @param lattice
     * @param roots
     */
    private static void findRoots(Lattice lattice, List<Site> roots) {

        for(int i = 0; i < Lattice.LATTICE_DIM; i++) {
            if(lattice.getSiteAt(i, 0).isOccupied()) {
                roots.add(lattice.getSiteAt(i, 0));
            }
        }

        for(int j = 0; j < Lattice.LATTICE_DIM; j++) {
            if(lattice.getSiteAt(0, j).isOccupied()) {
                roots.add(lattice.getSiteAt(0, j));
            }
        }
    }

    /**
     * Determines the percolation probability of a given lattice with a specified occupation probability,
     * and records the result to a file.
     * The percolation probability is defined as (# of sample lattices with percolation) / (# of total samples)
     * @param prob
     * @param bw
     * @throws IOException
     */
    private static void test(float prob, BufferedWriter bw) throws IOException {

        Lattice lattice;
        List<Site> roots;

        int testNum = 0;
        int percolates = 0;

        while(testNum < 50) {

            lattice = new Lattice(prob);
            roots = new ArrayList<>();

            findRoots(lattice, roots);

            if(!roots.isEmpty()) for(Site root : roots) depthFirst(lattice, root);

            percolates += lattice.percolates() ? 1 : 0;
            testNum++;
        }

        bw.write("" + (percolates / 50.0) + "," + prob + "\n");

        System.out.println("RESULT: 50 lattices with " + percolates + " that percolate.");

    }

    /**
     * Non-recursive depth-first search using a {@link Stack}.
     * @param lattice
     * @param root
     */
    private static void depthFirst(Lattice lattice, Site root) {

        Stack<Site> stack = new Stack<>();
        Cluster cluster = new Cluster();

        stack.push(root);
        cluster.addSite(root);

        while(!stack.isEmpty()) {

            Site nextNode = stack.pop();

            for(Iterator<Site> iterator = nextNode.getNeighborsIterator();
                iterator.hasNext(); )
            {

                Site neighbor = iterator.next();

                if(Math.abs(neighbor.getX() - nextNode.getX()) > 1 ||
                        Math.abs(neighbor.getY() - nextNode.getY()) > 1) {
                    continue;
                }
                if(neighbor.isOccupied() && !neighbor.wasVisited()) {
                    neighbor.setVisited(true);
                    stack.push(neighbor);
                    cluster.addSite(neighbor);
                }
            }
        }

        lattice.addCluster(cluster);
        lattice.reset();
    }
}