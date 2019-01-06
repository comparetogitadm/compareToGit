package org.bcjj.gitCompare.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcjj.gitCompare.Diferencia;
import org.bcjj.gitCompare.EstadoProcesado;
import org.bcjj.gitCompare.FileInTreeInfo;
import org.bcjj.gitCompare.FileVsGit;
import org.bcjj.gitCompare.GitFileVersionInfo;
import org.bcjj.gitCompare.GitRepo;
import org.bcjj.gitCompare.gui.ComboUtil.ComboName;

import net.iharder.dnd.FileDrop;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

// obtener el commit de una fecha     git rev-list -1 --before="$DATE" master
/*
 * There is a setRetainBody(false) method you can use to discard the body of a commit if you don't need the author, committer or message information during the traversal. Examples of when you don't need this data is when you are only using the RevWalk to compute the merge base between branches, or to perform a task you would have used `git rev-list` with its default formatting for.

RevWalk walk = new RevWalk(repository);
walk.setRetainBody(false);
// ...

If you do need the body, consider extracting the data you need and then calling dispose() on the RevCommit, assuming you only need the data once and can then discard it. If you need to hang onto the data, you may find that JGit's internal representation uses less overall memory than if you held onto it yourself, especially if you want the full message. This is because JGit uses a byte[] internally to store the message in UTF-8. Java String storage would be bigger using UTF-16, assuming the message is mostly US-ASCII data.

RevWalk walk = new RevWalk(repository);
// more setup
Set<String> authorEmails = new HashSet<String>();

for (RevCommit commit : walk) {
	// extract the commit fields you need, for example:
	authorEmails.add(commit.getAuthorIdent().getEmailAddress());

 	commit.dispose();
}
walk.dispose();
 */

//git log --pretty=format:"%h - %an, local:%ad remote:%cd : %s  %d" --graph --all --date=format:%Y%m%d.%H%M%S

/* Beyond: 
Pair of files: Opens the specified files in the associated file view.  For example:
 BCompare.exe "C:\Left File.ext" "C:\Right File.ext"
comp:    BCompare.exe fichEnDirectorioFiles fichEnDirectorioGit
compVer: BCompare.exe fichEnDirectorioFiles versionEnGit
2ver:    BCompare.exe versionEnGit1 versionEnGit2

3 files
Opens a Text Merge view with the specified files in the left, right, and center panes.  For example:
 BCompare.exe C:\Left.ext C:\Right.ext C:\Center.ext

4 files
Opens a Text Merge view with the specified files in the left, right, center, and output panes.  For example:
 BCompare.exe C:\Left.ext C:\Right.ext C:\Center.ext C:\Output.ext
  
comp3: BCompare.exe ultimaVersionEnGit fichEnDirectorioFiles versionBaseEnGit fichEnDirectorioGit
 */

public class GitCompareMainWindow implements ContenedorHistorico {

	private JFrame frmGitcompare;
	private JPanel panelCabecera;
	private JSplitPane splitPaneCabecera;
	private JPanel panelGitDir;
	private JPanel mainPanelFilesDir;
	private JPanel panelGitSelec;
	private JPanel panelCommitInfo;
	private JPanel panelFilesDir;
	private JPanel panelPrj;
	private JLabel lblGit;
	private JTextField txtBranchname;
	private JLabel lblBranchname;
	private JTextField txtGitdirectory;
	private JLabel lblFilesDir;
	private JTextField txtFilesDir;
	private JButton btnOpenGitDir;
	private JButton btnOpenFilesDir;
	private JPanel panelResultados;
	private JSplitPane splitPaneResultados;
	private JPanel panelArbol;
	private JPanel panelContenedorHistoricos;
	private JScrollPane scrollPaneArbol;
	private JTree treeFiles;
	private JPanel panel_10;
	private JButton btnCheck;
	private DefaultTreeModel filesTreeModelRoot=null;
	private JPanel panelSelected;
	private JLabel lblFich;
	private JTextField txtFileSelected;
	private JSplitPane splitPaneGitFilter;
	private JPanel panelGitFilterCommit;
	private JPanel panelGitFilterUser;
	private JLabel lblCommitid;
	private JTextField txtCommitId;
	private JLabel lblUsername;
	private JTextField txtUserName;
	private JLabel label;
	private JLabel label_1;
	private JSplitPane splitPaneHistoricos;
	private JPanel panelDiferencias;
	private JScrollPane scrollPaneDiferencias;
	private JPanel panelHistoricos;
	private JScrollPane scrollPaneHistoricos;
	private JPanel panelListaHistoricos;
	private JPanel paneGitFilter;

	private Historico historicoSel1;
	private Historico historicoSel2;
	private JTextArea txtrTextareaOldversion;
	
	ImageIcon iconoSinConflictos;
	ImageIcon iconoConflictosMismoUsuario;
	ImageIcon iconoConflictosDistintoUsuario;
	ImageIcon iconoPendiente;
	ImageIcon iconoHecho;
	ImageIcon iconoPendiente10;
	ImageIcon iconoHecho10;
	
	private JButton btnRefresh;
	private JLabel lblPrjfile;
	private JPanel panelPrjButtons;
	private JButton btnOpenprj;
	private JComboBox<String> CmbProjectFile;
	private DefaultComboBoxModel<String> cmbProjectFileModel;

	private ComboUtil comboUtil=new ComboUtil("gitCompare", 10);
	private ComboName comboNameProject=new ComboName("project");
	private ComboName comboNameCompareApp=new ComboName("compareApp");
	private DefaultComboBoxModel<String> cmbFechaDesdeModel;
	private ComboName comboNameFecha=new ComboName("fromDate");
	private JButton btnMaps;
	private JButton btnSave;
	private String compareCommand;
	
	private List<String> reglasMapeo;
	private JPanel panel_8;
	private JTextField commitInfo;
	private JPanel panelBrachName;
	private JPanel panel_6;
	private JSplitPane splitPane_1;
	private JPanel panel_9;
	private JPanel panel_11;
	private JPanel panel_12;
	private JPanel panel_13;
	private JPanel panelAux;
	private JPanel panelAcciones;
	private JSplitPane splitPaneAux;
	private JPanel panelFechaFiles;
	private JPanel panelAuxDir;
	private JLabel lblFecha;
	private JComboBox<String> cmbFechaDesde;
	private JPanel panelAccionesSeleccion;
	private JButton btnCompareVsGit;
	private JLabel lblNewLabel;
	private JLabel lblAuxDir;
	private JTextField txtTempDir;
	private JButton btnComparebase;
	private JCheckBox chck2versions;
	private JButton btn2Versiones;
	private JButton btnCopy;
	private JButton botonOpcionCompare;
	
	private DefaultMutableTreeNode selectedNode;
	private JCheckBox chckbxDone;
	
	private boolean modoTratarHistoricosDoble=false;
	private JButton btnCompG;
	private JPanel panel;
	private JButton btnExplorerFich;
	private JButton btnExplorerTemp;
	private JButton btnExplorerFGD;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GitCompareMainWindow window = new GitCompareMainWindow();
					window.frmGitcompare.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GitCompareMainWindow() {
		initIcons();
		initialize();

		try {
			compareCommand=comboUtil.loadPreference(comboNameCompareApp);
		} catch (Exception e) {
			compareCommand="C:/Program Files (x86)/Beyond Compare 3/BCompare.exe";
			try {
				comboUtil.savePreference(compareCommand,comboNameCompareApp);
			} catch (IOException e1) {
				//ignorar
			}
		}
		
	}

	private void initIcons() {
		iconoSinConflictos = createImageIcon("/images/cart-go.png");
		iconoConflictosMismoUsuario = createImageIcon("/images/dialog-warning-3.png");
		iconoConflictosDistintoUsuario = createImageIcon("/images/arrow-divide2.png");
		iconoPendiente = createImageIcon("/images/edit-find-6.png");
		iconoHecho = createImageIcon("/images/dialog-ok-4.png");
		//iconoPendiente10=resize(iconoPendiente, 10);
		//iconoHecho10=resize(iconoHecho, 10);
	}

	

	
	
	
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path) {
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmGitcompare = new JFrame();
		frmGitcompare.setTitle("GitCompare");
		frmGitcompare.setBounds(100, 100, 920, 572);
		frmGitcompare.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGitcompare.getContentPane().add(getPanelCabecera(), BorderLayout.NORTH);
		frmGitcompare.getContentPane().add(getPanelResultados(), BorderLayout.CENTER);
		initFileDrop();
	}

	public void setVisible(boolean visible) {
		frmGitcompare.setVisible(true);
	}
	
	protected void checkStatus() throws Exception {
		
		FileInTreeInfo fileInTreeInfo=new FileInTreeInfo("");
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(fileInTreeInfo);
		getFilesTreeModel().setRoot(rootNode);
		getFilesTreeModel().reload();
		expandAll(getTreeFiles());		
		
		File dirFiles=new File(getTxtFilesDir().getText());
		if (!dirFiles.isDirectory()) {
			showMessage("el directorio de ficheros debe ser un directorio que exista");
			return;
		}
		Date fechaDesde=null;
		try {
			fechaDesde=getFecha();
		} catch (Exception r) {
			showMessage("la fecha no es correcta");
			return;
		}
		GitRepo gitRepo=null;
		try {
			gitRepo=new GitRepo(getTxtGitdirectory().getText(),getTxtCommitId().getText());
		} catch (Exception r) {
			showMessage("revisa los parametros de git (directorio o idCommit)");
			return;
		}
		showGitInfo(gitRepo);

		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd-HHmmss");
		Date ahora=new Date();
		File tempDir=new File("c:/temp/gitCompare-"+sdf.format(ahora));
		getTxtTempDir().setText(tempDir.getPath());
		gitRepo.setTempDir(tempDir);
		readTreeDir(dirFiles,gitRepo,fechaDesde,tempDir);
	}	
	
	protected void refreshBranch() {
		try {
			getTxtBranchname().setText("");
			getCommitInfo().setText("");
			GitRepo gitRepo=new GitRepo(getTxtGitdirectory().getText(),getTxtCommitId().getText());
			showGitInfo(gitRepo);
			
		} catch (Exception r) {
			showMessage("Error al obtener el branch: "+r);
		}
	}
	
	
	protected void showGitInfo(GitRepo gitRepo) {
		getTxtBranchname().setText(gitRepo.getBranchName());
		getCommitInfo().setText(gitRepo.getFromCommitInfo());
		getCommitInfo().setCaretPosition(0);
		getCommitInfo().setToolTipText(gitRepo.getFromCommitInfo());
	}
	
	protected void refreshMap() {
		
	}
	
	
	public enum Estado {
	    Sincronizado,NoSincronizado,NoExisteEnGit,ParaBorrar
	}
	
	private void openProject() throws IOException {
		String filenameValue=getProjectFile();
		Properties p=new Properties();
		InputStream r=null;
		reglasMapeo=new ArrayList<String>();
		try {
			r=new FileInputStream(filenameValue);
			p.load(r);
			String branchName=p.getProperty("branchName");
			getTxtFilesDir().setText(p.getProperty("filesDir"));
			getTxtGitdirectory().setText(p.getProperty("gitDir"));
			refreshBranch();
			if (!getTxtBranchname().getText().equals(branchName)) {
				showMessage("CUIDADO el branch no coincide, actual:"+getTxtBranchname().getText()+", esperado:"+branchName);
			}
			for (int i=0;i<1000;i++) {
				String x=p.getProperty("path."+i);
				if (x!=null) {
					reglasMapeo.add(x);
				}
			}
		} catch (Exception e) {
			showMessage("Error abriendo "+filenameValue);
		} finally {
			try {
				if (r!=null) {
					r.close();
				}
			} catch (IOException e) {
				//ignorar
			}
		}
	}
	

	protected void saveMaps() throws IOException {
		String filenameValue=getProjectFile();
		Properties p=new Properties();
		p.setProperty("branchName", getTxtBranchname().getText());
		p.setProperty("filesDir", getTxtFilesDir().getText());
		p.setProperty("gitDir", getTxtGitdirectory().getText());
		int i=0;
		for (String r:reglasMapeo) {
			p.setProperty("path."+i, r);
			i++;
		}
		FileOutputStream fileOutputStream=new FileOutputStream(filenameValue);
		p.save(fileOutputStream, "");
		fileOutputStream.close();
	}
	
	
	private void tratar2Historicos(Historico historico) {
		Color defaultBackgroundColor=this.getPanelCabecera().getBackground();
		if (historico==historicoSel1) {
			historico.setBackground(defaultBackgroundColor);
			historicoSel1=historicoSel2;
			historicoSel2=null;
		} else if (historico==historicoSel2) {
			historico.setBackground(defaultBackgroundColor);
			historicoSel2=null;
		} else {
			if (historicoSel2!=null) {
				historicoSel2.setBackground(defaultBackgroundColor);
			}
			historicoSel2=historicoSel1;
			historicoSel1=historico;
		}
		if (historicoSel1!=null) {
			historicoSel1.setBackground(Color.CYAN);
			GitFileVersionInfo gitFileVersionInfo=historicoSel1.getGitFileVersionInfo();
			ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
			try {
				gitFileVersionInfo.outputGitFile(byteArrayOutputStream);
				getTextareaOldversion().setText(byteArrayOutputStream.toString());
				getTextareaOldversion().setCaretPosition(0);
			} catch (Exception r) {
				getTextareaOldversion().setText("*ERROR ::"+r);
			}
		}
		if (historicoSel2!=null) {
			historicoSel2.setBackground(Color.BLUE);
		}			
	}
	

	private void tratar1Historico(Historico historico) {
		Color defaultBackgroundColor=this.getPanelCabecera().getBackground();
		if (historico==historicoSel1) {
			historico.setBackground(defaultBackgroundColor);
			historicoSel1=null;
		} else {
			if (historicoSel1!=null) {
				historicoSel1.setBackground(defaultBackgroundColor);
			}
			historicoSel1=historico;
		}
		if (historicoSel1!=null) {
			historicoSel1.setBackground(Color.CYAN);
			GitFileVersionInfo gitFileVersionInfo=historicoSel1.getGitFileVersionInfo();
			ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
			try {
				gitFileVersionInfo.outputGitFile(byteArrayOutputStream);
				getTextareaOldversion().setText(byteArrayOutputStream.toString());
				getTextareaOldversion().setCaretPosition(0);
			} catch (Exception r) {
				getTextareaOldversion().setText("*ERROR ::"+r);
			}
		}
	}
	
	@Override
	public void onHistoricoSeleccionado(Historico historico) {
		if (modoTratarHistoricosDoble) {
			tratar2Historicos(historico);
		} else {
			tratar1Historico(historico);
		}
	}

	
	
	private void readTreeDir(File dirFiles,GitRepo gitRepo,Date fechaDesde,File tempDir) throws Exception {
		FileInTreeInfo fileInTreeInfo=new FileInTreeInfo(dirFiles.getAbsolutePath());
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(fileInTreeInfo);
		getFilesTreeModel().setRoot(rootNode);
		String baseDirAbsolute = getDirectoryName(dirFiles);
		String tempDirectoy=getDirectoryName(tempDir);
		
		
		leeDirectorio(dirFiles,rootNode,baseDirAbsolute,gitRepo,fechaDesde,tempDirectoy);
		getFilesTreeModel().reload();
		expandAll(getTreeFiles());
	}

	private String getDirectoryName(File dir) {
		String dirAbsolute=dir.getAbsolutePath();
		dirAbsolute=StringUtils.replace(dirAbsolute,"\\","/");
		if (!dirAbsolute.endsWith("/")) {
			dirAbsolute=dirAbsolute+"/";
		}
		return dirAbsolute;
	}

	  public void expandAll(JTree tree) {
		    TreeNode root = (TreeNode) tree.getModel().getRoot();
		    expandAll(tree, new TreePath(root));
		  }

		  private void expandAll(JTree tree, TreePath parent) {
		    TreeNode node = (TreeNode) parent.getLastPathComponent();
		    if (node.getChildCount() >= 0) {
		      for (Enumeration e = node.children(); e.hasMoreElements();) {
		        TreeNode n = (TreeNode) e.nextElement();
		        TreePath path = parent.pathByAddingChild(n);
		        expandAll(tree, path);
		      }
		    }
		    tree.expandPath(parent);
		    // tree.collapsePath(parent);
		  }
	
	private void leeDirectorio(File dirFiles, DefaultMutableTreeNode padreNode,String baseDirAbsolute,GitRepo gitRepo,Date fechaDesde,String tempDirectoy) throws Exception {
		File [] fichs=dirFiles.listFiles();
		Arrays.sort(fichs, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				if (f1.isDirectory() && f2.isFile()) {
					return -1;
				}
				if (f1.isFile() && f2.isDirectory()) {
					return 1;
				}
				return f1.getName().compareTo(f2.getName());
			}
		});
		
		for (File fich:fichs) {
			DefaultMutableTreeNode node =null;
			if (fich.isFile() && esMasActual(fich,fechaDesde)) {
				String fileAbs=fich.getAbsolutePath();
				fileAbs=fileAbs.substring(baseDirAbsolute.length()); //como baseDirAbsolute termina forzosamente por /, fileAbs no comenzar� por /
				fileAbs=StringUtils.replace(fileAbs,"\\","/");
				boolean borrar=false;
				if (fileAbs.endsWith("-del")) {
					fileAbs=fileAbs.substring(0, fileAbs.length()-"-del".length());
					borrar=true;
				}
				String fileAbsMapeado=mapearReglas(fileAbs);  //  src/web/web-prod/srv/main.jsp
				if (fileAbsMapeado!="!") {
					String tempF=tempDirectoy+fileAbsMapeado;
					FileVsGit fileVsGit=new FileVsGit(fich,gitRepo,fileAbsMapeado,borrar,tempF);
					if (fileAbsMapeado.contains("codigo.java")) {
						System.out.println(fileAbsMapeado);
					}
					fileVsGit.check();
					List<GitFileVersionInfo> versiones=fileVsGit.getVersiones();
					if (versiones.size()==0) {
						//fichero nuevo
					}
					
					List<GitFileVersionInfo> versionesRev=new ArrayList<GitFileVersionInfo>(versiones);
					Collections.reverse(versionesRev);
					int i=0;
					for (GitFileVersionInfo version:versionesRev) {
						i++;
						String numVer = String.format("%03d", i);
						String tempFile=tempDirectoy+fileAbsMapeado+"--"+numVer+"--"+version.getCommitId().substring(0, 6)+"--"+version.getFechaComprimidaYMDHMS();
						if (version.isBase()) {
							tempFile=tempFile+"-base";
						}
						version.setTempFile(new File(tempFile));
					}
					
					
					if (fileVsGit.getVersionesConCambios()<=0) {
						fileVsGit.setDiferencia(Diferencia.SinNuevasVersiones);
					} else {
						boolean distintoAutor=false;
						for (GitFileVersionInfo version:versiones) {
							if (!version.isBase() && !version.getAuthorName().equals(getTxtUserName().getText())) {
								distintoAutor=true;
							}
						}
						if (distintoAutor) {
							fileVsGit.setDiferencia(Diferencia.NuevasVersionesDistintosAutores);
						} else {
							fileVsGit.setDiferencia(Diferencia.NuevasVersionesMismoAutor);
						}
					}
					node = new DefaultMutableTreeNode(fileVsGit);
					padreNode.add(node);
					actualizaConflictoEnNodosPadres(padreNode,fileVsGit.getDiferencia());
				}
			}
			if (fich.isDirectory()) {
				FileInTreeInfo fileInTreeInfo=new FileInTreeInfo(fich.getName());
				fileInTreeInfo.setDiferencia(Diferencia.SinNuevasVersiones);
				node = new DefaultMutableTreeNode(fileInTreeInfo);
				padreNode.add(node);
				leeDirectorio(fich, node,baseDirAbsolute,gitRepo,fechaDesde,tempDirectoy);
			}
			
		}
	}


	private boolean esMasActual(File fich, Date fechaDesde) {
		if (fechaDesde==null) {
			return true;
		}
		if (fich.lastModified()>=fechaDesde.getTime()) {
			return true;
		}
		return false;
	}

	private String trimPathSymbol(String exp) {
		exp=exp.trim();
		while (exp.startsWith("/")) {
			exp=exp.substring(1);
		}
		while (exp.endsWith("/")) {
			exp=exp.substring(0, exp.length()-1);
		}
		exp=exp.trim();
		return exp;
	}
	
	private String mapearReglas(String fileAbs) { //como baseDirAbsolute termina forzosamente por /, fileAbs no comenzar� por /
		fileAbs=StringUtils.replace(fileAbs, "\\", "/");
		if (reglasMapeo==null) {
			return fileAbs;
		}
		for (String s:reglasMapeo) {
			StringTokenizer st=new StringTokenizer(s,"|");
			String buscar=trimPathSymbol(st.nextToken())+"/";
			String mapear=trimPathSymbol(st.nextToken())+"/";
			if (fileAbs.startsWith(buscar)) {
				if (mapear.equals("!/")) {
					return "!";
				}
				String mapeado=fileAbs.substring(buscar.length());
				mapeado=mapear+mapeado;
				return mapeado;
			}
		}
		return fileAbs;
	}

	private void actualizaConflictoEnNodosPadres(DefaultMutableTreeNode padreNode, Diferencia diferencia) {
		if (padreNode!=null) {
			Object userObject=padreNode.getUserObject();
			boolean seguir=true;
			if (userObject instanceof FileInTreeInfo) {
				FileInTreeInfo fileInTreeInfo=(FileInTreeInfo)userObject;
				if (fileInTreeInfo.getDiferencia()==Diferencia.NuevasVersionesDistintosAutores) {
					seguir=false;
				} else if (fileInTreeInfo.getDiferencia()==diferencia) {
					seguir=false;
				} else if (fileInTreeInfo.getDiferencia()==Diferencia.SinNuevasVersiones) {
					fileInTreeInfo.setDiferencia(diferencia);
				} else if (fileInTreeInfo.getDiferencia()==Diferencia.NuevasVersionesMismoAutor && diferencia==Diferencia.NuevasVersionesDistintosAutores) {
					fileInTreeInfo.setDiferencia(diferencia);
				}
			}
			if(seguir && padreNode.getParent()!=null && padreNode.getParent() instanceof DefaultMutableTreeNode) {
				actualizaConflictoEnNodosPadres((DefaultMutableTreeNode)padreNode.getParent(), diferencia);
			}
		}
	}

	private JTree getTreeFiles() {
		if (treeFiles == null) {
			treeFiles = new JTree(getFilesTreeModel());
			GitCompareMainWindow esteGitCompareMainWindow=this;
			treeFiles.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			
			FileTreeCellRenderer fileTreeCellRenderer=new FileTreeCellRenderer( iconoSinConflictos,iconoConflictosMismoUsuario,iconoConflictosDistintoUsuario,iconoPendiente,iconoHecho,iconoPendiente10,iconoHecho10);
			
			treeFiles.setCellRenderer(fileTreeCellRenderer);
			
			treeFiles.addTreeSelectionListener(new TreeSelectionListener() {
	            @Override
	            public void valueChanged(TreeSelectionEvent e) {
	            	getChckbxDone().setSelected(false);
	            	getPanelListaHistoricos().removeAll();
	            	getTextareaOldversion().setText("");
	                selectedNode = (DefaultMutableTreeNode) treeFiles.getLastSelectedPathComponent();
	                if (selectedNode!=null) {
		                Object userObject=selectedNode.getUserObject();
		                if (userObject!=null && userObject instanceof FileVsGit) {
			                FileVsGit fileVsGit=(FileVsGit)userObject;
			                getTxtFileSelected().setText(fileVsGit.getGitFile());
			                
			                if (fileVsGit.getEstadoProcesado()==EstadoProcesado.Revisado) {
			                	getChckbxDone().setSelected(true);
			                } 
			                
			                for (GitFileVersionInfo gitFileVersionInfo:fileVsGit.getVersiones()) {
			                	Historico historico=new Historico(gitFileVersionInfo,esteGitCompareMainWindow);
			                	String gitAuthor=gitFileVersionInfo.getAuthorName();
			                	if (gitFileVersionInfo.isBase()) {
			                		historico.setLineColor(Color.GREEN);
			                	} else if (gitFileVersionInfo.getAuthorName().equals(esteGitCompareMainWindow.getTxtUserName().getText())) {
			                		historico.setLineColor(Color.ORANGE);
			                	} else {
			                		historico.setLineColor(Color.RED);
			                	}
			                	getPanelListaHistoricos().add(historico);
			                }
		                }
	                }
	                getPanelListaHistoricos().revalidate();
	                getPanelListaHistoricos().repaint();
	                treeFiles.repaint();
	            }
			});
			
			
			
		}
		return treeFiles;
	}
	
	
	
	public void initFileDrop() {
		FileDrop fileDropDir=new FileDrop( getPanelFilesDir(), new FileDrop.Listener() {   
			public void filesDropped( java.io.File[] files ) {   
			              if (files.length==1) {
			            	  File f=files[0];
			            	  if (f.isDirectory()) {
			            		  String dir=f.getAbsolutePath();
			            		  //comboDirectory.getEditor().setItem(dir);
			            		  getTxtFilesDir().setText(dir);
			            		  refreshMap();
			            	  } else {
			            		  showMessage("drop only a directory");
			            	  }
			              } else {
			            	  showMessage("drop only 1 directory");
			            	  
			              }
			          }   // end filesDropped
			      });		
		FileDrop fileDropGitDir=new FileDrop( getPanelGitDir(), new FileDrop.Listener() {   
			public void filesDropped( java.io.File[] files ) {   
			              if (files.length==1) {
			            	  File f=files[0];
			            	  if (f.isDirectory()) {
			            		  String dir=f.getAbsolutePath();
			            		  //comboDirectory.getEditor().setItem(dir);
			            		  getTxtGitdirectory().setText(dir);
			            		  refreshBranch();
			            	  } else {
			            		  showMessage("drop only a directory");
			            	  }
			              } else {
			            	  showMessage("drop only 1 directory");
			            	  
			              }
			          }   // end filesDropped
			      });	
		
		FileDrop fileDropPrjFile=new FileDrop( getPanelPrj(), new FileDrop.Listener() {   
			public void filesDropped( java.io.File[] files ) {   
			              if (files.length==1) {
			            	  File f=files[0];
			            	  if (f.isDirectory()) {
			            		  showMessage("drop only a file");
			            	  } else {
			            		  String fich=f.getAbsolutePath();
			            		 try {
									comboUtil.setComboNewValue(fich,getCmbProjectFile(), getCmbProjectFileModel(), comboNameProject);
								} catch (IOException e) {
									//ignorar
								}			            		  
			            	  }
			              } else {
			            	  showMessage("drop only 1 directory");
			            	  
			              }
			          }   // end filesDropped
			      });		
		
	}	
	
	
	protected void selGitDir() {
		getTxtGitdirectory().setText(askDirectory(getTxtGitdirectory().getText(),"Git directory"));
		refreshBranch();
	}
	protected void selFilesDir() {
		getTxtFilesDir().setText(askDirectory(getTxtFilesDir().getText(),"files directory"));
		refreshMap();
	}	
	protected void selPrjFile() throws IOException {//
		String comboPrj=askFile(getProjectFile(),"project File");
		comboUtil.setComboNewValue(comboPrj,getCmbProjectFile(), getCmbProjectFileModel(), comboNameProject);
	}
	public static void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message,"CompareToGit",JOptionPane.OK_OPTION);
	}
	
	
	public String askFile(String actual,String titulo) {
		JFileChooser chooser = new JFileChooser(); 
		File d=null;
		if (actual!=null && !actual.equals("")) {
			d=new File(actual);
			if (!d.exists()) {
				d=null;
			}
			if (d!=null && d.isFile()) {
				d=d.getParentFile();
			}
		}
		if (d==null) {
			d=new File(".");
		}
	    chooser.setCurrentDirectory(d);
	    chooser.setDialogTitle(titulo);
	    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
	    if (chooser.showOpenDialog(frmGitcompare) == JFileChooser.APPROVE_OPTION) { 
	    	return chooser.getSelectedFile().getAbsolutePath();
	    } else {
	      return null;
	    }
	}
	
	public String askDirectory(String actual,String titulo) {
		JFileChooser chooser = new JFileChooser(); 
		File d=null;
		if (actual!=null && !actual.equals("")) {
			d=new File(actual);
			if (!d.exists()) {
				d=null;
			}
			if (d!=null && d.isFile()) {
				d=d.getParentFile();
			}
		}
		if (d==null) {
			d=new File(".");
		}
	    chooser.setCurrentDirectory(d);
	    chooser.setDialogTitle(titulo);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
	    if (chooser.showOpenDialog(frmGitcompare) == JFileChooser.APPROVE_OPTION) { 
	    	return chooser.getSelectedFile().getAbsolutePath();
	    } else {
	      return null;
	    }
	}
	
	
	
	private JButton getBtnCheckStatus() {
		if (btnCheck == null) {
			btnCheck = new JButton("check status");
			btnCheck.setMargin(new Insets(1, 1, 2, 1));
			btnCheck.setFont(new Font("Tahoma", Font.BOLD, 11));
			btnCheck.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						checkStatus();
					} catch (Exception r) {
						r.printStackTrace();
						showMessage("Error al chequear :: "+r);
					}
				}
			});
			btnCheck.setBounds(0, 0, 129, 23);
		}
		return btnCheck;
	}
	
	
	
	
	

	private JLabel getLblGit() {
		if (lblGit == null) {
			lblGit = new JLabel("GIT :");
			lblGit.setMinimumSize(new Dimension(24, 24));
			lblGit.setPreferredSize(new Dimension(24, 24));
		}
		return lblGit;
	}
	private JTextField getTxtBranchname() {
		if (txtBranchname == null) {
			txtBranchname = new JTextField();
			txtBranchname.setFont(new Font("Tahoma", Font.PLAIN, 16));
			txtBranchname.setForeground(Color.MAGENTA);
			txtBranchname.setText("-");
			txtBranchname.setEditable(false);
			txtBranchname.setColumns(10);
		}
		return txtBranchname;
	}
	private JLabel getLblBranchname() {
		if (lblBranchname == null) {
			lblBranchname = new JLabel("branchName:");
		}
		return lblBranchname;
	}
	private JTextField getTxtGitdirectory() {
		if (txtGitdirectory == null) {
			txtGitdirectory = new JTextField();
			txtGitdirectory.setPreferredSize(new Dimension(6, 20));
			txtGitdirectory.setMinimumSize(new Dimension(6, 20));
			txtGitdirectory.setText("");
			txtGitdirectory.setColumns(10);
		}
		return txtGitdirectory;
	}
	private JLabel getLblFilesDir() {
		if (lblFilesDir == null) {
			lblFilesDir = new JLabel("filesDir:");
		}
		return lblFilesDir;
	}
	private JTextField getTxtFilesDir() {
		if (txtFilesDir == null) {
			txtFilesDir = new JTextField();
			txtFilesDir.setText("");
			txtFilesDir.setColumns(10);
		}
		return txtFilesDir;
	}
	private JButton getBtnOpenGitDir() {
		if (btnOpenGitDir == null) {
			btnOpenGitDir = new JButton("selectGit");
			btnOpenGitDir.setMargin(new Insets(1, 1, 2, 1));
			btnOpenGitDir.setMinimumSize(new Dimension(73, 20));
			btnOpenGitDir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selGitDir();
				}
			});
		}
		return btnOpenGitDir;
	}

	private JButton getBtnOpenFilesDir() {
		if (btnOpenFilesDir == null) {
			btnOpenFilesDir = new JButton("selectDir");
			btnOpenFilesDir.setMargin(new Insets(1, 1, 2, 1));
			btnOpenFilesDir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selFilesDir();
				}
			});
		}
		return btnOpenFilesDir;
	}


	private JPanel getPanelResultados() {
		if (panelResultados == null) {
			panelResultados = new JPanel();
			panelResultados.setLayout(new BorderLayout(0, 0));
			panelResultados.add(getSplitPaneResultados(), BorderLayout.CENTER);
			panelResultados.add(getPanelSelected(), BorderLayout.NORTH);
		}
		return panelResultados;
	}
	private JSplitPane getSplitPaneResultados() {
		if (splitPaneResultados == null) {
			splitPaneResultados = new JSplitPane();
			splitPaneResultados.setLeftComponent(getPanelArbol());
			splitPaneResultados.setRightComponent(getPanelContenedorHistoricos());
			splitPaneResultados.setDividerLocation(400);
		}
		return splitPaneResultados;
	}
	private JPanel getPanelArbol() {
		if (panelArbol == null) {
			panelArbol = new JPanel();
			panelArbol.setLayout(new BorderLayout(0, 0));
			panelArbol.add(getScrollPaneArbol(), BorderLayout.CENTER);
		}
		return panelArbol;
	}
	private JPanel getPanelContenedorHistoricos() {
		if (panelContenedorHistoricos == null) {
			panelContenedorHistoricos = new JPanel();
			panelContenedorHistoricos.setLayout(new BorderLayout(0, 0));
			panelContenedorHistoricos.add(getSplitPaneHistoricos(), BorderLayout.CENTER);
		}
		return panelContenedorHistoricos;
	}
	private JScrollPane getScrollPaneArbol() {
		if (scrollPaneArbol == null) {
			scrollPaneArbol = new JScrollPane();
			scrollPaneArbol.setViewportView(getTreeFiles());
		}
		return scrollPaneArbol;
	}

	
	private DefaultTreeModel  getFilesTreeModel() {
		if (filesTreeModelRoot==null) {
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("-");
			filesTreeModelRoot = new DefaultTreeModel(rootNode);
		}
		return filesTreeModelRoot;
	}

	private JPanel getPanel_10() {
		if (panel_10 == null) {
			panel_10 = new JPanel();
			panel_10.setPreferredSize(new Dimension(20, 20));
			panel_10.setLayout(null);
			//panel_10.add(getBtnCheckStatus());
		}
		return panel_10;
	}

	private JPanel getPanelSelected() {
		if (panelSelected == null) {
			panelSelected = new JPanel();
			panelSelected.setPreferredSize(new Dimension(20, 20));
			panelSelected.setMinimumSize(new Dimension(20, 20));
			panelSelected.setLayout(new BorderLayout(0, 0));
			panelSelected.add(getChckbxDone(), BorderLayout.WEST);
			panelSelected.add(getTxtFileSelected(), BorderLayout.CENTER);
			panelSelected.add(getPanel(), BorderLayout.EAST);
		}
		return panelSelected;
	}

	private JTextField getTxtFileSelected() {
		if (txtFileSelected == null) {
			txtFileSelected = new JTextField();
			txtFileSelected.setColumns(10);
			txtFileSelected.setEditable(false);
		}
		return txtFileSelected;
	}
	private JSplitPane getSplitPaneGitFilter() {
		if (splitPaneGitFilter == null) {
			splitPaneGitFilter = new JSplitPane();
			splitPaneGitFilter.setLeftComponent(getPanelGitFilterCommit());
			splitPaneGitFilter.setRightComponent(getPanelGitFilterUser());
			splitPaneGitFilter.setDividerLocation(200);
		}
		return splitPaneGitFilter;
	}
	private JPanel getPanelGitFilterCommit() {
		if (panelGitFilterCommit == null) {
			panelGitFilterCommit = new JPanel();
			panelGitFilterCommit.setPreferredSize(new Dimension(24, 24));
			panelGitFilterCommit.setLayout(new BorderLayout(0, 0));
			panelGitFilterCommit.add(getLblCommitid(), BorderLayout.WEST);
			panelGitFilterCommit.add(getTxtCommitId(), BorderLayout.CENTER);
			panelGitFilterCommit.add(getLabel(), BorderLayout.EAST);
		}
		return panelGitFilterCommit;
	}
	private JPanel getPanelGitFilterUser() {
		if (panelGitFilterUser == null) {
			panelGitFilterUser = new JPanel();
			panelGitFilterUser.setLayout(new BorderLayout(0, 0));
			panelGitFilterUser.add(getLblUsername(), BorderLayout.WEST);
			panelGitFilterUser.add(getTxtUserName(), BorderLayout.CENTER);
			panelGitFilterUser.add(getLabel_1(), BorderLayout.EAST);
		}
		return panelGitFilterUser;
	}
	private JLabel getLblCommitid() {
		if (lblCommitid == null) {
			lblCommitid = new JLabel("commitId:");
			lblCommitid.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					getTxtCommitId().setCaretPosition(0);
				}
			});
		}
		return lblCommitid;
	}
	private JTextField getTxtCommitId() {
		if (txtCommitId == null) {
			txtCommitId = new JTextField();
			txtCommitId.setText("338685d");
			txtCommitId.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					txtCommitId.setToolTipText(txtCommitId.getText());
				}
			});
			txtCommitId.setColumns(10);
		}
		return txtCommitId;
	}
	private JLabel getLblUsername() {
		if (lblUsername == null) {
			lblUsername = new JLabel("userName:");
		}
		return lblUsername;
	}
	private JTextField getTxtUserName() {
		if (txtUserName == null) {
			txtUserName = new JTextField();
			txtUserName.setColumns(10);
		}
		return txtUserName;
	}
	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("  ");
		}
		return label;
	}
	private JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel("  ");
		}
		return label_1;
	}
	private JSplitPane getSplitPaneHistoricos() {
		if (splitPaneHistoricos == null) {
			splitPaneHistoricos = new JSplitPane();
			splitPaneHistoricos.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPaneHistoricos.setRightComponent(getPanelDiferencias());
			splitPaneHistoricos.setLeftComponent(getPanelHistoricos());
			splitPaneHistoricos.setDividerLocation(200);
		}
		return splitPaneHistoricos;
	}
	private JPanel getPanelDiferencias() {
		if (panelDiferencias == null) {
			panelDiferencias = new JPanel();
			panelDiferencias.setMinimumSize(new Dimension(50, 50));
			panelDiferencias.setLayout(new BorderLayout(0, 0));
			panelDiferencias.add(getScrollPaneDiferencias(), BorderLayout.CENTER);
		}
		return panelDiferencias;
	}
	private JScrollPane getScrollPaneDiferencias() {
		if (scrollPaneDiferencias == null) {
			scrollPaneDiferencias = new JScrollPane();
			scrollPaneDiferencias.setViewportView(getTextareaOldversion());
		}
		return scrollPaneDiferencias;
	}
	private JPanel getPanelHistoricos() {
		if (panelHistoricos == null) {
			panelHistoricos = new JPanel();
			panelHistoricos.setLayout(new BorderLayout(0, 0));
			panelHistoricos.add(getScrollPaneHistoricos(), BorderLayout.CENTER);
		}
		return panelHistoricos;
	}
	private JScrollPane getScrollPaneHistoricos() {
		if (scrollPaneHistoricos == null) {
			scrollPaneHistoricos = new JScrollPane();
			scrollPaneHistoricos.setViewportView(getPanelListaHistoricos());
		}
		return scrollPaneHistoricos;
	}
	private JPanel getPanelListaHistoricos() {
		if (panelListaHistoricos == null) {
			panelListaHistoricos = new JPanel();
			panelListaHistoricos.setLayout(new BoxLayout(panelListaHistoricos, BoxLayout.Y_AXIS));
		}
		return panelListaHistoricos;
	}
	private JTextArea getTextareaOldversion() {
		if (txtrTextareaOldversion == null) {
			txtrTextareaOldversion = new JTextArea();
			txtrTextareaOldversion.setText("");
		}
		return txtrTextareaOldversion;
	}
	private JButton getBtnRefresh() {
		if (btnRefresh == null) {
			btnRefresh = new JButton("refresh");
			btnRefresh.setMargin(new Insets(1, 1, 2, 1));
			btnRefresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					refreshBranch();
				}
			});
		}
		return btnRefresh;
	}
	private JLabel getLblPrjfile() {
		if (lblPrjfile == null) {
			lblPrjfile = new JLabel("prjFile:");
		}
		return lblPrjfile;
	}
	private JPanel getPanelPrjButtons() {
		if (panelPrjButtons == null) {
			panelPrjButtons = new JPanel();
			panelPrjButtons.setLayout(new FlowLayout(FlowLayout.LEADING, 3, 0));
			panelPrjButtons.add(getBtnOpenprj());
			panelPrjButtons.add(getBtnMaps());
			panelPrjButtons.add(getBtnSave());
		}
		return panelPrjButtons;
	}
	private JButton getBtnOpenprj() {
		if (btnOpenprj == null) {
			btnOpenprj = new JButton("openPrj");
			btnOpenprj.setMargin(new Insets(1, 1, 2, 1));
			btnOpenprj.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						selPrjFile();
					} catch (IOException r) {
						showMessage("error "+r);
					}
				}
			});
		}
		return btnOpenprj;
	}
	private JComboBox<String> getCmbProjectFile() {
		if (CmbProjectFile == null) {
			CmbProjectFile = new JComboBox();
			CmbProjectFile.setEditable(true);
			try {
				CmbProjectFile.setModel(getCmbProjectFileModel());
				openProject();
			} catch (Exception r) {
				showMessage("error obteniendo modelo para proyectos");
			}
			CmbProjectFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						comboProjectFileChanged();
					} catch (IOException e1) {
						showMessage("error refrescando modelo para proyectos");
					}
				}
			});
		}
		return CmbProjectFile; 
	}
	
	private DefaultComboBoxModel<String> getCmbProjectFileModel() throws IOException {
		if (cmbProjectFileModel==null) {
			cmbProjectFileModel = new DefaultComboBoxModel<String>();
			comboUtil.loadPreference(cmbProjectFileModel, comboNameProject);
		}
		return cmbProjectFileModel;
	}
	
	
	protected String getProjectFile() throws IOException {
		String filenameValue=comboUtil.getComboValue(getCmbProjectFile(), getCmbProjectFileModel(), comboNameProject);
		return filenameValue;
	}
	
	protected void comboProjectFileChanged() throws IOException {
		openProject();
	}


	private JButton getBtnMaps() {
		if (btnMaps == null) {
			btnMaps = new JButton("maps");
			btnMaps.setMargin(new Insets(1, 1, 2, 1));
			btnMaps.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openMaps();
				}
			});
		}
		return btnMaps;
	}
	
	protected String getReglasMapeoAsString() {
		StringBuilder strReglasMapeo=new StringBuilder();
		for (String r:reglasMapeo) {
			strReglasMapeo.append(r).append("\r\n");
		}
		return strReglasMapeo.toString();
	}
	
	protected List<String> listReglasMapeo(String reglas) throws Exception {
		List<String> lista=new ArrayList<String>();
		StringTokenizer st=new StringTokenizer(reglas, "\r\n");
		while (st.hasMoreTokens()) {
			String r=st.nextToken().trim();
			if (!r.trim().equals("")) {
				if (!r.contains("|")) {
					throw new Exception("falta  |  en  "+r);
				}
				StringTokenizer st2=new StringTokenizer(r,"|");
				String buscar=trimPathSymbol(st2.nextToken());
				String mapear=trimPathSymbol(st2.nextToken());
				if (st2.hasMoreTokens()) {
					throw new Exception("demasiadas  |  en  "+r);
				}
				lista.add(buscar+"|"+mapear);
			}
		}
		return lista;
	}
	
	protected void openMaps() {
		openMaps(getReglasMapeoAsString());
	}

	protected void openMaps(String reglas) {
		MapeoDlg mapeoDlg=new MapeoDlg(null,reglas);
		boolean aceptado=mapeoDlg.showDialog();
		String respuestaReglas="";
		if (aceptado) {
			respuestaReglas=mapeoDlg.getReglasMapeo();
			respuestaReglas=StringUtils.replace(respuestaReglas, "\\", "/");
			try {
				reglasMapeo=listReglasMapeo(respuestaReglas);
			} catch (Exception r) {
				showMessage(""+r);
				openMaps(respuestaReglas);
				
			}
		}
	}
	
	private JButton getBtnSave() {
		if (btnSave == null) {
			btnSave = new JButton("save");
			btnSave.setMargin(new Insets(1, 1, 2, 1));
			btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						saveMaps();
					} catch (Exception r) {
						showMessage("error al guardar el proyecto "+r);
					}
				}
			});
		}
		return btnSave;
	}
	private JPanel getPanel_8() {
		if (panel_8 == null) {
			panel_8 = new JPanel();
			panel_8.setPreferredSize(new Dimension(24, 24));
			panel_8.setLayout(new BorderLayout(0, 0));
		}
		return panel_8;
	}
	private JTextField getCommitInfo() {
		if (commitInfo == null) {
			commitInfo = new JTextField();
			commitInfo.setEditable(false);
			commitInfo.setColumns(10);
		}
		return commitInfo;
	}
	private JPanel getPanelBrachName() {
		if (panelBrachName == null) {
			panelBrachName = new JPanel();
			panelBrachName.setLayout(new BorderLayout(0, 0));
			panelBrachName.setPreferredSize(new Dimension(24, 24));
			panelBrachName.add(getLblBranchname(), BorderLayout.WEST);
			panelBrachName.add(getTxtBranchname(), BorderLayout.CENTER);
			panelBrachName.add(getBtnRefresh(), BorderLayout.EAST);
		}
		return panelBrachName;
	}
	private JPanel getPanelCabecera() {
		if (panelCabecera == null) {
			panelCabecera = new JPanel();
			panelCabecera.setLayout(new BorderLayout(0, 0));
			panelCabecera.add(getSplitPaneCabecera());
		}
		return panelCabecera;
	}
	private JSplitPane getSplitPaneCabecera() {
		if (splitPaneCabecera == null) {
			splitPaneCabecera = new JSplitPane();
			splitPaneCabecera.setLeftComponent(getMainPanelFilesDir());
			splitPaneCabecera.setRightComponent(getPanelGitDir());
			splitPaneCabecera.setDividerLocation(500);
		}
		return splitPaneCabecera;
	}
	private JPanel getMainPanelFilesDir() {
		if (mainPanelFilesDir == null) {
			mainPanelFilesDir = new JPanel();
			mainPanelFilesDir.setLayout(new BoxLayout(mainPanelFilesDir, BoxLayout.Y_AXIS));
			mainPanelFilesDir.add(getPanelFilesDir());
			mainPanelFilesDir.add(getPanelPrj());
			mainPanelFilesDir.add(getPanelAux());
			mainPanelFilesDir.add(getPanelAcciones());
		}
		return mainPanelFilesDir;
	}
	private JPanel getPanelGitDir() {
		if (panelGitDir == null) {
			panelGitDir = new JPanel();
			panelGitDir.setPreferredSize(new Dimension(24, 24));
			panelGitDir.setLayout(new BoxLayout(panelGitDir, BoxLayout.Y_AXIS));
			panelGitDir.add(getPanelGitSelec());
			panelGitDir.add(getPanelBrachName());
			panelGitDir.add(getPanelCommitInfo());
			panelGitDir.add(getPaneGitFilter());
		}
		return panelGitDir;
	}
	
	private JPanel getPaneGitFilter() {
		if (paneGitFilter == null) {
			paneGitFilter = new JPanel();
			paneGitFilter.setPreferredSize(new Dimension(24, 24));
			paneGitFilter.setLayout(new BorderLayout(0, 0));
			paneGitFilter.add(getSplitPaneGitFilter(), BorderLayout.CENTER);
		}
		return paneGitFilter;
	}
	
	private JPanel getPanelFilesDir() {
		if (panelFilesDir == null) {
			panelFilesDir = new JPanel();
			panelFilesDir.setPreferredSize(new Dimension(24, 24));
			panelFilesDir.setLayout(new BorderLayout(0, 0));
			panelFilesDir.add(getLblFilesDir(), BorderLayout.WEST);
			panelFilesDir.add(getTxtFilesDir(), BorderLayout.CENTER);
			panelFilesDir.add(getBtnOpenFilesDir(), BorderLayout.EAST);
		}
		return panelFilesDir;
	}
	private JPanel getPanelPrj() {
		if (panelPrj == null) {
			panelPrj = new JPanel();
			panelPrj.setLayout(new BorderLayout(0, 0));
			panelPrj.setPreferredSize(new Dimension(24, 24));
			panelPrj.add(getCmbProjectFile(), BorderLayout.CENTER);
			panelPrj.add(getLblPrjfile(), BorderLayout.WEST);
			panelPrj.add(getPanelPrjButtons(), BorderLayout.EAST);
		}
		return panelPrj;
	}
	private JPanel getPanelAux() {
		if (panelAux == null) {
			panelAux = new JPanel();
			panelAux.setLayout(new BorderLayout(0, 0));
			panelAux.add(getSplitPaneAux(), BorderLayout.NORTH);
		}
		return panelAux;
	}
	private JPanel getPanelAcciones() {
		if (panelAcciones == null) {
			panelAcciones = new JPanel();
			panelAcciones.setLayout(new BorderLayout(0, 0));
			panelAcciones.setPreferredSize(new Dimension(20, 20));
			panelAcciones.add(getBtnCheckStatus(), BorderLayout.WEST);
			panelAcciones.add(getPanelAccionesSeleccion(), BorderLayout.EAST);
		}
		return panelAcciones;
	}
	private JSplitPane getSplitPaneAux() {
		if (splitPaneAux == null) {
			splitPaneAux = new JSplitPane();
			splitPaneAux.setLeftComponent(getPanel_14_1());
			splitPaneAux.setRightComponent(getPanelAuxDir());
			splitPaneAux.setDividerLocation(200);
		}
		return splitPaneAux;
	}
	private JPanel getPanel_14_1() {
		if (panelFechaFiles == null) {
			panelFechaFiles = new JPanel();
			panelFechaFiles.setLayout(new BorderLayout(0, 0));
			panelFechaFiles.add(getLblFecha(), BorderLayout.WEST);
			panelFechaFiles.add(getCmbFechaDesde(), BorderLayout.CENTER);
			panelFechaFiles.add(getLblNewLabel(), BorderLayout.EAST);
		}
		return panelFechaFiles;
	}
	private JPanel getPanelAuxDir() {
		if (panelAuxDir == null) {
			panelAuxDir = new JPanel();
			panelAuxDir.setLayout(new BorderLayout(0, 0));
			panelAuxDir.add(getLblAuxDir(), BorderLayout.WEST);
			panelAuxDir.add(getTxtTempDir(), BorderLayout.CENTER);
			panelAuxDir.add(getBotonOpcionCompare(), BorderLayout.EAST);
		}
		return panelAuxDir;
	}
	private JLabel getLblFecha() {
		if (lblFecha == null) {
			lblFecha = new JLabel("fechaDesde");
		}
		return lblFecha;
	}
	private JComboBox<String> getCmbFechaDesde() {
		if (cmbFechaDesde == null) {
			cmbFechaDesde = new JComboBox<String>();
			cmbFechaDesde.setToolTipText("yyyy/MM/dd HH:mm:ss");
			cmbFechaDesde.setEditable(true);
			try {
				cmbFechaDesde.setModel(getCmbFechaDesdeModel());
				cmbFechaDesde.setSelectedIndex(0);
			} catch (Exception r) {
				showMessage("error obteniendo modelo para fecha");
			}
		}
		return cmbFechaDesde;
	}
	

	
	
	
	private DefaultComboBoxModel<String> getCmbFechaDesdeModel() throws IOException {
		if (cmbFechaDesdeModel==null) {
			cmbFechaDesdeModel = new DefaultComboBoxModel<String>();
			try {
				comboUtil.loadPreference(cmbFechaDesdeModel, comboNameFecha);
				String x0=cmbFechaDesdeModel.getElementAt(0);
				if (x0!=null && !x0.trim().equals("")) {
					cmbFechaDesdeModel.insertElementAt("", 0);
				}
			} catch (Exception r) {
				comboUtil.savePreference("", comboNameFecha);
			}
		}
		return cmbFechaDesdeModel;
	}
	
	
	protected Date getFecha() throws Exception {
		String fechaValue=comboUtil.getComboValue(getCmbFechaDesde(), getCmbFechaDesdeModel(), comboNameFecha);
		if (fechaValue==null || fechaValue.trim().equals("")) {
			return null;
		}
		Date date = null;
		SimpleDateFormat sdf=null;
		try {
			sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			date=sdf.parse(fechaValue);
		} catch (ParseException e) {
			try {
				sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm");
				date=sdf.parse(fechaValue);
			} catch (ParseException e1) {
				try {
					sdf=new SimpleDateFormat("yyyy/MM/dd");
					date=sdf.parse(fechaValue);
				} catch (Exception e2) {	
					throw new Exception("ERROR "+fechaValue+" is not  yyyy/MM/dd HH:mm:ss   or   yyyy/MM/dd HH:mm  "+e);
				}
			}
		}
		
		return date;
	}
	
	
	
	private JPanel getPanelGitSelec() {
		if (panelGitSelec == null) {
			panelGitSelec = new JPanel();
			panelGitSelec.setPreferredSize(new Dimension(48, 48));
			panelGitSelec.setLayout(new BorderLayout(0, 0));
			panelGitSelec.add(getLblGit(), BorderLayout.WEST);
			panelGitSelec.add(getTxtGitdirectory(), BorderLayout.CENTER);
			panelGitSelec.add(getBtnOpenGitDir(), BorderLayout.EAST);
		}
		return panelGitSelec;
	}
	private JPanel getPanelCommitInfo() {
		if (panelCommitInfo == null) {
			panelCommitInfo = new JPanel();
			panelCommitInfo.setPreferredSize(new Dimension(24, 24));
			panelCommitInfo.setLayout(new BorderLayout(0, 0));
			panelCommitInfo.add(getCommitInfo(), BorderLayout.SOUTH);
		}
		return panelCommitInfo;
	}
	
	private JPanel getPanelAccionesSeleccion() {
		if (panelAccionesSeleccion == null) {
			panelAccionesSeleccion = new JPanel();
			panelAccionesSeleccion.setLayout(new FlowLayout(FlowLayout.LEADING, 2, 0));
			panelAccionesSeleccion.add(getBtnCompareVsGit());
			panelAccionesSeleccion.add(getBtnComparebase());
			panelAccionesSeleccion.add(getBtnCopy());
			panelAccionesSeleccion.add(getBtnCompG());
		}
		return panelAccionesSeleccion;
	}
	private JButton getBtnCompareVsGit() {
		if (btnCompareVsGit == null) {
			btnCompareVsGit = new JButton("comp-F-FGD");
			btnCompareVsGit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					comparaFicheroConVersionEnDirectorioGit();
				}
			});
			btnCompareVsGit.setMargin(new Insets(1, 1, 2, 1));
			btnCompareVsGit.setFont(new Font("Tahoma", Font.BOLD, 11));
			btnCompareVsGit.setToolTipText("compara fichero con version en directorio git");
		}
		return btnCompareVsGit;
	}
	protected void comparaFicheroConVersionEnDirectorioGit() {
		boolean comparado=false;
        if (selectedNode!=null) {
            Object userObject=selectedNode.getUserObject();
            if (userObject!=null && userObject instanceof FileVsGit) {
                FileVsGit fileVsGit=(FileVsGit)userObject;
                File leftFile=fileVsGit.getFich();
                File rigthFile=new File(fileVsGit.getFileInGit());
                comparado=true;
                compare2(leftFile,rigthFile);
            }
        } 
        if (!comparado) {
        	showMessage("Nada que comparar");
        }
        
	}

	private void compare2(File leftFile, File rigthFile) {
		String [] command={compareCommand,leftFile.getPath(),rigthFile.getPath()};
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			showMessage("ERROR executing compare command "+compareCommand+" :: "+e);
		}
	}

	private void compare4(File leftFile, File rigthFile, File centerFile, File outputFile) {
		/*
4 files
Opens a Text Merge view with the specified files in the left, right, center, and output panes.  For example:
 BCompare.exe C:\Left.ext C:\Right.ext C:\Center.ext C:\Output.ext
		 */
		String [] command={compareCommand,leftFile.getPath(),rigthFile.getPath(),centerFile.getPath(),outputFile.getPath()};
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			showMessage("ERROR executing compare command "+compareCommand+" :: "+e);
		}
	}	
	
	protected void compararFicheroConVersionEnDirGitTeniendoEnCuentaLaVersionBase() {
		boolean comparado=false;
        if (selectedNode!=null) {
            Object userObject=selectedNode.getUserObject();
            if (userObject!=null && userObject instanceof FileVsGit) {
                FileVsGit fileVsGit=(FileVsGit)userObject;
                if (fileVsGit.getVersiones().size()>1 && fileVsGit.isTieneBase()) {
	                File leftFile=fileVsGit.getFich();
	                File centerFile=fileVsGit.getBaseVersion().getTempFile();
	                
	                File tempFile=new File(fileVsGit.getGitRepo().getTempDir().getPath()+"/"+fileVsGit.getFich().getName()+"-"+System.currentTimeMillis());
	                try {
						FileUtils.copyFile(new File(fileVsGit.getFileInGit()), tempFile);
		                File rigthFile=tempFile; //new File(fileVsGit.getFileInGit()); //fileVsGit.getVersiones().get(0).getTempFile();
		                File outputFile=new File(fileVsGit.getFileInGit());
		                comparado=true;
		                compare4(leftFile,rigthFile,centerFile,outputFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                } else {
                	showMessage("No tiene version base");
                }
            }
        } 
        if (!comparado){
        	showMessage("Nada que comparar");
        }
	}
	
	
	protected void openInExplorer(File f) {
			try {
				Desktop.getDesktop().open(f.getParentFile());
			} catch (Exception e) {
				showMessage("ERROR "+e);
			}
	}
	
	protected void explorerFicheroGit() {
        if (selectedNode!=null) {
            Object userObject=selectedNode.getUserObject();
            if (userObject!=null && userObject instanceof FileVsGit) {
                FileVsGit fileVsGit=(FileVsGit)userObject;
                openInExplorer(new File(fileVsGit.getFileInGit()));
            }
        }
	}

	protected void explorerFichero() {
        if (selectedNode!=null) {
            Object userObject=selectedNode.getUserObject();
            if (userObject!=null && userObject instanceof FileVsGit) {
                FileVsGit fileVsGit=(FileVsGit)userObject;
                openInExplorer(fileVsGit.getFich());
            }
        }
	}
	protected void explorerTemporal() {
        if (selectedNode!=null) {
            Object userObject=selectedNode.getUserObject();
            if (userObject!=null && userObject instanceof FileVsGit) {
                FileVsGit fileVsGit=(FileVsGit)userObject;
                openInExplorer(new File(fileVsGit.getTempFichBase()));
            }
        }
	}	
	
	
	protected void copyFicheroADirectoriGit() {
        if (selectedNode!=null) {
            Object userObject=selectedNode.getUserObject();
            if (userObject!=null && userObject instanceof FileVsGit) {
                FileVsGit fileVsGit=(FileVsGit)userObject;
                File origen=fileVsGit.getFich();
                File destino=new File(fileVsGit.getFileInGit());
                try { 
                	if (origen.getName().endsWith("-del")) {
                		FileUtils.forceDelete(destino);
                	} else {
                		FileUtils.copyFile(origen, destino);
                	}
                	getChckbxDone().setSelected(true);
                	hecho();
                } catch (Exception r) {
                	showMessage("ERROR copiando fichero: "+r);
                }
            }
        } else {
        	showMessage("Nada que copiar");
        }
	}
	
	protected void comparar2versionesGit() {
		if (historicoSel1!=null && historicoSel2!=null) {
		 GitFileVersionInfo gitFileVersion1=historicoSel1.getGitFileVersionInfo();
		 GitFileVersionInfo gitFileVersion2=historicoSel2.getGitFileVersionInfo();
		 compare2(gitFileVersion1.getTempFile(), gitFileVersion2.getTempFile());
		} else {
			showMessage("debes elegir 2 versiones");
		}
	}
	
	@Override
	public void compareFileToGitVersion(GitFileVersionInfo gitFileVersionInfo) {
		compare2(gitFileVersionInfo.getFileVsGit().getFich(), gitFileVersionInfo.getTempFile());
	}

	@Override
	public void compareGitVersionConFichEnDirectorioGit(GitFileVersionInfo gitFileVersionInfo) {
		compare2(gitFileVersionInfo.getTempFile(),new File(gitFileVersionInfo.getFileVsGit().getFileInGit()));
	}
	
	protected void ComparaUltimaVersionGitConVersionDirectorioGit() {
		boolean comparado=false;
        if (selectedNode!=null) {
            Object userObject=selectedNode.getUserObject();
            if (userObject!=null && userObject instanceof FileVsGit) {
                FileVsGit fileVsGit=(FileVsGit)userObject;
                if (fileVsGit.getVersiones().size()>1) {
                	File leftFile=fileVsGit.getVersiones().get(0).getTempFile();
		            File rigthFile=new File(fileVsGit.getFileInGit());
		            comparado=true;
		            compare2(leftFile,rigthFile);
                } 
            }
        } 
        if (!comparado){
        	showMessage("Nada que comparar");
        }
	}
	
	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel(" ");
		}
		return lblNewLabel;
	}
	private JLabel getLblAuxDir() {
		if (lblAuxDir == null) {
			lblAuxDir = new JLabel("tempDir:");
		}
		return lblAuxDir;
	}
	private JTextField getTxtTempDir() {
		if (txtTempDir == null) {
			txtTempDir = new JTextField();
			txtTempDir.setEditable(false);
			txtTempDir.setColumns(10);
		}
		return txtTempDir;
	}
	private JButton getBtnComparebase() {
		if (btnComparebase == null) {
			btnComparebase = new JButton("comp3-F-BG-FGD->FGD");
			btnComparebase.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					compararFicheroConVersionEnDirGitTeniendoEnCuentaLaVersionBase();
				}
			});
			btnComparebase.setMargin(new Insets(1, 1, 2, 1));
			btnComparebase.setFont(new Font("Tahoma", Font.BOLD, 11));
			btnComparebase.setToolTipText("compara fichero con ultima version en directorio git teniendo en cuenta la version base ");
		}
		return btnComparebase;
	}
	private JCheckBox getChck2versions() {
		if (chck2versions == null) {
			chck2versions = new JCheckBox("");
			chck2versions.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					habilitar2versiones();
				}
			});
		}
		return chck2versions;
	}
	protected void habilitar2versiones() {
		if (getChck2versions().isSelected()) {
			modoTratarHistoricosDoble=true;
		} else {
			modoTratarHistoricosDoble=false;
		}
		getPanelHistoricos().repaint();
		getBtn2Versiones().setEnabled(modoTratarHistoricosDoble);
	}

	private JButton getBtn2Versiones() {
		if (btn2Versiones == null) {
			btn2Versiones = new JButton("2vers");
			btn2Versiones.setEnabled(false);
			btn2Versiones.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					comparar2versionesGit();
				}
			});
			btn2Versiones.setMargin(new Insets(1, 1, 2, 1));
			btn2Versiones.setFont(new Font("Tahoma", Font.PLAIN, 10));
			btn2Versiones.setToolTipText("comparar 2 versiones de git");
		}
		return btn2Versiones;
	}


	private JButton getBtnCopy() {
		if (btnCopy == null) {
			btnCopy = new JButton("copy-F->FGD");
			btnCopy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					copyFicheroADirectoriGit();
				}
			});
			btnCopy.setFont(new Font("Tahoma", Font.BOLD, 11));
			btnCopy.setMargin(new Insets(1, 1, 2, 1));
			btnCopy.setToolTipText("copiar (o borrar (si nombre es *-del)) fichero en directorio git");
		}
		return btnCopy;
	}


	private JButton getBotonOpcionCompare() {
		if (botonOpcionCompare == null) {
			botonOpcionCompare = new JButton("?");
			botonOpcionCompare.setToolTipText("settings / ajustes de comparacion");
			botonOpcionCompare.setFont(new Font("Tahoma", Font.PLAIN, 9));
			botonOpcionCompare.setMargin(new Insets(1, 1, 2, 1));
			botonOpcionCompare.setBounds(65, 80, 15, 15);
			botonOpcionCompare.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						compareOptions();
					} catch (IOException e1) {
						showMessage("error al fijar la app de comparacion");
					}
				}
			});
		}
		return botonOpcionCompare;
	}

	protected void compareOptions() throws IOException {
		String x=comboUtil.askForCombo("set compare command", comboNameCompareApp, compareCommand);
		if (x!=null) {
			compareCommand=x;
		}
	}
	private JCheckBox getChckbxDone() {
		if (chckbxDone == null) {
			chckbxDone = new JCheckBox("done)  Fich:");
			chckbxDone.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					hecho();
				}
			});
		}
		return chckbxDone;
	}

	protected void hecho() {
        if (selectedNode!=null) {
            Object userObject=selectedNode.getUserObject();
            if (userObject!=null && userObject instanceof FileVsGit) {
                FileVsGit fileVsGit=(FileVsGit)userObject;
                if (getChckbxDone().isSelected()) {
                	fileVsGit.setEstadoProcesado(EstadoProcesado.Revisado);
                } else {
                	fileVsGit.setEstadoProcesado(EstadoProcesado.SinRevisar);
                }
            }
            actualizaHechoEnNodosPadres((DefaultMutableTreeNode)selectedNode.getParent());
        }
        getPanelListaHistoricos().revalidate();
        getPanelListaHistoricos().repaint();
        treeFiles.repaint();
	}
	
	private void actualizaHechoEnNodosPadres(DefaultMutableTreeNode padreNode) {
		if (padreNode!=null) {
			Object userObject=padreNode.getUserObject();
			if (userObject instanceof FileInTreeInfo) {
				boolean hechos=true;
				for (int i=0;i<padreNode.getChildCount();i++) {
					DefaultMutableTreeNode hijo=(DefaultMutableTreeNode)padreNode.getChildAt(i);
					Object hijoObj=hijo.getUserObject();
					if (hijoObj instanceof FileInTreeInfo) {
						FileInTreeInfo fileInTreeInfoHijo=(FileInTreeInfo)hijoObj;
						if (fileInTreeInfoHijo.getEstadoProcesado()==EstadoProcesado.SinRevisar) {
							hechos=false;
						}
					}
				}
				if (hechos) {
					((FileInTreeInfo) userObject).setEstadoProcesado(EstadoProcesado.Revisado);
				} else {
					((FileInTreeInfo) userObject).setEstadoProcesado(EstadoProcesado.SinRevisar);
				}
			}
			if(padreNode.getParent()!=null && padreNode.getParent() instanceof DefaultMutableTreeNode) {
				actualizaHechoEnNodosPadres((DefaultMutableTreeNode)padreNode.getParent());
			}
		}
	}

	
	
	
	private JButton getBtnCompG() {
		if (btnCompG == null) {
			btnCompG = new JButton("comp-G-FGD");
			btnCompG.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ComparaUltimaVersionGitConVersionDirectorioGit();
				}
			});
			btnCompG.setMargin(new Insets(1, 1, 2, 1));
			btnCompG.setFont(new Font("Tahoma", Font.BOLD, 11));
			btnCompG.setToolTipText("compara ultima version git con version en directorio git");
		}
		return btnCompG;
	}


	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEADING, 2, 0));
			panel.add(getChck2versions());
			panel.add(getBtn2Versiones());
			panel.add(getBtnExplorerFich());
			panel.add(getBtnExplorerTemp());
			panel.add(getBtnExplorerFGD());
		}
		return panel;
	}


	private JButton getBtnExplorerFich() {
		if (btnExplorerFich == null) {
			btnExplorerFich = new JButton("expF");
			btnExplorerFich.setToolTipText("explorer en carpeta del fichero");
			btnExplorerFich.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					explorerFichero();
				}
			});
			btnExplorerFich.setMargin(new Insets(1, 1, 2, 1));
			btnExplorerFich.setFont(new Font("Tahoma", Font.PLAIN, 10));
		}
		return btnExplorerFich;
	}


	private JButton getBtnExplorerTemp() {
		if (btnExplorerTemp == null) {
			btnExplorerTemp = new JButton("expT");
			btnExplorerTemp.setToolTipText("explorer en carpeta temporal");
			btnExplorerTemp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					explorerTemporal();
				}
			});
			btnExplorerTemp.setMargin(new Insets(1, 1, 2, 1));
			btnExplorerTemp.setFont(new Font("Tahoma", Font.PLAIN, 10));
		}
		return btnExplorerTemp;
	}


	private JButton getBtnExplorerFGD() {
		if (btnExplorerFGD == null) {
			btnExplorerFGD = new JButton("expFGD");
			btnExplorerFGD.setToolTipText("explorer en carpeta del fichero en el directorio git");
			btnExplorerFGD.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					explorerFicheroGit();
				}
			});
			btnExplorerFGD.setMargin(new Insets(1, 1, 2, 1));
			btnExplorerFGD.setFont(new Font("Tahoma", Font.PLAIN, 10));
		}
		return btnExplorerFGD;
	}
	

}