import java.util.LinkedList;
import java.util.Random;

/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */
public class Eleicao {

	public Processo novaEleicao(int idProcessoIniciador) {
		LinkedList<Integer> idProcessosConsultados = new LinkedList<>();

		for (Processo p : ControladorDeProcessos.getProcessosAtivos())
			consultarProcesso(p.getPid(), idProcessosConsultados);

		Random rand = new Random();
		int idNovoCoordenador = idProcessosConsultados.get(rand.nextInt(idProcessosConsultados.size()));

		Processo coordenador = atualizarCoordenador(idNovoCoordenador);

		return coordenador;
	}

	private void consultarProcesso(int idProcesso, LinkedList<Integer> processosConsultados) {
		processosConsultados.add(idProcesso);
	}

	private Processo atualizarCoordenador(int idNovoCoordenador) {
		Processo coordenador = null;
		for (Processo p : ControladorDeProcessos.getProcessosAtivos()) {
			if (p.getPid() == idNovoCoordenador) {
				p.setCoordenador(true);
				coordenador = p;
			} else {
				p.setCoordenador(false);
			}
		}

		return coordenador;
	}

}
