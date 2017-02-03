import java.util.ArrayList;
import java.util.List;

/**
 * @author David O'Neill
 * @since 2/3/2017
 * The Cluster object. Essentially a glorified {@link List}.
 */
public class Cluster {

    private List<Site> sites;

    public void addSite(Site site) {
        if(sites == null)  sites = new ArrayList<>();
        sites.add(site);
    }

    public ArrayList<Site> getSites() {
        return (ArrayList<Site>) sites;
    }


}
