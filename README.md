# FleetFlow – Intelligent Logistics Management Platform

**FleetFlow** is an enterprise-grade logistics and supply-chain management platform implemented in **Java (Standard Edition)**. The system models, simulates, and optimizes real-world logistics workflows across delivery fleets, regional warehouses, cargo manifests, vehicle IoT telemetry, driver shifts, and multi-tier transport networks.

Every algorithm in FleetFlow is directly mapped to a concrete logistics business requirement, leveraging state-of-the-art Data Structures & Algorithms (DSA).

---

## Architecture & Professional Directory Structure

The project avoids arbitrary naming (such as `module1`, `m1`, etc.) and adheres strictly to industry domain-driven design and algorithm package categorization:

```
Fleet_flow/
├── dataset/                                   # Real-world Logistics CSV Data
│   ├── warehouses.csv                         # Regional hubs, capacities & coordinates
│   ├── vehicles.csv                           # Fleet trucks/vans, load ratings, status & fuel
│   ├── drivers.csv                            # CDL license classes, experience & shift status
│   ├── packages.csv                           # Parcels, tracking codes, destinations & priorities
│   ├── shipment_documents.csv                 # Bills of lading, customs notes & hazardous cargo forms
│   ├── telemetry_events.csv                   # Vehicle IoT speed, fuel, temperature & sensor logs
│   └── delivery_constraints.csv               # 3-SAT dispatch, safety & regulatory constraints
│
├── src/
│   └── com/fleetflow/
│       ├── model/                             # Domain Entities
│       │   ├── Warehouse.java                 # Warehouse representation
│       │   ├── Vehicle.java                   # Fleet vehicle properties
│       │   ├── Driver.java                    # Commercial driver records
│       │   ├── PackageItem.java               # Parcel payload specifications
│       │   ├── ShipmentDocument.java          # Shipping documents and manifests
│       │   ├── TelemetryEvent.java            # Vehicle streaming telemetry
│       │   └── DeliveryConstraint.java        # 3-SAT CNF logistics constraints
│       │
│       ├── data/                              # Data Ingestion & Persistence
│       │   ├── DataLoader.java                # RFC 4180-compliant CSV parser
│       │   └── FleetRepository.java           # In-memory entity repository & cache
│       │
│       ├── search/                            # String Algorithms & Pattern Detection
│       │   ├── KMPSearcher.java               # Knuth-Morris-Pratt O(N + M) search
│       │   ├── ZAlgorithm.java                # Z-function linear pattern & cycle detection
│       │   ├── RabinKarpSearcher.java         # Polynomial rolling hash multi-pattern search
│       │   └── AhoCorasickAutomaton.java      # Trie + Failure Links simultaneous alert monitor
│       │
│       ├── indexing/                          # Suffix Structures & Document Similarity
│       │   ├── SuffixArray.java               # Suffix Array construction & binary search
│       │   ├── SAIS.java                      # Induced Sorting linear O(N) Suffix Array
│       │   ├── KasaiLCP.java                  # Kasai O(N) Longest Common Prefix algorithm
│       │   └── SuffixTree.java                # Suffix Tree with Longest Common Substring (LCS)
│       │
│       ├── optimization/                      # Advanced Dynamic Programming
│       │   ├── DamerauLevenshtein.java        # Address correction with transpositions
│       │   ├── RouteBitmaskDP.java            # Exact TSP delivery route optimization O(2^N * N^2)
│       │   ├── MatrixChainMultiplier.java     # Operational pipeline staging optimization O(N^3)
│       │   └── OptimalBST.java                # Expected search depth optimization for tracking keys
│       │
│       ├── network/                           # Network Flow & Resource Assignment
│       │   ├── FlowEdge.java                  # Residual graph directed edge
│       │   ├── FlowNetwork.java               # Adjacency list residual capacity network
│       │   ├── FordFulkerson.java             # Augmenting paths via DFS
│       │   ├── EdmondsKarp.java               # Shortest augmenting paths via BFS O(V * E^2)
│       │   ├── DinicsFlow.java                # Level graph + blocking flows O(V^2 * E)
│       │   ├── BipartiteDriverMatcher.java    # Max bipartite matching for driver dispatch
│       │   └── LogisticsMinCut.java           # Max-Flow Min-Cut bottleneck corridor detection
│       │
│       ├── constraints/                       # NP-Completeness & Conflict Modeling
│       │   ├── DeliverySatSolver.java         # DPLL 3-SAT solver with unit propagation
│       │   ├── CliqueReducer.java             # 3-SAT -> CLIQUE polynomial reduction
│       │   ├── ConflictGraphModeler.java      # CLIQUE <-> INDEPENDENT SET <-> VERTEX COVER
│       │   └── VertexCoverApproximator.java   # 2-Approximation for critical conflict checkpoints
│       │
│       ├── analytics/                         # Randomized & Parallel Algorithms
│       │   ├── RandomizedQuickSort.java       # Dual/Random-pivot 3-way partition package ranking
│       │   ├── ReservoirStreamSampler.java    # Algorithm R uniform stream sampling O(k) space
│       │   ├── MillerRabinCryptoToken.java    # Probabilistic primality & tamper-proof seals
│       │   ├── BlellochParallelScan.java      # Work-efficient parallel prefix sum O(N) work, O(log N) span
│       │   ├── ParallelLogisticsReducer.java  # ForkJoin recursive tree logistics metrics aggregation
│       │   └── BrentTheoremAnalyzer.java      # Theoretical speedup, work-depth & efficiency modeling
│       │
│       └── FleetFlowApp.java                  # Main interactive menu-driven console platform
│
├── bin/                                       # Compiled bytecode (.class)
├── compile.bat                                # Windows build script
├── run.bat                                    # Windows launcher script
├── run.ps1                                    # PowerShell launcher script
└── README.md                                  # Complete technical documentation
```

---

## Detailed Algorithm Mapping & Logistics Fit

| Category | Algorithm | Concrete Logistics Application | Time Complexity | Space Complexity |
|---|---|---|---|---|
| **String Algorithms** | **KMP (Knuth-Morris-Pratt)** | Instant lookup of tracking IDs (`TRK-CHI-98214-X`) & critical keywords in manifests. | $O(N + M)$ | $O(M)$ |
| | **Z-Function** | Detects cyclical route loops, repeating checkpoint anomalies, and oscillating delivery hops. | $O(N)$ | $O(N)$ |
| | **Rabin-Karp** | Multi-pattern rolling hash search for package barcodes across dense warehouse manifests. | $O(N + M)$ avg | $O(1)$ |
| | **Aho-Corasick** | Simultaneous detection of multiple emergency alerts (`CUSTOMS_HOLD`, `TEMPERATURE_BREACH`, etc.) across IoT streams. | $O(N + \text{matches})$ | $O(K \times \Sigma)$ |
| **Suffix Structures** | **Suffix Array** | Lexicographical indexing of legal bills of lading and consignment documents for fast range search. | $O(N \log^2 N)$ | $O(N)$ |
| | **SA-IS** | Suffix Array Induced Sorting: Linear-time indexing of large-scale logistics document libraries. | $O(N)$ | $O(N)$ |
| | **Kasai's LCP** | Computes Longest Common Prefix array to discover duplicate addresses and boilerplate clauses. | $O(N)$ | $O(N)$ |
| | **Suffix Tree** | Substring queries, pattern frequency, and Longest Common Substring (LCS) across document versions. | $O(N)$ | $O(N)$ |
| **Dynamic Programming** | **Damerau-Levenshtein** | Auto-corrects typo-ridden shipping addresses and customer street names including transpositions. | $O(L_1 \times L_2)$ | $O(L_1 \times L_2)$ |
| | **Bitmask DP (TSP)** | Solves exact minimum-cost delivery tour for vans visiting local customer drop-off points. | $O(2^N \cdot N^2)$ | $O(2^N \cdot N)$ |
| | **Matrix-Chain Mult** | Optimizes execution order of multi-stage logistics matrix pipelines (geospatial, consolidation, customs). | $O(N^3)$ | $O(N^2)$ |
| | **Optimal BST** | Minimizes expected cache search depth for frequently tracked shipment codes by access probability. | $O(N^3)$ | $O(N^2)$ |
| **Network Flow** | **Ford-Fulkerson** | Determines maximum freight throughput from origin factories to destination distribution centers. | $O(E \cdot \text{maxFlow})$ | $O(V + E)$ |
| | **Edmonds-Karp** | Shortest augmenting path (BFS) maximum flow for capacity-constrained logistics networks. | $O(V \cdot E^2)$ | $O(V + E)$ |
| | **Dinic's Algorithm** | Layered graph + blocking flow max flow for large multi-depot national freight networks. | $O(V^2 \cdot E)$ | $O(V + E)$ |
| | **Bipartite Matching** | Matches certified available drivers to vehicles/routes based on CDL class and shift schedules. | $O(E \sqrt{V})$ | $O(V + E)$ |
| | **Logistics Min-Cut** | Isolates saturated freight bottlenecks and critical corridor cut-edges limiting national throughput. | $O(V + E)$ post-flow | $O(V)$ |
| **NP-Completeness** | **DPLL 3-SAT Solver** | Solves complex dispatch constraints (driver rest limits, hazardous material separation, lane windows). | $O(2^V)$ worst-case | $O(V + C)$ |
| | **3-SAT $\to$ CLIQUE** | Polynomial reduction transforming 3-CNF delivery constraints into an equivalent $k$-clique graph. | $O(M^2)$ reduction | $O(M^2)$ |
| | **CLIQUE $\to$ IS $\to$ VC** | Logistics conflict modeling: Max Independent Set = safe routes; Min Vertex Cover = conflict points. | $O(2^V)$ exact | $O(V^2)$ |
| | **Vertex Cover 2-Approx**| Polynomial maximal matching 2-approximation to place minimal inspection stations covering conflicts. | $O(V + E)$ | $O(V)$ |
| **Randomized & Parallel** | **Randomized QuickSort**| Expected $O(N \log N)$ priority and deadline sorting of packages, impervious to sorted inputs. | $O(N \log N)$ exp | $O(\log N)$ |
| | **Reservoir Sampling** | Algorithm R: Extracts an unbiased random sample of $k$ events from unbounded vehicle IoT telemetry streams. | $O(N)$ stream | $O(k)$ |
| | **Miller-Rabin Test** | Probabilistic primality test to generate 128-bit cryptographic tokens and digital consignment seals. | $O(k \log^3 N)$ | $O(1)$ |
| | **Blelloch Parallel Scan**| Work-efficient parallel prefix sum: calculates cumulative cargo weight and fuel burn across delivery legs. | $O(N)$ work, $O(\log N)$ span | $O(N)$ |
| | **Parallel Reducer** | Multi-threaded tree reduction via ForkJoinPool for real-time logistics analytics across millions of parcels. | $O(N)$ work, $O(\log N)$ span | $O(\log N)$ |
| | **Brent's Theorem** | Evaluates theoretical speedup $T_p \le (W - D)/p + D$ and core efficiency curves for batch processing. | $O(1)$ | $O(1)$ |

---

## CSV Dataset Specifications

All datasets are situated in `dataset/` and populated with realistic logistics data:

1. **`warehouses.csv`**: Contains major distribution centers (Chicago, Los Angeles, Dallas, Newark, Atlanta, Seattle, Denver, Detroit, Miami, Pittsburgh) with storage capacity in tons and GPS coordinates.
2. **`vehicles.csv`**: Contains fleet inventory (Freightliner Cascadia, Ford E-Transit, Volvo VNL, Kenworth, Peterbilt, Rivian EDV) with payload capacity in kilograms, assigned depot, status (`ACTIVE`, `MAINTENANCE`, `IDLE`), and fuel percentages.
3. **`drivers.csv`**: Commercial driver roster with license certification (`CDL-CLASS-A`, `CDL-CLASS-B`), years of experience, shift availability, and driver rating.
4. **`packages.csv`**: Customer orders with tracking numbers, origin/destination addresses, parcel weights, delivery priority (`URGENT`, `HIGH`, `STANDARD`, `LOW`), status, and monetary declared value.
5. **`shipment_documents.csv`**: Bills of lading, customs clearance manifests, hazardous goods declarations, and exception reports used for text indexing and similarity queries.
6. **`telemetry_events.csv`**: Live vehicle telemetry streams with GPS coordinates, engine temperature, speed readings, and operational status logs.
7. **`delivery_constraints.csv`**: Operational dispatch rules encoded into 3-SAT CNF format for driver rest scheduling, hazmat handling, and lane exclusivity.

---

## How to Compile & Run

### Prerequisites
- Java Development Kit (JDK 8 or higher; tested and compatible with JDK 26)

### Option 1: Using the provided Windows Batch scripts
```cmd
compile.bat
run.bat
```

### Option 2: Using PowerShell
```powershell
.\run.ps1
```

### Option 3: Direct CLI execution
```cmd
javac -d bin src\com\fleetflow\model\*.java src\com\fleetflow\data\*.java src\com\fleetflow\search\*.java src\com\fleetflow\indexing\*.java src\com\fleetflow\optimization\*.java src\com\fleetflow\network\*.java src\com\fleetflow\constraints\*.java src\com\fleetflow\analytics\*.java src\com\fleetflow\FleetFlowApp.java

java -cp bin com.fleetflow.FleetFlowApp
```

---

## Interactive Logistics Operations & Algorithm Selection

FleetFlow presents an action-driven menu organized around real logistics workflows:

```
=========================== FLEETFLOW LOGISTICS OPERATIONS ===========================
  [1] Search Shipment / Package / Cargo Manifest
  [2] Index Documents & Detect Duplicate Manifest Clauses
  [3] Verify & Auto-Correct Delivery Address
  [4] Optimize Delivery Route & Operational Staging Pipeline
  [5] Calculate Freight Flow & Dispatch Driver Shifts
  [6] Resolve Dispatch Conflicts & Safety Constraints (NP-Hard Modeling)
  [7] Stream Telemetry Analytics & High-Throughput Processing
  [8] Register New Shipment Package into Inventory
  [9] "Which Algorithm is Best?" – Empirical Benchmarks & Algorithmic Advisor
  [0] Exit FleetFlow
======================================================================================
```

### Action Workflow:
When a user selects an operation (e.g. `[1] Search Shipment / Package / Cargo Manifest` or `[5] Calculate Freight Flow`), the platform prompts:
1. **Choose Algorithm**: Select which algorithmic strategy to execute (e.g. KMP vs Rabin-Karp vs Z-Algorithm vs Aho-Corasick, or Ford-Fulkerson vs Edmonds-Karp vs Dinic).
2. **Execute & Analyze**: View live results, execution latency in milliseconds, and algorithmic complexity.
3. **"Which Algorithm is Best?" Option**: Benchmarks all competing algorithms under load and provides actionable recommendations explaining why one algorithm is best suited for that specific operational condition.
4. **Action [9] Comprehensive Comparative Advisor**: Cross-cuts all algorithm families to generate a complete optimization decision matrix.

