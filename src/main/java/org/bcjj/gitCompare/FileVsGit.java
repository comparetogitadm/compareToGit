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
				long milisecs=1000L*revCommit.getCommitTime();// commit time, expressed as seconds since the epoch
				String authorName=revCommit.getAuthorIdent().getName();
				String authorEmail=revCommit.getAuthorIdent().getEmailAddress();
				
				System.out.println("revCommit:"+revCommit+"    commitTime:"+revCommit.getCommitTime());
			
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
				//PONGO LA VERSION BASE 
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
	
	
	
	public static void main(String [] s) throws Exception {
		

		
		//D:\linuxKernel-git\linux
		//  git log --name-only --pretty="format:" | sed '/^\s*$/'d | sort | uniq -c | sort -r >> commitsEnFicheros.txt
/*
8408 MAINTAINERS
3350 drivers/gpu/drm/i915/i915_drv.h
		Revision: d53b8e19c24bab37f72a2fc4b61d6f4d77b84ab4   Date: 11/05/2022 3:00:14	Message:Merge tag 'drm-intel-next-2022-05-06' of git://anongit.freedesktop.org/drm/drm-intel into drm-next
		Revision: 448a54ace4bb20216f5bfcecf272871d387d03dd   Date: 02/05/2022 18:34:07	Message:drm/i915/pvc: add initial Ponte Vecchio definitions
1001 fs/ext4/inode.c
 */
		
		
/*
 * 
 * 
[alias]
  ver = log --pretty=format:'%h - %an, local:%ad : %s  %d' --date=format:%Y%m%d.%H%M%S
  verline = log --pretty=format:'%h%x09%an%x09%ad%x09%s' --date=format:%Y%m%d.%H%M%S
  
  
yo7@DESKTOP-E6S3NVQ MINGW64 /d/linuxKernel-git/linux (master)

$ git verline -10 -- fs/ext4/inode.c
*1* 8d5459c11f54    Jan Kara        20220520.131402 ext4: improve write performance with disabled delalloc
*2* fdaf9a5840ac    Linus Torvalds  20220524.195507 Merge tag 'folio-5.19' of git://git.infradead.org/users/willy/pagecache
*3* b10b6278ae17    Yang Li 20220505.065025 ext4: remove duplicated #include of dax.h in inode.c
*4* f87c7a4b084a    Baokun Li       20220428.214031 ext4: fix race condition between ext4_write and ext4_convert_inline_data
*5* 6493792d3299    Zhang Yi        20220424.220936 ext4: convert symlink external data block mapping to bdev
9558cf14e8d2    Zhang Yi        20220424.220935 ext4: add nowait mode for ext4_getblk()
f4534c9fc94d    Ye Bin  20220326.145351 ext4: fix warning in ext4_handle_inode_extension
68189fef88c7    Matthew Wilcox (Oracle) 20220501.010808 fs: Change try_to_free_buffers() to take a folio
c56a6eb03deb    Matthew Wilcox (Oracle) 20220501.004603 jbd2: Convert jbd2_journal_try_to_free_buffers to take a folio
3c402f1543cc    Matthew Wilcox (Oracle) 20220430.233338 ext4: Convert to release_folio


$ git verline -30 -- fs/ext4
1f3ddff37559    Xiang wangx     20220605.171503 ext4: fix a doubled word "need" in a comment
b55c3cd102a6    Zhang Yi        20220601.172717 ext4: add reserved GDT blocks check
bc75a6eb856c    Ding Xiang      20220530.180047 ext4: make variable "count" signed
cf4ff938b47f    Baokun Li       20220528.190016 ext4: correct the judgment of BUG in ext4_mb_normalize_request
a08f789d2ab5    Baokun Li       20220528.190015 ext4: fix bug_on ext4_mb_use_inode_pa
85456054e10b    Eric Biggers    20220525.210412 ext4: fix up test_dummy_encryption handling for new mount API
4efd9f0d120c    Shuqi Zhang     20220525.110120 ext4: use kmemdup() to replace kmalloc + memcpy
9b6641dd95a0    Ye Bin  20220525.092904 ext4: fix super block checksum incorrect after mount
*1* 8d5459c11f54    Jan Kara        20220520.131402 ext4: improve write performance with disabled delalloc
15baa7dcadf1    Zhang Yi        20220520.103216 ext4: fix warning when submitting superblock in ext4_commit_super()
48e02e611382    Wang Jianjian   20220520.102254 ext4: fix incorrect comment in ext4_bio_write_page()
*2* fdaf9a5840ac    Linus Torvalds  20220524.195507 Merge tag 'folio-5.19' of git://git.infradead.org/users/willy/pagecache
fea3043314f3    Linus Torvalds  20220524.190446 Merge tag 'ext4_for_linus' of git://git.kernel.org/pub/scm/linux/kernel/git/tytso/ext4
bd1b7c1384ec    Linus Torvalds  20220524.185235 Merge tag 'for-5.19-tag' of git://git.kernel.org/pub/scm/linux/kernel/git/kdave/linux
5f41fdaea63d    Eric Biggers    20220519.134437 ext4: only allow test_dummy_encryption when supported
d36f6ed761b5    Baokun Li       20220518.200816 ext4: fix bug_on in __es_tree_search
3ba733f879c2    Jan Kara        20220518.113329 ext4: avoid cycles in directory h-tree
*R* 46c116b920eb    Jan Kara        20220518.113328 ext4: verify dir block before splitting it
c878bea3c9d7    Theodore Ts'o   20220517.132755 ext4: filter out EXT4_FC_REPLAY from on-disk superblock field s_state
115cd47132d7    Linus Torvalds  20220523.135639 Merge tag 'for-5.19/block-2022-05-22' of git://git.kernel.dk/linux-block
ef09ed5d37b8    Ye Bin  20220516.202634 ext4: fix bug_on in ext4_writepages
72f63f4a7703    Ritesh Harjani  20220515.120748 ext4: refactor and move ext4_ioctl_get_encryption_pwsalt()
3030b59c8533    Ritesh Harjani  20220515.120747 ext4: cleanup function defs from ext4.h into crypto.c
b1241c8eb977    Ritesh Harjani  20220515.120746 ext4: move ext4 crypto code to its own file crypto.c
c069db76ed7b    Eric Biggers    20220513.161601 ext4: fix memory leak in parse_apply_sb_mount_options()
cb8435dc8ba3    Eric Biggers    20220510.113232 ext4: reject the 'commit' option on ext2 filesystems
*3* b10b6278ae17    Yang Li 20220505.065025 ext4: remove duplicated #include of dax.h in inode.c
*4* f87c7a4b084a    Baokun Li       20220428.214031 ext4: fix race condition between ext4_write and ext4_convert_inline_data
*5* 6493792d3299    Zhang Yi        20220424.220936 ext4: convert symlink external data block mapping to bdev
*6* 9558cf14e8d2    Zhang Yi        20220424.220935 ext4: add nowait mode for ext4_getblk()
 */
		
		String commitIdCreacionRama="ac27a0ec112a";
		String commitIdControl="46c116b920eb";
		
		GitRepo gitRepo=new GitRepo("D:/linuxKernel-git/linux",commitIdControl); 
		
		FileVsGit fVsGit=new FileVsGit(new File("c:/isTheSame.txt"), gitRepo, "fs/ext4/inode.c",false,"c:/temp/");
		fVsGit.getVersiones().forEach(gitFVInfo -> {
			System.out.println(gitFVInfo.getCommitId()+" f:"+gitFVInfo.getFechaYMDHMS()+" c:"+gitFVInfo.getShortMessage());
		});
		
	}
	
	
	
}
