package mx.com.amx.unotv.backoffice.dto;

public class PruebaDTO {
	private String fcIdContenido;
	private String fcTitulo;
	private String fcTipoSeccion;
	private String fcSeccion;
	private String fcIdCategoria;
	private String fcNombre;
	
	private String htmlAMP;
	
	@Override
	public String toString() {
		String NEW_LINE = System.getProperty("line.separator");
		StringBuffer result=new StringBuffer();
		result.append(" [Begin of Class] " + NEW_LINE);
		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(" fcIdContenido: _" + this.getFcIdContenido() + "_" + NEW_LINE);
		result.append(" fcIdCategoria: _" + this.getFcIdCategoria() + "_" + NEW_LINE);
		result.append(" fcNombre: _" + this.getFcNombre() + "_" + NEW_LINE);
		result.append(" fcTitulo: _" + this.getFcTitulo() + "_" + NEW_LINE);
		result.append(" fcTipoSeccion: _" + this.getFcTipoSeccion() + "_" + NEW_LINE);
		result.append(" fcSeccion: _" + this.getFcSeccion() + "_" + NEW_LINE);
		result.append(" htmlAMP: _" + this.getHtmlAMP()+ "_" + NEW_LINE);
		result.append(" [End of Class] " + NEW_LINE);
		result.append("}");
		NEW_LINE = null;

		return result.toString();
	}
	
	/**
	 * @return the fcIdContenido
	 */
	public String getFcIdContenido() {
		return fcIdContenido;
	}

	/**
	 * @param fcIdContenido the fcIdContenido to set
	 */
	public void setFcIdContenido(String fcIdContenido) {
		this.fcIdContenido = fcIdContenido;
	}
	/**
	 * @return the fcTitulo
	 */
	public String getFcTitulo() {
		return fcTitulo;
	}
	/**
	 * @param fcTitulo the fcTitulo to set
	 */
	public void setFcTitulo(String fcTitulo) {
		this.fcTitulo = fcTitulo;
	}
	/**
	 * @return the fcTipoSeccion
	 */
	public String getFcTipoSeccion() {
		return fcTipoSeccion;
	}
	/**
	 * @param fcTipoSeccion the fcTipoSeccion to set
	 */
	public void setFcTipoSeccion(String fcTipoSeccion) {
		this.fcTipoSeccion = fcTipoSeccion;
	}
	/**
	 * @return the fcSeccion
	 */
	public String getFcSeccion() {
		return fcSeccion;
	}
	/**
	 * @param fcSeccion the fcSeccion to set
	 */
	public void setFcSeccion(String fcSeccion) {
		this.fcSeccion = fcSeccion;
	}
	/**
	 * @return the fcIdCategoria
	 */
	public String getFcIdCategoria() {
		return fcIdCategoria;
	}
	/**
	 * @param fcIdCategoria the fcIdCategoria to set
	 */
	public void setFcIdCategoria(String fcIdCategoria) {
		this.fcIdCategoria = fcIdCategoria;
	}
	/**
	 * @return the fcNombre
	 */
	public String getFcNombre() {
		return fcNombre;
	}
	/**
	 * @param fcNombre the fcNombre to set
	 */
	public void setFcNombre(String fcNombre) {
		this.fcNombre = fcNombre;
	}
	/**
	 * @return the htmlAMP
	 */
	public String getHtmlAMP() {
		return htmlAMP;
	}
	/**
	 * @param htmlAMP the htmlAMP to set
	 */
	public void setHtmlAMP(String htmlAMP) {
		this.htmlAMP = htmlAMP;
	}
	
	
}
