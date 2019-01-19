package org.bcjj.gitCompare.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.Dialog.ModalityType;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.Toolkit;

public class MapeoDlg extends JDialog implements ActionListener {

	private final JPanel contentPanel;

	JTextPane textReglasMapeo;
	JButton btnGensample;
	JPanel panelBotoneraIzq;
	JButton okButton;
	JButton cancelButton;
	JPanel panelBotoneraDer;
	JPanel buttonPane;
	JTextPane txtPanelAyuda;
	JPanel panelAyuda;
	JScrollPane scrollPaneReglas;
	
	String NL="\r\n";
	
	boolean aceptado=false;
	private JPanel panelOtrosDatos;
	private JPanel panelFichParaFecha;
	private JPanel panelFichParaCommitId;
	private JPanel panelCommitIdRegExp;
	private JLabel lblFichParaFecha;
	private JTextField textFichParaFecha;
	private JLabel lblFichParaCommitId;
	private JTextField textFichParaCommitId;
	private JLabel lblCommitIdRegExp;
	private JTextField textCommitIdRegExp;
	private JTextField textGroupNumber;
	private JButton btnNewButton;
	
	public static class MapeoInfo {
		String reglasMapeo;
		String ficheroParaFecha;
		String ficheroParaCommitId;
		String commitIdExpresion;
		int commitIdExpresionGroup;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			String txt=" el commitId: 1a2b3c4d5e6f8 es el origen ";
			String pat="(.*)(commitId:)(\\s)*(\\w{6})(.*)";
			
			pat="(.*)(commitId:)(\\s)*(\\w{6})(.*)";
			txt="  commitId: 2c24e4d   ";
			
			Pattern pattern = Pattern.compile(pat);
	        Matcher matcher = pattern.matcher(txt);
			if (matcher.find()) {
				System.out.println("grupo2: ["+matcher.group(4)+"]");
			}
			
			MapeoDlg dialog = new MapeoDlg(null,"x","fich4date","info",txt,4);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public MapeoDlg(Window owner,String reglasMapeo,String ficheroParaFecha,String ficheroParaCommitId, String expresionCommitId, int groupCommitId ) {
		 super(owner,ModalityType.APPLICATION_MODAL);
		 setTitle("Mapeo y otra configuracion");
		 setIconImage(Toolkit.getDefaultToolkit().getImage(MapeoDlg.class.getResource("/images/arrow-divide2.png")));
		contentPanel = new JPanel();
		setBounds(100, 100, 600, 541);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		contentPanel.add(getScrollPaneReglas(), BorderLayout.CENTER);
		contentPanel.add(getPanelAyuda(), BorderLayout.NORTH);
		getContentPane().add(getButtonPane(), BorderLayout.SOUTH);
		contentPanel.add(getPanelOtrosDatos(), BorderLayout.SOUTH);
		getRootPane().setDefaultButton(getOkButton());
		getTextReglasMapeo().setText(reglasMapeo);
		getTextFichParaFecha().setText(ficheroParaFecha);
		getTextFichParaCommitId().setText(ficheroParaCommitId);
		getTextCommitIdRegExp().setText(expresionCommitId);
		getTextGroupNumber().setText(""+groupCommitId);
		
	}
	
	private JTextPane getTextReglasMapeo() {
		if (textReglasMapeo==null) {
			textReglasMapeo = new JTextPane();
		}
		return textReglasMapeo;
	}
	
	private JScrollPane getScrollPaneReglas() {
		if (scrollPaneReglas==null) {
			scrollPaneReglas = new JScrollPane();
			scrollPaneReglas.setViewportView(getTextReglasMapeo());
		}
		return scrollPaneReglas;
	}
	
	private JPanel getPanelAyuda() {
		if (panelAyuda==null) {
			panelAyuda = new JPanel();
			
			panelAyuda.setLayout(new BorderLayout(0, 0));
			panelAyuda.add(getTxtPanelAyuda());
		}
		return panelAyuda;
	}
	
	private JTextPane getTxtPanelAyuda() {
		if (txtPanelAyuda==null) {
			txtPanelAyuda = new JTextPane();
			txtPanelAyuda.setText("- Una linea por cada mapeo, separando las rutas con | (pipe)\r\n- ruta file | ruta git\r\n- Las rutas ser\u00E1n relativas a los directorios de files o git.\r\n- El orden es importante, la primera ruta file que coincida, determina el mapeo.\r\n- Si no coinicide ninguna se supone que va de file directo a git\r\n- Si ruta git es ! entonces es que no se debe tener en cuenta (ejemplo: deployment/srv-war/WEB-INF/class|!)");
		}
		return txtPanelAyuda;
	}

	private JPanel getButtonPane() {
		if (buttonPane==null) {
			buttonPane = new JPanel();
			buttonPane.setLayout(new BorderLayout(0, 0));
			buttonPane.add(getPanelBotoneraDer(), BorderLayout.EAST);
			buttonPane.add(getPanelBotoneraIzq(), BorderLayout.WEST);
		}
		return buttonPane;
	}

	
	private JPanel getPanelBotoneraDer() {
		if (panelBotoneraDer==null) {
			panelBotoneraDer = new JPanel();
			panelBotoneraDer.add(getCancelButton());
			panelBotoneraDer.add(getOkButton());
		}
		return panelBotoneraDer;
	}
	
	private JButton getCancelButton() {
		if (cancelButton==null) {
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}
	
	private JButton getOkButton() {
		if (okButton==null) {
			okButton = new JButton("OK");
			okButton.addActionListener(this);
		}
		return okButton;
	}

	
	public boolean getAceptado() {
		return aceptado;
	}


	
	
	public MapeoInfo getReglasMapeo() throws Exception {
		MapeoInfo mapeoInfo=new MapeoInfo();
		mapeoInfo.reglasMapeo=getTextReglasMapeo().getText();
		mapeoInfo.ficheroParaFecha=getTextFichParaFecha().getText();
		mapeoInfo.ficheroParaCommitId=getTextFichParaCommitId().getText();
		mapeoInfo.commitIdExpresion=getTextCommitIdRegExp().getText();
		try {
			Pattern pattern = Pattern.compile(mapeoInfo.commitIdExpresion);
		} catch (Exception r) {
			throw new Exception("Error la expresion para el commitId no funciona");
		}
		try {
			mapeoInfo.commitIdExpresionGroup=Integer.parseInt(getTextGroupNumber().getText());
		} catch (Exception r) {
			throw new Exception("el grupo para la expresion de commitId no es un numero");
		}
		return mapeoInfo;
	}
	
	private JPanel getPanelBotoneraIzq() {
		if (panelBotoneraIzq==null) {
			panelBotoneraIzq = new JPanel();
			panelBotoneraIzq.add(getBtnGensample());
			panelBotoneraIzq.add(getBtnNewButton());
		}
		return panelBotoneraIzq;
	}
	
	private JButton getBtnGensample() {
		if (btnGensample==null) {
			btnGensample = new JButton("genSample");
			btnGensample.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getTextReglasMapeo().setText("srv-war\\WEB-INF\\java|src\\java"+NL
							+"srv-war\\WEB-INF\\config|src\\web\\web-prod\\properties"+NL
							+"srv-war/WEB-INF/classes|!"+NL
							+"srv-war/WEB-INF/lib|!"+NL
							+"srv-war|src\\web\\web-prod\\srv"+NL);
				}
			});
		}
		return btnGensample;
	}

	public static void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message,"CompareToGit",JOptionPane.OK_OPTION);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		      Object source = ae.getSource();
		      if (source == getOkButton()) {
		    	 try {
		    	  getReglasMapeo();
		    	 } catch (Exception r) {
		    		 showMessage("datos incorrectos: "+r);
		    		 return;
		    	 }
		    	  aceptado=true;
		      }
		      else {
		    	  aceptado=false;
		      }
		      dispose();
	}
	
	public boolean showDialog() {
		      this.setVisible(true);
		      return aceptado;
	}
	
	private JPanel getPanelOtrosDatos() {
		if (panelOtrosDatos == null) {
			panelOtrosDatos = new JPanel();
			panelOtrosDatos.setLayout(new BorderLayout(0, 0));
			panelOtrosDatos.add(getPanelFichParaFecha(), BorderLayout.NORTH);
			panelOtrosDatos.add(getPanelFichParaCommitId(), BorderLayout.CENTER);
			panelOtrosDatos.add(getPanelCommitIdRegExp(), BorderLayout.SOUTH);
		}
		return panelOtrosDatos;
	}
	private JPanel getPanelFichParaFecha() {
		if (panelFichParaFecha == null) {
			panelFichParaFecha = new JPanel();
			panelFichParaFecha.setLayout(new BorderLayout(0, 0));
			panelFichParaFecha.add(getLblFichParaFecha(), BorderLayout.WEST);
			panelFichParaFecha.add(getTextFichParaFecha(), BorderLayout.CENTER);
		}
		return panelFichParaFecha;
	}
	private JPanel getPanelFichParaCommitId() {
		if (panelFichParaCommitId == null) {
			panelFichParaCommitId = new JPanel();
			panelFichParaCommitId.setLayout(new BorderLayout(0, 0));
			panelFichParaCommitId.add(getLblFichParaCommitId(), BorderLayout.WEST);
			panelFichParaCommitId.add(getTextFichParaCommitId(), BorderLayout.CENTER);
		}
		return panelFichParaCommitId;
	}
	private JPanel getPanelCommitIdRegExp() {
		if (panelCommitIdRegExp == null) {
			panelCommitIdRegExp = new JPanel();
			panelCommitIdRegExp.setLayout(new BorderLayout(0, 0));
			panelCommitIdRegExp.add(getLblCommitIdRegExp(), BorderLayout.WEST);
			panelCommitIdRegExp.add(getTextCommitIdRegExp(), BorderLayout.CENTER);
			panelCommitIdRegExp.add(getTextGroupNumber(), BorderLayout.EAST);
		}
		return panelCommitIdRegExp;
	}
	private JLabel getLblFichParaFecha() {
		if (lblFichParaFecha == null) {
			lblFichParaFecha = new JLabel(" Fichero para fechaDesde: ");
		}
		return lblFichParaFecha;
	}
	private JTextField getTextFichParaFecha() {
		if (textFichParaFecha == null) {
			textFichParaFecha = new JTextField();
			textFichParaFecha.setColumns(10);
		}
		return textFichParaFecha;
	}
	private JLabel getLblFichParaCommitId() {
		if (lblFichParaCommitId == null) {
			lblFichParaCommitId = new JLabel(" Fichero para commitId:   ");
		}
		return lblFichParaCommitId;
	}
	private JTextField getTextFichParaCommitId() {
		if (textFichParaCommitId == null) {
			textFichParaCommitId = new JTextField();
			textFichParaCommitId.setColumns(10);
		}
		return textFichParaCommitId;
	}
	private JLabel getLblCommitIdRegExp() {
		if (lblCommitIdRegExp == null) {
			lblCommitIdRegExp = new JLabel(" Patron commitId:   ");
		}
		return lblCommitIdRegExp;
	}
	private JTextField getTextCommitIdRegExp() {
		if (textCommitIdRegExp == null) {
			textCommitIdRegExp = new JTextField();
			textCommitIdRegExp.setToolTipText("ejemplo:  (.*)(commitId:)(\\s)*(\\w{6})(.*)  y  4  para la cadena 'el commitId: 1a2b3c4d5e6f8 es el origen'");
			textCommitIdRegExp.setColumns(10);
		}
		return textCommitIdRegExp;
	}
	private JTextField getTextGroupNumber() {
		if (textGroupNumber == null) {
			textGroupNumber = new JTextField();
			textGroupNumber.setToolTipText("ejemplo: 4");
			textGroupNumber.setColumns(10);
		}
		return textGroupNumber;
	}
	private JButton getBtnNewButton() {
		if (btnNewButton == null) {
			btnNewButton = new JButton("genPatronCommit");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getTextCommitIdRegExp().setText("(.*)(commitId:)(\\s)*(\\w{6})(.*)");
					getTextGroupNumber().setText("4");
				}
			});
		}
		return btnNewButton;
	}
}
