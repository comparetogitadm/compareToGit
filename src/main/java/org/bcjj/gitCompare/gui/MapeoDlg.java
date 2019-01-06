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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			MapeoDlg dialog = new MapeoDlg(null,"x");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public MapeoDlg(Window owner,String reglasMapeo) {
		 super(owner,ModalityType.APPLICATION_MODAL);
		contentPanel = new JPanel();
		setBounds(100, 100, 600, 541);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		contentPanel.add(getScrollPaneReglas(), BorderLayout.CENTER);
		contentPanel.add(getPanelAyuda(), BorderLayout.NORTH);
		getContentPane().add(getButtonPane(), BorderLayout.SOUTH);
		getRootPane().setDefaultButton(getOkButton());
		getTextReglasMapeo().setText(reglasMapeo);
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
			txtPanelAyuda.setEditable(false);
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

	public String getReglasMapeo() {
		return getTextReglasMapeo().getText();
	}
	
	private JPanel getPanelBotoneraIzq() {
		if (panelBotoneraIzq==null) {
			panelBotoneraIzq = new JPanel();
			panelBotoneraIzq.add(getBtnGensample());
		}
		return panelBotoneraIzq;
	}
	
	private JButton getBtnGensample() {
		if (btnGensample==null) {
			btnGensample = new JButton("genSample");
			btnGensample.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getTextReglasMapeo().setText("deployment\\srv-war\\WEB-INF\\java|src\\java"+NL
							+"deployment\\srv-war\\WEB-INF\\resources|src\\web\\web-prod\\conf"+NL
							+"deployment\\srv-war|src\\web\\web-prod\\srv"+NL
							+"deployment/srv-war/WEB-INF/classes|!"+NL
							+"deployment/srv-war/WEB-INF/lib|!");
				}
			});
		}
		return btnGensample;
	}

	
	@Override
	public void actionPerformed(ActionEvent ae) {
		      Object source = ae.getSource();
		      if (source == getOkButton()) {
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
	
}
