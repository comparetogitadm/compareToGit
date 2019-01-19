package org.bcjj.gitCompare;

import java.io.File;

import org.apache.commons.io.FileUtils;

public enum EstadoComparacion {
	OrigenNoExisteDestinoSi,OrigenExisteDestinoNo,Iguales,Distintos,Error;

	public static EstadoComparacion obtenComparacion(File origen, File destino) {
		if (origen==null && destino==null) {
			return Iguales;
		}
		if (origen==null && destino!=null) {
			return OrigenNoExisteDestinoSi;
		}
		if (origen!=null && destino==null) {
			return OrigenExisteDestinoNo;
		}
		try {
			boolean iguales=FileUtils.contentEqualsIgnoreEOL(origen, destino,null);
			if (iguales) {
				return EstadoComparacion.Iguales;
			} else {
				return EstadoComparacion.Distintos;
			}
		} catch (Exception r) {
			return Error;
		}
	}
}
