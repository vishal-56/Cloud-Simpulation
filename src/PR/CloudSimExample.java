package PR;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import java.util.*;
import java.io.FileWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;

public class CloudSimExample {
    @SuppressWarnings("unchecked")

    public static void main(String[] args) {
        try {
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            ReplicatedDatacenter datacenter0 = createDatacenter("Datacenter_0");

            RoundRobinBroker broker = new RoundRobinBroker("Broker");
            int brokerId = broker.getId();

            List<Vm> vmList = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                Vm vm = new Vm(i, brokerId, 1000, 1, 2048, 10000, 20000, "Xen", new CloudletSchedulerTimeShared());
                vmList.add(vm);
            }
            broker.submitVmList(vmList);

            List<Cloudlet> cloudletList = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                Cloudlet cloudlet = new Cloudlet(i, 40000, 1, 300, 300,
                        new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
                cloudlet.setUserId(brokerId);
                cloudletList.add(cloudlet);
            }
            broker.submitCloudletList(cloudletList);

            CloudSim.startSimulation();
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();

            JSONArray results = new JSONArray();

            for (Cloudlet cloudlet : newList) {
                System.out.println("Cloudlet " + cloudlet.getCloudletId() + " executed on VM " + cloudlet.getVmId());

                JSONObject cloudletObj = new JSONObject();
                cloudletObj.put("cloudletId", (Object) cloudlet.getCloudletId());
                cloudletObj.put("vmId", (Object) cloudlet.getVmId());
                cloudletObj.put("status", (Object) (cloudlet.getStatus() == Cloudlet.SUCCESS ? "Completed" : "Failed"));

                results.add(cloudletObj);
            }

            try (FileWriter file = new FileWriter("output.json")) {
                file.write(results.toJSONString());
                System.out.println("Results saved to output.json");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ReplicatedDatacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(4000)));

        int hostId = 0;
        hostList.add(new Host(hostId, new RamProvisionerSimple(4096),
                new BwProvisionerSimple(100000),
                1000000, peList, new VmSchedulerTimeShared(peList)));

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen", hostList, 10.0, 0.01, 0.02, 0.001, 0.0);

        ReplicatedDatacenter datacenter = null;
        try {
            datacenter = new ReplicatedDatacenter(name, characteristics,
                    new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }
}
