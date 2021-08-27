import java.util.LinkedList;
import java.util.Random;

/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */
public class Processo {

	private int pid;
	private boolean ehCoordenador = false;
	private Thread utilizaRecurso = new Thread();
	private Conexao conexao = new Conexao();

	private LinkedList<Processo> listaDeEspera;
	private boolean recursoEmUso;

	private static final int min = 5000;
	private static final int max = 15000;

	public Processo(int pid) {
		this.pid = pid;
		setCoordenador(false);
	}

	private void startAskForConsumeResource() {
		final Processo process = this;
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (Cluster.getInstance().getCoordinator() != null
							&& !Cluster.getInstance().getCoordinator().isProcessInQueue(process)) {
						System.out.println("Processo: " + process.getPid() + " / Solicita consumir recursos");
						Cluster.getInstance().getCoordinator().requestConsume(process);
					}

					ThreadUtil.delay(getRandomAskInterval());

				}
			}

		}).start();
	}

	public int getPid() {
		return pid;
	}

	public boolean isCoordenador() {
		return ehCoordenador;
	}

	public void setCoordenador(boolean ehCoordenador) {
		this.ehCoordenador = ehCoordenador;
		if (this.ehCoordenador) {
			listaDeEspera = new LinkedList<>();
			conexao.conectar(this);

			if (ControladorDeProcessos.isSendoConsumido())
				ControladorDeProcessos.getConsumidor().interronperAcessoRecurso();

			recursoEmUso = false;
		}
	}

	private void interronperAcessoRecurso() {
		if (utilizaRecurso.isAlive())
			utilizaRecurso.interrupt();
	}

	public boolean isRecursoEmUso() {
		return encontrarCoordenador().recursoEmUso;
	}

	public void setRecursoEmUso(boolean estaEmUso, Processo consumidor) {
		Processo coordenador = encontrarCoordenador();

		coordenador.recursoEmUso = estaEmUso;
		ControladorDeProcessos.setConsumidor(estaEmUso ? consumidor : null);
	}

	private LinkedList<Processo> getListaDeEspera() {
		return encontrarCoordenador().listaDeEspera;
	}

	public boolean isListaDeEsperaVazia() {
		return getListaDeEspera().isEmpty();
	}

	private void removerDaListaDeEspera(Processo processo) {
		if (getListaDeEspera().contains(processo))
			getListaDeEspera().remove(processo);
	}

	private Processo encontrarCoordenador() {
		Processo coordenador = ControladorDeProcessos.getCoordenador();

		if (coordenador == null) {
			Eleicao eleicao = new Eleicao();
			coordenador = eleicao.novaEleicao(this.getPid());
		}
		return coordenador;
	}

	public void acessarRecursoCompartilhado() {
		if (ControladorDeProcessos.isUsandoRecurso(this) || this.isCoordenador())
			return;

		String resultado = conexao.realizarRequisicao("Processo " + this + " solicita permição.\n");

		System.out.printf("Permição para %s: %s\n", this, resultado);

		if (resultado.equals(Conexao.PERMITIDO))
			utilizarRecurso(this);
		else if (resultado.equals(Conexao.NEGADO))
			adicionarNaListaDeEspera(this);
	}

	private void adicionarNaListaDeEspera(Processo processoEmEspera) {
		getListaDeEspera().add(processoEmEspera);

		System.out.printf("Processo %s, adicionado na lista de espera.\n", this);
		System.out.printf("Em espera: %s\n", getListaDeEspera());
	}

	private void utilizarRecurso(Processo processo) {
		Random random = new Random();
		int randomUsageTime = min + random.nextInt(max - min);

		utilizaRecurso = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.printf("Processo %s utilizando do recurso.\n", processo);
				setRecursoEmUso(true, processo);

				try {
					Thread.sleep(randomUsageTime);
				} catch (InterruptedException e) {
				}

				System.out.printf("Processo %s terminou de consumir o recurso.\n", processo);
				processo.liberarRecurso();
			}
		});
		utilizaRecurso.start();
	}

	private void liberarRecurso() {
		setRecursoEmUso(false, this);

		if (!isListaDeEsperaVazia()) {
			Processo processoEmEspera = getListaDeEspera().removeFirst();
			processoEmEspera.acessarRecursoCompartilhado();
			System.out.printf("Processo %s saiu da espera.\n", processoEmEspera);
			System.out.printf("Em espera: %s\n", getListaDeEspera());
		}
	}

	public void destruir() {
		if (isCoordenador()) {
			conexao.encerrarConexao();
		} else {
			removerDaListaDeEspera(this);
			if (ControladorDeProcessos.isUsandoRecurso(this)) {
				interronperAcessoRecurso();
				liberarRecurso();
			}
		}

		ControladorDeProcessos.removerProcesso(this);
	}

	@Override
	public boolean equals(Object objeto) {
		Processo processo = (Processo) objeto;
		if (processo == null)
			return false;

		return this.pid == processo.pid;
	}

	@Override
	public String toString() {
		return String.valueOf(this.getPid());
	}
}