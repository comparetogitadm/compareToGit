package org.bcjj.gitCompare.gui;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Iconos {

	static ImageIcon iconoSinConflictos;
	static ImageIcon iconoConflictosMismoUsuario;
	static ImageIcon iconoConflictosDistintoUsuario;
	static ImageIcon iconoPendiente;
	static ImageIcon iconoHecho;
	//static ImageIcon iconoIgual;
	//static ImageIcon iconoDistinto;
	
	static ImageIcon iconoPreCopy;
	static ImageIcon iconoCopy;
	static ImageIcon iconoCompare;
	
	static ImageIcon iconoSinConflictosMini;
	static ImageIcon iconoConflictosMismoUsuarioMini;
	static ImageIcon iconoConflictosDistintoUsuarioMini;
	static ImageIcon iconoPendienteMini;
	static ImageIcon iconoHechoMini;
	
	
	static {
		iconoSinConflictos = createImageIcon("/images/cart-go.png"); //$NON-NLS-1$
		iconoConflictosMismoUsuario = createImageIcon("/images/dialog-warning-3.png"); //$NON-NLS-1$
		iconoConflictosDistintoUsuario = createImageIcon("/images/arrow-divide2.png"); //$NON-NLS-1$
		iconoPendiente = createImageIcon("/images/application-x-qet-project-blanco.png"); //$NON-NLS-1$
		iconoHecho = createImageIcon("/images/dialog-ok-4.png"); //$NON-NLS-1$
		//static ImageIcon iconoIgual;
		//static ImageIcon iconoDistinto;
		
		iconoPreCopy = createImageIcon("/images/page-white_go.png"); //$NON-NLS-1$
		iconoCopy = createImageIcon("/images/go-next.png"); //$NON-NLS-1$
		iconoCompare = createImageIcon("/images/project-development-new-template.png"); //$NON-NLS-1$
		
		iconoSinConflictosMini=halfSize(iconoSinConflictos);
		iconoConflictosMismoUsuarioMini=halfSize(iconoConflictosMismoUsuario);
		iconoConflictosDistintoUsuarioMini=halfSize(iconoConflictosDistintoUsuario);
		iconoPendienteMini=halfSize(iconoPendiente);
		iconoHechoMini=halfSize(iconoHecho);
	}
	
	
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon(String path) {
	    java.net.URL imgURL = Iconos.class.getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
	
	public static ImageIcon halfSize(ImageIcon imageIcon) {
		Image image = imageIcon.getImage(); // transform it 
		Image newimg = image.getScaledInstance(image.getWidth(null)/2, image.getHeight(null)/2,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		imageIcon = new ImageIcon(newimg);  // transform it back
		return imageIcon;
	}	
	
	

	
}
