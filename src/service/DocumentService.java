package service;

import java.util.ArrayList;
import java.util.List;

import algorithms.suffix.KasaiLCP;
import algorithms.suffix.SuffixArray;
import algorithms.suffix.SuffixTree;
import model.ShipmentDocument;
import util.SampleData;

/**
 * DocumentService binds Module 2 Suffix Structures & Document Similarity algorithms
 * to FleetFlow document indexing and logistics contract analysis.
 */
public class DocumentService {

    private final List<ShipmentDocument> documents;

    public DocumentService() {
        this.documents = SampleData.getSampleDocuments();
    }

    public DocumentService(List<ShipmentDocument> documents) {
        this.documents = documents != null ? documents : new ArrayList<>();
    }

    public List<ShipmentDocument> getDocuments() {
        return documents;
    }

    public void addDocument(ShipmentDocument doc) {
        if (doc != null) {
            documents.add(doc);
        }
    }

    /**
     * Requirement 1: Suffix Array indexing of shipment descriptions & delivery documents.
     */
    public SuffixArray createSuffixArrayForDocument(ShipmentDocument doc) {
        if (doc == null || doc.getContent() == null) {
            return new SuffixArray("");
        }
        System.out.println("\n[Suffix Array] Indexing document: '" + doc.getTitle() + "' (" + doc.getContent().length() + " chars)");
        return new SuffixArray(doc.getContent());
    }

    /**
     * Search for pattern occurrences in a document using Suffix Array binary search.
     */
    public List<Integer> searchDocumentWithSuffixArray(ShipmentDocument doc, String pattern) {
        SuffixArray sa = new SuffixArray(doc.getContent());
        int[] indices = sa.searchAllOccurrences(pattern);
        List<Integer> result = new ArrayList<>();
        for (int idx : indices) {
            result.add(idx);
        }
        return result;
    }

    /**
     * Requirement 2: SA-IS (Suffix Array Induced Sorting) for large-scale document indexing.
     */
    public int[] performSAISIndexing(String documentContent) {
        System.out.println("\n[SA-IS Linear Construction] Building SA-IS suffix array for large document (" + documentContent.length() + " chars)...");
        return SuffixArray.buildSAIS(documentContent);
    }

    /**
     * Requirement 3: Kasai's LCP Algorithm to detect repeated addresses & document boilerplate.
     */
    public KasaiLCP.LCPResult detectRepeatedContentWithKasai(String text) {
        System.out.println("\n[Kasai LCP] Computing LCP array to detect repeated addresses/descriptions...");
        SuffixArray sa = new SuffixArray(text);
        return KasaiLCP.computeLCP(text, sa.getSuffixArray());
    }

    /**
     * Requirement 4: Suffix Tree for fast substring queries.
     */
    public List<Integer> querySubstringWithSuffixTree(ShipmentDocument doc, String pattern) {
        System.out.println("\n[Suffix Tree Query] Searching pattern '" + pattern + "' in document '" + doc.getTitle() + "'");
        SuffixTree st = new SuffixTree(doc.getContent());
        return st.search(pattern);
    }

    /**
     * Demonstrates all Module 2 Suffix Structure algorithms in a structured suite.
     */
    public void runAllDocumentDemos() {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println("   FLEETFLOW SUFFIX STRUCTURES & DOCUMENT SIMILARITY DEMO");
        System.out.println("                     (Module 2 - CO2)");
        System.out.println("==================================================================");

        if (documents.isEmpty()) {
            System.out.println("No documents available for demonstration.");
            return;
        }

        ShipmentDocument doc1 = documents.get(0);
        ShipmentDocument doc2 = documents.get(1);

        // 1. Suffix Array Demo
        System.out.println("\n--- 1. Suffix Array Document Indexing & Binary Search ---");
        SuffixArray sa = createSuffixArrayForDocument(doc1);
        System.out.println("Indexed Document ID: " + doc1.getDocumentId());
        System.out.println("Suffix Array length: " + sa.length());
        String query = "Boston";
        int[] occurrences = sa.searchAllOccurrences(query);
        System.out.println("Found " + occurrences.length + " occurrence(s) of '" + query + "' at character positions: " + java.util.Arrays.toString(occurrences));

        // 2. SA-IS Demo
        System.out.println("\n--- 2. SA-IS (Suffix Array Induced Sorting) Large-Scale Indexing ---");
        String largeDocContent = doc1.getContent() + " " + doc2.getContent();
        int[] saisArray = performSAISIndexing(largeDocContent);
        System.out.println("SA-IS Linear Time Suffix Array constructed successfully.");
        System.out.println("First 10 SA-IS suffix starting positions: ");
        for (int i = 0; i < Math.min(10, saisArray.length); i++) {
            System.out.print(saisArray[i] + " ");
        }
        System.out.println();

        // 3. Kasai LCP Demo
        System.out.println("\n--- 3. LCP / Kasai's Algorithm (Repeated Address & Description Detection) ---");
        KasaiLCP.LCPResult lcpResult = detectRepeatedContentWithKasai(largeDocContent);
        System.out.println("Kasai LCP Analysis Result:");
        System.out.println("  " + lcpResult);

        // 4. Suffix Tree Demo
        System.out.println("\n--- 4. Suffix Tree Fast Substring Queries ---");
        SuffixTree st = new SuffixTree(doc1.getContent());
        String treeQuery = "Handle with care";
        List<Integer> treeMatches = st.search(treeQuery);
        System.out.println("Searching Suffix Tree for: '" + treeQuery + "'");
        System.out.println("Matches found at indices: " + treeMatches);
        System.out.println("==================================================================\n");
    }
}
