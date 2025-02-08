package PR;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.File;
import java.util.*;

public class ReplicatedDatacenter extends Datacenter {
    private List<Storage> replicas = new ArrayList<>();

    public ReplicatedDatacenter(String name, DatacenterCharacteristics characteristics,
            VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval)
            throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
        this.replicas.addAll(storageList);
    }

    public void addReplica(Storage storage) {
        replicas.add(storage);
    }

    public Storage getBestReplica(String fileName) {
        for (Storage storage : getStorageList()) {
            if (storage.getFile(fileName) != null) {
                return storage;
            }
        }
        return null;
    }
}
