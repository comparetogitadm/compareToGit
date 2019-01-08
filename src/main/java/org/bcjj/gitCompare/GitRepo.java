package org.bcjj.gitCompare;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class GitRepo {
	private String path;
	private FileRepository fileRepository;
	private Git git;
	private ObjectId lastCommitId;
	private String fromPartialCommitId;
	private ObjectId fromId; //MutableObjectId
	private String branchName;
	
	
	private String fromCommitShortMessage;
	private String fromCommitFullMessage;
	private long fromCommitMilisecs=0;
	private String fromCommitAuthorName;
	private String fromCommitAuthorEmail;
	private File tempDir;
	
	public GitRepo(String path,String fromPartialCommitId) throws Exception {
		this.path=path;
		
		fileRepository=new FileRepository(path + "/.git");
		git=new Git(fileRepository);
		lastCommitId = fileRepository.resolve(Constants.HEAD);
		if (lastCommitId==null) {
			throw new Exception(path+" no es un repositorio git. No se resuelve HEAD");
		}
		branchName=fileRepository.getBranch();
		//fromId=new MutableObjectId();
		//desde.fromString("31978578756cafa678eb7c86b1e96a4e9753440e");
		//fromId.fromString(fromCommitId);//sha1 40
		if (fromPartialCommitId==null || fromPartialCommitId.trim().equals("")) {
			fromId=lastCommitId;
			this.fromPartialCommitId=fromId.getName();
		} else {
			this.fromPartialCommitId=fromPartialCommitId;
			fromId = fileRepository.resolve(fromPartialCommitId);
		}
		
        try (RevWalk walk = new RevWalk(fileRepository)) {
            RevCommit commit = walk.parseCommit(fromId);
            String autor=commit.getAuthorIdent().getName();
            long milisecs=1000*commit.getCommitTime();// commit time, expressed as seconds since the epoch
            
            PersonIdent authorIdent = commit.getAuthorIdent();
            Date authorDate = authorIdent.getWhen();
            TimeZone authorTimeZone = authorIdent.getTimeZone();
            
            
            
    		fromCommitAuthorEmail=commit.getAuthorIdent().getEmailAddress();
    		fromCommitAuthorName=commit.getAuthorIdent().getName();
    		fromCommitFullMessage=commit.getFullMessage();
    		fromCommitShortMessage=commit.getShortMessage();
    		fromCommitMilisecs=authorDate.getTime();
    		
            walk.dispose();
        } 
		
		
		
		//ObjectId beforeFromId=fileRepository.resolve(fromCommitId+"^");
		//System.out.println("commit anterior:"+beforeFromId);
		
	}

	
	
	
	public String toString() {
		return getFromCommitInfo();
	}

	public String getFromCommitInfo() {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (fromId!=null) {
			String commitInfo=getFromCommitId().substring(0,8)+" - "+fromCommitAuthorName+ " - "+ sdf.format(new Date(fromCommitMilisecs))+" - "+fromCommitFullMessage;
			return commitInfo;
		}
		return "";
	}


	public String getBranchName() {
		return branchName;
	}

	public String getFromCommitShortMessage() {
		return fromCommitShortMessage;
	}

	public String getFromCommitFullMessage() {
		return fromCommitFullMessage;
	}

	public long getFromCommitMilisecs() {
		return fromCommitMilisecs;
	}

	public String getFromCommitAuthorName() {
		return fromCommitAuthorName;
	}

	public String getFromCommitAuthorEmail() {
		return fromCommitAuthorEmail;
	}


	public void outputGitFile(String commitId, String filePath, OutputStream outputStream) throws Exception {
		
		MutableObjectId commitIdObjId=new MutableObjectId();
		//desde.fromString("31978578756cafa678eb7c86b1e96a4e9753440e");
		commitIdObjId.fromString(commitId);//sha1 40		
		
        // find the HEAD
		Repository repository=getFileRepository();
		
        //ObjectId commitId = repository.resolve(Constants.HEAD);

        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(commitIdObjId);
            // and using commit's tree find the path
            RevTree tree = commit.getTree();
            System.out.println("Having tree: " + tree);

            // now try to find a specific file
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                treeWalk.setFilter(PathFilter.create(filePath));
                if (!treeWalk.next()) {
                    throw new IllegalStateException("Did not find expected file '"+filePath+"'");
                }

                ObjectId objectId = treeWalk.getObjectId(0);
                ObjectLoader loader = repository.open(objectId);

                // and then one can the loader to read the file
                loader.copyTo(outputStream);
            }

            revWalk.dispose();
        }
	}
	
	
	
	public String getPath() {
		return path;
	}

	public FileRepository getFileRepository() {
		return fileRepository;
	}

	public Git getGit() {
		return git;
	}

	public ObjectId getLastCommitId() {
		return lastCommitId;
	}

	public String getFromPartialCommitId() {
		return fromPartialCommitId;
	}

	public ObjectId getFromId() {
		return fromId;
	}

	public String getFromCommitId() {
		return fromId.getName();
	}




	public void setTempDir(File tempDir) {
		this.tempDir=tempDir;
	}

	public File getTempDir() {
		return tempDir;
	}

	
}
