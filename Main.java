import java.util.ArrayList;
import java.util.Random;

/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */

public class Main {

	private static final int ADD = 4000;
	private static final int PROCESS_INTV = 8000;
	private static final int MORRE = 60000;
	private static final int CONSUMO_MIN = 5000;
	private static final int CONSUMO_MAX = 10000;

	private static final Object lock = new Object();

	public static void main(String[] args) {
		criarProcessos(ControladorDeProcessos.getProcessosAtivos());
		inativarCoordenador(ControladorDeProcessos.getProcessosAtivos());
		inativarProcesso(ControladorDeProcessos.getProcessosAtivos());
		acessarRecurso(ControladorDeProcessos.getProcessosAtivos());
	}

	public static void criarProcessos(ArrayList<Processo> processosAtivos) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					synchronized (lock) {
						Processo processo = new Processo(gerarIdUnico(processosAtivos));

						if (processosAtivos.isEmpty())
							processo.setCoordenador(true);

						processosAtivos.add(processo);
					}

					esperar(ADD);

				}
			}
		}).start();
	}

	public static void inativarProcesso(ArrayList<Processo> processosAtivos) {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					esperar(PROCESS_INTV);

					synchronized (lock) {
						if (!processosAtivos.isEmpty()) {
							int indexProcessoAleatorio = new Random().nextInt(processosAtivos.size());
							Processo pRemover = processosAtivos.get(indexProcessoAleatorio);
							if (pRemover != null && !pRemover.isCoordenador())
								pRemover.destruir();
						}
					}
				}
			}
		}).start();
	}

	public static void inativarCoordenador(ArrayList<Processo> processosAtivos) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					esperar(MORRE);

					synchronized (lock) {
						Processo coordenador = null;
						for (Processo p : processosAtivos) {
							if (p.isCoordenador())
								coordenador = p;
						}
						if (coordenador != null) {
							coordenador.destruir();
							System.out.printf("Coordenador %s morreu. F\n", coordenador);
						}
					}
				}
			}
		}).start();
	}

	public static void acessarRecurso(ArrayList<Processo> processosAtivos) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Random random = new Random();
				int intervalo = 0;
				while (true) {
					intervalo = random.nextInt(CONSUMO_MAX - CONSUMO_MIN);
					esperar(CONSUMO_MIN + intervalo);

					synchronized (lock) {
						if (!processosAtivos.isEmpty()) {
							int indexProcessoAleatorio = new Random().nextInt(processosAtivos.size());

							Processo processoConsumidor = processosAtivos.get(indexProcessoAleatorio);
							processoConsumidor.acessarRecursoCompartilhado();
						}
					}
				}
			}
		}).start();
	}

	private static int gerarIdUnico(ArrayList<Processo> processosAtivos) {
		Random random = new Random();
		int idRandom = random.nextInt(1000);

		for (Processo p : processosAtivos) {
			if (p.getPid() == idRandom)
				return gerarIdUnico(processosAtivos);
		}

		return idRandom;
	}

	private static void esperar(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
