package org.bcjj.gitCompare.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.bcjj.gitCompare.GitFileVersionInfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Historico extends JPanel {
	private Historico esteHistorico;
	private JPanel panelDatos;
	private JLabel lblCommitid;
	private JTextField txtCommitid;
	private JLabel lblFecha;
	private JTextField txtFecha;
	private JLabel lblAutor;
	private JTextField txtAutor;
	private JLabel lblMsg;
	private JScrollPane scrollPane;
	private JTextArea txtareaMessage;
	private GitFileVersionInfo gitFileVersionInfo;
	private ContenedorHistorico contenedorHistorico;
	private JPanel panelPrincipal;
	private JPanel panelBotonera;
	private JButton btnCompFile;
	private JLabel lblNewLabel;
	private JButton btnCompFileGitDir;
	private JPanel panelInf;
	private JPanel panelLeft;
	/**
	 * Create the panel.
	 */
	public Historico() {
		esteHistorico=this;
		setPreferredSize(new Dimension(300, 134));
		setMinimumSize(new Dimension(300, 180));
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(getPanelPrincipal());
		this.setVisible(true);
	}
	
	private JPanel getPanelPrincipal() {
		if (panelPrincipal == null) {
			panelPrincipal = new JPanel();
			panelPrincipal.setLayout(new BorderLayout(0, 0));
			panelPrincipal.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
			panelPrincipal.add(getPanelDatos());
			panelPrincipal.add(getPanelBotonera(), BorderLayout.NORTH);
			panelPrincipal.add(getPanelInf(), BorderLayout.SOUTH);
			panelPrincipal.add(getPanelLeft(), BorderLayout.WEST);
		}
		return panelPrincipal;
	}
	
	public Historico(GitFileVersionInfo gitFileVersionInfo,ContenedorHistorico contenedorHistorico) {
		this();
		this.gitFileVersionInfo=gitFileVersionInfo;
		this.contenedorHistorico=contenedorHistorico;
		if (gitFileVersionInfo.isRealCommitId()) {
			getTxtCommitid().setText(gitFileVersionInfo.getCommitId());
		} else {
			getTxtCommitid().setText("*"+gitFileVersionInfo.getCommitId());
		}
		getTxtCommitid().setCaretPosition(0);
		getTxtFecha().setText(gitFileVersionInfo.getFechaYMDHMS());
		getTxtAutor().setText(gitFileVersionInfo.getAuthorName());
		getTxtAutor().setCaretPosition(0);
		getTxtareaMessage().setText(gitFileVersionInfo.getFullMessage()); //+"\r\n 1 En mi ecplise no sale el asistente y hay que rellenar la especificacion de columnas y filas a mano+\r\n 2 En mi ecplise no sale el asistente y hay que rellenar la especificacion de columnas y filas a mano");
		getTxtareaMessage().setCaretPosition(0);
	}
	
	public GitFileVersionInfo getGitFileVersionInfo() {
		return gitFileVersionInfo;
	}
	
	private JPanel getPanelDatos() {
		if (panelDatos == null) {
			panelDatos = new JPanel(); //En mi ecplise no sale el asistente y hay que rellenar la especificacion de columnas y filas a mano. 
			panelDatos.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (contenedorHistorico!=null) {
						contenedorHistorico.onHistoricoSeleccionado(esteHistorico);
					}
				}
			});
			panelDatos.setPreferredSize(new Dimension(100, 100));
			panelDatos.setLayout(new FormLayout(new ColumnSpec[] {
					FormSpecs.DEFAULT_COLSPEC,
					FormSpecs.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("80dlu"),
					FormSpecs.RELATED_GAP_COLSPEC,
					FormSpecs.DEFAULT_COLSPEC,
					FormSpecs.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow"),
					FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
					FormSpecs.DEFAULT_ROWSPEC,
					FormSpecs.RELATED_GAP_ROWSPEC,
					FormSpecs.DEFAULT_ROWSPEC,
					FormSpecs.RELATED_GAP_ROWSPEC,
					RowSpec.decode("40dlu"),
					FormSpecs.RELATED_GAP_ROWSPEC,}));
			panelDatos.add(getLblCommitid(), "1, 1, right, default");
			panelDatos.add(getTxtCommitid(), "3, 1, 5, 1, fill, default");
			panelDatos.add(getLblFecha(), "1, 3, right, default");
			panelDatos.add(getTxtFecha(), "3, 3, fill, default");
			panelDatos.add(getLblAutor(), "5, 3, right, default");
			panelDatos.add(getTxtAutor(), "7, 3, fill, default");
			panelDatos.add(getLblMsg(), "1, 5");
			panelDatos.add(getScrollPane(), "3, 5, 5, 1, fill, fill");
		}
		return panelDatos;
	}
	
	public void setLineColor(Color color) {
		getPanelPrincipal().setBorder(new LineBorder(color, 2, true));
	}
	
	
	public void setBackground(Color color) {
		getPanelLeft().setBackground(color);
	}
	
	private JLabel getLblCommitid() {
		if (lblCommitid == null) {
			lblCommitid = new JLabel(" commitId: ");
		}
		return lblCommitid;
	}
	public JTextField getTxtCommitid() {
		if (txtCommitid == null) {
			txtCommitid = new JTextField();
			txtCommitid.setText("");
			txtCommitid.setEditable(false);
			txtCommitid.setColumns(10);
		}
		return txtCommitid;
	}
	private JLabel getLblFecha() {
		if (lblFecha == null) {
			lblFecha = new JLabel(" fecha: ");
		}
		return lblFecha;
	}
	public JTextField getTxtFecha() {
		if (txtFecha == null) {
			txtFecha = new JTextField();
			txtFecha.setText("9888/88/88 88:88:00");
			txtFecha.setEditable(false);
			txtFecha.setColumns(10);
		}
		return txtFecha;
	}
	private JLabel getLblAutor() {
		if (lblAutor == null) {
			lblAutor = new JLabel(" autor: ");
		}
		return lblAutor;
	}
	public JTextField getTxtAutor() {
		if (txtAutor == null) {
			txtAutor = new JTextField();
			txtAutor.setText("");
			txtAutor.setEditable(false);
			txtAutor.setColumns(10);
		}
		return txtAutor;
	}
	private JLabel getLblMsg() {
		if (lblMsg == null) {
			lblMsg = new JLabel("   msg:");
		}
		return lblMsg;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTxtareaMessage());
		}
		return scrollPane;
	}
	public JTextArea getTxtareaMessage() {
		if (txtareaMessage == null) {
			txtareaMessage = new JTextArea();
			txtareaMessage.setEditable(false);
			txtareaMessage.setText("");
		}
		return txtareaMessage;
	}

	private JPanel getPanelBotonera() {
		if (panelBotonera == null) {
			panelBotonera = new JPanel();
			panelBotonera.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (contenedorHistorico!=null) {
						contenedorHistorico.onHistoricoSeleccionado(esteHistorico);
					}
				}
			});
			panelBotonera.setLayout(new BorderLayout(0, 0));
			panelBotonera.add(getBtnCompFile(), BorderLayout.WEST);
			panelBotonera.add(getLblNewLabel(), BorderLayout.CENTER);
			panelBotonera.add(getBtnCompFileGitDir(), BorderLayout.EAST);
		}
		return panelBotonera;
	}
	private JButton getBtnCompFile() {
		if (btnCompFile == null) {
			btnCompFile = new JButton("comp-F-VG");
			btnCompFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					contenedorHistorico.compareFileToGitVersion(gitFileVersionInfo);
				}
			});
			btnCompFile.setMargin(new Insets(1, 1, 2, 1));
			btnCompFile.setFont(new Font("Tahoma", Font.PLAIN, 11));
			btnCompFile.setToolTipText("compara  el Fichero  con  la Version en Git");
		}
		return btnCompFile;
	}
	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel(" ");
		}
		return lblNewLabel;
	}
	private JButton getBtnCompFileGitDir() {
		if (btnCompFileGitDir == null) {
			btnCompFileGitDir = new JButton("comp-VG-FGD");
			btnCompFileGitDir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					contenedorHistorico.compareGitVersionConFichEnDirectorioGit(gitFileVersionInfo);
				}
			});
			btnCompFileGitDir.setMargin(new Insets(1, 1, 2, 1));
			btnCompFileGitDir.setFont(new Font("Tahoma", Font.PLAIN, 11));
			btnCompFileGitDir.setToolTipText("compara  la Version en Git  con  el Fichero en Directorio Git");
		}
		return btnCompFileGitDir;
	}
	private JPanel getPanelInf() {
		if (panelInf == null) {
			panelInf = new JPanel();
		}
		return panelInf;
	}
	private JPanel getPanelLeft() {
		if (panelLeft == null) {
			panelLeft = new JPanel();
			panelLeft.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (contenedorHistorico!=null) {
						contenedorHistorico.onHistoricoSeleccionado(esteHistorico);
					}
				}
			});
		}
		return panelLeft;
	}
}
