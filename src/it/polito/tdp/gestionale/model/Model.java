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
	
	//Lista finale ricorsione
	private List<Corso> finale;
	private boolean primaVolta=true;
	
	public Model() {
		dao=new DidatticaDAO();
		mappaStudenti=new HashMap<Integer,Studente>();
		finale=new ArrayList<Corso>();
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
		Collections.sort(corsi);
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
	
	public List<Corso> trovaSequenza(){
		
		this.creaGrafo();
		int numeroStudenti=0;
		List<Integer> lista=this.getStatCorsi();
		for(int i=1;i<lista.size();i++){
			numeroStudenti+=lista.get(i);
		}
		
		List<Corso> parziale=new ArrayList<Corso>();
		Set<Studente> studentiParziale=new HashSet<>();
		int step=0;
		
		recursive(parziale,studentiParziale,numeroStudenti,step);
		
		return finale;
	}

	private void recursive(List<Corso> parziale,Set<Studente> studenti, int numeroStudenti, int step) {
		
		if(studenti.size()==numeroStudenti){
			if(finale.size()>parziale.size() || primaVolta){
				finale.clear();
				finale.addAll(parziale);
				primaVolta=false;
				return;
			}
		}
		
		for(Corso c: corsi){
			if(!parziale.contains(c)){
				List<Studente> s=c.getStudenti();
				studenti.addAll(s);
				parziale.add(c);
				System.out.println(parziale);
				System.out.println(studenti.size());
				recursive(parziale,studenti,numeroStudenti,step+1);
				studenti.removeAll(s);
				parziale.remove(c);
			}
		}	
	}
	
}
