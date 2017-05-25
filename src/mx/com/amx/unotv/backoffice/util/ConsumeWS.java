package mx.com.amx.unotv.backoffice.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import mx.com.amx.unotv.backoffice.dto.ParametrosPlemDTO;

import org.apache.log4j.Logger;

public class ConsumeWS {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	private String rutaWS; 
	
	public ConsumeWS(String url) {
		try {
			rutaWS = url;
		} catch (Exception ex) {
			logger.error("No se encontro el Archivo de propiedades: ", ex);			
		}
	}
	
	
	/*public ConsumeWS(String properties, String audiencia) {
		try {
			Properties propsTmp = new Properties();
		    propsTmp.load(this.getClass().getResourceAsStream( "/ApplicationResources.properties" ));
			String rutaProperties = propsTmp.getProperty(properties);			
			Properties props = new Properties();
			props.load(new FileInputStream(new File(rutaProperties)));		
	        rutaWS = props.getProperty("WSPLEM-"+audiencia);
		} catch (Exception ex) {
			logger.error("No se encontro el Archivo de propiedades: ", ex);			
		}
	}*/
	
	//public String executeWS(String metodo, String parametros) {
	public String executeWS(ParametrosPlemDTO parametrosPlemDTO, String parametros) {
		String ambiente="";
		URL url;
		HttpURLConnection connection = null;
		String respuesta = "";
		String respuestaFAIL="{\"codigo\":\"-1\",\"descripcion\":\"$DESCRIPCION_ERROR$\"}";
		try {
			Properties propsTmp = new Properties();
		    propsTmp.load(this.getClass().getResourceAsStream( "/general.properties" ));
			ambiente = propsTmp.getProperty("ambiente");
			
			logger.debug("Conectandose a: "+rutaWS + parametrosPlemDTO.getMetodoEnvioPlem());
			//Create connection
			url = new URL(rutaWS + parametrosPlemDTO.getMetodoEnvioPlem());
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");		
			connection.setRequestProperty("Content-Length", "" + Integer.toString(parametros.getBytes().length));						
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			if(ambiente.equalsIgnoreCase("produccion")){
				logger.debug("Ambiente Productivo, seteamos X-Target --> "+parametrosPlemDTO.getxTarget());
				connection.setRequestProperty("X-Target", parametrosPlemDTO.getxTarget());
			}
				
			
			connection.setConnectTimeout(60000);
			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
			wr.writeBytes (parametros);
			wr.flush ();
			wr.close ();
			//Get Response	
			InputStream is = null;			
			if (connection.getResponseCode() >= 400) {
			    is = connection.getErrorStream();
			} else {
			    is = connection.getInputStream();
			}
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			respuesta =  response.toString();	
			logger.info("Respuesta del WS: "+respuesta);
			//obj = new JSONObject(respuesta);
			
		}  catch (java.net.SocketTimeoutException e) {
			//respuesta="Se ha tardado demasiado tiempo en esta solicitud, favor de contactar con su administrador";
			respuestaFAIL=respuestaFAIL.replaceAll("$DESCRIPCION_ERROR$", e.getMessage());
			respuesta=respuestaFAIL;

			logger.error(e);
		} catch (Exception e) {
			logger.error("Ocurrio error al abrir la Url: " , e);
			respuestaFAIL=respuestaFAIL.replaceAll("$DESCRIPCION_ERROR$", e.getMessage());
			respuesta=respuestaFAIL;
		} finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
		return respuesta;
	}

}
