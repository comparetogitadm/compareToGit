package org.bcjj.gitCompare.gui;

import org.bcjj.gitCompare.GitFileVersionInfo;

public interface ContenedorHistorico {

	public void onHistoricoSeleccionado(Historico historico);

	public void compareFileToGitVersion(GitFileVersionInfo gitFileVersionInfo);

	public void compareGitVersionConFichEnDirectorioGit(GitFileVersionInfo gitFileVersionInfo);
	
}
