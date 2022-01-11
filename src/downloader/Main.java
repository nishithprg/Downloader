package downloader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import downloader.fc.Downloader;

@SuppressWarnings("serial")
public class Main extends JFrame {
    JPanel panelCentre = new JPanel(new StackLayout());
    JPanel panelBas = new JPanel(new BorderLayout()); 
    JSplitPane divPanelBas = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    Container fenetre = getContentPane();
    
    JTextField champTexte = new JTextField(" http://iihm.imag.fr/index.html");
    JButton bontonAjouter = new JButton("\u21EA");
  
   
    Main(String titre, String[] urls) {
        super(titre);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800,400));
        champTexte.setPreferredSize(new Dimension(650,champTexte.getHeight()));
 
        bontonAjouter.addActionListener(actionEvent -> lancerTelechargement(champTexte.getText()));
        divPanelBas.add(champTexte);
        divPanelBas.add(bontonAjouter);
        panelBas.add(divPanelBas);
        
        fenetre.add(panelCentre,BorderLayout.CENTER);
        fenetre.add(panelBas,BorderLayout.SOUTH);

        pack();
        for(String url: urls) {
        	lancerTelechargement(url);
        }
    }
    
	public void lancerTelechargement(String url) {
		Download download = new Download(new Downloader(url));
		download.execute();
		panelCentre.add(download.panelCentre);
		pack();
	}
		
    public static void main(String[] argv) {
        String[] telechargements = new String[3];
        telechargements[0] = " http://iihm.imag.fr/blanch/ens/2015-2016/M1/TLI/";
        telechargements[1] = " http://iihm.imag.fr/blanch/ens/2018-2019/M1/TLI/";
        telechargements[2] = " http://iihm.imag.fr/blanch/ens/2021-2022/M1/TLI/";
        SwingUtilities.invokeLater(() -> new Main("Téléchargements", telechargements).setVisible(true));
    }


}
