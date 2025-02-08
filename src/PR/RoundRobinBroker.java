package PR;

import org.cloudbus.cloudsim.core.SimEvent;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import java.util.*;

public class RoundRobinBroker extends DatacenterBroker {
    private int vmIndex = 0;
    private List<ReplicatedDatacenter> replicatedDatacenters = new ArrayList<>();

    public RoundRobinBroker(String name) throws Exception {
        super(name);
    }

    public void addReplicatedDatacenter(ReplicatedDatacenter datacenter) {
        replicatedDatacenters.add(datacenter);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.CLOUDLET_SUBMIT:
                scheduleCloudlets();
                break;
            default:
                super.processEvent(ev);
                break;
        }
    }

    private void scheduleCloudlets() {
        if (getVmList() == null || getVmList().isEmpty()) {
            System.err.println("Error: No VMs available in the broker.");
            return;
        }

        for (Cloudlet cloudlet : getCloudletList()) {
            int vmId = getVmList().get(vmIndex).getId();
            vmIndex = (vmIndex + 1) % getVmList().size();
            cloudlet.setVmId(vmId);

            Integer datacenterId = getVmsToDatacentersMap().get(vmId);
            if (datacenterId == null) {
                System.err.println("Error: No Datacenter found for VM " + vmId);
                continue;
            }

            ReplicatedDatacenter selectedDatacenter = null;
            for (ReplicatedDatacenter datacenter : replicatedDatacenters) {
                if (datacenter.getId() == datacenterId) {
                    selectedDatacenter = datacenter;
                    break;
                }
            }

            if (selectedDatacenter != null) {
                Storage bestReplica = selectedDatacenter.getBestReplica("Dataset1");
                if (bestReplica != null) {
                    System.out.println(
                            "Cloudlet " + cloudlet.getCloudletId() + " will fetch data from " + bestReplica.getName());
                }
            }

            sendNow(datacenterId, CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
        }
    }

    @Override
    public void submitCloudletList(List<? extends Cloudlet> cloudletList) {
        super.submitCloudletList(cloudletList);
        schedule(getId(), 0.1, CloudSimTags.CLOUDLET_SUBMIT);
    }
}
