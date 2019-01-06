package org.bcjj.gitCompare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
	
	public FileVsGit(File fich, GitRepo gitRepo, String gitFile,boolean borrar,String tempFichBase) {
		super(fich.getName());
		this.fich = fich;
		this.gitRepo = gitRepo;
		this.gitFile = gitFile;
		this.borrar=borrar;
		this.tempFichBase=tempFichBase;
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

	public void check() {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	
	
	
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
