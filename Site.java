import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author David O'Neill
 * @since 2/3/2017
 * The Site object. Represents a node in the lattice.
 */
public class Site {

    /* x-coordinate of the node */
    private int posX;
    /* y-coordinate of the node */
    private int posY;

    /** The parent {@link Lattice} */
    private Lattice lattice;

    private boolean occupied;

    /* Have we been visited by the depth-first search? */
    private boolean visited;

    /* A list of the nearest neighbors */
    private List<Site> neighbors;

    public Site(Lattice lat, int x, int y) {
        this.lattice = lat;
        this.posX = x;
        this.posY = y;

        visited = false;

        if(Math.random() < lattice.getOccupationProb())
            occupied = true;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public int getX() {
        return posX;
    }
    public int getY() {
        return posY;
    }

    /* Particularly useful to have for testing */
    public Iterator<Site> getNeighborsIterator() {
        return neighbors.iterator();
    }

    /**
     *   Here we impose the infinite boundary conditions.
         S = site, N = neighbor, 0 = node
         Example:          Example:
         N 0 0 0 0 0 0     N S N 0 0 0 0
         S 0 0 0 0 0 N     0 0 0 0 0 0 0
         N 0 0 0 0 0 0     0 0 0 0 0 0 0
         0 0 0 0 0 0 0     0 N 0 0 0 0 0
     */
    public void initNeighbors() {
        neighbors = new ArrayList<>();
        neighbors.add(lattice.getSiteAt(posX == 0 ? Lattice.LATTICE_DIM - 1 : posX - 1, posY));
        neighbors.add(lattice.getSiteAt(posX == Lattice.LATTICE_DIM - 1 ? 0 : posX + 1, posY));
        neighbors.add(lattice.getSiteAt(posX, posY == 0 ? Lattice.LATTICE_DIM - 1 : posY - 1));
        neighbors.add(lattice.getSiteAt(posX, posY == Lattice.LATTICE_DIM - 1 ? 0 : posY + 1));
    }

    public boolean wasVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }


}
