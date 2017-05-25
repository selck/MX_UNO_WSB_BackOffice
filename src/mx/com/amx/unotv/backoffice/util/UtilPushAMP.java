package mx.com.amx.unotv.backoffice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

import org.apache.log4j.Logger;

import mx.com.amx.unotv.backoffice.dto.ParametrosAMP;
import mx.com.amx.unotv.backoffice.dto.PushAMP;
import mx.com.amx.unotv.backoffice.dto.RespuestaWSAMP;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class UtilPushAMP {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	
	public RespuestaWSAMP sendPushAMP(String feed, ParametrosAMP parametrosAMP){
		
		logger.info("sendPushAMP Act");
		String scopes = "https://www.googleapis.com/auth/indexing";//con este se pide token
		String endPoint = "https://indexing.googleapis.com/v1/index/public:update";

		RespuestaWSAMP respuestaWSAMP=new RespuestaWSAMP();
		GenericUrl genericUrl=null;
		GoogleCredential credentials=null;
		try {
			
			JsonFactory jsonFactory = new JacksonFactory();
			InputStream in = new FileInputStream(parametrosAMP.getPathCertificado());
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			try {
				credentials = GoogleCredential.fromStream(in, httpTransport, jsonFactory).createScoped(Collections.singleton(scopes));
			} catch (Exception e) {
				logger.error("Error credentials: ",e);
			}
			
			try {
				genericUrl= new GenericUrl(endPoint);
			} catch (Exception e) {
				logger.error("Error genericUrl: ",e);
			}
							
			
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			
			HttpRequest request = requestFactory.buildPostRequest(genericUrl, ByteArrayContent.fromString(null, feed));

			credentials.initialize(request);
			HttpResponse response = request.execute(); 
			
			int statusCode = response.getStatusCode();
			logger.info("statusCode: "+statusCode);
			respuestaWSAMP.setCodigo("00");
			respuestaWSAMP.setRespuesta(statusCode+"");
			
		} catch (Exception e) {
			logger.error("Error sendPushAMP: ",e);
			respuestaWSAMP.setCodigo("-1");
			respuestaWSAMP.setRespuesta(e.getMessage());
		}
		return respuestaWSAMP;
	}
	public static String generateFeedContent(PushAMP pushAMP) throws Exception{		
		System.out.println("GENERATE_FEED_CONTENT");
		StringBuffer sbFeed = new StringBuffer();
		sbFeed.append("<?xml version=\"1.0\" encoding= \"utf-8\"?>");
		sbFeed.append("<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:inline=\"http://www.google.com/schemas/atom-inline/1.0\">");
		sbFeed.append("<id>https://news.net/all-the-news.xml</id>");
		sbFeed.append("<title>"+pushAMP.getFcTitulo()+"</title>"); //CONTENIDO
		sbFeed.append("<author><name>www.unotv.com</name></author>");
		sbFeed.append("<updated>"+pushAMP.getFdFechaPublicacion()+"</updated>");//CONTENIDO
	/*HTML DE LA NOTA*/
		sbFeed.append("<entry>");
		sbFeed.append("<id>"+pushAMP.getFcIdContenido()+"</id>");
		sbFeed.append("<updated>"+pushAMP.getFdFechaPublicacion()+"</updated>");
		sbFeed.append("<title>"+pushAMP.getFcTitulo()+"</title>");
		//sbFeed.append("<link rel=\"canonical\" href=\"http://www.unotv.com/noticias/portal/internacional/detalle/trump-y-clintos-hablan-de-mexico-en-primer-debate-577665/\" />");
		sbFeed.append("<link rel=\"canonical\" href=\""+"http://www.unotv.com/"+pushAMP.getFcTipoSeccion() + "/" + pushAMP.getFcSeccion()+"/"+ pushAMP.getFcIdCategoria()+"/detalle/" +pushAMP.getFcNombre()+"/\" />");
		//sbFeed.append("<link rel=\"amphtml\" href=\"http://www.unotv.com/noticias/portal/internacional/detalle/trump-y-clintos-hablan-de-mexico-en-primer-debate-577665/amp.html\" >");
		sbFeed.append("<link rel=\"amphtml\" href=\""+"http://www.unotv.com/"+pushAMP.getFcTipoSeccion() + "/" + pushAMP.getFcSeccion()+"/"+ pushAMP.getFcIdCategoria()+"/detalle/" +pushAMP.getFcNombre()+"/amp.html/\" >");
		
		sbFeed.append("<inline:inline type=\"text/html\">");
		sbFeed.append("<![CDATA[");
		sbFeed.append("<!doctype html>");
		sbFeed.append(pushAMP.getHtmlAMP());
		sbFeed.append("]]>");
		sbFeed.append("</inline:inline>");
		sbFeed.append("</link>");
		sbFeed.append("</entry>");
		sbFeed.append("</feed>");			
		return sbFeed.toString();
	}
	
	public ParametrosAMP obtenerPropiedades(String properties) {
		ParametrosAMP parametrosDTO = new ParametrosAMP();		 
		try {	    		
			Properties propsTmp = new Properties();
		    propsTmp.load(this.getClass().getResourceAsStream( "/general.properties" ));
		    String ambiente = propsTmp.getProperty("ambiente");
		    String rutaProperties = propsTmp.getProperty(properties.replace("ambiente", ambiente));			
			Properties props = new Properties();
			props.load(new FileInputStream(new File(rutaProperties)));
			parametrosDTO.setAmbiente(ambiente);
			parametrosDTO.setUrlEndPointScope(props.getProperty("urlEndPointScope"));
			parametrosDTO.setUrlScope(props.getProperty("urlScope"));
			parametrosDTO.setxTargetEndPoint(props.getProperty("xTargetEndPoint"));
			parametrosDTO.setxTargetScope(props.getProperty("xTargetScope"));
			parametrosDTO.setPathCertificado(props.getProperty("pathCertificado"));
		} catch (Exception ex) {
			parametrosDTO = new ParametrosAMP();
			logger.error("No se ParametrosAMP el Archivo de propiedades: ", ex);			
		}
		return parametrosDTO;
	}
	
}
