import java.util.LinkedList;
import java.util.Queue;

/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */
public class Coordinator {
    private Process process;
    private Queue<Process> queue;

    public Coordinator(Process p) {
        queue = new LinkedList<Process>();
        setProcess(p);
    }

    public void requestConsume(Process process) {
        boolean queueClean = queueClean();
        addProcessInQueue(process);

        if (queueClean) {
            process.ConsumeResource();
        }
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public void notifyStopConsume() {
        queue.poll();

        if (!queueClean()) {
            Process process = queue.peek();
            process.ConsumeResource();
        }
    }

    public boolean isProcessInQueue(Process process) {
        return queue != null && queue.contains(process);
    }

    private boolean queueClean() {
        return queue == null || queue.isEmpty();
    }

    private void addProcessInQueue(Process process) {
        if (queue == null) {
            queue = new LinkedList<Process>();
        }
        queue.add(process);
        System.out.printf("Processo %s adicionado na fila: %s\n", process.getPid(), this.getQueue());
    }

    public String getQueue() {
        String queue = "< ";

        for (Process p : this.queue) {
            if (((LinkedList<Process>) this.queue).get(this.queue.size() - 1) == p) {
                queue += String.format("%s ", p.getPid());
            } else {
                queue += String.format("%s - ", p.getPid());
            }
        }
        queue += ">";

        return queue;
    }

}
