package PR;

import org.cloudbus.cloudsim.Storage;
import java.util.*;

public class DataReplicationManager {
    private Map<String, List<Storage>> replicatedDataMap = new HashMap<>();
    private int replicationFactor;

    public DataReplicationManager(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public void replicateData(String dataName, List<Storage> storages) {
        if (!replicatedDataMap.containsKey(dataName)) {
            replicatedDataMap.put(dataName, new ArrayList<>());
        }
        for (int i = 0; i < replicationFactor && i < storages.size(); i++) {
            Storage storage = storages.get(i);
            replicatedDataMap.get(dataName).add(storage);
        }
    }

    public Storage getReplica(String dataName) {
        List<Storage> replicas = replicatedDataMap.get(dataName);
        if (replicas != null && !replicas.isEmpty()) {
            return replicas.get(new Random().nextInt(replicas.size()));
        }
        return null;
    }
}
