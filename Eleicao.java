import java.util.LinkedList;
import java.util.Random;

/**
 * TRABALHO PRÁTICO 01 – ALGORITMOS DE EXCLUSÃO MÚTUA
 *
 * Hélio Potelicki, Luis Felipe Zaguini e Pedro Henrique Roweder
 */
public class Eleicao {

	public Processo novaEleicao(int idPIniciador) {
		LinkedList<Integer> idPConsultados = new LinkedList<>();

		for (Processo p : ControladorDeProcessos.getProcessosAtivos())
			consultarProcesso(p.getPid(), idPConsultados);

		Random rand = new Random();
		int idNovoCoordenador = idPConsultados.get(rand.nextInt(idPConsultados.size()));

		Processo coordenador = atualizarCoordenador(idNovoCoordenador);

		return coordenador;
	}

	private void consultarProcesso(int idProcesso, LinkedList<Integer> pConsultados) {
		pConsultados.add(idProcesso);
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
