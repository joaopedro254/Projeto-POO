package aplicacaoConsole;
import java.util.ArrayList;

import fachada.Fachada;
import modelo.Participante;
import modelo.Reuniao;

public class AplicacaoConsole {

	public AplicacaoConsole() {
		try {
			Participante participante;				
			Reuniao reuniao;		
			Fachada.inicializar();

			participante = Fachada.criarParticipante("carlos", "carlos@gmail.com");
			System.out.println("participante criado:"+participante);

			ArrayList<String> nomes = new ArrayList<>();
			nomes.add("joao");
			nomes.add("carlos");
			reuniao = Fachada.criarReuniao("20/10/2021 08:00", "teste", nomes);
			System.out.println("reuniao criada:"+reuniao);


			System.out.println("\n---------listagem de participantes-----");
			for(Participante p : Fachada.listarParticipantes()) 
				System.out.println(p);
			System.out.println("\n---------listagem de reunioes");
			for(Reuniao r : Fachada.listarReunioes()) 
				System.out.println(r);

			Fachada.adicionarParticipanteReuniao("jose", 2);
			Fachada.removerParticipanteReuniao("paulo", 3);
			Fachada.removerParticipanteReuniao("maria", 3);		//reuniao 3 sera cancelada
			Fachada.cancelarReuniao(6);
			
			System.out.println("\n---------listagem de participantes-----");
			for(Participante p : Fachada.listarParticipantes()) 
				System.out.println(p);
			System.out.println("\n---------listagem de reunioes");
			for(Reuniao r : Fachada.listarReunioes()) 
				System.out.println(r);

			Fachada.finalizar();
			
		} catch (Exception e) {
			System.out.println("--->"+e.getMessage());
		}		

		//****************
		testarExcecoes();
		//****************

	}


	public static void testarExcecoes() {
		System.out.println("\n-------TESTE EXCE��ES LAN�ADAS--------");
		try {
			Participante p = Fachada.criarParticipante("carlos", "carlos@gmail.com");
			System.out.println("*************1Nao lan�ou exce��o para: criar participante "); 
		}catch (Exception e) {System.out.println("1ok--->"+e.getMessage());}

		try {
			ArrayList<String> nomes = new ArrayList<>();
			nomes.add("joao");
			nomes.add("carlos");
			Reuniao r = Fachada.criarReuniao("20/10/2021 08:00", "teste", nomes);
			System.out.println("*************2Nao lan�ou exce��o para: criar reuniao"); 
		}catch (Exception e) {System.out.println("2ok--->"+e.getMessage());}

		try {
			ArrayList<String> nomes = new ArrayList<>();
			nomes.add("carlos");
			Reuniao r = Fachada.criarReuniao("20/10/2021 08:00", "teste", nomes);
			System.out.println("*************3Nao lan�ou exce��o: criar reuniao 1 participante"); 
		}catch (Exception e) {System.out.println("3ok--->"+e.getMessage());}

		try {
			Fachada.removerParticipanteReuniao("ze", 1);	
			System.out.println("*************4Nao lan�ou exce��o: remover participante inexistente"); 
		}catch (Exception e) {System.out.println("4ok--->"+e.getMessage());}

		try {
			Fachada.cancelarReuniao(999);	
			System.out.println("*************5Nao lan�ou exce��o: cancelar reuniao inexistente"); 
		}catch (Exception e) {System.out.println("5ok--->"+e.getMessage());}

	}


	public static void main (String[] args) {
		AplicacaoConsole aplicacaoConsole = new AplicacaoConsole();
	}
}
