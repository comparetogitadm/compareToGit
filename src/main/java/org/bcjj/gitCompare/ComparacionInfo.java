package org.bcjj.gitCompare;

public class ComparacionInfo {
//comp3-F-V(B)G-FGD->FGD
	EstadoComparacion F_FGD; //   F_FGD : Fichero  vs  Fichero Directorio Git
	EstadoComparacion VG_FGD; //  VG_FGD : (ultima) Version Git (tmp)   vs   Fichero Directorio Git
	EstadoComparacion F_VG; //    F_VG : Fichero   vs   (ultima) Version Git (tmp) --  hace falta?
	
	// F_FGD : Fichero vs Fichero Directorio Git -- VG_FGD : (ultima) Version Git (tmp) vs Fichero Directorio Git -- F_VG : Fichero vs (ultima) Version Git (tmp)
	
	
	public EstadoComparacion getF_FGD() {
		return F_FGD;
	}
	public void setF_FGD(EstadoComparacion f_FGD) {
		F_FGD = f_FGD;
	}
	public EstadoComparacion getVG_FGD() {
		return VG_FGD;
	}
	public void setVG_FGD(EstadoComparacion vG_FGD) {
		VG_FGD = vG_FGD;
	}
	public EstadoComparacion getF_VG() {
		return F_VG;
	}
	public void setF_VG(EstadoComparacion f_VG) {
		F_VG = f_VG;
	}
	
	
	
}
