package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

/**********************************
 * IFPB - Curso Superior de Tec. em Sist. para Internet
 * Programa��o Orientada a Objetos
 * Prof. Fausto Maranh�o Ayres
 **********************************/

public class Reuniao {
    private int id;
    private static int contador = 0;
    private LocalDateTime datahora;
    private String assunto;
    private ArrayList<Participante> participantes = new ArrayList();

    public Reuniao(LocalDateTime datahora, String assunto) {
        contador++;
        this.id = contador;
        this.datahora = datahora;
        this.assunto = assunto;
    }

    public Reuniao(LocalDateTime datahora, String assunto, ArrayList<Participante> participantes) {
        contador++;
        this.id = contador;
        this.datahora = datahora;
        this.assunto = assunto;
        this.participantes = participantes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDatahora() {
        return datahora;
    }

    public void setDatahora(LocalDateTime datahora) {
        this.datahora = datahora;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public ArrayList<Participante> getParticipantes() { return participantes; }

    public void setParticipantes(ArrayList<Participante> participantes) {
        this.participantes = participantes;
    }

    public void adicionar(Participante p) {
        this.participantes.add(p);
    }

    public void remover(Participante p) {
        this.participantes.remove(p);
    }
}
