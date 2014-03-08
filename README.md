jsim_corba_client
=================

Bate-papo utilizando JAX-WS e Corba.

Implementação no lado [Cliente Corba] utilizando o protocolo JSON.


* Autor: Gabriel Tavares
* Disciplina: Programação Paralela e Distribuída
* Curso: Engenharia de Computação - IFCE



![ScreenShot](https://github.com/gabrieltavaresmelo/jcac_client/raw/master/gui.png)



Execução:
----------------------------

1) Importe os seguites projetos pelo Eclipse:
	
	1.1) jsim_app_server

	1.2) jsim_app_middle (Maven-Project)
	
	1.3) jsim_corba_client (Maven-Project)


2) Execute o script service_name.sh (ou service_name.bat se estiver no windows) que se encontra no projeto jsim_app_middle.

3) Execute os projetos na ordem a seguir:

	2.1) jsim_app_server -> br.com.gtmf.MainAppPublisher.java

    2.2) jsim_app_middle -> br.com.gtmf.MainApp.java

    2.3) jsim_corba_client -> br.com.gtmf.MainApp.java (Abra várias instâncias dessa classe!)



Bibliotecas e Tecnologias utilizadas:
----------------------------

* Java 7 (Oracle)
* JavaFX
* Maven 3
* JavaFX MigLayout
* JavaFX Maven Deploy (Zen)
* JavaWebSocket (TooTallNate)
* Logs (Log4j e slf4j)
* GSON
* Corba
* [ZenJava](http://zenjava.com/javafx/maven/index.html )

 
