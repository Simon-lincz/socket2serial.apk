package com.fornut.wificartest;


interface IConnection{
	
	void newSocket(String address,int port);
	void changeConnectionTo(String address,int port);
	void sendCMD(String cmd);

}