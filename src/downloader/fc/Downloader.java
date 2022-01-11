package downloader.fc;



/* downloader.fc.Downloader
 * (c) blanch@imag.fr 2021                                               */



import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.util.concurrent.locks.ReentrantLock;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;


public class Downloader {
	public static final int CHUNK_SIZE = 1024;
	URL url;
	int content_length;
	BufferedInputStream in;
	String filename;
	File temp;
	FileOutputStream out;
	private int _progress;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	
	boolean supprimer = false;
	boolean telecharger = false;
	ReentrantLock lock = new ReentrantLock();
	
	public Downloader(String uri) {
		try {
			url = new URL(uri);
			
			URLConnection connection = url.openConnection();
			content_length = connection.getContentLength();
			
			in = new BufferedInputStream(connection.getInputStream());
			
			String[] path = url.getFile().split("/");
			filename = path[path.length-1];
			temp = File.createTempFile(filename, ".part");
			out = new FileOutputStream(temp);
		}
		catch(MalformedURLException e) { throw new RuntimeException(e); }
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
	public String toString() {
		return url.toString();
	}
	
	public String download() throws InterruptedException {
		byte buffer[] = new byte[CHUNK_SIZE];
		int size = 0;
		int count = 0;
		boolean suppression, telechargement;
		telecharger = true;
		while(true) {
			lock.lock();
			try {
				telechargement = telecharger;
				suppression = supprimer;
			} finally {
				lock.unlock();
			}
			if(suppression) {
				temp.delete();
				return "";
			}
			if(telechargement) {
				try { count = in.read(buffer, 0, CHUNK_SIZE); }
				catch(IOException e) { continue; }

				if(count < 0) { break; }
			
				try { out.write(buffer, 0, count); }
				catch(IOException e) { continue; }
			
				size += count;
				setProgress(100*size/content_length);
			}
				Thread.sleep(1000);
			
		}
		
		if(size < content_length) {
			temp.delete();
			throw new InterruptedException();
		}
		
		try {
			Files.copy(temp.toPath(), new File(filename).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			temp.delete();
		}
		catch(IOException e){
			throw new InterruptedException();
		}
		return filename;
	}
	
	public int getProgress() {
		return _progress;
	}
	
	public void setProgress(int progress) {
		int old_progress = _progress;
		_progress = progress;
		pcs.firePropertyChange("progress", old_progress, progress);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}



	public void play(){
		lock.lock();
		try {
			telecharger = true;
		} finally {
			lock.unlock();
		}
	}
	public void pause(){
		lock.lock();
		try {
			telecharger = false;
		} finally {
			lock.unlock();
		}
	}

	public void supprimer(){
		lock.lock();
		try {
			supprimer = true;
		} finally {
			lock.unlock();
		}
	}
	
	public boolean getBoolTelechargement() {
		return telecharger;
	}
	
	public boolean  getBoolSuppression() {
		return supprimer;
	}
}
