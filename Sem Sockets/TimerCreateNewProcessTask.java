import java.util.TimerTask;

/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */
public class TimerCreateNewProcessTask extends TimerTask {
    public void run() {
        Cluster.getInstance().createNewProcess();
    }
}
