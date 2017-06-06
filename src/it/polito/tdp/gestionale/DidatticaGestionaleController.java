package it.polito.tdp.gestionale;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

import it.polito.tdp.gestionale.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DidatticaGestionaleController {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField txtMatricolaStudente;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCorsiFrequentati(ActionEvent event) {
		txtResult.clear();
		txtResult.setText("premuto Corsi Frequentati.\n");
		
		model.creaGrafo();
		List<Integer> lista=model.getStatCorsi();
		for(int i=0; i<lista.size(); i++){
			txtResult.appendText(String.format("Alunni che seguono %d corsi: %d\n", i,lista.get(i)));
		}
	}
	
	@FXML
	void doVisualizzaCorsi(ActionEvent event) {
		txtResult.clear();
		txtResult.setText("premuto Visualizza Corsi");
	}

	@FXML
	void initialize() {
		assert txtMatricolaStudente != null : "fx:id=\"txtMatricolaStudente\" was not injected: check your FXML file 'DidatticaGestionale.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'DidatticaGestionale.fxml'.";
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
