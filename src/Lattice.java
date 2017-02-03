import java.util.ArrayList;
import java.util.List;

/**
 * @author David O'Neill
 * @since 2/3/2017
 * The Lattice object. Represents a square lattice with "infinite" boundary conditions.
 */
public class Lattice {

    /* The side-length of the lattice. (L) */
    protected static final int LATTICE_DIM = 50;

    /* The sites in the lattice, represented as a 2D array */
    private Site[][] nodes;
    /* Lattice site occupation probability */
    private float p;
    /* Total sites in the lattice */
    private int N;
    /* Occupied sites in the lattice */
    private int n;

    /* A list of clusters present in the lattice */
    private List<Cluster> clusters;

    public Lattice(float p) {
        this.p = p;
        init();
    }

    private void init() {
        nodes = new Site[LATTICE_DIM][LATTICE_DIM];
        for(int i = 0; i < LATTICE_DIM; i++) {
            for(int j = 0; j < LATTICE_DIM; j++) {
                nodes[i][j] = new Site(this, i, j);
                if(nodes[i][j].isOccupied()) n++;
                N++;
            }
        }
        for(int i = 0; i < LATTICE_DIM; i++) {
            for(int j = 0; j < LATTICE_DIM; j++) {
                nodes[i][j].initNeighbors();
            }
        }
    }

    public float getOccupationProb() {
        return p;
    }

    public void addCluster(Cluster cluster) {
        if(clusters == null) clusters = new ArrayList<>();
        clusters.add(cluster);
    }

    public ArrayList<Cluster> getClusters() {
        return (ArrayList<Cluster>) clusters;
    }

    public int getAmountOccupied() {
        return n;
    }
    public int getAmountSites() {
        return N;
    }

    public Site getSiteAt(int x, int y) {
        return nodes[x][y];
    }

    /**
     *
     * @return  Is the root site at the top row or at the left rank?
     *          Top: Does the cluster contain an occupied node on the bottom row?
                Left: Does the cluster contain an occupied node on the right rank?
     */
    public boolean percolates() {
        if(clusters != null) {
            for(Cluster cluster : clusters) {
                Site root = cluster.getSites().get(0);
                for(Site node : cluster.getSites()) {
                    if(node == root) continue;

                    if(Math.abs(node.getY() - root.getY()) == LATTICE_DIM - 1
                            || Math.abs(node.getX() - root.getX()) == LATTICE_DIM - 1) {
                        if(root.getX() == 0 || root.getY() == 0)
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
