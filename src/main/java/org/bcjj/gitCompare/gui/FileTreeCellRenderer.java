package org.bcjj.gitCompare.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.bcjj.gitCompare.Diferencia;
import org.bcjj.gitCompare.EstadoProcesado;
import org.bcjj.gitCompare.FileInTreeInfo;
import org.bcjj.gitCompare.FileVsGit;

public class FileTreeCellRenderer implements TreeCellRenderer {

	public boolean activa=false;
	ImageIcon iconoSinConflictos;
	ImageIcon iconoConflictosMismoUsuario;
	ImageIcon iconoConflictosDistintoUsuario;
	ImageIcon iconoPendiente;
	ImageIcon iconoHecho;
	
	ImageIcon iconoSinConflictosMini;
	ImageIcon iconoConflictosMismoUsuarioMini;
	ImageIcon iconoConflictosDistintoUsuarioMini;
	ImageIcon iconoPendienteMini;
	ImageIcon iconoHechoMini;
	
	public FileTreeCellRenderer(ImageIcon iconoSinConflictos, ImageIcon iconoConflictosMismoUsuario, ImageIcon iconoConflictosDistintoUsuario,ImageIcon iconoPendiente,ImageIcon iconoHecho,ImageIcon iconoPendiente10,ImageIcon iconoHecho10) {
		this.iconoSinConflictos=iconoSinConflictos;
		this.iconoConflictosMismoUsuario=iconoConflictosMismoUsuario;
		this.iconoConflictosDistintoUsuario=iconoConflictosDistintoUsuario;
		this.iconoPendiente=iconoPendiente;
		this.iconoHecho=iconoHecho;
		
		this.iconoSinConflictosMini=halfSize(iconoSinConflictos);
		this.iconoConflictosMismoUsuarioMini=halfSize(iconoConflictosMismoUsuario);
		this.iconoConflictosDistintoUsuarioMini=halfSize(iconoConflictosDistintoUsuario);
		this.iconoPendienteMini=halfSize(iconoPendiente);
		this.iconoHechoMini=halfSize(iconoHecho);
	}

	private ImageIcon halfSize(ImageIcon imageIcon) {
		Image image = imageIcon.getImage(); // transform it 
		Image newimg = image.getScaledInstance(image.getWidth(null)/2, image.getHeight(null)/2,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		imageIcon = new ImageIcon(newimg);  // transform it back
		return imageIcon;
	}	
	
	
	public void setActiva(boolean activa) {
		this.activa=activa;
	}
	
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		DefaultMutableTreeNode nodo=(DefaultMutableTreeNode)value;
		Object userObject=nodo.getUserObject();
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEADING, 2, 0));
		JLabel label1=null;
		JLabel label2=new JLabel("");
		if (userObject instanceof FileInTreeInfo) {
			FileInTreeInfo fileInTreeInfo=(FileInTreeInfo)userObject;
			//JPanel renderer = new JPanel(new GridLayout(0, 1));
			label1=new JLabel(fileInTreeInfo.getPathName());
			if (fileInTreeInfo.getDiferencia()==Diferencia.SinNuevasVersiones) {
				if (fileInTreeInfo instanceof FileVsGit) {
					label1.setIcon(iconoSinConflictos);
				} else {
					label1.setIcon(iconoSinConflictosMini);
				}
			} else if (fileInTreeInfo.getDiferencia()==Diferencia.NuevasVersionesMismoAutor) {
				if (fileInTreeInfo instanceof FileVsGit) {
					label1.setIcon(iconoConflictosMismoUsuario);
				} else {
					label1.setIcon(iconoConflictosMismoUsuarioMini);
				}
			} else if (fileInTreeInfo.getDiferencia()==Diferencia.NuevasVersionesDistintosAutores) {
				if (fileInTreeInfo instanceof FileVsGit) {
					label1.setIcon(iconoConflictosDistintoUsuario);
				} else {
					label1.setIcon(iconoConflictosDistintoUsuarioMini);
				}
			}
			if (userObject instanceof FileVsGit) {
				label1.setForeground(Color.BLACK);
			} else {
				label1.setForeground(Color.GRAY);
			}
			if (fileInTreeInfo.getEstadoProcesado()==EstadoProcesado.Revisado) {
				if (fileInTreeInfo instanceof FileVsGit) {
					label2.setIcon(iconoHecho);
				} else {
					label2.setIcon(iconoHechoMini);
				}
			} else {
				if (fileInTreeInfo instanceof FileVsGit) {
					label2.setIcon(iconoPendiente);
				} else {
					label2.setIcon(iconoPendienteMini);
				}
			}
		} else {
			label1=new JLabel(userObject.toString());
		}
		if (selectedNode==nodo) {
			label1.setOpaque(true);
			label1.setBackground(Color.YELLOW);
			label1.setText(label1.getText());
		} else {
			label1.setOpaque(false);
			label1.setBackground(Color.WHITE);
		}
		panel.add(label1);
		panel.add(label2);
		panel.setOpaque(false);
		return panel; 
		
	}		

	
	
}
