package downloader;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import downloader.fc.Downloader;

public class Download extends SwingWorker<Object,Object>{
    JPanel panelCentre = new JPanel();
    Downloader downloader;
    JProgressBar barreProgres = new JProgressBar();
    JPanel telechargements = new JPanel();
    JPanel panelGauche = new JPanel();
    JButton boutonSuppr = new JButton("X");
    JButton boutonTelechargerEtPause = new JButton("\u25B6");
    JPanel panelTelechargerEtPause = new JPanel();
    JPanel panelSuppr = new JPanel();
    
    Download(Downloader d){
    	JLabel url = new JLabel(d.toString());
    	url.setFont(new Font("Serif", Font.PLAIN, 12));
    	
    	downloader = d ;
        downloader.addPropertyChangeListener(new  PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evenement) {
                barreProgres.setValue((int)evenement.getNewValue());
            }
        });
        barreProgres.setStringPainted(true);
        
        panelGauche.setLayout(new BoxLayout(panelGauche, BoxLayout.Y_AXIS));
        panelGauche.add(new JLabel(downloader.toString()));
        panelGauche.add(barreProgres);
        
        panelTelechargerEtPause.add(boutonTelechargerEtPause);
        panelSuppr.add(boutonSuppr);
        
        boutonSuppr.setPreferredSize(new Dimension(50,35));
        boutonSuppr.addActionListener(actionEvent -> supprimer());
        
        telechargements.setLayout(new BoxLayout(telechargements, BoxLayout.Y_AXIS));
        telechargements.add(url);
        telechargements.add(barreProgres);
        telechargements.setPreferredSize(new Dimension(700,50));
      
        panelCentre.setLayout(new BoxLayout(panelCentre, BoxLayout.X_AXIS));
        panelCentre.add(telechargements);
        panelCentre.add(panelTelechargerEtPause);
        panelCentre.add(panelSuppr);
        
        boutonTelechargerEtPause.setPreferredSize(new Dimension(50,35));
        boutonTelechargerEtPause.addActionListener(actionEvent -> telechargerEtPause());
    }
    
    @Override
    public Object doInBackground () {
        try {
            downloader.download();
        }
        catch(InterruptedException e) {
            System.err.println("Echec du téléchargement !");
        }
        return null;
    }
    
    public void telechargerEtPause(){
        if(downloader.getBoolTelechargement()){
        	boutonTelechargerEtPause.setText("\u25A0");
            downloader.pause();
        }else {
        	boutonTelechargerEtPause.setText("\u25B6");
            downloader.play();
        }
    }

    public void supprimer(){
        downloader.supprimer();
        Component parent = panelCentre.getParent();
        panelCentre.getParent().remove(panelCentre);
        parent.revalidate();
        parent.repaint();
    }

    
}