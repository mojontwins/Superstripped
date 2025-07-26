package net.minecraft.src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

class ServerGuiCommandListener implements ActionListener {
	final JTextField texts_field;
	final ServerGUI mcServerGui;

	ServerGuiCommandListener(ServerGUI serverGUI1, JTextField JTextField2) {
		this.mcServerGui = serverGUI1;
		this.texts_field = JTextField2;
	}

	public void actionPerformed(ActionEvent actionEvent1) {
		String string2 = this.texts_field.getText().trim();
		if(string2.length() > 0) {
			ServerGUI.getMinecraftServer(this.mcServerGui).addCommand(string2, this.mcServerGui);
		}

		this.texts_field.setText("");
	}
}
