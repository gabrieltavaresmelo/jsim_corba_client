package br.com.gtmf.controller;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;

import sim.SimCorbaServerHelper;
import br.com.gtmf.window.ListenerWindow;

public class ClientCreator implements Runnable{

	private ORB orb;
	
	private sim.SimCorbaServer simCorbaServer;
	private sim.SimCorbaClient simCorbaClient;
	private String nickname;
	private String room;

	Thread thread = new Thread(this);
	
	public ClientCreator(String[] args, String nickname, ListenerWindow listener) throws Exception {
				
		// Inicializando o ORB
		orb = ORB.init(args, null);
		
		// Obtendo o NameService
		org.omg.CORBA.Object obj = orb
				.resolve_initial_references("NameService");
		NamingContextExt ncRef = org.omg.CosNaming.NamingContextExtHelper
				.narrow(obj);

		// Determinando o nome do servidor
		obj = ncRef.resolve_str("sim_corba");
		simCorbaServer = SimCorbaServerHelper.narrow(obj);

		// Instanciando o cliente
		SimCorbaClient cc = new SimCorbaClient(listener);
		
		// Conectando com o cliente com o ORB
		simCorbaClient = cc._this(orb);
		
		this.nickname = nickname;
		room = simCorbaServer.login(nickname, simCorbaClient);				
		
		thread.start();		
	}
	
	public ORB getOrb() {
		return orb;
	}
	
	public sim.SimCorbaClient getSimCorbaClient() {
		return simCorbaClient;
	}
	
	public sim.SimCorbaServer getSimCorbaServer() {
		return simCorbaServer;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}
	
	public void run() {
		try {
			// Obtendo uma referencia ao POA
			org.omg.CORBA.Object obj = orb
					.resolve_initial_references("RootPOA");
			POA rootpoa = POAHelper.narrow(obj);
			
			// Obtendo uma referencia ao POA manager
			POAManager manager = rootpoa.the_POAManager();
			
			// Ativando o manager
			manager.activate();
			
			orb.run();
			
		} catch (Exception e) {
		}
	}
	
	public void close() {
		try {
			System.out.print("desconectando...");
			simCorbaServer.logout(nickname);
			System.out.println(" desconectado");
			
			Thread.sleep(1000);
			
			orb.destroy();
			thread.join();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}