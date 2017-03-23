import random


# Represents a node in the lattice
class Site(object):
    def __init__(self, position, lattice):
        self.position = position  # the site's lattice position
        self.lattice = lattice  # the parent lattice
        self.occupied = False  # is the site occupied?
        self.visited = False  # has the site been visited by the DFS?
        self.row_number = None  # row position in the 2D lattice representation
        self.cluster = None  # the cluster this site belongs to (if any)
        self.left = False  # is the site a left-boundary node?
        self.right = False  # is the site a right-boundary node?
        self.top = False  # is the site a top-boundary node?
        self.bottom = False  # is the site a bottom-boundary node?

        self.check_boundary()

    # establishes the boundary nature of the site
    def check_boundary(self):
        x = self.position
        l = self.lattice.length
        if ((x + 1) % l) == 0:
            self.right = True
        if (x + 1) > (l * (l-1)):
            self.bottom = True
        if x % l == 0:
            self.left = True
        if (x + 1) < l:
            self.top = True

    # gets the set of closest neighboring sites (neighbors at r = 1)
    def get_nearest_neighbor(self, direction):
        x = self.position
        l = self.lattice.length

        if direction == 1:
            if self.right:
                return self.lattice.sites[x]
            return self.lattice.sites[x + 1]
        elif direction == 2:
            if self.bottom:
                return self.lattice.sites[x]
            return self.lattice.sites[x + l]
        elif direction == 3:
            if self.left:
                return self.lattice.sites[x]
            return self.lattice.sites[x - 1]
        elif direction == 4:
            if self.top:
                return self.lattice.sites[x]
            return self.lattice.sites[x - l]

    # a general neighbor-finding function for a specified distance r
    def find_neighbors_at_radius(self, r):
        x = self.position
        neighbors = []
        for i in range(0, r):
            try:
                if (self.lattice.sites[x + i * self.lattice.length].row_number ==
                        self.lattice.sites[x + (r - i) + self.lattice.length * i].row_number):
                    neighbors.append(self.lattice.sites[x + (r - i) + self.lattice.length * i])

            except IndexError:
                pass
            try:
                if (self.lattice.sites[x + self.lattice.length * (r - i)].row_number ==
                        self.lattice.sites[x - i + self.lattice.length * (r - i)].row_number):
                    neighbors.append(self.lattice.sites[x - i + self.lattice.length * (r - i)])
            except IndexError:
                pass
            try:
                if (self.lattice.sites[x - self.lattice.length * i].row_number ==
                        self.lattice.sites[x - (r - i) - self.lattice.length * i].row_number):
                    if (x - (r - i) - self.lattice.length * i) >= 0:
                        neighbors.append(self.lattice.sites[x - (r - i) - self.lattice.length * i])
            except IndexError:
                pass
            try:
                if (self.lattice.sites[x - self.lattice.length * (r - i)].row_number ==
                        self.lattice.sites[x + i - self.lattice.length * (r - i)].row_number):
                    if (x + i - self.lattice.length * (r - i)) >= 0:
                        neighbors.append(self.lattice.sites[x + i - self.lattice.length * (r - i)])
            except IndexError:
                pass
        return neighbors


# The 2D square lattice
class Lattice(object):
    def __init__(self, length, occupation_probability):
        self.length = length  # lattice dimension is (length x length)
        self.occupation_probability = occupation_probability
        self.size = self.length ** 2  # total number of sites in the lattice
        self.clusters = []  # the collection of clusters present in the lattice
        self.sites = []  # the collection of sites in the lattice

        row_number = 0

        for i in range(0, self.size):
            site = Site(i, self)
            self.sites.append(site)
            if i % length == 0:
                row_number += 1
            self.sites[i].row_number = row_number
            if random.random() < self.occupation_probability:
                self.sites[i].occupied = True

    def add_cluster(self, cluster):
        self.clusters.append(cluster)


# Represents a cluster in the lattice. A cluster is a set of connected sites.
class Cluster(object):
    def __init__(self, lattice, root):
        self.lattice = lattice
        self.sites = []
        self.root = root
        self.root_pos = root.position

        self.sites.append(root)

    def get_size(self):
        return len(self.sites)

    def is_percolating(self):
        if self.get_size() < self.lattice.length:
            return False
        top = False
        bottom = False
        left = False
        right = False
        for i in range(0, self.get_size()):
            if self.sites[i].top:
                top = True
                if bottom:
                    return True
            elif self.sites[i].bottom:
                bottom = True
                if top:
                    return True
            elif self.sites[i].left:
                left = True
                if right:
                    return True
            elif self.sites[i].right:
                right = True
                if left:
                    return True
        return False

    def add_site(self, site):
        self.sites.append(site)


def dfs(lattice, root_pos):
    stack = []
    root = lattice.sites[root_pos]
    cluster = Cluster(lattice, root)
    stack.append(root)
    cluster.add_site(root)
    root.cluster = cluster
    while stack:
        site = stack.pop()
        for direction in range(1, 5):
            neighbor = site.get_nearest_neighbor(direction)
            if neighbor.occupied and not neighbor.visited:
                neighbor.visited = True
                stack.append(neighbor)
                cluster.add_site(neighbor)
                neighbor.cluster = cluster
    lattice.add_cluster(cluster)
