import numpy as np
import time
import matplotlib.pyplot as plt
from percolation_utils import *

length = 50
steps = 100
step = 1 / steps
samples = 100
probabilities = np.zeros(steps + 1)


# Runs a correlation-length analysis
def run_correlation():
    print("Beginning run...")
    start_time = time.time()
    xi_plot = correlation_length()
    print("Finished. Elapsed time: ", time.time() - start_time)
    plot(probabilities, xi_plot)


# Plots results
def plot(p, xi_array):
    plt.figure(0)
    plt.title("Correlation Length vs Occupation Probability")
    plt.xlabel("p")
    plt.ylabel(r'$\xi$(p)')
    plt.plot(p, xi_array)
    plt.grid()
    plt.show()


# Finds the correlation function for the center site in the lattice
def correlation_function(lattice):
    cf = np.zeros(2 * lattice.length)
    center = (lattice.length * (lattice.length - 1) + (lattice.length - 1)) // 2
    center_site = lattice.sites[center]
    if center_site.occupied and not center_site.cluster.is_percolating():
        distance = 1
        neighbors = center_site.find_neighbors_at_radius(distance)
        while distance < (2 * lattice.length) and neighbors:
            if distance == 1:
                cf[distance - 1] = 1
                distance += 1
                neighbors = center_site.find_neighbors_at_radius(distance)
                continue
            numerator = 0
            for i in range(0, len(neighbors)):
                neighbor = neighbors[i]
                if neighbor.occupied and id(neighbor.cluster) == id(center_site.cluster):
                    numerator += 1
            if numerator == 0:
                break
            cf[distance - 1] = numerator / len(neighbors)
            distance += 1
            neighbors = center_site.find_neighbors_at_radius(distance)
    return cf


# Finds the correlation length for a set of sample lattices through a probability range
def correlation_length():
    xi_squared = []
    for i in range(0, steps + 1):
        probabilities[i] = step * i
        if i != 0:
            print("Preparing new sample set...")
        print("Probability: ", probabilities[i])
        correlation_matrix = []
        for j in range(0, samples):
            lattice = Lattice(length, probabilities[i])
            for k in range(0, lattice.size):
                if lattice.sites[k].occupied and not lattice.sites[k].visited:
                    dfs(lattice, k)
            cf = correlation_function(lattice)
            correlation_matrix.append(cf)
        avg = np.mean(correlation_matrix, axis=0)
        xi_val = 0
        sum_cf = sum(avg)
        if sum_cf != 0:
            for j in range(0, len(avg)):
                xi_val += (j + 1) * (j + 1) * avg[j]
            xi_val /= sum_cf
            xi_squared.append(xi_val)
        else:
            xi_squared.append(0)
    xi = np.sqrt(xi_squared)
    return xi

run_correlation()
