package br.com.gtmf.window;

import java.util.ArrayList;
import java.util.Collection;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbee.javafx.scene.layout.fxml.MigPane;

import br.com.gtmf.controller.ClientCreator;
import br.com.gtmf.model.Bundle;
import br.com.gtmf.utils.FXOptionPane;
import br.com.gtmf.utils.FXOptionPane.Response;
import br.com.gtmf.utils.NetworkUtils;

/**
 * Classe que executa os eventos do Layout da
 * Tela MainLayout
 * 
 * @author Gabriel tavares
 *
 */
public class MainLayoutWindow implements ListenerWindow {

	private static final Logger LOG = LoggerFactory.getLogger(MainLayoutWindow.class);

	// Widgets    
	@FXML private TextField tfUsername;
	@FXML private TextField tfLatitude;
	@FXML private TextField tfLongitude;
    @FXML private ToggleButton tbStatus;
    @FXML private ToggleButton tbRadar;
    @FXML private Label lbRooms;
    @FXML private Label lbLatitude;
    @FXML private Label lbLongitude;
    @FXML private Label lbTitleUsers;
    @FXML private Label lbStatus;
    @FXML private ListView<String> lvUsersOn;
    @FXML private TextArea taHistory;
    @FXML private TextField tfSendChat;
    @FXML private Button btSendChat;
    @FXML private Button btChangeLocation;
    @FXML private ComboBox<String> cbRooms;
    @FXML private ComboBox<String> cbTopics;
    @FXML private CheckBox chkMsgAll;
    
	// Referencia a Stage
	private Stage stage;
	private MigPane migPane;
	
	private ClientCreator clientCreator;

	private String[] args;
	
	public void setParams(Stage stage, MigPane migPane) {
		this.stage = stage;
		this.migPane = migPane;
	}
	
	public void setArgs(String[] args) {
		this.args = args;
	}
	
	/**
	 * Inicializa a classe controller. 
	 * Eh chamado automaticamente depois do fxml ter sido carregado 
	 */
	@FXML
	private void initialize() {
		// Inicializa os componentes de entrada de texto dinamicamente
		tfUsername.setText(NetworkUtils.getIpHostnameLocal());
        
		// Desabilita os campos
        setLockToConnectUI(false);
		setLockToRadarUI(false);
		
		cbRooms.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override 
			public void changed(ObservableValue<? extends String> selected, String oldValue, String newValue) {
				
				if(newValue != null){
					clientCreator.getSimCorbaServer().changeClientRoom(clientCreator.getNickname(), newValue);
					clientCreator.getSimCorbaServer().listNearlyClients(clientCreator.getNickname());
				}
			}
		});
	}

	
	// ---------------------------------------------------
	// -- 
	// -- 	Metodos dos Eventos dos componentes da tela
	// --
	// ---------------------------------------------------

	@FXML
	private void handleExit() {
		finish();
	}
	
	@FXML
	private void handleAbout() {
		String body = "";

		body += "Curso:";
		body += "\n";
		body += "Engenharia de Computação";
		body += "\n";
		body += "\n";
		
		body += "Disciplina:";
		body += "\n";
		body += "Programação Paralela e Distribuída";
		body += "\n";
		body += "18/11/2013";
		body += "\n";
		body += "\n";

		body += "Trabalho:";
		body += "\n";
		body += "Sistema de Interação Móvel (SIM)";
		body += "\n";
		body += "\n";
		
		body += "Aluno:";
		body += "\n";
		body += "Gabriel Tavares";
		body += "\n";
		body += "gabrieltavaresmelo@gmail.com";
		body += "\n";
		body += "https://github.com/gabrieltavaresmelo";
		body += "\n";
		body += "\n";

		FXOptionPane.showMessageDialog(stage, body, "Sobre", "OK");
	}
	
	@FXML
	public void sendChat() {
		String textToSend = tfSendChat.getText();
		
		if(!textToSend.trim().equals("")){
			tfSendChat.setText("");		
			
			String body = tfUsername.getText() + ": " + textToSend;
			sendMessage(Bundle.CHAT_MSG_SEND, body);
//			printTextArea(body);
		}
	}
	
	@FXML
	public void changeLocation() {
		if(clientCreator != null){
			try {
				String location = getLocation();				
				clientCreator.getSimCorbaServer().changeLocation(clientCreator.getNickname(), location);
				
			} catch (NumberFormatException e) {
				FXOptionPane.showMessageDialog(stage, "Valores de Latitude e Longitude inválidos!", "Sobre", "OK");
			}
		}
	}
	
	@FXML
	public void tbStatusItemChange() {
		if(tbStatus.isSelected()){
			turnOn();
		} else{			
			turnOff();			
		}
	}
	
	@FXML
	public void tbRadarItemChange() {
		if(tbStatus.isSelected()){
			if(clientCreator != null){
				try {
					String location = getLocation();
					
					clientCreator.getSimCorbaServer().startRadar(clientCreator.getNickname(), location);
//					clientCreator.getSimCorbaServer().listNearlyClients(clientCreator.getNickname());

			        setLockToRadarUI(true);
			        
				} catch (NumberFormatException e) {
					FXOptionPane.showMessageDialog(stage, "Valores de Latitude e Longitude inválidos!", "Alerta", "OK");
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		} else{
			if(clientCreator != null){
				setLockToRadarUI(false);
		        
				clientCreator.getSimCorbaServer().stopRadar(clientCreator.getNickname());
			}
		}
	}
	
	@FXML
	public void addRoom() {
		String room = FXOptionPane.showInputDialog(stage, "Sala:", "Cadastro de Sala", "Criar", "Cancelar", "");
		
		if(room != null && room.equals("")){
			clientCreator.getSimCorbaServer().addRoom(room);	
		}
	}

	private void listRooms(final Collection<String> rooms) {
		
		// Para enviar dados de outra thread para a GUI deve-se usar o Platform
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {	
	        	if(rooms != null && rooms.size() > 0){
					ObservableList<String> items = FXCollections.observableArrayList(rooms);
					cbRooms.setItems(items);
	        	}
	        }
		});
	}

	private void setRoom(final String room) {
		clientCreator.setRoom(room);
		
		// Para enviar dados de outra thread para a GUI deve-se usar o Platform
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
				cbRooms.getSelectionModel().select(room);
	        }
		});
	}

	private String getLocation() throws NumberFormatException{
		double lat = Double.parseDouble(tfLatitude.getText().toString());
		double lng = Double.parseDouble(tfLongitude.getText().toString());
		String location = lat  + "," + lng;
		
		return location;
	}

	/**
	 * Evento ON do ToggleButton
	 */
	@FXML
	public void turnOn(){
		tbStatus.setText("Desconectar");
		
		try {
			String username = tfUsername.getText();
			
			clientCreator = new ClientCreator(args, username, this);
			
			// Lista as salas na tela
			String json = clientCreator.getSimCorbaServer().listRooms();
			Bundle message = Bundle.fromJson(json);
			listRooms(jsonToList(message.getBody()));
			
			setRoom(clientCreator.getRoom());
			setConnected(true);
			
									
			if(!tbStatus.isSelected()){
				tbStatus.setSelected(true);
			}
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			LOG.error("[Erro] Não foi possível estabelecer a conexão com o servidor!");
			tbStatus.setSelected(false);
			turnOff();
			
			FXOptionPane.showMessageDialog(stage, "Não foi possível estabelecer a conexão com o servidor!", "Alerta", "OK");
		}
	}

	/**
	 * Evento OFF do ToggleButton
	 */
	public void turnOff(){
		tbStatus.setText("Conectar");
		lbStatus.setText("Desconectando");
		
		try {			
			if(clientCreator != null){
				clientCreator.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// --------------------------------------------
	// -- 
	// -- 	Metodos da implementacao do Listener
	// --
	// --------------------------------------------
	
	/**
	 * Recebe a lista de usuarios e mostra na tela
	 */
	public void lvUsers(final Collection<String> users) {				
		// Para enviar dados de outra thread para a GUI deve-se usar o Platform
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {	
				ObservableList<String> items = FXCollections.observableArrayList(users);
				lvUsersOn.setItems(items);
	        }
		});
	}


	@Override
	public void update(final String messageStr) {
		// Para enviar dados de outra thread para a GUI deve-se usar o Platform
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {		
				try {
//					System.out.println(messageStr);
					Bundle message = Bundle.fromJson(messageStr);
					
					String messageBody = "";
					
					switch (message.getHead()) {
						case Bundle.LIST_ROOMS:			
							messageBody = message.getBody();
							listRooms(jsonToList(messageBody));
							
							String room = clientCreator.getSimCorbaServer().getRoom(clientCreator.getNickname());
							setRoom(room);
							
							break;
					
						case Bundle.LIST_USERS_ON:							
							messageBody = message.getBody();
							lvUsers(jsonToList(messageBody));
														
							break;
							
						case Bundle.CHAT_MSG_SEND:
							messageBody = message.getBody();
							
							if(messageBody != null){
								printTextArea(messageBody);
							}
							break;
							
						case Bundle.CHANGE_USER_LOCATION:
							try {
								if(clientCreator != null){
									String jsonReturn = clientCreator.getSimCorbaServer()
										.listNearlyClients(
												clientCreator.getNickname());
									
									if(jsonReturn != null && !jsonReturn.equals("")){
										Bundle clientsOn = Bundle.fromJson(jsonReturn);
//										System.out.println(clientsOn.getBody());
										
										lvUsers(jsonToList(clientsOn.getBody()));
									}
								}
							} catch (NumberFormatException e) {
								FXOptionPane.showMessageDialog(stage, "Valores de Latitude e Longitude inválidos!", "Sobre", "OK");
							} catch (Exception e) {
								System.err.println(e.getMessage());
							}
							
							break;
					}					
					
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
	        }
		});		
	}

	private Collection<String> jsonToList(String messageBody) {
		Collection<String> list = new ArrayList<String>();
		
		if(messageBody.contains(";")){
			String [] split = messageBody.split(";");
			
			for (String nickname : split) {
				if(!nickname.equals(tfUsername.getText().toString())){
					list.add(nickname);
				}
			}
		} else if(messageBody.equals("")){
			list.add("");
		} else{
			list.add(messageBody);
		}
		
		return list;
	}

	public void setConnected(final boolean isConnected) {
		// Para enviar dados de outra thread para a GUI deve-se usar o Platform
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {						
	        	if(isConnected){
	        		if(clientCreator != null){
		        		String username = clientCreator.getNickname();
		    			
		    			lbStatus.setText("Conectado " + username);
//		    			System.out.println("Conectado " + username);
		    			
				        setLockToConnectUI(true);
	        		}
	    			
	    		} else{
	    			if(clientCreator != null){
		    			lbStatus.setText("Desconectado");
		    			clientCreator.close();

				        setLockToConnectUI(false);
				        setLockToRadarUI(false);
	    			}
	    		}
	        }
		});		
	}
	
	public void setLockToConnectUI(final boolean isLock) {
		// Para enviar dados de outra thread para a GUI deve-se usar o Platform
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {		
	        	tfUsername.setDisable(isLock);
				tbRadar.setVisible(isLock);				
	        }
		});	
	}

	public void setLockToRadarUI(final boolean isLock) {
		// Para enviar dados de outra thread para a GUI deve-se usar o Platform
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
				
				lvUsersOn.setVisible(isLock);
				taHistory.setVisible(isLock);
				tfSendChat.setVisible(isLock);
				btSendChat.setVisible(isLock);

				lbLatitude.setVisible(isLock);
				lbLongitude.setVisible(isLock);
				tfLatitude.setVisible(isLock);
				tfLongitude.setVisible(isLock);
				btChangeLocation.setVisible(isLock);
				
				lbRooms.setVisible(isLock);
				cbRooms.setVisible(isLock);
				lbTitleUsers.setVisible(isLock);

				chkMsgAll.setVisible(isLock);
	        }
		});	
	}

	
	// --------------------------------------------
	// -- 
	// -- 	Metodos auxiliares
	// --
	// --------------------------------------------
	
	/**
	 * Envia uma mensagem (Bundle-JSON) generica para o servidor
	 * 
	 * @param head
	 * @param textToSend
	 */
	private void sendMessage(Bundle bundle) {
		try {
			String textToSend = Bundle.toJson(bundle);			
//			LOG.debug(textToSend);
			
			if(clientCreator != null){
				
				if(chkMsgAll.isSelected()){ // Mensagem para todos
					clientCreator.getSimCorbaServer().write(clientCreator.getNickname(), textToSend);
					
				} else{ // Mensagem para um cliente especifico
					try {
						String selectedLv = lvUsersOn.getSelectionModel().getSelectedItem();
						
						if(selectedLv != null && !selectedLv.equals("")){
							String [] split = selectedLv.trim().split(" ");
							String from = split[0];
							
							if(from != null && !from.equals("")){
								clientCreator.getSimCorbaServer().writeTo(
										clientCreator.getNickname(), from, textToSend);
							}
						}
					} catch (Exception e) {
					}
				}				
			}
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	private void sendMessage(String head, String textToSend) {		
		try {
			Bundle bundle = new Bundle(head, textToSend);
			sendMessage(bundle);
						
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void printTextArea(String message) {
		if(taHistory.getLength() > 700){
			taHistory.clear();
		}
		
		taHistory.appendText(message + "\n");
	}

	/**
	 * Encerra a aplicacao
	 */
	public void finish() {
		Response resp = FXOptionPane.showConfirmDialog(stage,
				"Deseja encerrar a Aplicação?", "Sair", "Sim", "Não");
		
		if(resp == Response.YES){
			turnOff();
			stage.close();
	//		Platform.exit();
			System.exit(0); // FIXME Procurar um metodo menos "agressivel"
		}
	}
}
