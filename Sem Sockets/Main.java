/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("Inicializando...");

        Process a = Cluster.getInstance().createNewProcess();
        Process b = Cluster.getInstance().createNewProcess();
        Process c = Cluster.getInstance().createNewProcess();
        Process p = Cluster.getInstance().createNewProcess();
        Cluster.getInstance().setCoordinator(p);
        System.out.printf("Coordenador eleito: %s\n", p.getPid());

        new TimerScheduler();
    }
}