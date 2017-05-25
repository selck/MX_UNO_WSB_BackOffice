package mx.com.amx.unotv.backoffice.controller;

import java.util.ArrayList;

import mx.com.amx.unotv.backoffice.bo.BackOfficeBO;
import mx.com.amx.unotv.backoffice.dto.NotaDTO;
import mx.com.amx.unotv.backoffice.dto.ParametrosAMP;
import mx.com.amx.unotv.backoffice.dto.ParametrosPlemDTO;
import mx.com.amx.unotv.backoffice.dto.PushAMP;
import mx.com.amx.unotv.backoffice.dto.RespuestaWSAMP;
import mx.com.amx.unotv.backoffice.dto.RespuestaWSPlem;
import mx.com.amx.unotv.backoffice.util.ConsumeWS;
import mx.com.amx.unotv.backoffice.util.UtilPlem;
import mx.com.amx.unotv.backoffice.util.UtilPushAMP;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("backOfficeController")
public class BackOfficeController {
	
	private static Logger logger=Logger.getLogger(BackOfficeController.class);
	private BackOfficeBO backOfficeBO;
			
	@RequestMapping( value = "sendPushAMP" , method=RequestMethod.POST , headers="Accept=application/json" )
	@ResponseBody
	public RespuestaWSAMP sendPushAMP (@RequestBody PushAMP pushAMP) {
		
		logger.info("===== sendPushAMP =====");
		logger.info(pushAMP.toString());
		RespuestaWSAMP respuestaWSAMP=new RespuestaWSAMP();
		UtilPushAMP utilPushAMP=new UtilPushAMP();
		
		try {
			ParametrosAMP parametrosAMP=utilPushAMP.obtenerPropiedades("ambiente.resources.properties");
			respuestaWSAMP=utilPushAMP.sendPushAMP(UtilPushAMP.generateFeedContent(pushAMP),parametrosAMP );
		} catch (Exception e) {
			logger.error("Error sendPushAMP[Controller] ",e);
			respuestaWSAMP.setCodigo("-1");
			respuestaWSAMP.setRespuesta(e.getMessage());
		}
		return respuestaWSAMP;
	}
		
	@RequestMapping( value = "callPlem" , method=RequestMethod.POST , headers="Accept=application/json" )
	@ResponseBody
	public RespuestaWSPlem callPlem (@RequestParam String idMagazine,@RequestParam String audiencia) {
		logger.info("===== callPlem =====");
		logger.info("idMagazine: "+idMagazine);
		logger.info("audiencia: "+audiencia);
		RespuestaWSPlem respuestaWSPlem=new RespuestaWSPlem();
		String jsonEnviar="", respuestaConsumeWS="";
		ParametrosPlemDTO parametrosPlemDTO=new ParametrosPlemDTO();
		UtilPlem utilPlem=new UtilPlem();
		try {
			
			parametrosPlemDTO=utilPlem.obtenerPropiedades("ambiente.resources.properties", audiencia);
			
			backOfficeBO=new BackOfficeBO(parametrosPlemDTO.getURL_WS_BASE());
			
			ArrayList<NotaDTO>listNotas=(ArrayList<NotaDTO>) backOfficeBO.getNotesPublished(idMagazine);
			
			if(listNotas!=null && listNotas.size()>0){
				logger.debug("listNotas.Size: "+listNotas.size());
				jsonEnviar=utilPlem.getJSON(parametrosPlemDTO, listNotas, audiencia);
				logger.debug("----jsonEnviar [INI]----");
				logger.debug(jsonEnviar);
				logger.debug("----jsonEnviar [END]----");
				logger.debug("WSPLEM: "+parametrosPlemDTO.getWSPLEM());
				logger.debug("metodoEnvioWS: "+parametrosPlemDTO.getMetodoEnvioPlem());
				//plemDTO.setJson(jsonEnviar);
				//plemDTO.setUrlWS(parametrosPlemDTO.getWSPLEM());
				//plemDTO.setMetodo(parametrosPlemDTO.getMetodoEnvioPlem());
				
				try {
					logger.info("=====ConsumeWS=====");
		    		ConsumeWS consume=new ConsumeWS(parametrosPlemDTO.getWSPLEM());
		    		//respuestaConsumeWS=consume.executeWS(parametrosPlemDTO.getMetodoEnvioPlem(), jsonEnviar);
		    		respuestaConsumeWS=consume.executeWS(parametrosPlemDTO, jsonEnviar);
		    		logger.info("Respuesta: "+respuestaConsumeWS);
		    		respuestaWSPlem.setRespuesta(respuestaConsumeWS);
		    		respuestaWSPlem.setCodigo("00");
				} catch ( Exception e ){
					logger.error("Error ConsumeWS: ",e);
					respuestaWSPlem.setCodigo("-1");
					respuestaWSPlem.setRespuesta(e.getMessage());
				}
			}
			
		} catch ( Exception e ){
			logger.error("Error callPlem[Controller] ",e);
			respuestaWSPlem.setCodigo("-1");
			respuestaWSPlem.setRespuesta(e.getMessage());
		}	 	
		
		return respuestaWSPlem;
	}
	
	/**
	 * @return the backOfficeBO
	 */
	public BackOfficeBO getBackOfficeBO() {
		return backOfficeBO;
	}
	/**
	 * @param backOfficeBO the backOfficeBO to set
	 */
	
	@Autowired
	public void setBackOfficeBO(BackOfficeBO backOfficeBO) {
		this.backOfficeBO = backOfficeBO;
	}
	
	
	
}
