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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.bcjj.gitCompare.NuevasVersionesInfo;
import org.bcjj.gitCompare.EstadoProcesado;
import org.bcjj.gitCompare.FileInTreeInfo;
import org.bcjj.gitCompare.FileVsGit;
import org.bcjj.gitCompare.GitFileVersionInfo;
import org.bcjj.gitCompare.GitRepo;
import org.bcjj.gitCompare.gui.ComboUtil.ComboName;
import org.bcjj.gitCompare.gui.MapeoDlg.MapeoInfo;

import net.iharder.dnd.FileDrop;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.Toolkit;

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

	private final static String SUFIX_DEL="-del"; //$NON-NLS-1$
	private final static String TEMP_DIR_GITCOMPARE="/temp/gitCompare-"; //$NON-NLS-1$
	
	private static final String PROPERTY_PATH = "path."; //$NON-NLS-1$
	private static final String PROPERTY_USER_NAME = "userName"; //$NON-NLS-1$
	private static final String PROPERTY_COMMIT_ID_EXPRESION_GROUP = "commitIdExpresionGroup"; //$NON-NLS-1$
	private static final String PROPERTY_COMMIT_ID_EXPRESION = "commitIdExpresion"; //$NON-NLS-1$
	private static final String PROPERTY_FICHERO_PARA_COMMIT_ID = "ficheroParaCommitId"; //$NON-NLS-1$
	private static final String PROPERTY_FICHERO_PARA_FECHA = "ficheroParaFecha"; //$NON-NLS-1$
	private static final String PROPERTY_GIT_DIR = "gitDir"; //$NON-NLS-1$
	private static final String PROPERTY_FILES_DIR = "filesDir"; //$NON-NLS-1$
	private static final String PROPERTY_BRANCH_NAME = "branchName"; //$NON-NLS-1$
	private static final String PROPERTY_MINUTOS_ADD_FECHA = "minutesAddFecha"; //$NON-NLS-1$

	private static final String DATEFORMAT_compact_yyyyMMdd_g_HHmmss="yyyyMMdd-HHmmss"; //$NON-NLS-1$
	private static final String DATEFORMAT_yyyyMMdd_HHmm="yyyy/MM/dd HH:mm"; //$NON-NLS-1$
	private static final String DATEFORMAT_yyyyMMdd_HHmmss="yyyy/MM/dd HH:mm:ss"; //$NON-NLS-1$
	private static final String DATEFORMAT_yyyyMMdd="yyyy/MM/dd"; //$NON-NLS-1$
	
	private final static String StringVacio=""; //$NON-NLS-1$
	
	private final static String NL="\r\n"; //$NON-NLS-1$
	

	
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
	private JButton btnGitCompare;
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
	ImageIcon iconoIgual;
	ImageIcon iconoDistinto;
	
	private JButton btnRefresh;
	private JLabel lblPrjfile;
	private JPanel panelPrjButtons;
	private JButton btnOpenprj;
	private JComboBox<String> CmbProjectFile;
	private DefaultComboBoxModel<String> cmbProjectFileModel;

	private ComboUtil comboUtil=new ComboUtil("gitCompare", 10); //$NON-NLS-1$
	private ComboName comboNameProject=new ComboName("project"); //$NON-NLS-1$
	private ComboName comboNameCompareApp=new ComboName("compareApp"); //$NON-NLS-1$
	private DefaultComboBoxModel<String> cmbFechaDesdeModel;
	private ComboName comboNameFecha=new ComboName("fromDate"); //$NON-NLS-1$
	private JButton btnMaps;
	private JButton btnSave;
	private String compareCommand;
	
	private List<String> confMapeoReglasMapeo;
	private String confMapeoFicheroParaFecha;
	private String confMapeoFicheroParaCommitId;
	private String confMapeoCommitIdExpresion;
	private int confMapeoCommitIdExpresionGroup=0;
	private int confMapeoMinutosAddFecha=0;
	
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
	private JButton btnLoad;
	private JPanel panelGitOptions;
	private JSplitPane splitPaneGitOptions;
	private JPanel panelGitOptionBranch;
	private JLabel lblOptionBranch;
	private JTextField textOptionBranch;
	private JLabel lblNewLabel_1;
	private JPanel panelOpcionesArbol;
	private JPanel panelBotoneraArbol;
	private JButton fileStatus;
	private JButton allFilesStatus;

	private List<FileVsGit> filesGit=null;
	private JPanel panelOpcionFecha;
	
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
			compareCommand="C:/Program Files (x86)/Beyond Compare 3/BCompare.exe"; //$NON-NLS-1$
			try {
				comboUtil.savePreference(compareCommand,comboNameCompareApp);
			} catch (IOException e1) {
				//ignorar
			}
		}
		
	}

	private void initIcons() {
		iconoSinConflictos = createImageIcon("/images/cart-go.png"); //$NON-NLS-1$
		iconoConflictosMismoUsuario = createImageIcon("/images/dialog-warning-3.png"); //$NON-NLS-1$
		iconoConflictosDistintoUsuario = createImageIcon("/images/arrow-divide2.png"); //$NON-NLS-1$
		iconoPendiente = createImageIcon("/images/edit-find-6.png"); //$NON-NLS-1$
		iconoHecho = createImageIcon("/images/dialog-ok-4.png"); //$NON-NLS-1$
		
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
		frmGitcompare.setIconImage(Toolkit.getDefaultToolkit().getImage(GitCompareMainWindow.class.getResource("/images/arrow-divide2.png"))); //$NON-NLS-1$
		frmGitcompare.setTitle("GitCompare"); //$NON-NLS-1$
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
		
		FileInTreeInfo fileInTreeInfo=new FileInTreeInfo(StringVacio);
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
			getTxtCommitId().setText(gitRepo.getFromPartialCommitId());
		} catch (Exception r) {
			showMessage("revisa los parametros de git (directorio o idCommit)");
			return;
		}
		showGitInfo(gitRepo);

		SimpleDateFormat sdf=new SimpleDateFormat(DATEFORMAT_compact_yyyyMMdd_g_HHmmss);
		Date ahora=new Date();
		File tempDir=new File(TEMP_DIR_GITCOMPARE+sdf.format(ahora));
		getTxtTempDir().setText(tempDir.getPath());
		gitRepo.setTempDir(tempDir);
		readTreeDir(dirFiles,gitRepo,fechaDesde,tempDir);
	}	
	
	protected void refreshBranch() {
		try {
			getTxtBranchname().setText(StringVacio);
			getCommitInfo().setText(StringVacio);
			GitRepo gitRepo=new GitRepo(getTxtGitdirectory().getText(),getTxtCommitId().getText());
			getTxtCommitId().setText(gitRepo.getFromPartialCommitId());
			showGitInfo(gitRepo);
			
		} catch (Exception r) {
			showMessage("Error al obtener el branch: "+r);
		}
		if (!getTxtBranchname().getText().equals(getTextOptionBranch().getText())) {
			showMessage("CUIDADO el branch no coincide, actual:"+getTxtBranchname().getText()+", esperado:"+getTextOptionBranch().getText());
		}
	}
	
	
	protected void showGitInfo(GitRepo gitRepo) {
		getTxtBranchname().setText(gitRepo.getBranchName());
		getCommitInfo().setText(gitRepo.getFromCommitInfo());
		getCommitInfo().setCaretPosition(0);
		getCommitInfo().setToolTipText("informacion de commit id");
	}
	
	protected void refreshMap() {
		
	}
	
	
	public enum Estado {
	    Sincronizado,NoSincronizado,NoExisteEnGit,ParaBorrar
	}
	
	private void openProject() throws IOException {
		String filenameValue=getProjectFile();
		if (filenameValue==null || filenameValue.trim().equals(StringVacio)) {
			return;
		}
		Properties p=new Properties();
		InputStream r=null;
		confMapeoReglasMapeo=new ArrayList<String>();
		try {
			r=new FileInputStream(filenameValue);
			p.load(r);
			String branchName=p.getProperty(PROPERTY_BRANCH_NAME); 
			getTextOptionBranch().setText(branchName);
			getTxtFilesDir().setText(p.getProperty(PROPERTY_FILES_DIR)); 
			getTxtGitdirectory().setText(p.getProperty(PROPERTY_GIT_DIR)); 
			getTxtUserName().setText(p.getProperty(PROPERTY_USER_NAME)); 
			confMapeoFicheroParaFecha=p.getProperty(PROPERTY_FICHERO_PARA_FECHA,StringVacio); 
			confMapeoFicheroParaCommitId=p.getProperty(PROPERTY_FICHERO_PARA_COMMIT_ID,StringVacio); 
			confMapeoCommitIdExpresion=p.getProperty(PROPERTY_COMMIT_ID_EXPRESION,StringVacio);
			try {
				confMapeoCommitIdExpresionGroup=Integer.parseInt(p.getProperty(PROPERTY_COMMIT_ID_EXPRESION_GROUP,"0")); //$NON-NLS-1$
			} catch (Exception ee) {
				confMapeoCommitIdExpresionGroup=0;
			}
			try {
				confMapeoMinutosAddFecha=Integer.parseInt(p.getProperty(PROPERTY_MINUTOS_ADD_FECHA,"0")); //$NON-NLS-1$
			} catch (Exception ee) {
				confMapeoMinutosAddFecha=0;
			}
			
			refreshMapeoData();
			for (int i=0;i<1000;i++) {
				String x=p.getProperty(PROPERTY_PATH+i); //$NON-NLS-1$
				if (x!=null) {
					confMapeoReglasMapeo.add(x);
				}
			}
		} catch (Exception e) {
			showMessage("Error abriendo "+filenameValue);
			e.printStackTrace();
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
	

	private void refreshMapeoData() {
		boolean ponerFecha=false;
		boolean puestaFecha=false;
		boolean ponerCommitId=false;
		boolean puestoCommitId=false;
		try {
			if (confMapeoFicheroParaFecha!=null && !confMapeoFicheroParaFecha.trim().equals(StringVacio)) {
				ponerFecha=true;
				File fFecha=new File(confMapeoFicheroParaFecha);
				if (fFecha.exists() && fFecha.isFile()) {
					Date d=new Date(fFecha.lastModified()+(confMapeoMinutosAddFecha*60*1000));
					SimpleDateFormat sdf=new SimpleDateFormat(DATEFORMAT_yyyyMMdd_HHmm);
					String fec=sdf.format(d);
					getCmbFechaDesde().setSelectedItem(fec);
					puestaFecha=true;
				}
			}
		} catch (Exception r) {
			System.out.println("error al refrescar los datos de mapeo para el fichero para fecha "+r);
		}
		try {
			if (confMapeoFicheroParaCommitId!=null && !confMapeoFicheroParaCommitId.trim().equals(StringVacio)) {
				ponerCommitId=true;
				File fCommit=new File(confMapeoFicheroParaCommitId);
				if (fCommit.exists() && fCommit.isFile()) {
					String commitId=getCommitIdFromFile(fCommit);
					if (commitId!=null) {
						getTxtCommitId().setText(commitId);
						puestoCommitId=true;
					}
				}
			}
		} catch (Exception r) {
			System.out.println("error al refrescar los datos de mapeo para el obterner el commitId "+r);
		}
		String falta=StringVacio;
		if (ponerFecha && !puestaFecha) {
			falta=falta+ "No se ha establecido la fecha desde.";
		}
		if (ponerCommitId && !puestoCommitId) {
			falta=falta+"No se ha puesto el commitId";
		}
		if (!falta.trim().equals(StringVacio)) {
			showMessage(falta);
		}
		refreshBranch();
	}

	private String getCommitIdFromFile(File fCommit) throws IOException {
		String leido = FileUtils.readFileToString(fCommit);
		String[] lineas = leido.split(NL);
		Pattern pattern = Pattern.compile(confMapeoCommitIdExpresion);
		for (String txt:lineas) {
			try {
		        Matcher matcher = pattern.matcher(txt);
				if (matcher.find()) {
					String encontrado=matcher.group(confMapeoCommitIdExpresionGroup);
					return encontrado;
				}							
			} catch (Exception r) {
				//ignorar
			}
		}
		return null;
	}

	protected void saveMaps() throws IOException {
		String filenameValue=getProjectFile();
		Properties p=new Properties();
		p.setProperty(PROPERTY_BRANCH_NAME, getTextOptionBranch().getText());
		p.setProperty(PROPERTY_FILES_DIR, getTxtFilesDir().getText());
		p.setProperty(PROPERTY_GIT_DIR, getTxtGitdirectory().getText());
		
		p.setProperty(PROPERTY_FICHERO_PARA_FECHA, confMapeoFicheroParaFecha);
		p.setProperty(PROPERTY_FICHERO_PARA_COMMIT_ID, confMapeoFicheroParaCommitId);
		p.setProperty(PROPERTY_COMMIT_ID_EXPRESION, confMapeoCommitIdExpresion);
		p.setProperty(PROPERTY_COMMIT_ID_EXPRESION_GROUP, StringVacio+confMapeoCommitIdExpresionGroup);
		p.setProperty(PROPERTY_MINUTOS_ADD_FECHA, StringVacio+confMapeoMinutosAddFecha);
		
		
		p.setProperty(PROPERTY_USER_NAME, getTxtUserName().getText());
		
		int i=0;
		for (String r:confMapeoReglasMapeo) {
			p.setProperty(PROPERTY_PATH+i, r);
			i++;
		}
		FileOutputStream fileOutputStream=new FileOutputStream(filenameValue);
		p.save(fileOutputStream, StringVacio);
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
				getTextareaOldversion().setText("*ERROR ::"+r); //$NON-NLS-1$
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
				getTextareaOldversion().setText("*ERROR ::"+r); //$NON-NLS-1$
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
		filesGit=new ArrayList<FileVsGit>();
		
		leeDirectorio(dirFiles,rootNode,filesGit,baseDirAbsolute,gitRepo,fechaDesde,tempDirectoy);
		getFilesTreeModel().reload();
		expandAll(getTreeFiles());
	}

	private String getDirectoryName(File dir) {
		String dirAbsolute=dir.getAbsolutePath();
		dirAbsolute=StringUtils.replace(dirAbsolute,"\\","/"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!dirAbsolute.endsWith("/")) { //$NON-NLS-1$
			dirAbsolute=dirAbsolute+"/"; //$NON-NLS-1$
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
	
	private void leeDirectorio(File dirFiles, DefaultMutableTreeNode padreNode,List<FileVsGit> filesGit,String baseDirAbsolute,GitRepo gitRepo,Date fechaDesde,String tempDirectoy) throws Exception {
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
				fileAbs=fileAbs.substring(baseDirAbsolute.length()); //como baseDirAbsolute termina forzosamente por /, fileAbs no comenzará por /
				fileAbs=StringUtils.replace(fileAbs,"\\","/"); //$NON-NLS-1$ //$NON-NLS-2$
				boolean borrar=false;
				if (fileAbs.endsWith(SUFIX_DEL)) {
					fileAbs=fileAbs.substring(0, fileAbs.length()-SUFIX_DEL.length());
					borrar=true;
				}
				String fileAbsMapeado=mapearReglas(fileAbs);  //  src/web/web-prod/srv/main.jsp
				if (fileAbsMapeado!="!") { //$NON-NLS-1$

					
					FileVsGit fileVsGit=new FileVsGit(fich,gitRepo,fileAbsMapeado,borrar,tempDirectoy);
					filesGit.add(fileVsGit);
					if (fileAbsMapeado.contains("codigo.java")) {
						System.out.println(fileAbsMapeado);
					}
					//fileVsGit.initialize(); //.check()
					List<GitFileVersionInfo> versiones=fileVsGit.getVersiones();

					
					
					if (fileVsGit.getVersionesConCambios()<=0) {
						fileVsGit.setNuevasVersionesInfo(NuevasVersionesInfo.SinNuevasVersiones);
					} else {
						boolean distintoAutor=false;
						for (GitFileVersionInfo version:versiones) {
							if (!version.isBase() && !version.getAuthorName().equals(getTxtUserName().getText())) {
								distintoAutor=true;
							}
						}
						if (distintoAutor) {
							fileVsGit.setNuevasVersionesInfo(NuevasVersionesInfo.NuevasVersionesDistintosAutores);
						} else {
							fileVsGit.setNuevasVersionesInfo(NuevasVersionesInfo.NuevasVersionesMismoAutor);
						}
					}
					node = new DefaultMutableTreeNode(fileVsGit);
					padreNode.add(node);
					actualizaConflictoEnNodosPadres(padreNode,fileVsGit.getNuevasVersionesInfo());
				}
			}
			if (fich.isDirectory()) {
				FileInTreeInfo fileInTreeInfo=new FileInTreeInfo(fich.getName());
				fileInTreeInfo.setNuevasVersionesInfo(NuevasVersionesInfo.SinNuevasVersiones);
				node = new DefaultMutableTreeNode(fileInTreeInfo);
				leeDirectorio(fich, node,filesGit, baseDirAbsolute,gitRepo,fechaDesde,tempDirectoy);
				if (node.getChildCount()>0) {
					padreNode.add(node);
				}
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
		while (exp.startsWith("/")) { //$NON-NLS-1$
			exp=exp.substring(1);
		}
		while (exp.endsWith("/")) { //$NON-NLS-1$
			exp=exp.substring(0, exp.length()-1);
		}
		exp=exp.trim();
		return exp;
	}
	
	private String mapearReglas(String fileAbs) { //como baseDirAbsolute termina forzosamente por /, fileAbs no comenzará por /
		fileAbs=StringUtils.replace(fileAbs, "\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		if (confMapeoReglasMapeo==null) {
			return fileAbs;
		}
		for (String s:confMapeoReglasMapeo) {
			StringTokenizer st=new StringTokenizer(s,"|"); //$NON-NLS-1$
			String buscar=trimPathSymbol(st.nextToken())+"/"; //$NON-NLS-1$
			String mapear=trimPathSymbol(st.nextToken())+"/"; //$NON-NLS-1$
			if (fileAbs.startsWith(buscar)) {
				if (mapear.equals("!/")) { //$NON-NLS-1$
					return "!"; //$NON-NLS-1$
				}
				String mapeado=fileAbs.substring(buscar.length());
				mapeado=mapear+mapeado;
				return mapeado;
			}
		}
		return fileAbs;
	}

	private void actualizaConflictoEnNodosPadres(DefaultMutableTreeNode padreNode, NuevasVersionesInfo diferencia) {
		if (padreNode!=null) {
			Object userObject=padreNode.getUserObject();
			boolean seguir=true;
			if (userObject instanceof FileInTreeInfo) {
				FileInTreeInfo fileInTreeInfo=(FileInTreeInfo)userObject;
				if (fileInTreeInfo.getNuevasVersionesInfo()==NuevasVersionesInfo.NuevasVersionesDistintosAutores) {
					seguir=false;
				} else if (fileInTreeInfo.getNuevasVersionesInfo()==diferencia) {
					seguir=false;
				} else if (fileInTreeInfo.getNuevasVersionesInfo()==NuevasVersionesInfo.SinNuevasVersiones) {
					fileInTreeInfo.setNuevasVersionesInfo(diferencia);
				} else if (fileInTreeInfo.getNuevasVersionesInfo()==NuevasVersionesInfo.NuevasVersionesMismoAutor && diferencia==NuevasVersionesInfo.NuevasVersionesDistintosAutores) {
					fileInTreeInfo.setNuevasVersionesInfo(diferencia);
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
			
			FileTreeCellRenderer fileTreeCellRenderer=new FileTreeCellRenderer( iconoSinConflictos,iconoConflictosMismoUsuario,iconoConflictosDistintoUsuario,iconoPendiente,iconoHecho);
			
			treeFiles.setCellRenderer(fileTreeCellRenderer);
			
			treeFiles.addTreeSelectionListener(new TreeSelectionListener() {
	            @Override
	            public void valueChanged(TreeSelectionEvent e) {
	            	getChckbxDone().setSelected(false);
	            	getPanelListaHistoricos().removeAll();
	            	getTextareaOldversion().setText(StringVacio);
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
									openProject();
								} catch (IOException e) {
									//ignorar
								}			            		  
			            	  }
			              } else {
			            	  showMessage("drop only 1 file");
			            	  
			              }
			          }   // end filesDropped
			      });		
		
	}	
	
	
	protected void selGitDir() {
		String dir=askDirectory(getTxtGitdirectory().getText(),"Git directory");
		if (dir!=null) {
			getTxtGitdirectory().setText(dir);
			refreshBranch();
		}
	}
	protected void selFilesDir() {
		String dir=askDirectory(getTxtFilesDir().getText(),"files directory");
		if (dir!=null) {
			getTxtFilesDir().setText(dir);
			refreshMap();
		}
	}	
	protected void selPrjFile() throws IOException {//
		String comboPrj=askFile(getProjectFile(),"project File");
		if (comboPrj!=null) {
			comboUtil.setComboNewValue(comboPrj,getCmbProjectFile(), getCmbProjectFileModel(), comboNameProject);
			openProject();
		}
	}
	public static void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message,"CompareToGit",JOptionPane.OK_OPTION);
	}
	
	
	public String askFile(String actual,String titulo) {
		JFileChooser chooser = new JFileChooser(); 
		File d=null;
		if (actual!=null && !actual.equals(StringVacio)) {
			d=new File(actual);
			if (!d.exists()) {
				d=null;
			}
			if (d!=null && d.isFile()) {
				d=d.getParentFile();
			}
		}
		if (d==null) {
			d=new File("."); //$NON-NLS-1$
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
		if (actual!=null && !actual.equals(StringVacio)) {
			d=new File(actual);
			if (!d.exists()) {
				d=null;
			}
			if (d!=null && d.isFile()) {
				d=d.getParentFile();
			}
		}
		if (d==null) {
			d=new File("."); //$NON-NLS-1$
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
		if (btnGitCompare == null) {
			btnGitCompare = new JButton("git Compare"); //$NON-NLS-1$
			btnGitCompare.setToolTipText("Realizar la comparacion entre el directorio y el repositorio git");
			btnGitCompare.setForeground(Color.BLUE);
			btnGitCompare.setMargin(new Insets(1, 1, 2, 1));
			btnGitCompare.setFont(new Font("Tahoma", Font.BOLD, 13)); //$NON-NLS-1$
			btnGitCompare.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						checkStatus();
					} catch (Exception r) {
						r.printStackTrace();
						showMessage("Error al chequear :: "+r);
					}
				}
			});
			btnGitCompare.setBounds(0, 0, 129, 23);
		}
		return btnGitCompare;
	}
	
	
	
	
	

	private JLabel getLblGit() {
		if (lblGit == null) {
			lblGit = new JLabel("GIT :"); //$NON-NLS-1$
			lblGit.setMinimumSize(new Dimension(24, 24));
			lblGit.setPreferredSize(new Dimension(24, 24));
		}
		return lblGit;
	}
	private JTextField getTxtBranchname() {
		if (txtBranchname == null) {
			txtBranchname = new JTextField();
			txtBranchname.setToolTipText("branch actual del repositorio");
			txtBranchname.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
			txtBranchname.setForeground(Color.MAGENTA);
			txtBranchname.setText("-"); //$NON-NLS-1$
			txtBranchname.setEditable(false);
			txtBranchname.setColumns(10);
		}
		return txtBranchname;
	}
	private JLabel getLblBranchname() {
		if (lblBranchname == null) {
			lblBranchname = new JLabel("branchName:"); //$NON-NLS-1$
		}
		return lblBranchname;
	}
	private JTextField getTxtGitdirectory() {
		if (txtGitdirectory == null) {
			txtGitdirectory = new JTextField();
			txtGitdirectory.setToolTipText("directorio del repositorio git");
			txtGitdirectory.setPreferredSize(new Dimension(6, 20));
			txtGitdirectory.setMinimumSize(new Dimension(6, 20));
			txtGitdirectory.setText(StringVacio);
			txtGitdirectory.setColumns(10);
		}
		return txtGitdirectory;
	}
	private JLabel getLblFilesDir() {
		if (lblFilesDir == null) {
			lblFilesDir = new JLabel("filesDir:"); //$NON-NLS-1$
		}
		return lblFilesDir;
	}
	private JTextField getTxtFilesDir() {
		if (txtFilesDir == null) {
			txtFilesDir = new JTextField();
			txtFilesDir.setToolTipText("directorio a comparar");
			txtFilesDir.setText(StringVacio);
			txtFilesDir.setColumns(10);
		}
		return txtFilesDir;
	}
	private JButton getBtnOpenGitDir() {
		if (btnOpenGitDir == null) {
			btnOpenGitDir = new JButton("selectGit"); //$NON-NLS-1$
			btnOpenGitDir.setToolTipText("selecciona el directorio del repositorio git");
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
			btnOpenFilesDir = new JButton("selectDir"); //$NON-NLS-1$
			btnOpenFilesDir.setToolTipText("selecciona el directorio a comparar con git");
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
			panelArbol.add(getPanelOpcionesArbol(), BorderLayout.SOUTH);
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
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("-"); //$NON-NLS-1$
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
			lblCommitid = new JLabel("commitId:"); //$NON-NLS-1$
		}
		return lblCommitid;
	}
	private JTextField getTxtCommitId() {
		if (txtCommitId == null) {
			txtCommitId = new JTextField();
			//txtCommitId.setText("338685d");
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
			lblUsername = new JLabel("userName:"); //$NON-NLS-1$
		}
		return lblUsername;
	}
	private JTextField getTxtUserName() {
		if (txtUserName == null) {
			txtUserName = new JTextField();
			txtUserName.setToolTipText("tu usuario, del que dar por bueno los cambios");
			txtUserName.setColumns(10);
		}
		return txtUserName;
	}
	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("  "); //$NON-NLS-1$
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					getTxtCommitId().setCaretPosition(0);
				}
			});
		}
		return label;
	}
	private JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel("  "); //$NON-NLS-1$
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
			txtrTextareaOldversion.setText(StringVacio);
		}
		return txtrTextareaOldversion;
	}
	private JButton getBtnRefresh() {
		if (btnRefresh == null) {
			btnRefresh = new JButton("refresh"); //$NON-NLS-1$
			btnRefresh.setToolTipText("actualizar la informacion de git");
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
			lblPrjfile = new JLabel("prjFile:"); //$NON-NLS-1$
		}
		return lblPrjfile;
	}
	private JPanel getPanelPrjButtons() {
		if (panelPrjButtons == null) {
			panelPrjButtons = new JPanel();
			panelPrjButtons.setLayout(new FlowLayout(FlowLayout.LEADING, 3, 0));
			panelPrjButtons.add(getBtnOpenprj());
			panelPrjButtons.add(getBtnLoad());
			panelPrjButtons.add(getBtnMaps());
			panelPrjButtons.add(getBtnSave());
			
		}
		return panelPrjButtons;
	}
	private JButton getBtnOpenprj() {
		if (btnOpenprj == null) {
			btnOpenprj = new JButton("openPrj"); //$NON-NLS-1$
			btnOpenprj.setToolTipText("selecciona un nuevo fichero de proyecto");
			btnOpenprj.setMargin(new Insets(1, 1, 2, 1));
			btnOpenprj.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						selPrjFile();
					} catch (IOException r) {
						showMessage("error "+r); //$NON-NLS-1$
					}
				}
			});
		}
		return btnOpenprj;
	}
	
	private JButton getBtnLoad() {
		if (btnLoad == null) {
			btnLoad = new JButton("load"); //$NON-NLS-1$
			btnLoad.setToolTipText("carga el fichero de proyecto");
			btnLoad.setMargin(new Insets(1, 1, 2, 1));
			btnLoad.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						openProject();
					} catch (IOException r) {
						showMessage("error "+r); //$NON-NLS-1$
					}
				}
			});
		}
		return btnLoad;
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
						if (false) {
							comboProjectFileChanged();
						}
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
			btnMaps = new JButton("maps"); //$NON-NLS-1$
			btnMaps.setToolTipText("muestra los mapeos y otras configuraciones");
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
		for (String r:confMapeoReglasMapeo) {
			strReglasMapeo.append(r).append(NL);
		}
		return strReglasMapeo.toString();
	}
	
	protected List<String> listReglasMapeo(String reglas) throws Exception {
		List<String> lista=new ArrayList<String>();
		StringTokenizer st=new StringTokenizer(reglas, NL);
		while (st.hasMoreTokens()) {
			String r=st.nextToken().trim();
			if (!r.trim().equals(StringVacio)) {
				if (!r.contains("|")) { //$NON-NLS-1$
					throw new Exception("falta  |  en  "+r);
				}
				StringTokenizer st2=new StringTokenizer(r,"|"); //$NON-NLS-1$
				String buscar=trimPathSymbol(st2.nextToken());
				String mapear=trimPathSymbol(st2.nextToken());
				if (st2.hasMoreTokens()) {
					throw new Exception("demasiadas  |  en  "+r);
				}
				lista.add(buscar+"|"+mapear); //$NON-NLS-1$
			}
		}
		return lista;
	}
	
	protected void openMaps() {
		openMaps(getReglasMapeoAsString());
	}

	protected void openMaps(String reglas) {
		MapeoDlg mapeoDlg=new MapeoDlg(null,reglas,confMapeoFicheroParaFecha,confMapeoFicheroParaCommitId,confMapeoCommitIdExpresion,confMapeoCommitIdExpresionGroup,confMapeoMinutosAddFecha);
		boolean aceptado=mapeoDlg.showDialog();
		String respuestaReglas=StringVacio;
		if (aceptado) {
			try {
				MapeoInfo mapeoInfo=mapeoDlg.getReglasMapeo();
				confMapeoFicheroParaFecha=mapeoInfo.ficheroParaFecha;
				confMapeoFicheroParaCommitId=mapeoInfo.ficheroParaCommitId;
				confMapeoCommitIdExpresion=mapeoInfo.commitIdExpresion;
				confMapeoCommitIdExpresionGroup=mapeoInfo.commitIdExpresionGroup;
				confMapeoMinutosAddFecha=mapeoInfo.minutesAddDate;
				respuestaReglas=mapeoInfo.reglasMapeo;
				respuestaReglas=StringUtils.replace(respuestaReglas, "\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
				confMapeoReglasMapeo=listReglasMapeo(respuestaReglas);
			} catch (Exception r) {
				showMessage(StringVacio+r);
				openMaps(respuestaReglas);
			}
			refreshMapeoData();
		}
	}
	
	private JButton getBtnSave() {
		if (btnSave == null) {
			btnSave = new JButton("save"); //$NON-NLS-1$
			btnSave.setToolTipText("guarda el fichero de proyecto");
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
			panelGitDir.add(getPanelGitOptions()); 
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
			panelFechaFiles.add(getPanelOpcionFecha(), BorderLayout.EAST);
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
			lblFecha = new JLabel("fromDate"); //$NON-NLS-1$
			lblFecha.setToolTipText("solo listar ficheros modificados desde yyyy/MM/dd HH:mm:ss");
		}
		return lblFecha;
	}
	private JComboBox<String> getCmbFechaDesde() {
		if (cmbFechaDesde == null) {
			cmbFechaDesde = new JComboBox<String>();
			cmbFechaDesde.setToolTipText(DATEFORMAT_yyyyMMdd_HHmmss);
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
				if (x0!=null && !x0.trim().equals(StringVacio)) {
					cmbFechaDesdeModel.insertElementAt(StringVacio, 0);
				}
			} catch (Exception r) {
				comboUtil.savePreference(StringVacio, comboNameFecha);
			}
		}
		return cmbFechaDesdeModel;
	}
	
	
	protected Date getFecha() throws Exception {
		String fechaValue=comboUtil.getComboValue(getCmbFechaDesde(), getCmbFechaDesdeModel(), comboNameFecha);
		if (fechaValue==null || fechaValue.trim().equals(StringVacio)) {
			return null;
		}
		Date date = null;
		SimpleDateFormat sdf=null;
		try {
			sdf=new SimpleDateFormat(DATEFORMAT_yyyyMMdd_HHmmss);
			date=sdf.parse(fechaValue);
		} catch (ParseException e) {
			try {
				sdf=new SimpleDateFormat(DATEFORMAT_yyyyMMdd_HHmm);
				date=sdf.parse(fechaValue);
			} catch (ParseException e1) {
				try {
					sdf=new SimpleDateFormat(DATEFORMAT_yyyyMMdd);
					date=sdf.parse(fechaValue);
				} catch (Exception e2) {	
					throw new Exception("ERROR "+fechaValue+" is not  "+DATEFORMAT_yyyyMMdd_HHmmss+"   or   "+DATEFORMAT_yyyyMMdd_HHmm+"  "+e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				}
			}
		}
		
		return date;
	}
	
	
	
	private JPanel getPanelGitSelec() {
		if (panelGitSelec == null) {
			panelGitSelec = new JPanel();
			panelGitSelec.setPreferredSize(new Dimension(24, 24));
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
			panelAccionesSeleccion.add(getBtnCompG());
			panelAccionesSeleccion.add(getLblNewLabel_1());
			panelAccionesSeleccion.add(getBtnCopy());
		}
		return panelAccionesSeleccion;
	}
	private JButton getBtnCompareVsGit() {
		if (btnCompareVsGit == null) {
			btnCompareVsGit = new JButton("comp-F-FGD"); //$NON-NLS-1$
			btnCompareVsGit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					comparaFicheroConVersionEnDirectorioGit();
				}
			});
			btnCompareVsGit.setMargin(new Insets(1, 1, 2, 1));
			btnCompareVsGit.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
			btnCompareVsGit.setToolTipText("compara  el Fichero  con  el Fichero en Directorio Git");
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
			showMessage("ERROR executing compare command "+compareCommand+" :: "+e); //$NON-NLS-2$
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
			showMessage("ERROR executing compare command "+compareCommand+" :: "+e); //$NON-NLS-2$
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
	                
	                File tempFile=new File(fileVsGit.getGitRepo().getTempDir().getPath()+"/"+fileVsGit.getFich().getName()+"-"+System.currentTimeMillis()); //$NON-NLS-1$ //$NON-NLS-2$
	                try {
						FileUtils.copyFile(new File(fileVsGit.getFileInGit()), tempFile);
		                File rigthFile=tempFile; //new File(fileVsGit.getFileInGit()); //fileVsGit.getVersiones().get(0).getTempFile();
		                File outputFile=new File(fileVsGit.getFileInGit());
		                comparado=true;
		                compare4(leftFile,rigthFile,centerFile,outputFile);
					} catch (IOException e) {
						showMessage("Error al abrir la comparacion a 3");
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
				showMessage("ERROR "+e); //$NON-NLS-1$
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
                	if (origen.getName().endsWith(SUFIX_DEL)) {
                		FileUtils.forceDelete(destino);
                	} else {
                		FileUtils.copyFile(origen, destino);
                	}
                	getChckbxDone().setSelected(true);
                	refreshFileStatus();
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
                if (fileVsGit.getVersiones().size()>0) {
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
			btnComparebase = new JButton("comp3-F-VBG-FGD->FGD"); //$NON-NLS-1$
			btnComparebase.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					compararFicheroConVersionEnDirGitTeniendoEnCuentaLaVersionBase();
				}
			});
			btnComparebase.setMargin(new Insets(1, 1, 2, 1));
			btnComparebase.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
			btnComparebase.setToolTipText("compara  el Fichero  con  el Fichero en Directorio Git  teniendo en cuenta  la Version Base en Git");
		}
		return btnComparebase;
	}
	private JCheckBox getChck2versions() {
		if (chck2versions == null) {
			chck2versions = new JCheckBox(StringVacio);
			chck2versions.setToolTipText("enable 2vers (compare between 2 git versions) - habilita 2vers");
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
			btn2Versiones = new JButton("2vers"); //$NON-NLS-1$
			btn2Versiones.setEnabled(false);
			btn2Versiones.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					comparar2versionesGit();
				}
			});
			btn2Versiones.setMargin(new Insets(1, 1, 2, 1));
			btn2Versiones.setFont(new Font("Tahoma", Font.PLAIN, 10)); //$NON-NLS-1$
			btn2Versiones.setToolTipText("comparar 2 versiones de git");
		}
		return btn2Versiones;
	}


	private JButton getBtnCopy() {
		if (btnCopy == null) {
			btnCopy = new JButton("copy-F->FGD"); //$NON-NLS-1$
			btnCopy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					copyFicheroADirectoriGit();
				}
			});
			btnCopy.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
			btnCopy.setMargin(new Insets(1, 1, 2, 1));
			btnCopy.setToolTipText("copiar (o borrar (si nombre es *-del))  el Fichero  sobre  el Fichero en Directorio Git");
		}
		return btnCopy;
	}


	private JButton getBotonOpcionCompare() {
		if (botonOpcionCompare == null) {
			botonOpcionCompare = new JButton("?"); //$NON-NLS-1$
			botonOpcionCompare.setToolTipText("settings / ajustes de comparacion");
			botonOpcionCompare.setFont(new Font("Tahoma", Font.PLAIN, 9)); //$NON-NLS-1$
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
			chckbxDone = new JCheckBox("done)  Fich:"); //$NON-NLS-1$
			chckbxDone.setToolTipText("Marca el fichero seleccionado como Realizado");
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
			btnCompG = new JButton("comp-VG-FGD"); //$NON-NLS-1$
			btnCompG.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ComparaUltimaVersionGitConVersionDirectorioGit();
				}
			});
			btnCompG.setMargin(new Insets(1, 1, 2, 1));
			btnCompG.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
			btnCompG.setToolTipText("compara  la ultima Version en Git  con  la Fichero en Directorio Git");
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
			btnExplorerFich = new JButton("expF"); //$NON-NLS-1$
			btnExplorerFich.setToolTipText("explorer en carpeta del Fichero");
			btnExplorerFich.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					explorerFichero();
				}
			});
			btnExplorerFich.setMargin(new Insets(1, 1, 2, 1));
			btnExplorerFich.setFont(new Font("Tahoma", Font.PLAIN, 10)); //$NON-NLS-1$
		}
		return btnExplorerFich;
	}


	private JButton getBtnExplorerTemp() {
		if (btnExplorerTemp == null) {
			btnExplorerTemp = new JButton("expT"); //$NON-NLS-1$
			btnExplorerTemp.setToolTipText("explorer en carpeta Temporal");
			btnExplorerTemp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					explorerTemporal();
				}
			});
			btnExplorerTemp.setMargin(new Insets(1, 1, 2, 1));
			btnExplorerTemp.setFont(new Font("Tahoma", Font.PLAIN, 10)); //$NON-NLS-1$
		}
		return btnExplorerTemp;
	}


	private JButton getBtnExplorerFGD() {
		if (btnExplorerFGD == null) {
			btnExplorerFGD = new JButton("expFGD"); //$NON-NLS-1$
			btnExplorerFGD.setToolTipText("explorer en carpeta del Fichero en el Directorio Git");
			btnExplorerFGD.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					explorerFicheroGit();
				}
			});
			btnExplorerFGD.setMargin(new Insets(1, 1, 2, 1));
			btnExplorerFGD.setFont(new Font("Tahoma", Font.PLAIN, 10)); //$NON-NLS-1$
		}
		return btnExplorerFGD;
	}
	


	private JPanel getPanelGitOptions() {
		if (panelGitOptions == null) {
			panelGitOptions = new JPanel();
			panelGitOptions.setPreferredSize(new Dimension(24, 24));
			panelGitOptions.setLayout(new BorderLayout(0, 0));
			panelGitOptions.add(getSplitPaneGitOptions(), BorderLayout.CENTER);
		}
		return panelGitOptions;
	}
	private JSplitPane getSplitPaneGitOptions() {
		if (splitPaneGitOptions == null) {
			splitPaneGitOptions = new JSplitPane();
			splitPaneGitOptions.setDividerLocation(200);
			splitPaneGitOptions.setLeftComponent(getPanelGitSelec());
			splitPaneGitOptions.setRightComponent(getPanelGitOptionBranch());
		}
		return splitPaneGitOptions;
	}
	private JPanel getPanelGitOptionBranch() {
		if (panelGitOptionBranch == null) {
			panelGitOptionBranch = new JPanel();
			panelGitOptionBranch.setLayout(new BorderLayout(0, 0));
			panelGitOptionBranch.add(getLblOptionBranch(), BorderLayout.WEST);
			panelGitOptionBranch.add(getTextOptionBranch(), BorderLayout.CENTER);
		}
		return panelGitOptionBranch;
	}
	private JLabel getLblOptionBranch() {
		if (lblOptionBranch == null) {
			lblOptionBranch = new JLabel("branch"); //$NON-NLS-1$
		}
		return lblOptionBranch;
	}
	private JTextField getTextOptionBranch() {
		if (textOptionBranch == null) {
			textOptionBranch = new JTextField();
			textOptionBranch.setToolTipText("comprobar que el repositorio se encuentra en este branch");
			textOptionBranch.setColumns(10);
		}
		return textOptionBranch;
	}
	private JLabel getLblNewLabel_1() {
		if (lblNewLabel_1 == null) {
			lblNewLabel_1 = new JLabel("-"); //$NON-NLS-1$
		}
		return lblNewLabel_1;
	}
	private JPanel getPanelOpcionesArbol() {
		if (panelOpcionesArbol == null) {
			panelOpcionesArbol = new JPanel();
			panelOpcionesArbol.setLayout(new BorderLayout(0, 0));
			panelOpcionesArbol.add(getPanelBotoneraArbol());
		}
		return panelOpcionesArbol;
	}
	private JPanel getPanelBotoneraArbol() {
		if (panelBotoneraArbol == null) {
			panelBotoneraArbol = new JPanel();
			panelBotoneraArbol.setLayout(new FlowLayout(FlowLayout.LEADING, 2, 0));
			panelBotoneraArbol.add(getFileStatus());
			panelBotoneraArbol.add(getAllFilesStatus());
		}
		return panelBotoneraArbol;
	}
	private JButton getFileStatus() {
		if (fileStatus == null) {
			fileStatus = new JButton("fileStatus");
			fileStatus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					refreshFileStatus();
				}
			});
			fileStatus.setMargin(new Insets(1, 1, 2, 1));
			fileStatus.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
			fileStatus.setToolTipText("recarga el estado de comparacion del fichero");
		}
		return fileStatus;
	}
	protected void refreshFileStatus() {
        if (selectedNode!=null) {
            Object userObject=selectedNode.getUserObject();
            if (userObject!=null && userObject instanceof FileVsGit) {
                FileVsGit fileVsGit=(FileVsGit)userObject;
                fileVsGit.evaluaComparacionInfo();
            }
        }
        getPanelListaHistoricos().revalidate();
        getPanelListaHistoricos().repaint();
        treeFiles.revalidate();
        treeFiles.repaint();
	}

	private JButton getAllFilesStatus() {
		if (allFilesStatus == null) {
			allFilesStatus = new JButton("allFilesStatus");
			allFilesStatus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					refreshFilesStatus();
				}
			});
			allFilesStatus.setMargin(new Insets(1, 1, 2, 1));
			allFilesStatus.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
			allFilesStatus.setToolTipText("recarga el estado de comparacion de todos los ficheros");
		}
		return allFilesStatus;
	}

	protected void refreshFilesStatus() {
		if (filesGit!=null) {
			for (FileVsGit fileVsGit:filesGit) {
				fileVsGit.evaluaComparacionInfo();
			}
		}
        getPanelListaHistoricos().revalidate();
        getPanelListaHistoricos().repaint();
        treeFiles.revalidate();
        treeFiles.repaint();
        /*
        DefaultTreeModel model = (DefaultTreeModel)treeFiles.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        //root.add(new DefaultMutableTreeNode("another_child"));
        model.reload(root);
        */
	}
	private JPanel getPanelOpcionFecha() {
		if (panelOpcionFecha == null) {
			panelOpcionFecha = new JPanel();
		}
		return panelOpcionFecha;
	}
}
