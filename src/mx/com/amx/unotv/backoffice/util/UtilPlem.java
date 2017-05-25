package mx.com.amx.unotv.backoffice.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import mx.com.amx.unotv.backoffice.dto.NotaDTO;
import mx.com.amx.unotv.backoffice.dto.ParametrosPlemDTO;




//import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class UtilPlem {
private static Logger logger=Logger.getLogger(UtilPlem.class);
	
	public static void main(String [] args){
		try {
			String titulo="Reconoce papá ungüento al Papa Francisco como un “hombre bueno”";
			String url="http://www.unotv.com/noticias/estados/nuevo-leon/detalle/se-registra-rina-en-penal-del-topo-chico-460670/";
			System.out.println(UtilPlem.isEstado(url));
		} catch (Exception e) {
			System.out.println("Error main: "+e.getMessage());
		}
	}
	public ParametrosPlemDTO obtenerPropiedades(String properties, String sufijo) {
		ParametrosPlemDTO parametrosDTO = new ParametrosPlemDTO();		 
		try {	    		
			Properties propsTmp = new Properties();
		    propsTmp.load(this.getClass().getResourceAsStream( "/general.properties" ));
		    String ambiente = propsTmp.getProperty("ambiente");
		    String rutaProperties = propsTmp.getProperty(properties.replace("ambiente", ambiente));			
			Properties props = new Properties();
			props.load(new FileInputStream(new File(rutaProperties)));
			parametrosDTO.setIdCliente(props.getProperty("idCliente"));
			parametrosDTO.setIdSolicitud(props.getProperty("idSolicitud-"+sufijo.toUpperCase()));
			parametrosDTO.setPwd(props.getProperty("pwd"));
			parametrosDTO.setJsonPLEM(props.getProperty("jsonPLEM-"+sufijo.toUpperCase()));
			parametrosDTO.setWSPLEM(props.getProperty("WSPLEM-"+sufijo.toUpperCase()));
			parametrosDTO.setMetodoEnvioPlem(props.getProperty("metodoEnvioWS-"+sufijo.toUpperCase()));
			parametrosDTO.setEstructuraSeccion(props.getProperty("estructuraSeccion"));
			parametrosDTO.setURL_WS_BASE(props.getProperty("URL_WS_BASE"));
			parametrosDTO.setDominio(props.getProperty("dominio"));
			parametrosDTO.setListEmailsPrueba(props.getProperty("listEmailsPrueba"));
			parametrosDTO.setArchDestinatario(props.getProperty("archDestinatario"));
			parametrosDTO.setxTarget(props.getProperty("xTarget"));
		} catch (Exception ex) {
			parametrosDTO = new ParametrosPlemDTO();
			logger.error("No se encontro el Archivo de propiedades: ", ex);			
		}
		return parametrosDTO;
    }
	
	public  String getJSON(ParametrosPlemDTO parametrosDTO, ArrayList<NotaDTO> listaNotas, String audiencia){
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			String secciones="";
			String JSONPlem = parametrosDTO.getJsonPLEM();
			JSONPlem = JSONPlem.replace("$ID_CLIENTE$", parametrosDTO.getIdCliente());
			JSONPlem = JSONPlem.replace("$ID_SOLICITUD$", parametrosDTO.getIdSolicitud());
			JSONPlem = JSONPlem.replace("$PASSWORD$", parametrosDTO.getPwd());
			if(audiencia.equalsIgnoreCase("T")){
				String patron="$correoDestino$|1,2,3,4,5,6,7,8,9,10||||\n";
				//NOMBRE|APPATERNO|APMATERNO|
				String [] listEmails=parametrosDTO.getListEmailsPrueba().split("\\|");
				StringBuffer listaCorreos=new StringBuffer();
				for (String email : listEmails) {
					listaCorreos.append(StringEscapeUtils.escapeJava(patron.replace("$correoDestino$", email)));
				}
				String archivoDestinatario=parametrosDTO.getArchDestinatario();
				archivoDestinatario=archivoDestinatario.replace("$LIST_EMAILS$", StringEscapeUtils.escapeJava("\n") +listaCorreos.toString());
				JSONPlem = JSONPlem.replace("$ARCH_DEST$", archivoDestinatario);
			}
				
			//Recorremos las notas
			for (int i = 0; i < listaNotas.size(); i++) {
				String urlSeccion="";
				//urlSeccion+=noticiaObject.getString("fcUrlNota").substring(0, noticiaObject.getString("fcUrlNota").indexOf("detalle"));
				int index= i + 1;
				NotaDTO noticiaDTO=listaNotas.get(i);
				String estructuraSeccion = parametrosDTO.getEstructuraSeccion().replaceAll("\n", "");
				String url="";
				if(noticiaDTO.getFcNombre().contains("http:") || noticiaDTO.getFcNombre().contains("https:")){
					url=noticiaDTO.getFcNombre();
					urlSeccion=noticiaDTO.getFcLinkDetalle().substring(0, noticiaDTO.getFcLinkDetalle().indexOf("detalle"));
				}else{
					url=noticiaDTO.getFcLinkDetalle().replace("especialess", "especiales");
					urlSeccion=url.substring(0, url.indexOf("detalle"));
					if(UtilPlem.isEstado(urlSeccion)){
						urlSeccion="http://www.unotv.com/noticias/estados/";
					}
				}
				
			
				estructuraSeccion = estructuraSeccion.replace("$URL_NOTA$", url);
				estructuraSeccion = estructuraSeccion.replace("$ID_SECCION$", String.valueOf(index));
				estructuraSeccion = estructuraSeccion.replace("$IMG_PRINCIPAL$", parametrosDTO.getDominio() + noticiaDTO.getFcImgPrincipal());
				estructuraSeccion = estructuraSeccion.replace("$TITULO_NOTA$", StringEscapeUtils.escapeHtml(noticiaDTO.getFcTitulo()));
				estructuraSeccion = estructuraSeccion.replace("$FECHA$", noticiaDTO.getFcFechaPublicacion());
				estructuraSeccion = estructuraSeccion.replace("$SECCION$", StringEscapeUtils.escapeHtml(noticiaDTO.getFcDescripcionCategoria()));
				estructuraSeccion = estructuraSeccion.replace("$DESCRIPCION_CORTA$", StringEscapeUtils.escapeHtml(noticiaDTO.getFcDescripcion()));
				estructuraSeccion = estructuraSeccion.replace("$FECHA_CORTA$", dateFormat.format(date));
				estructuraSeccion = estructuraSeccion.replace("$URL_SECCION$", urlSeccion);
				//sbSecciones.append(estructuraSeccion);
				secciones=secciones+estructuraSeccion;

			}
			
			//JSONPlem=JSONPlem.replace("$SECCIONES$", secciones.toString().replaceAll("\n", ""));
			secciones=StringEscapeUtils.escapeJava(secciones);
			JSONPlem=JSONPlem.replace("$SECCIONES$", secciones.toString().replaceAll("\n", ""));
			
			
			JSONPlem=JSONPlem.replaceAll("\n", "");
			
			return JSONPlem;
		} catch (Exception e) {
			logger.error("Exception en getJSON: ",e);
			StringBuffer jsonError= new StringBuffer();
			jsonError.append("{");
			jsonError.append("	\"idCliente\": \"29\",");
			jsonError.append("	\"idSolicitud\": \"250\",");
			jsonError.append("	\"pwd\": \"hjdsajhja1\",");
			jsonError.append("	\"contenidoArchivoTxtXml\": \"\"");
			jsonError.append("}");
			return	jsonError.toString();
		}
	}
	
	private static boolean isEstado(String url){
		boolean esEstado=false;
		
		if(url.contains("aguascalientes"))
			esEstado=true;
		else if(url.contains("baja-california"))
					esEstado=true;
		else if(url.contains("baja-california-sur"))
					esEstado=true;
		else if(url.contains("campeche"))
					esEstado=true;
		else if(url.contains("chiapas"))
					esEstado=true;
		else if(url.contains("chihuahua"))
					esEstado=true;
		else if(url.contains("coahuila"))
					esEstado=true;
		else if(url.contains("colima"))
					esEstado=true;
		else if(url.contains("distrito-federal"))
					esEstado=true;
		else if(url.contains("durango"))
					esEstado=true;
		else if(url.contains("estado-de-mexico"))
					esEstado=true;
		else if(url.contains("guanajuato"))
					esEstado=true;
		else if(url.contains("guerrero"))
					esEstado=true;
		else if(url.contains("hidalgo"))
					esEstado=true;
		else if(url.contains("jalisco"))
					esEstado=true;
		else if(url.contains("michoacan"))
					esEstado=true;
		else if(url.contains("morelos"))
					esEstado=true;
		else if(url.contains("nayarit"))
					esEstado=true;
		else if(url.contains("nuevo-leon"))
					esEstado=true;
		else if(url.contains("oaxaca"))
					esEstado=true;
		else if(url.contains("puebla"))
					esEstado=true;
		else if(url.contains("queretaro"))
					esEstado=true;
		else if(url.contains("quintana-roo"))
					esEstado=true;
		else if(url.contains("san-luis-potosi"))
					esEstado=true;
		else if(url.contains("sinaloa"))
					esEstado=true;
		else if(url.contains("sonora"))
					esEstado=true;
		else if(url.contains("tabasco"))
					esEstado=true;
		else if(url.contains("tamaulipas"))
					esEstado=true;
		else if(url.contains("tlaxcala"))
					esEstado=true;
		else if(url.contains("veracruz"))
					esEstado=true;
		else if(url.contains("yucatan"))
					esEstado=true;
		else if(url.contains("zacatecas"))
					esEstado=true;
		return esEstado;
	}
	
	private String cambiaCaracteres(String texto) {
		
		texto = texto.replaceAll("&#8220;","&#34;");        
        texto = texto.replaceAll("&#8221;","&#34;");
		
		texto = texto.replaceAll("\"", "&#34;");
		texto = texto.replaceAll("'", "&#39;");
		texto = texto.replaceAll("á", "&#225;");
        texto = texto.replaceAll("é", "&#233;");
        texto = texto.replaceAll("í", "&#237;");
        texto = texto.replaceAll("ó", "&#243;");
        texto = texto.replaceAll("ú", "&#250;");  
        texto = texto.replaceAll("Á", "&#193;");
        texto = texto.replaceAll("É", "&#201;");
        texto = texto.replaceAll("Í", "&#205;");
        texto = texto.replaceAll("Ó", "&#211;");
        texto = texto.replaceAll("Ú", "&#218;");
        texto = texto.replaceAll("Ñ", "&#209;");
        texto = texto.replaceAll("ñ", "&#241;");        
        texto = texto.replaceAll("ª", "&#170;");          
        texto = texto.replaceAll("ä", "&#228;");
        texto = texto.replaceAll("ë", "&#235;");
        texto = texto.replaceAll("ï", "&#239;");
        texto = texto.replaceAll("ö", "&#246;");
        texto = texto.replaceAll("ü", "&#252;");    
        texto = texto.replaceAll("Ä", "&#196;");
        texto = texto.replaceAll("Ë", "&#203;");
        texto = texto.replaceAll("Ï", "&#207;");
        texto = texto.replaceAll("Ö", "&#214;");
        texto = texto.replaceAll("Ü", "&#220;");
        texto = texto.replaceAll("¿", "&#191;");
        texto = texto.replaceAll("“", "&#8220;");        
        texto = texto.replaceAll("”", "&#8221;");
        texto = texto.replaceAll("‘", "&#8216;");
        texto = texto.replaceAll("’", "&#8217;");
        texto = texto.replaceAll("…", "...");	 
        
        texto = texto.replaceAll("¡", "&#161;");
        texto = texto.replaceAll("¿", "&#191;");
        texto = texto.replaceAll("°", "&#176;");
        texto = texto.replaceAll("–", "&#8211;");
        texto = texto.replaceAll("—", "&#8212;");
		return texto;
	}

}
