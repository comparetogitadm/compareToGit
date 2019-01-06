package org.bcjj.gitCompare;

public class FileInTreeInfo {

	String pathName;
	EstadoProcesado estadoProcesado=EstadoProcesado.SinRevisar;
	Diferencia diferencia=null;
	
	public FileInTreeInfo(String pathName) {
		this.pathName=pathName;
	}
	
	public String getPathName() {
		return pathName;
	}
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	public EstadoProcesado getEstadoProcesado() {
		return estadoProcesado;
	}
	public void setEstadoProcesado(EstadoProcesado estadoProcesado) {
		this.estadoProcesado = estadoProcesado;
	}
	public Diferencia getDiferencia() {
		return diferencia;
	}
	public void setDiferencia(Diferencia diferencia) {
		this.diferencia = diferencia;
	}
	
}
