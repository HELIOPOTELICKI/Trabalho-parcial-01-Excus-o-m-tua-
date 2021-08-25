import java.util.Timer;

/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */
public class TimerScheduler {
    Timer timer;

    public TimerScheduler() {
        timer = new Timer();
        timer.schedule(new TimerCreateNewProcessTask(), 40000);
        timer.schedule(new TimerKillCoordinatorTask(), 60000);
    }
}
