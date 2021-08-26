import java.util.List;
import java.util.Random;

/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */
public class Process {

    private static Integer MINIMUM_SECONDS_CONSUME = 5;
    private static Integer MAXIMUM_SECONDS_CONSUME = 15;
    private static Integer MINIMUM_SECONDS_INTERVAL = 10;
    private static Integer MAXIMUM_SECONDS_INTERVAL = 25;
    private Thread resources = new Thread();
    private int pid;

    public Process(int pid) {
        setPid(pid);
        startAskForConsumeResource();
    }

    private void startAskForConsumeResource() {
        int randomUsageTime = getRandomAskInterval();
        final Process process = this;

        resources = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (Cluster.getInstance().getCoordinator().getProcess().getPid() != process.getPid()) {
                            if (Cluster.getInstance().getCoordinator() != null
                                    && !Cluster.getInstance().getCoordinator().isProcessInQueue(process)) {
                                System.out.printf("Processo: %s solicita consumir recursos\n", process.getPid());
                                Cluster.getInstance().getCoordinator().requestConsume(process);
                            } else {
                                System.out.printf("Em espera: %s\n", Cluster.getInstance().getCoordinator().getQueue());
                            }
                        }
                    } catch (NullPointerException e) {
                    }
                    try {
                        Thread.sleep(randomUsageTime);
                    } catch (InterruptedException e) {

                    }
                }
            }
        });

        resources.start();

    }

    public Process election() {
        List<Process> processes = Cluster.getInstance().getProcesses();
        Process newCoordinator = this;

        for (Process p : processes) {
            if (p.getPid() > newCoordinator.getPid()) {
                newCoordinator = p;
            }

        }
        System.out.printf("Novo coordenador: %s\n", newCoordinator.getPid());
        return newCoordinator;
    }

    public void ConsumeResource() {
        final Process process = this;

        resources = new Thread(new Runnable() {
            @Override
            public void run() {
                int randomConsumeTime = getRandomConsumeTime();
                try {
                    Thread.sleep(randomConsumeTime);
                } catch (InterruptedException e) {
                }
                Cluster.getInstance().getCoordinator().notifyStopConsume();
                System.out.printf("Processo %s terminou de consumir o recurso. Consumiu por %ss\n", process.getPid(),
                        (randomConsumeTime / 1000));

            }
        });
        resources.start();
    }

    private int getRandomAskInterval() {
        return ((MINIMUM_SECONDS_INTERVAL + new Random().nextInt(MAXIMUM_SECONDS_INTERVAL - MINIMUM_SECONDS_INTERVAL))
                * 1000);
    }

    private int getRandomConsumeTime() {
        return ((MINIMUM_SECONDS_CONSUME + new Random().nextInt(MAXIMUM_SECONDS_CONSUME - MINIMUM_SECONDS_CONSUME))
                * 1000);
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPid() {
        return this.pid;
    }

}
