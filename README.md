# Treasure Hunt Simulator

An interactive Java Swing GUI application that simulates a competitive treasure hunt. The project combines classical graph theory and optimization algorithms to determine which pirate captain maximizes their profit while racing to different treasure locations.

##Features & Algorithms Used

*   **Interactive Graph UI:** Click to dynamically add map nodes and open dialogs to connect them with weighted edges (distances).
*   **Dijkstra’s Algorithm:** Calculates the absolute shortest path from the starting point (`East-Blue`) to the destination nodes.
*   **0/1 Knapsack Optimization (DP):** Used to maximize the treasure value hauled from the **Cave** and **Mountain** locations based on strict capacity limitations.
*   **Fractional Knapsack (Greedy Algorithm):** Used for the **Sea-Floor** location, simulating divisible loot.
*   **Economic Strategy Simulation:** Compares Luffy, Law, and Blackbeard's journeys, deducting food and fuel overhead costs from their total treasure hauled to declare a definitive profit winner.

## 🛠️ Built With

*   Java SE (AWT & Swing for the graphics engine)
*   Dynamic Dynamic Programming (DP) & Greedy data structures
