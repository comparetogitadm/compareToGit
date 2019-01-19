package org.bcjj.gitCompare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bcjj.gitCompare.gui.GitCompareMainWindow.Estado;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class FileVsGit extends FileInTreeInfo {
	private File fich;
	private GitRepo gitRepo;
	private String gitFile;
	private Estado estado;
	private List<GitFileVersionInfo> versiones;
	private boolean tieneBase=false;
	private GitFileVersionInfo baseVersion=null;
	private boolean borrar;
	private String tempFichBase;
	private String tempDirectoy;
	
	ComparacionInfo comparacionInfo=null;
	
	
	public FileVsGit(File fich, GitRepo gitRepo, String gitFile,boolean borrar,String tempDirectoy) {
		super(fich.getName());
		this.tempDirectoy=tempDirectoy;
		this.fich = fich;
		this.gitRepo = gitRepo;
		this.gitFile = gitFile;
		this.borrar=borrar;
		this.tempFichBase=tempDirectoy+gitFile;
		initialize();
	}

	

	
	
	public String getTempFichBase() {
		return tempFichBase;
	}

	public boolean isBorrar() {
		return borrar;
	}

	@Override
	public String getPathName() {
		return fich.getName()+" ("+getVersionesConCambios()+") ";
	}
	
	public int getVersionesConCambios() {
		int v=versiones.size();
		if (tieneBase) {
			v--;
		}
		return v;
	}
	
	public String toString() {
		String commits="";
		for (GitFileVersionInfo gitFileVersionInfo:versiones) {
			commits=commits+" :: "+gitFileVersionInfo.getCommitId();
		}
		return fich.getName()+" ("+getVersionesConCambios()+") "+commits;
	}

	protected void initialize() { // check()
		try {
			versiones=new ArrayList<GitFileVersionInfo>();
			//ObjectId objectId=new ObjectId(new_1, new_2, new_3, new_4, new_5); addRange(since, until).
			//MutableObjectId desde=new MutableObjectId();
			//desde.fromString("31978578756cafa678eb7c86b1e96a4e9753440e");
			//desde.fromString(fromFilter);//sha1 40
			//ObjectId until = git.getRepository().resolve("HEAD");
			
			Iterable<RevCommit> revCommitIterable=gitRepo.getGit().log().addPath(gitFile).addRange(gitRepo.getFromId(), gitRepo.getLastCommitId()).call();
			boolean buscarMas=true;
			for (RevCommit revCommit:revCommitIterable) {
				String commitId=revCommit.getId().getName();
				boolean base=false;
				if (commitId.equals(gitRepo.getFromId().getName())) {
					buscarMas=false;
					base=true;
					tieneBase=true;
				}
				
				String shortMessage=revCommit.getShortMessage();
				String fullMessage=revCommit.getFullMessage();
				long milisecs=1000*revCommit.getCommitTime();// commit time, expressed as seconds since the epoch
				String authorName=revCommit.getAuthorIdent().getName();
				String authorEmail=revCommit.getAuthorIdent().getEmailAddress();
			
				GitFileVersionInfo gitFileVersionInfo=new GitFileVersionInfo(this,commitId,shortMessage,fullMessage,milisecs,authorName,authorEmail,true,base);
				if (base) {
					baseVersion=gitFileVersionInfo;
				}
				versiones.add(gitFileVersionInfo);
			}
			
			if (buscarMas) {
				/*
				ObjectReader reader = gitRepo.getFileRepository().newObjectReader();
				RevWalk walk = new RevWalk(reader);
				RevCommit commit = walk.parseCommit(gitRepo.getFromId());
				RevTree tree = commit.getTree();
				TreeWalk treewalk = TreeWalk.forPath(reader, gitFile, tree);
				*/
				try {
					GitFileVersionInfo gitFileVersionInfo=new GitFileVersionInfo(this,gitRepo.getFromId().getName(),gitRepo.getFromCommitShortMessage(),gitRepo.getFromCommitFullMessage(),gitRepo.getFromCommitMilisecs(),gitRepo.getFromCommitAuthorName(),gitRepo.getFromCommitAuthorEmail(),false,true);
					ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
					gitFileVersionInfo.outputGitFile(byteArrayOutputStream);
					versiones.add(gitFileVersionInfo);//solo la añadimos si existe.
					tieneBase=true;
					baseVersion=gitFileVersionInfo;
				} catch (Exception ignore) {
					
				}
			}
			
			
			if (versiones.size()==0) {
				//fichero nuevo
			}
			
			inicializaFicherosTemporales();
			
			evaluaComparacionInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void inicializaFicherosTemporales() throws Exception {
		
		File f=new File(tempFichBase);
		String soloNombre=f.getName();
		String ext="";
		if (soloNombre.lastIndexOf(".")>0) { //$NON-NLS-1$
			ext=soloNombre.substring(soloNombre.lastIndexOf(".")); //incluye el punto //$NON-NLS-1$
		}
		List<GitFileVersionInfo> versionesRev=new ArrayList<GitFileVersionInfo>(versiones);
		Collections.reverse(versionesRev);
		int i=0;
		for (GitFileVersionInfo version:versionesRev) {
			i++;
			String numVer = String.format("%03d", i); //$NON-NLS-1$
			
			String tempFile=tempDirectoy+gitFile+"--"+numVer+"--"+version.getCommitId().substring(0, 6)+"--"+version.getFechaComprimidaYMDHMS(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (version.isBase()) {
				tempFile=tempFile+"-base"; //$NON-NLS-1$
			}
			tempFile=tempFile+ext;
			version.setTempFile_(new File(tempFile));
		}
	}
	
	
	public ComparacionInfo evaluaComparacionInfo() {
		File ultimaVersionEnGit=null;
        if (this.getVersiones().size()>0) {
        	ultimaVersionEnGit=this.getVersiones().get(0).getTempFile();
        }
        File ficheroDirectorioGit=null;
        if (this.getFileInGit()!=null && !this.getFileInGit().trim().equals("")) {
        	ficheroDirectorioGit=new File(this.getFileInGit());
		}
        File fichero=null;
        if (this.getFich()!=null && !this.getFich().getName().endsWith("-del")) {
        	fichero=this.getFich();
        }
        
		comparacionInfo=new ComparacionInfo();
		comparacionInfo.setF_FGD(EstadoComparacion.obtenComparacion(fichero,ficheroDirectorioGit));
		comparacionInfo.setF_VG(EstadoComparacion.obtenComparacion(fichero,ultimaVersionEnGit));
		comparacionInfo.setVG_FGD(EstadoComparacion.obtenComparacion(ultimaVersionEnGit,ficheroDirectorioGit));
        return comparacionInfo;
        
	}
	

	public ComparacionInfo getComparacionInfo() {
		return comparacionInfo;
	}

	/*
	public void setComparacionInfo(ComparacionInfo comparacionInfo) {
		this.comparacionInfo = comparacionInfo;
	}
	*/
	
	
	public GitFileVersionInfo getBaseVersion() {
		return baseVersion;
	}
	
	
	public boolean isTieneBase() {
		return tieneBase;
	}

	public File getFich() {
		return fich;
	}

	public GitRepo getGitRepo() {
		return gitRepo;
	}

	public String getGitFile() {
		return gitFile;
	}
	
	public String getFileInGit() {
		return gitRepo.getPath()+"/"+gitFile;
	}

	public List<GitFileVersionInfo> getVersiones() {
		return versiones;
	}
}
