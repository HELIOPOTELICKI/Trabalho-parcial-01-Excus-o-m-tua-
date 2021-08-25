import java.util.TimerTask;

public class TimerKillCoordinatorTask extends TimerTask {
    public void run() {
        Cluster.getInstance().removeCoordinator();
    }
}
