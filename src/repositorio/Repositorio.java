package repositorio;

/**********************************
 * IFPB - Curso Superior de Tec. em Sist. para Internet
 * Programa��o Orientada a Objetos
 * Prof. Fausto Maranh�o Ayres
 **********************************/
import java.util.ArrayList;
import java.util.TreeMap;

import modelo.Participante;
import modelo.Reuniao;


public class Repositorio {
	private TreeMap<String, Participante> participantes = new TreeMap<>();
	private ArrayList<Reuniao> reunioes = new ArrayList<>(); 

	
	public void adicionar(Participante p){ participantes.put(p.getNome(), p); }

	public void remover(Participante p){ participantes.remove(p.getNome()); }

	public Participante localizarParticipante(String nome){
		if (participantes.containsKey(nome))
			return participantes.get(nome);
		return null;
	}

	public void adicionar(Reuniao r){ reunioes.add(r); }

	public void remover(Reuniao r){ reunioes.remove(r); }

	public Reuniao localizarReuniao(int id){
		for (Reuniao r : reunioes)
			if (r.getId() == id)
				return r;
		return null;
	}

	public ArrayList<Participante> getParticipantes() { return new ArrayList<Participante>(participantes.values()); }

	public ArrayList<Reuniao> getReunioes() { return this.reunioes; }

	public int getTotalParticipante(){ return this.participantes.size(); }

	public int getTotalReunioes(){ return this.reunioes.size(); }

	//...demais metodos
}

