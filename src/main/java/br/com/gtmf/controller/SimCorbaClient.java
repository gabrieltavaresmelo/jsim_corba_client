package br.com.gtmf.controller;

import br.com.gtmf.window.ListenerWindow;

public class SimCorbaClient extends sim.SimCorbaClientPOA {

	private ListenerWindow listener = null;

	public SimCorbaClient(ListenerWindow listener) {
		this.listener = listener;
	}
	
	@Override
	public void update(String message) {
		if(listener == null){
			System.out.println(message);
		} else{
			listener.update(message);
		}
	}

}
