package it.polito.tdp.gestionale.model;

import java.util.*;

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
	
	public List<Corso> findMinimalSet(){
		List<Corso> parziale=new ArrayList<Corso>();
		List<Corso> migliore=new ArrayList<Corso>();
		
		recursive(parziale,migliore);
		
		return migliore;
	}

	private void recursive(List<Corso> parziale, List<Corso> migliore) {
		
		//System.out.println(parziale);
		Set<Studente> setStudenti=new HashSet<Studente>(this.getTuttiStudenti());
		for(Corso corso: parziale){
			setStudenti.removeAll(corso.getStudenti());
		}
		
		if(setStudenti.isEmpty()){
			if(migliore.isEmpty()){
				migliore.addAll(parziale);
			}
			if(parziale.size()<migliore.size()){
				migliore.clear();
				migliore.addAll(parziale);
			}
		}
		
		for(Corso corso: this.getTuttiCorsi()){
			if(parziale.isEmpty() || corso.compareTo(parziale.get(parziale.size()-1))>0){
				parziale.add(corso);
				recursive(parziale,migliore);
				parziale.remove(corso);
			}
		}
		
	}
}
