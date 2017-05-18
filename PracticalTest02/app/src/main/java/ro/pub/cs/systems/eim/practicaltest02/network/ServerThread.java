package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.model.DataModel;

/**
 * Created by jeni-rotaru on 17.05.2017.
 */

public class ServerThread extends Thread {

    /* Obiectele corespunzatoare elementelor grafice */
    private int portServer = 0;
    //TODO : de completat cu alte date specifice cerintei
    /* Socket-ul folosit pentru comunicatie */
    private ServerSocket server = null;
    /* HashMap-ul in care sunt retinute datele preluate de la serverul web */
    //TODO : de adaptat in functie de cerinta
    private HashMap<String, DataModel> data = null;

    /* Constructorul clasei */
    public ServerThread(int portServer) {
        this.portServer = portServer;
        try {
            /* Deschiderea unui canal de comunicație folosind portul primit ca parametru */
            this.server = new ServerSocket(portServer);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "S-a produs eroarea: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        this.data = new HashMap<>();
    }

    /* Get si Set pentru campurile private */
    public void setPort(int portServer) {
        this.portServer = portServer;
    }
    public int getPort() {
        return portServer;
    }
    public void setServerSocket(ServerSocket server) {
        this.server = server;
    }
    public ServerSocket getServerSocket() {
        return server;
    }
    //TODO : de adaptat in functie de cerinta
    /* Neaparat sincronizate */
    public synchronized void setData(String city, DataModel dataModel) {
        this.data.put(city, dataModel);
    }
    public synchronized HashMap<String, DataModel> getData() {
        return data;
    }

    @Override
    public void run() {
        try {
            /* Atâta vreme cât firul de execuție nu este întrerupt */
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Se asteapta conexiunea din partea unui client...");
                /* Se accepta conexiuni de la clienti */
                Socket socket = server.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] S-a conectat clientul: " + socket.getInetAddress() + ":" + socket.getLocalPort());
                /* Obtinerea unui fir de executie dedicat pe care se va realiza comunicatia efectiva intre server si clienti */
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (ClientProtocolException clientProtocolException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + clientProtocolException.getMessage());
            if (Constants.DEBUG) {
                clientProtocolException.printStackTrace();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    /* Metoda necesara opririi serverului */
    public void stopThread() {
        interrupt();
        if (server != null) {
            try {
                server.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] S-a produs eroarea: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}
