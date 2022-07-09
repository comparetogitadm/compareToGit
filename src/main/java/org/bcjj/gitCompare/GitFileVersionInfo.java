package org.bcjj.gitCompare;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GitFileVersionInfo {
	private String commitId;
	private String shortMessage;
	private String fullMessage;
	private long milisecs=0;
	private String authorName;
	private String authorEmail;
	private FileVsGit fileVsGit;
	private boolean realCommitId; //no es un fichero de ese commit, pero refleja como esta el fichero en ese commit
	private boolean base;
	private File tempFile;

	public GitFileVersionInfo(FileVsGit fileVsGit,String commitId, String shortMessage, String fullMessage, long milisecs, String authorName, String authorEmail, boolean realCommitId, boolean base) {
		super();
		this.fileVsGit=fileVsGit;
		this.commitId = commitId;
		this.shortMessage = shortMessage;
		this.fullMessage = fullMessage;
		this.milisecs = milisecs;
		this.authorName = authorName;
		this.authorEmail = authorEmail;
		this.realCommitId=realCommitId;
		this.base=base;
	}

	public boolean isBase() {
		return base;
	}

	public boolean isRealCommitId() {
		return realCommitId;
	}

	public void outputGitFile(OutputStream outputStream) throws Exception {
		fileVsGit.getGitRepo().outputGitFile(commitId, fileVsGit.getGitFile(), outputStream);
	}
	
	public FileVsGit getFileVsGit() {
		return fileVsGit;
	}

	public String getCommitId() {
		return commitId;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public String getFullMessage() {
		return fullMessage;
	}

	public long getMilisecs() {
		return milisecs;
	}

	public String getAuthorName() {
		return authorName;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}
	
	public Date getFecha() {
		return new Date(milisecs);
	}
	
	public String getFechaYMDHMS() {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(getFecha());
	}
	public String getFechaComprimidaYMDHMS() {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd-HHmmss");
		return sdf.format(getFecha());
	}
	
	public File setTempFile_(File file) throws Exception {
		tempFile=file;
		createTempFile();
		return tempFile;
	}
	
	private void createTempFile() throws Exception {
		tempFile.getParentFile().mkdirs();
		FileOutputStream fos=new FileOutputStream(tempFile);
		try {
			outputGitFile(fos);
		} catch (Exception r) {
			tempFile=new File(tempFile.getPath()+"--err");
			FileWriter fw=new FileWriter(tempFile);
			fw.write("error obteniendo fichero "+fileVsGit.getGitFile()+" idCommit:"+commitId+"  Posiblemente borrado");
			fw.close();
		} finally {
			try {
				fos.close();
			} catch (Exception r) {
				//ignorar
			}
		}
	}
	
	public File getTempFile() {
		return tempFile;
	}
	
	
	public static void main(String [] s) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		int commitTimeInt=1547856139; 
		Date fechaInt=new  Date(commitTimeInt*1000);
		
		long commitTimeLong=1547856139; 
		Date fechaLong=new  Date(commitTimeLong*1000);

		System.out.println("espero:2019/01/19 01:02:19    MAL:fechaInt:"+sdf.format(fechaInt)+"  BIEN:fechaLong:"+sdf.format(fechaLong));
	}
	
}