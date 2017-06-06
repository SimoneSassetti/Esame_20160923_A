package it.polito.tdp.gestionale.model;

import java.util.HashMap;
import java.util.*;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.gestionale.db.DidatticaDAO;

public class Model {

	private List<Corso> corsi;
	private List<Studente> studenti;
	private DidatticaDAO dao;
	private SimpleGraph<Nodo, DefaultEdge> grafo;
	private Map<Integer, Studente> mappaStudenti;
	
	public Model() {
		dao=new DidatticaDAO();
		mappaStudenti=new HashMap<Integer,Studente>();
	}
	
	public void creaGrafo(){
		grafo=new SimpleGraph<Nodo,DefaultEdge>(DefaultEdge.class);
		studenti=getTuttiStudenti();
		//System.out.println("\nNumero di studenti: "+studenti.size());
		corsi=getTuttiCorsi();
		//System.out.println("\nNumero di corsi: "+corsi.size());
		
		//AGGIUNGO I VERTICI
		Graphs.addAllVertices(grafo, studenti);
		Graphs.addAllVertices(grafo, corsi);
		//System.out.println("\nNumero di vertici: "+grafo.vertexSet().size());
		
		//AGGIUNGO GLI ARCHI
		for(Corso c: corsi){
			for(Studente s: c.getStudenti()){
				grafo.addEdge(c,s);
			}
		}
		//System.out.println("\nNumero di archi: "+grafo.edgeSet().size());
	}

	private List<Corso> getTuttiCorsi() {
		if(corsi==null){
			corsi=dao.getTuttiICorsi();
			getTuttiStudenti();//PER SICUREZZA LO RICHIAMO ANCHE QUI
			for(Corso c: corsi){
				dao.setStudentiIscrittiAlCorso(c, mappaStudenti);
			}
		}
		return corsi;
	}

	private List<Studente> getTuttiStudenti() {
		if(studenti==null){
			studenti=dao.getTuttiStudenti();
			for(Studente s: studenti){
				mappaStudenti.put(s.getMatricola(), s);
			}
		}
		return studenti;
	}
	
	public List<Integer> getStatCorsi(){
		List<Integer> statCorsi=new ArrayList<Integer>();
		
		//INIZIALIZZO LA STRUTTURA DATI X SALVARMI LE STATISCTICHE
		for(int i=0; i<corsi.size()+1;i++){
			statCorsi.add(0);
		}
		
		//AGGIORNO LE STAT
		for(Studente s: studenti){
			int ncorsi=Graphs.neighborListOf(grafo,s).size();
			int counter=statCorsi.get(ncorsi);
			counter++;
			statCorsi.set(ncorsi, counter);
		}
		return statCorsi;
	}
}
