package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    /** Obiecte specifice fiecarui element grafic **/
    /* SERVER */
    private EditText serverPort = null;
    private Button connect = null;
    /* CLIENT */
    private EditText clientAddress = null;
    private EditText clientPort = null;
    private Button getResponse = null;
    /* Camp pentru afisarea raspunsului de la server */
    private TextView showResponse = null;
    //TODO : de completat cu alte date specifice cerintei

    /* Thread-ul clientului */
    private ClientThread client = null;
    /* Thread-ul serverului */
    private ServerThread server = null;

    ConnectClickListener connectClickListener = new ConnectClickListener();
    private class ConnectClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            /* Se obtine valoarea din edit text-ul corespunzator */
            String serverPortValue = serverPort.getText().toString();
            if (serverPortValue==null || serverPortValue.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Completati campul corespunzator portului pentru server!", Toast.LENGTH_SHORT).show();
                return;
            }
            /* Se instantiaza obiectul de tip ServerThread */
            server = new ServerThread(Integer.parseInt(serverPortValue));
            /* Se verifica daca a fost introdusa o valoare pentru portul corespunzator serverului */
            if (server.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Serverul nu poate porni(Introduceti portul)!");
                return;
            }
            /* Se porneste thread-ul */
            server.start();
        }
    }

    GetResponseClickListener getResponseClickListener = new GetResponseClickListener();
    private class GetResponseClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            /* Se obtin valorile din edit text-urile corespunzatoare */
            String clientAddressValue = clientAddress.getText().toString();
            String clientPortValue = clientPort.getText().toString();
            if(clientAddressValue==null || clientAddressValue.isEmpty()
                || clientPortValue==null || clientPortValue.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Completati campurile necesare conectarii clientului la server(adresa si port)!", Toast.LENGTH_SHORT).show();
                return;
            }
            //TODO : de completat cu alte date specifice cerintei
            showResponse.setText(Constants.EMPTY_STRING);
            /* Se verifica daca thread-ul corespunzator serverului finctioneaza */
            if (server==null || !server.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Serverul nu a pornit!", Toast.LENGTH_SHORT).show();
                return;
            }
            /* Se instantiaza obiectul de tip ClientThread */
            client = new ClientThread(Integer.parseInt(clientPortValue), clientAddressValue, showResponse);
            /* Se porneste thread-ul */
            client.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] Metoda onCreate() a fost apelata!");
        setContentView(R.layout.activity_practical_test02_main);

        /** Se determina obiectele corespunzatoare elementelor grafice in functie de id **/
        /* SERVER */
        serverPort = (EditText)findViewById(R.id.server_port_edit_text);
        connect = (Button)findViewById(R.id.connect_button);
        connect.setOnClickListener(connectClickListener);
        /* CLIENT */
        clientAddress = (EditText)findViewById(R.id.client_address_edit_text);
        clientPort = (EditText)findViewById(R.id.client_port_edit_text);
        getResponse = (Button)findViewById(R.id.get_response);
        getResponse.setOnClickListener(getResponseClickListener);
        showResponse = (TextView)findViewById(R.id.show_response);
        //TODO : de completat cu alte date specifice cerintei
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] Metoda onDestroy() a fost apelata!");
        /* Se opreste thread-ul specific serverului */
        if (server != null) {
            server.stopThread();
        }
        super.onDestroy();
    }
}
