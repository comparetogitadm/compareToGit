package org.bcjj.gitCompare.gui;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;


public class ComboUtil {

	
	public static class ComboName {
		String name;

		public ComboName(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		public String toString() {
			return name;
		}
	}
	
	static String NL="\r\n";
	
	String programId;
	int maxPreferencesCombo;
	
	public ComboUtil(String programId,int maxPreferencesCombo) {
		this.programId=programId;
		this.maxPreferencesCombo=maxPreferencesCombo;
	}
	
	
	public String getComboValue(JComboBox<String> combo,DefaultComboBoxModel<String> modelo, ComboName fieldType) throws IOException {
		if (combo.getSelectedItem()==null) {
			return null;
		}
		String value= combo.getSelectedItem().toString();
		if (combo.getSelectedIndex()==-1) {
			modelo.insertElementAt(value,0);
		} else {
			try {
				modelo.removeElementAt(combo.getSelectedIndex());
			} catch (Exception r) {
				//ignorar
			}
			modelo.insertElementAt(value,0);
			modelo.setSelectedItem(value);
		}
		while (modelo.getSize()>maxPreferencesCombo) {
			modelo.removeElementAt(modelo.getSize()-1);
		}
		savePreference(modelo, fieldType);
		return value;
	}

	public  void loadPreference(DefaultComboBoxModel<String> modelo, ComboName fieldType) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(getPreferenceFile(fieldType)))) {
			String lin=null;
			while ((lin=br.readLine())!=null) {
				if (!lin.trim().equals("")) {
					modelo.addElement(lin);
				}
			}
		} catch (IOException e) {
			throw new IOException("Error reading preferences "+fieldType+" :: "+e);
		}
	}
	
	
	public  void savePreference(DefaultComboBoxModel<String> modelo, ComboName fieldType) throws IOException {
		try (FileWriter fw = new FileWriter(getPreferenceFile(fieldType))) {
			for (int i=0;i<modelo.getSize();i++) {
				String x=modelo.getElementAt(i);
				fw.write(x+NL);
			}
		} catch (IOException e) {
			throw new IOException("Error saving preferences "+fieldType+" :: "+e);
		}
	}
	
	public  File getPreferenceFile(ComboName fieldType) {
		String userHomeDir = System.getProperty("user.home");
		String prefDir=userHomeDir+"/."+programId;
		File d=new File(prefDir);
		d.mkdirs();
		String prefFile=prefDir+"/"+fieldType.getName();
		File f=new File(prefFile);
		return f;
	}


	public void setComboNewValue(String value, JComboBox<String> combo,DefaultComboBoxModel<String> modelo, ComboName fieldType) {
		while (modelo.getIndexOf(value)>-1) {
			modelo.removeElement(value);
		}
		combo.insertItemAt(value, 0);
		combo.setSelectedIndex(0);
	}
	
	public  String loadPreference(ComboName fieldType) throws IOException {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		loadPreference(model,fieldType);
		return model.getElementAt(0);
	}
	
	public  void savePreference(String value,ComboName fieldType) throws IOException {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		model.addElement(value);
		savePreference(model,fieldType);
	}
	
	public String askForCombo(String message,ComboName fieldType,String value) throws IOException {
		JComboBox<String> combo=new JComboBox<>();
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		loadPreference(model, fieldType);
		combo.setModel(model);
		combo.setEditable(true);
		combo.setSelectedItem(value);
		combo.setMinimumSize(new Dimension(400, 20));
		combo.setPreferredSize(new Dimension(400, 20));
		int dialogResult=JOptionPane.showConfirmDialog(null, combo,message,JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){
			  return getComboValue(combo, model, fieldType);
		}
		return null;
	}
	
}
