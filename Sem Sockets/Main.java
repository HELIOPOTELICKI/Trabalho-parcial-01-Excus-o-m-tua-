/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("\nInicializando...\n");

        for (int i = 0; i < 5; i++) {
            Cluster.getInstance().createNewProcess();
        }

        Cluster.getInstance().invokeElection();

        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
            }
            new TimerScheduler();
        }

    }
}