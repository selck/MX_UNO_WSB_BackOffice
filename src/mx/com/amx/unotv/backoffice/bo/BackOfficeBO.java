package mx.com.amx.unotv.backoffice.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.com.amx.unotv.backoffice.dto.NotaDTO;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Qualifier("backOfficeBO")
public class BackOfficeBO {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private RestTemplate restTemplate;
	private String URL_WS_BASE="";
	private HttpHeaders headers = new HttpHeaders();
	
	public BackOfficeBO() {
		super();
		restTemplate = new RestTemplate();
		ClientHttpRequestFactory factory = restTemplate.getRequestFactory();

	        if ( factory instanceof SimpleClientHttpRequestFactory)
	        {
	            ((SimpleClientHttpRequestFactory) factory).setConnectTimeout( 15 * 1000 );
	            ((SimpleClientHttpRequestFactory) factory).setReadTimeout( 15 * 1000 );
	            logger.info("Inicializando rest template");
	        }
	        else if ( factory instanceof HttpComponentsClientHttpRequestFactory)
	        {
	            ((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout( 15 * 1000);
	            ((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout( 15 * 1000);
	            logger.info("Inicializando rest template");
	        }
	        restTemplate.setRequestFactory( factory );
	        headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
	public BackOfficeBO(String urlWS) {
		super();
		restTemplate = new RestTemplate();
		ClientHttpRequestFactory factory = restTemplate.getRequestFactory();

	        if ( factory instanceof SimpleClientHttpRequestFactory)
	        {
	            ((SimpleClientHttpRequestFactory) factory).setConnectTimeout( 15 * 1000 );
	            ((SimpleClientHttpRequestFactory) factory).setReadTimeout( 15 * 1000 );
	            logger.info("Inicializando rest template");
	        }
	        else if ( factory instanceof HttpComponentsClientHttpRequestFactory)
	        {
	            ((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout( 15 * 1000);
	            ((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout( 15 * 1000);
	            logger.info("Inicializando rest template");
	        }
	        restTemplate.setRequestFactory( factory );
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        
			URL_WS_BASE = urlWS;
	}
	
	public List<NotaDTO> getNotesPublished(String idMagazine) {
		List<NotaDTO> listNotasRecibidas=null;
		String metodo="getNotesPublished";
		String URL_WS=URL_WS_BASE+metodo;
		try {
			logger.info("URL_WS: "+URL_WS);
			MultiValueMap<String, Object> parts;
			parts = new LinkedMultiValueMap<String, Object>();
			parts.add("idMagazine", idMagazine);
			NotaDTO[] arrayNotasRecibidas = restTemplate.postForObject(URL_WS,parts, NotaDTO[].class);
			listNotasRecibidas=new ArrayList<NotaDTO>(Arrays.asList(arrayNotasRecibidas));
		} catch(Exception e) {
			logger.error("Error getNotesPublished [BO]: ",e);
		}		
		return listNotasRecibidas;	
	}
	
	/*public RespuestaWSAMP sendPushAMP(String feed){
		logger.info("sendPushAMP");
		String scopes = "https://www.googleapis.com/auth/indexing";//con este se pide token
		String endPoint = "https://indexing.googleapis.com/v1/index/public:update";
		//String endPoint = "https://AMXSVROUT01-1.tmx-internacional.net/v1/index/public:update";
		RespuestaWSAMP respuestaWSAMP=new RespuestaWSAMP();
		GenericUrl genericUrl=null;
		GoogleCredential credential=null;
		try {
		
			JsonFactory jsonFactory = new JacksonFactory();
			InputStream in = new FileInputStream("/var/dev-repos/unotv/properties/My-Project-00abc649c685.json");
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			
			try {
				credential = GoogleCredential.fromStream(in, httpTransport, jsonFactory).createScoped(Collections.singleton(scopes));
			} catch (Exception e) {
				logger.error("Error credential: ",e);
			}
			
			try {
				genericUrl= new GenericUrl(endPoint);
				
			} catch (Exception e) {
				logger.error("Error genericUrl: ",e);
			}
			
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			HttpRequest request = requestFactory.buildPostRequest(genericUrl, ByteArrayContent.fromString(null, feed));
			credential.initialize(request);
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
	public static String generateFeedContent(ContentDTO contentDTO, String htmlAMP) throws Exception{		
		
		StringBuffer sbFeed = new StringBuffer();
		sbFeed.append("<?xml version=\"1.0\" encoding= \"utf-8\"?>");
		sbFeed.append("<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:inline=\"http://www.google.com/schemas/atom-inline/1.0\">");
		sbFeed.append("<id>https://news.net/all-the-news.xml</id>");
		sbFeed.append("<title>Breaking News</title>"); //CONTENIDO
		sbFeed.append("<author><name>www.unotv.com</name></author>");
		sbFeed.append("<updated>2015-07-20T00:44:51Z</updated>");//CONTENIDO
	//HTML DE LA NOTA
		sbFeed.append("<entry>");
		sbFeed.append("<id>"+contentDTO.getFcIdContenido()+"</id>");
		sbFeed.append("<updated>"+contentDTO.getFdFechaPublicacion()+"</updated>");
		sbFeed.append("<title>"+contentDTO.getFcTitulo()+"</title>");
		//sbFeed.append("<link rel=\"canonical\" href=\"http://www.unotv.com/noticias/portal/internacional/detalle/trump-y-clintos-hablan-de-mexico-en-primer-debate-577665/\" />");
		sbFeed.append("<link rel=\"canonical\" href=\""+"http://www.unotv.com/"+contentDTO.getFcTipoSeccion() + "/" + contentDTO.getFcSeccion()+"/"+ contentDTO.getFcIdCategoria()+"/detalle/" +contentDTO.getFcNombre()+"/\" />");
		//sbFeed.append("<link rel=\"amphtml\" href=\"http://www.unotv.com/noticias/portal/internacional/detalle/trump-y-clintos-hablan-de-mexico-en-primer-debate-577665/amp.html\" >");
		sbFeed.append("<link rel=\"amphtml\" href=\""+"http://www.unotv.com/"+contentDTO.getFcTipoSeccion() + "/" + contentDTO.getFcSeccion()+"/"+ contentDTO.getFcIdCategoria()+"/detalle/" +contentDTO.getFcNombre()+"/amp.html/\" >");
		
		sbFeed.append("<inline:inline type=\"text/html\">");
		sbFeed.append("<![CDATA[");
		sbFeed.append("<!doctype html>");
		sbFeed.append(htmlAMP);
		sbFeed.append("]]>");
		sbFeed.append("</inline:inline>");
		sbFeed.append("</link>");
		sbFeed.append("</entry>");
		sbFeed.append("</feed>");			
		return sbFeed.toString();
	}
		
	public static String generateFeedContent(PushAMP pushAMP) throws Exception{		
		
		StringBuffer sbFeed = new StringBuffer();
		sbFeed.append("<?xml version=\"1.0\" encoding= \"utf-8\"?>");
		sbFeed.append("<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:inline=\"http://www.google.com/schemas/atom-inline/1.0\">");
		sbFeed.append("<id>https://news.net/all-the-news.xml</id>");
		sbFeed.append("<title>"+pushAMP.getFcTitulo()+"</title>"); //CONTENIDO
		sbFeed.append("<author><name>www.unotv.com</name></author>");
		sbFeed.append("<updated>"+pushAMP.getFdFechaPublicacion()+"</updated>");//CONTENIDO
	//HTML DE LA NOTA
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
	} */
	
}
