package br.com.gtmf.model;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Bundle implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * Protocolo da mensagem
	 * 
	 */
	public static final String USER_IN = 					"UIN"; // Envia "eu" (recem-login) para o servidor
	public static final String START_RADAR = 				"SAR"; // Envia "eu" (recem-login) para o servidor
	public static final String STOP_RADAR = 				"SOR"; // Envia "eu" (recem-logout) para o servidor
	public static final String LIST_USERS_ON = 				"LUO"; // Recebe a lista dos usuarios logados
	public static final String CHAT_MSG_SEND = 				"CMS"; // Envia a mensagem via chat
	public static final String CHANGE_USER_LOCATION = 		"CHL"; // Notifica aos usuarios que alguem mudou a sua localizacao
	public static final String LIST_ROOMS = 				"LRD"; // Notifica aos usuarios que alguem mudou a sua localizacao
	
	
	private String head; // cabecalho da mensagem
	private String body; // conteudo da mensagem
	
	
	public Bundle(String head, String body) {
		this.head = head;
		this.body = body;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public static Bundle fromJson(String jsonStr) throws Exception {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Bundle bundle = gson.fromJson(jsonStr, Bundle.class);
		
		return bundle;
	}

	public static String toJson(Bundle bundle) throws Exception {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(bundle);

		return json;
	}
	
//	@Override
//	public String toString() {
//		try {
//			return toJson(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return "erro!";
//	}
	
}
