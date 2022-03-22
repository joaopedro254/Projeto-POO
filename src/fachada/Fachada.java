package fachada;
/**********************************
 * IFPB - Curso Superior de Tec. em Sist. para Internet
 * Programaï¿½ï¿½o Orientada a Objetos
 * Prof. Fausto Maranhï¿½o Ayres
 **********************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import modelo.Participante;
import modelo.Reuniao;
import repositorio.Repositorio;


public class Fachada {
	private static Repositorio repositorio = new Repositorio();	//existe somente um repositorio

	public static ArrayList<Participante> listarParticipantes() { return repositorio.getParticipantes();}

	public static ArrayList<Reuniao> listarReunioes() {
		return repositorio.getReunioes();
	}

	public static Participante criarParticipante(String nome, String email) throws Exception {
		nome = nome.trim();
		email = email.trim();

		if (repositorio.localizarParticipante(nome) != null && repositorio.localizarParticipante(nome).getNome().equals(nome))
			throw new Exception("Participante já cadastrado"); // Regra 1 contemplada

		Participante p = new Participante(nome, email);
		repositorio.adicionar(p);
		return p;
	}

	public static Reuniao criarReuniao (String datahora, String assunto, ArrayList<String> nomes) throws Exception {
		if (nomes.size() < 2) // Regra 6 parcialmente contemplada
			throw new Exception("Não é possível criar reuniões com menos de dois participantes");

		LocalDateTime horaFormatada = LocalDateTime.parse(datahora, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

		if (horaFormatada.isBefore(LocalDateTime.now()))
			throw new Exception("Data/Horário inválido - Adicione uma reunião com um horário posterior ao atual");

		for (Reuniao r : repositorio.getReunioes()){
			for (Participante p : r.getParticipantes())
				if (nomes.contains(p.getNome())){
					LocalDateTime inicio = r.getDatahora();
					LocalDateTime fim = inicio.plusMinutes(120);
					if (horaFormatada.isEqual(inicio) || horaFormatada.isAfter(inicio) && horaFormatada.isBefore(fim))
						throw new Exception("Não é possível cadastrar um participante em duas reuniões que ocorrem ao mesmo tempo."); //Regra 2, Regra 3 e Regra 4 parcialmente contempladas
				}
		}

		assunto = assunto.trim();
		ArrayList<Participante> participantesParaReuniao = new ArrayList<>();
		for (String nome : nomes) {
			participantesParaReuniao.add(repositorio.localizarParticipante(nome));
		}
		Reuniao r = new Reuniao(horaFormatada, assunto, participantesParaReuniao);

		repositorio.adicionar(r);

		String mensagem = "Reunião marcada para às " + horaFormatada.toLocalTime() + " do dia " + horaFormatada.getDayOfMonth() + "/" + horaFormatada.getMonthValue() + "/" + horaFormatada.getYear();
		for (String nome : nomes) {
			enviarEmail(repositorio.localizarParticipante(nome).getEmail(), assunto, mensagem);
		}

		return r;
	}

	public static void 	adicionarParticipanteReuniao(String nome, int id) throws Exception {
		nome = nome.trim();

		Participante p = repositorio.localizarParticipante(nome);
		if (p == null)
			throw new Exception("Não foi possível adicionar - participante inexistente");

		Reuniao r = repositorio.localizarReuniao(id);
		if(r == null)
			throw new Exception("Não foi possível adicionar - reunião inexistente");

		if (r.getParticipantes().contains(p))
			throw new Exception("Participante já cadastrado na reunião");

		for (Reuniao rer : repositorio.getReunioes()){
			for (Participante par : rer.getParticipantes())
				if (nome.equals(par.getNome())){
					LocalDateTime inicio = rer.getDatahora();
					LocalDateTime fim = inicio.plusMinutes(120);
					if (r.getDatahora().isEqual(inicio) || r.getDatahora().isAfter(inicio) && r.getDatahora().isBefore(fim))
						throw new Exception("Não é possível cadastrar um participante em duas reuniões que ocorrem ao mesmo tempo."); //Regra 2, Regra 3 e Regra 4 integralmente contempladas
				}
		}

		p.adicionar(r);
		r.adicionar(p);
		
		String mensagem = "Você foi adicionado a reunião que ocorrerá às " + r.getDatahora().toLocalTime() + " do dia " + r.getDatahora().getDayOfMonth() + "/" + r.getDatahora().getMonthValue() + "/" + r.getDatahora().getYear();
		enviarEmail(p.getEmail(), r.getAssunto(), mensagem);
	}

	public static void 	removerParticipanteReuniao(String nome, int id) throws Exception {
		nome = nome.trim();

		Participante p = repositorio.localizarParticipante(nome);
		if (p == null)
			throw new Exception("Não foi possível remover - participante inexistente");

		Reuniao r = repositorio.localizarReuniao(id);
		if (r == null)
			throw new Exception("Não foi possível remover - reunião inexistente");

		p.remover(r);
		r.remover(p);

		if(r.getParticipantes().size() < 2) { //Regra 6 integralmente contemplada
			cancelarReuniao(r.getId());
			throw new Exception("Reunião cancelada pois possui menos de dois integrantes");
		}
		
		String mensagem = "Você foi removido da reunião que ocorrerá às " + r.getDatahora().toLocalTime() + " do dia " + r.getDatahora().getDayOfMonth() + "/" + r.getDatahora().getMonthValue() + "/" + r.getDatahora().getYear();
		enviarEmail(p.getEmail(), r.getAssunto(), mensagem);
	}

	public static void	cancelarReuniao(int id) throws Exception {
		Reuniao r = repositorio.localizarReuniao(id);
		if(r == null)
			throw new Exception("Não foi possível cancelar - reunião inexistente");

		for (Participante p : r.getParticipantes())  // Regra 5 contemplada
			p.remover(r);

		repositorio.remover(r);
		
		for (Participante p : r.getParticipantes()) { 
			String mensagem = "Reunião às " + r.getDatahora().toLocalTime() + " do dia " + r.getDatahora().getDayOfMonth() + "/" + r.getDatahora().getMonthValue() + "/" + r.getDatahora().getYear() + " foi cancelada.";
			enviarEmail(p.getEmail(), r.getAssunto(), mensagem);
		}

	}

	public static void	inicializar() throws Exception {
		Scanner participantesLeitura = null;
		Scanner reunioesLeitura = null;

		try{
			participantesLeitura = new Scanner(new File("src/arquivos/participantes.txt"));
		}
		catch (Exception ex){
			throw new Exception("Arquivo de participantes inexistente");
		}

		try{
			reunioesLeitura = new Scanner(new File("src/arquivos/reunioes.txt"));
		}
		catch (Exception ex){
			throw new Exception("Arquivo de reuniões inexistente");
		}

		String[] participante;
		Participante p;
		while (participantesLeitura.hasNextLine()){
			participante = participantesLeitura.nextLine().trim().split(";");
			p = new Participante(participante[0], participante[1]);
			repositorio.adicionar(p);
		}
		participantesLeitura.close();

		String[] reuniao;
		Reuniao r;
		while (reunioesLeitura.hasNextLine()){
			reuniao = reunioesLeitura.nextLine().trim().split(";");
			r = new Reuniao(LocalDateTime.parse(reuniao[1], DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), reuniao[2]);
			repositorio.adicionar(r);
			for (String nome : reuniao[3].split(",")){
				Participante part = repositorio.localizarParticipante(nome);
				r.adicionar(part);
				part.adicionar(r);
			}
		}
		reunioesLeitura.close();
	}

	public static void	finalizar() throws Exception {
		FileWriter participantesEscrita, reunioesEscrita;

		try{
			participantesEscrita = new FileWriter( new File("src/arquivos/participantes.txt") );
		}catch(IOException e){
			throw new Exception("problema na criação do arquivo de participantes");
		}
		try{
			reunioesEscrita = new FileWriter( new File("src/arquivos/reunioes.txt") );
		}catch(IOException e){
			throw new Exception("problema na criação do arquivo de reuniões");
		}

		for(Participante p : repositorio.getParticipantes()) {
			participantesEscrita.write(p.getNome() +";" + p.getEmail() +"\n");
		}
		participantesEscrita.close();

		ArrayList<String> lista;
		String nomes;
		for(Reuniao r : repositorio.getReunioes()) {
			lista = new ArrayList<>();
			for(Participante p : r.getParticipantes()) {
				lista.add(p.getNome());
			}
			nomes = String.join(",", lista);
			reunioesEscrita.write(r.getId()+";"+r.getDatahora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))+";"+r.getAssunto()+";"+nomes+"\n");
		}
		reunioesEscrita.close();
	}

	/**************************************************************
	 * 
	 * Mï¿½TODO PARA ENVIAR EMAIL, USANDO UMA CONTA (SMTP) DO GMAIL
	 * ELE ABRE UMA JANELA PARA PEDIR A SENHA DO EMAIL DO EMITENTE
	 * ELE USA A BIBLIOTECA JAVAMAIL 1.6.2
	 * Lembrar de: 
	 * 1. desligar antivirus e de 
	 * 2. ativar opcao "Acesso a App menos seguro" na conta do gmail
	 * 
	 **************************************************************/
	public static void enviarEmail(String emaildestino, String assunto, String mensagem){
		try {
			//configurar emails
			String emailorigem = "joaojorgetestepoo@gmail.com";
			//String senhaorigem = pegarSenha();
			String senhaorigem = "testepoo12";

			//Gmail
			Properties props = new Properties();
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");

			Session session;
			session = Session.getInstance(props,
					new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(emailorigem, senhaorigem);
				}
			});

			MimeMessage message = new MimeMessage(session);
			message.setSubject(assunto);		
			message.setFrom(new InternetAddress(emailorigem));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emaildestino));
			message.setText(mensagem);   // usar "\n" para quebrar linhas
			Transport.send(message);

			//System.out.println("enviado com sucesso");

		} catch (MessagingException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * JANELA PARA DIGITAR A SENHA DO EMAIL
	 */
	public static String pegarSenha(){
		JPasswordField field = new JPasswordField(10);
		field.setEchoChar('*'); 
		JPanel painel = new JPanel();
		painel.add(new JLabel("Entre com a senha do seu email:"));
		painel.add(field);
		JOptionPane.showMessageDialog(null, painel, "Senha", JOptionPane.PLAIN_MESSAGE);
		String texto = new String(field.getPassword());
		return texto.trim();
	}
	
}
