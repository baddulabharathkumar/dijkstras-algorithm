import java.io.*;
import java.util.*;

class Graph {
    private final Map<String, List<Edge>> adjList = new HashMap<>();

    public void addEdge(String source, String destination, int distance) {
        adjList.computeIfAbsent(source, _ -> new ArrayList<>()).add(new Edge(destination, distance));
        adjList.computeIfAbsent(destination, _ -> new ArrayList<>());
    }

    public Map<String, Integer> dijkstra(String start) {
        Map<String, Integer> distances = new HashMap<>();
        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        distances.put(start, 0);
        priorityQueue.add(new Edge(start, 0));

        while (!priorityQueue.isEmpty()) {
            Edge currentEdge = priorityQueue.poll();
            String currentAirport = currentEdge.destination;

            if (currentEdge.weight > distances.getOrDefault(currentAirport, Integer.MAX_VALUE)) {
                continue;
            }

            for (Edge neighbor : adjList.getOrDefault(currentAirport, new ArrayList<>())) {
                int newDist = distances.get(currentAirport) + neighbor.weight;
                if (newDist < distances.getOrDefault(neighbor.destination, Integer.MAX_VALUE)) {
                    distances.put(neighbor.destination, newDist);
                    priorityQueue.add(new Edge(neighbor.destination, newDist));
                }
            }
        }
        return distances;
    }
}

class Edge {
    String destination;
    int weight;

    Edge(String destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }
}

class DijkstraAlgorithm {
    public static void main(String[] args) {
        Graph graph = new Graph();
        String filePath = "src/routes.csv";
        Random random = new Random();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip the first line (header)
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 6) {
                    System.out.println("Skipping malformed row: " + line);
                    continue; // Skip rows that don't have enough columns
                }

                String source = parts[2].trim();        // Using 'source airport' column
                String destination = parts[4].trim();   // Using 'destination airport' column

                // Assign a random distance between 300 and 1000
                int distance = 300 + random.nextInt(701);

                graph.addEdge(source, destination, distance);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Run Dijkstra's algorithm from a specified start point
        String startAirport = "BBA";
        Map<String, Integer> distances = graph.dijkstra(startAirport);

        // Print the shortest distances from the start airport
        System.out.println("Shortest distances from " + startAirport + ":");
        for (Map.Entry<String, Integer> entry : distances.entrySet()) {
            System.out.println("To " + entry.getKey() + ": " + entry.getValue() + " km");
        }
    }
}