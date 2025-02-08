package PR;

import java.util.*;

public class StorageReplication {
    private int replicationFactor;
    private List<String> storageNodes;

    public StorageReplication(int replicationFactor) {
        this.replicationFactor = replicationFactor;
        this.storageNodes = Arrays.asList("StorageNode1", "StorageNode2", "StorageNode3");
    }

    public List<String> replicateData(String data) {
        List<String> selectedNodes = new ArrayList<>();
        for (int i = 0; i < replicationFactor && i < storageNodes.size(); i++) {
            selectedNodes.add(storageNodes.get(i));
        }
        return selectedNodes;
    }
}
