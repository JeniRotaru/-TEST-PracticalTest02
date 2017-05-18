package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.DataModel;

/**
 * Created by jeni-rotaru on 18.05.2017.
 */

public class CommunicationThread extends Thread {

    /* Referință către obiectul gestionat de server ce reține informațiile
        care au fost realizate interogări anteriore */
    private ServerThread serverThread;
    /* Canalul de citire de la client */
    private Socket socket;

    /* Constructorul clasei */
    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket-ul de la client nu e valid!");
            return;
        }
        try {
            /* Se obțin obiectele prin care se vor realiza operațiile
                de citire și scriere pe canalul de comunicație */
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] BufferedReader/PrintWriter sunt null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Se asteapta informatiile/parametrii de la client!");
            /* Se obtin informatiile de pe socketul asociat clientului */
            /* Exemplu : String city = bufferedReader.readLine();
                         if (city == null || city.isEmpty()) {
                            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Eroare!");
                            return;
                         }
               TODO : de modificat conform cerintei
            */
            /* Se obtin obtin informatiile ce se gasesc in obiectul gestionat de server */
            //TODO : de modificat conform cerintei
            HashMap<String, DataModel> data = serverThread.getData();
            DataModel dataModel = null;
            /* Daca informatia necesara exista stocata local in obiectul gestionat de server */
            /* Exemplu :   if (data.containsKey(city)) {
                                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Se obtine informatia din obiectul gestionat de server(local)...");
                                dataModel = data.get(city);
                            }
                TODO : de modificat conform cerintei
            */
                /* Altfel informatia trebuie preluata de la serverul web si parsata */
                /* Exemplu : } else { */
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Se obtin inforamtiile de la serverul web...");
                /** ACCESAREA resurselor prin intermediul protocolului HTTP **/
                /* Pas 1 = instantierea unui obiect de tip HttpClient */
                HttpClient httpClient = new DefaultHttpClient();
                /* Se va utiliza o cerere de tip POST */
                /* Pas 2 = instantierea unui obiect de tip HttpPost
                           (va primi ca parametru adresa serverului web de la care se vor descarca datele)  */
                HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
                /* Pas 3 = definirea unei liste de perechi(<atribut, valoare>) care vor reprezenta datele
                           trimise de client si pe baza carora serverul web va genera continutul */
                List<NameValuePair> params = new ArrayList<>();
                /* Pas 4 = atasarea datelor(definite la Pasul 3) la obiectul de tip HttpPost */
                params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, /* TODO : de completat */));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
                /* Pas 5 = realizarea propriu-zisa a cererii HTTP, prin apelarea metodei execute() */
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                /* Rezultatul/Continutul intors in urma cererii de tip POST */
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD]Eroare in obtinerea informatiilor de la serverul web!");
                    return;
                }
                /** PARSAREA continutului intors de serverul web **/
                Document document = Jsoup.parse(pageSourceCode);
                Element element = document.child(0);
                Elements elements = element.getElementsByTag(Constants.SCRIPT_TAG);
                /* TODO : de modificat in functie de cerinte
                Exemplu :   for (Element script: elements) {
                            String scriptData = script.data();
                            if (scriptData.contains(Constants.SEARCH_KEY)) {
                                int position = scriptData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
                                scriptData = scriptData.substring(position);
                                JSONObject content = new JSONObject(scriptData);
                                JSONObject currentObservation = content.getJSONObject(Constants.CURRENT_OBSERVATION);
                                String temperature = currentObservation.getString(Constants.TEMPERATURE);
                                String windSpeed = currentObservation.getString(Constants.WIND_SPEED);
                                String condition = currentObservation.getString(Constants.CONDITION);
                                String pressure = currentObservation.getString(Constants.PRESSURE);
                                String humidity = currentObservation.getString(Constants.HUMIDITY);
                                weatherForecastInformation = new WeatherForecastInformation(
                                        temperature, windSpeed, condition, pressure, humidity
                                );
                                serverThread.setData(city, weatherForecastInformation);
                                break;
                            }
                        }
                 */
            }
            if (dataModel == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Informatia ceruta este null!");
                return;
            }
            /* Se trimite rezultatul catre client */
            /* Exemplu : String result = null; - TODO : se va prelucra conform cerintei
                         printWriter.println(result);
                         printWriter.flush();
               TODO : de completat cu datele specifice cerintei
            */
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD]A aparut eroarea: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (JSONException jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD]A aparut eroarea: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        } finally {
            /* Se inchide socket-ul de comunicatie cu clientul */
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD]A aparut eroarea: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }


}
