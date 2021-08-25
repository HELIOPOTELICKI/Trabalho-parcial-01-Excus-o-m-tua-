import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */
public class Cluster {
    private List<Process> processes;
    private Coordinator coordinator;
    private static Cluster cluster;

    private Cluster() {
        processes = new CopyOnWriteArrayList<>();
    }

    public static Cluster getInstance() {
        if (cluster == null) {
            cluster = new Cluster();
        }
        return cluster;
    }

    public void setCoordinator(Process coordinator) {
        this.coordinator = new Coordinator(coordinator);
    }

    public Coordinator getCoordinator() {
        return this.coordinator;
    }

    public Process createNewProcess() {
        Process process = new Process(generateValidId());
        System.out.printf("Novo processo criado: %s\n", process.getPid());
        processes.add(process);
        return process;
    }

    public void removeCoordinator() {
        System.out.printf("Coordenador %s morreu. F\n", coordinator.getProcess().getPid());
        processes.remove(getCoordinator().getProcess());
        this.coordinator = null;
        invokeElection();
    }

    private void invokeElection() {
        System.out.println("<<< Eleição >>>");
        Process randomProcess = processes.get(new Random().nextInt(processes.size()));
        setCoordinator(randomProcess.election());
    }

    public List<Process> getProcesses() {
        return processes;
    }

    protected int generateValidId() {
        Random random = new Random();
        int id = random.nextInt(100);

        for (Process p : processes) {
            if (p.getPid() == id) {
                continue;
            }
            break;
        }
        return id;
    }

}
