package org.bcjj.gitCompare.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

import org.bcjj.gitCompare.NuevasVersionesInfo;
import org.bcjj.gitCompare.ComparacionInfo;
import org.bcjj.gitCompare.EstadoComparacion;
import org.bcjj.gitCompare.EstadoProcesado;
import org.bcjj.gitCompare.FileInTreeInfo;
import org.bcjj.gitCompare.FileVsGit;

public class FileTreeCellRenderer implements TreeCellRenderer /*, TreeCellEditor*/ {

	public boolean activa=false;
	GitCompareMainWindow gitCompareMainWindow=null;

	
	public FileTreeCellRenderer(GitCompareMainWindow gitCompareMainWindow) {
		this.gitCompareMainWindow=gitCompareMainWindow;
	}


	
	
	public void setActiva(boolean activa) {
		this.activa=activa;
	}
	
	

	
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {			
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		DefaultMutableTreeNode nodo=(DefaultMutableTreeNode)value;
		Object userObject=nodo.getUserObject();
		JPanel panel = new JPanel();
		//panel.setLayout(new FlowLayout(FlowLayout.LEADING, 2, 0));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		JLabel nombreFicheroLabel=null;
		JLabel separa1Label=new JLabel("  ");
		JLabel infoFicheroLabel=new JLabel("");
		if (userObject instanceof FileInTreeInfo) {
			FileInTreeInfo fileInTreeInfo=(FileInTreeInfo)userObject;
			//JPanel renderer = new JPanel(new GridLayout(0, 1));
			nombreFicheroLabel=new JLabel(fileInTreeInfo.getPathName());
			if (fileInTreeInfo.getNuevasVersionesInfo()==NuevasVersionesInfo.SinNuevasVersiones) {
				if (fileInTreeInfo instanceof FileVsGit) {
					nombreFicheroLabel.setIcon(Iconos.iconoSinConflictos);
				} else {
					nombreFicheroLabel.setIcon(Iconos.iconoSinConflictosMini);
				}
			} else if (fileInTreeInfo.getNuevasVersionesInfo()==NuevasVersionesInfo.NuevasVersionesMismoAutor) {
				if (fileInTreeInfo instanceof FileVsGit) {
					nombreFicheroLabel.setIcon(Iconos.iconoConflictosMismoUsuario);
				} else {
					nombreFicheroLabel.setIcon(Iconos.iconoConflictosMismoUsuarioMini);
				}
			} else if (fileInTreeInfo.getNuevasVersionesInfo()==NuevasVersionesInfo.NuevasVersionesDistintosAutores) {
				if (fileInTreeInfo instanceof FileVsGit) {
					nombreFicheroLabel.setIcon(Iconos.iconoConflictosDistintoUsuario);
				} else {
					nombreFicheroLabel.setIcon(Iconos.iconoConflictosDistintoUsuarioMini);
				}
			}
			if (userObject instanceof FileVsGit) {
				FileVsGit fileVsGit=(FileVsGit)userObject;
				ComparacionInfo comparacionInfo=fileVsGit.getComparacionInfo();
				nombreFicheroLabel.setForeground(Color.BLACK);
				
				String n=fileInTreeInfo.getPathName();
				if (n.indexOf(".")>-1) {
					String ext=n.substring(n.indexOf("."));
					if (ext.toLowerCase().indexOf("-orig")>-1) {
						nombreFicheroLabel.setForeground(Color.GRAY);
					}
				} 
				
				String htmlInfo="";
				if (comparacionInfo!=null) {
					String spc="&nbsp;";
					htmlInfo="<html><b>"+ampliaFuenteTexto(spc+spc+spc+" F-FGD:",3)+getEstadoHtml(comparacionInfo.getF_FGD(),4)+"</b> "+
									ampliaFuenteTexto(spc+" / "+spc+" VG-FGD:",2)+getEstadoHtml(comparacionInfo.getVG_FGD(),2)+" "+
									ampliaFuenteTexto(spc+" / "+spc+" F-VG:",2)+getEstadoHtml(comparacionInfo.getF_VG(),2)
									/*
									+"   <font size='1'>1 = &lt;&gt</font>   "
									+"   <font size='2'>2 = &lt;&gt</font>   "
									+"   <font size='3'>3 = &lt;&gt</font>   "
									+"   <font size='4'>4 = &lt;&gt</font>   "
									+"   <font size='5'>5 = &lt;&gt</font>   "
									+"   <font size='6'>6 = &lt;&gt</font>   "
									+"   <font size='7'>7 = &lt;&gt</font>   "
									*/
									+nbsps(20) //<font color='white'> - - - - - - </font>"
									+"</html>";
				}
				//System.out.println(fileInTreeInfo.getPathName()+"  ------  "+htmlInfo);				
				infoFicheroLabel.setText(htmlInfo);
				//label2.setToolTipText("F: Fich --  FGD: Fich Directorio Git --  VG: Version en Git ultima"); 
				
			} else {
				nombreFicheroLabel.setForeground(Color.GRAY);
			}
			if (fileInTreeInfo.getEstadoProcesado()==EstadoProcesado.Revisado) {
				infoFicheroLabel.setToolTipText("revisado");
				if (fileInTreeInfo instanceof FileVsGit) {
					infoFicheroLabel.setIcon(Iconos.iconoHecho);
				} else {
					infoFicheroLabel.setIcon(Iconos.iconoHechoMini);
				}
			} else {
				infoFicheroLabel.setToolTipText("pendiente");
				if (fileInTreeInfo instanceof FileVsGit) {
					infoFicheroLabel.setIcon(Iconos.iconoPendiente);
				} else {
					infoFicheroLabel.setIcon(Iconos.iconoPendienteMini);
				}
			}
		} else {
			nombreFicheroLabel=new JLabel(userObject.toString());
		}
		if (selectedNode==nodo) {
			nombreFicheroLabel.setOpaque(true);
			nombreFicheroLabel.setBackground(Color.YELLOW);
			nombreFicheroLabel.setText(nombreFicheroLabel.getText());
			infoFicheroLabel.setOpaque(true);
			Color LightYellow=new Color(255, 255, 224);  //LightYellow 	FF FF E0 	255 255 224
			infoFicheroLabel.setBackground(LightYellow);
		} else {
			nombreFicheroLabel.setOpaque(false);
			nombreFicheroLabel.setBackground(Color.WHITE);
			infoFicheroLabel.setOpaque(false);
			infoFicheroLabel.setBackground(Color.WHITE);
		}
		panel.add(nombreFicheroLabel);
		
		panel.add(separa1Label);

		panel.add(infoFicheroLabel);
		panel.setOpaque(false);
		
		return panel; 
		
	}

	private String nbsps(int cuantos) {
		String h="";
		for (int i=0;i<cuantos;i++) {
			h=h+" &nbsp; ";
		}
		return h;
	}

	private String ampliaFuenteTexto(String txt, int size) {  // 1 a 6,  7 es muy grando
				return "<font size='"+size+"'>"+txt+"</font>";
	}

	private String getEstadoHtml(EstadoComparacion estadoComparacion, int size) {
		if (estadoComparacion==EstadoComparacion.Distintos) {
			return "<font color='red' size='"+size+"'>&lt;&gt;</font>"; // <>
		}
		if (estadoComparacion==EstadoComparacion.Iguales) {
			return "<font color='#00EA00' size='"+(size+1)+"'>=</font>";   // #00FF00 #00CC00 
		}
		if (estadoComparacion==EstadoComparacion.OrigenExisteDestinoNo) {
			return "<font color='purple' size='"+size+"'>!2</font>"; 
		}
		if (estadoComparacion==EstadoComparacion.OrigenNoExisteDestinoSi) {
			return "<font color='purple' size='"+size+"'>!1</font>"; 
		}
		return "<font color='orange'>Error</font>"; 
	}

	
}
