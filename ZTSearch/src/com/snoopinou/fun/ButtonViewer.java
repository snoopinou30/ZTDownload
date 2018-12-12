
package com.snoopinou.fun;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ButtonViewer extends JFrame{
	
	static DebugViewer debug = new DebugViewer();
	
	JPanel buttonPanel;
	JTextField search = new JTextField();
	JScrollPane scrollPane;
	
	JMenuBar menuBar = new JMenuBar();
	
	JMenu file = new JMenu("File");
	JMenuItem debugItem = new JMenuItem("Debug");
	JMenuItem login = new JMenuItem("Login");
	
	JMenu param = new JMenu("Parameters");
	
	JRadioButton radioBrowser = new JRadioButton("Open In Browser");
	JRadioButton radioClipboard = new JRadioButton("Copy to clipboard");
	JRadioButton radioPrint = new JRadioButton("Show in a window");
	
	JCheckBox checkDebrid = new JCheckBox("Use AllDebrid");
	
	ButtonGroup bg = new ButtonGroup();
	
	String stringURL = "";
	
//	final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36";
	final String AGENT = "snoopinou";
	final String ALLDEBRID_API = "https://api.alldebrid.com";
	String token = null;
	
	
	
	String username = null;
	String pass = null;
	
	public ButtonViewer(String stringURLp){
		
		stringURL = stringURLp;
		
		this.setTitle("ZTDownload");
		this.setSize(800,800);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.getContentPane().setLayout(new BorderLayout());
		
		// Search
		search.addActionListener(new SearchListener());
		this.getContentPane().add(search, BorderLayout.NORTH);
		
		// Menu
		bg.add(radioBrowser);
		bg.add(radioClipboard);
		bg.add(radioPrint);
		radioBrowser.setSelected(true);
		
		
		this.setJMenuBar(menuBar);
		login.addActionListener(new LoginListener());
		debugItem.addActionListener(new DebugListener());
		
		file.add(login);
		file.addSeparator();
		file.add(debugItem);
		
		param.add(radioBrowser);
		param.add(radioClipboard);
		param.add(radioPrint);
		
		param.addSeparator();
		
		param.add(checkDebrid);
		
		
		menuBar.add(file);
		menuBar.add(param);
		
		
		// Buttons
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0,2));
		
		// Scroll Pane with buttons
		scrollPane = new JScrollPane(buttonPanel);
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	
	private Map<String, String> buildSearchParam() {
		
		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("do", "search");
		map.put("subaction", "search");
		map.put("story", search.getText());
		
		return map;
	}
	
	
	// Fill panel with Buttons of Search Result
	public void fillWithButtons(Map<String,String> map) {
		if(map.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No Result found", "Error", JOptionPane.ERROR_MESSAGE);
			debug.println("No result found.", DebugViewer.LIGHT);
		}else {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(0,2));
			
			for(final Map.Entry<String,String> entry : map.entrySet()) {
				JButton button = new JButton();
				button.setText(entry.getKey());
				button.addActionListener(new ButtonListener(entry));
				buttonPanel.add(button);
			}
			scrollPane.setViewportView(buttonPanel);
		}
	}
	
	
	
	
	// Decompose notre recherche et isole les liens des differents films
	public Map<String,String> decomposeSearchResult(String response) {
		
		HashMap<String,String> map = new HashMap<String,String>();
		String url = null;
		String carac = "";
		
		if(response.contains("503 Service Unavailable")) {
			JOptionPane.showMessageDialog(null, "Error 503 : Service is currently unavailable. Try again later.", "ERROR 503", JOptionPane.ERROR_MESSAGE);
			debug.println("Error 503 : Unavailable", DebugViewer.LIGHT);
			return null;
		}
		
		String advancement = response;
		while(advancement.indexOf("href=\"https://www.annuaire-telechargement.com") != -1) {
			
			/* Je fais juste une petite precision pour le futur moi qui lira ce code
			 * Tu as vu je fais des commentaires et j'espere que tu comprendras quand meme
			 * 
			 * En gros Les liens dans le code viennent par 2
			 * Le premier et le deuxieme sont pareil mais le deuxieme est celui juste avant les caracteristiques
			 * Du coup, c'est pour ca que l'on passe au lien suivant plus tard
			 * 
			 * Et apres on verfie si les liens finissent par .html parce que y'a plein de lien poubelle au debut
			 * qui finissent pas par .html du coup on les saute
			 */
			
			
			// Get index Of without href=
			advancement = advancement.substring(advancement.indexOf("href=\"https://www.annuaire-telechargement.com")+6);
			
			// Get url only
			url = advancement.substring(0, advancement.indexOf('"'));
			
			
			// Si pas le bon lien on passe au suivant
			if(advancement.substring(0, advancement.indexOf('"')+3).endsWith("<")) {
				advancement = advancement.substring(advancement.indexOf("href=\"https://www.annuaire-telechargement.com")+6);
			}
			
			// If right URL
			if(url.endsWith(".html") == true) {
				// Reset carac
				carac = "";
				// Get name
				carac += advancement.substring(advancement.indexOf(">")+2, advancement.indexOf("<"));
				carac += " ";
				// Get Quality
				carac += advancement.substring(advancement.indexOf("<b>")+3, advancement.indexOf("</span>"));
				carac += " ";
				// Get Language
				carac += advancement.substring(advancement.indexOf("(")+1, advancement.indexOf(")"));
				
				
				map.put(carac, url);
			}
		}
		return map;
	}
	
	
	
	
	// Decompose la page du film et obtient les liens
	public List<String> decomposeDownloadResult(String response){
		
		LinkedList<String> list = new LinkedList<String>();
		String advancement = response;
		String link = "";
		boolean stop = false;
		
		
		// Premier lien recupere a la main
		advancement = advancement.substring(advancement.indexOf("href=\"https://www.dl-protect")+6);
		link = advancement.substring(0, advancement.indexOf('"'));
		list.add(link);
		
		while(stop == false) {
			// Si le "<div" qui signifie changement de section de lien (hebergeur) est plus proche que le prochain lien
			// Ca veut dire qu'on change d'hebergeur et donc qu'on arrete de lire les lien (on quitte la boucle)
			if(advancement.indexOf("href=\"https://www.dl-protect")+6 > advancement.indexOf("<div")) {
				stop = true;
			}else {
				// Isolement du prochain lien et avancement dans le texte
				advancement = advancement.substring(advancement.indexOf("href=\"https://www.dl-protect")+6);
				link = advancement.substring(0, advancement.indexOf('"'));
				list.add(link);
			}
		}
		return list;
	}
	
	
	
	// Get identification token
	public void getToken() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("password", pass);
		params.put("agent", AGENT);
		HttpRequest req = new HttpRequest(ALLDEBRID_API+"/user/login");
		try {
			String response = req.doGetRequest(params);
			// Isolate token in response only if one is returned
			if(response.contains("true")){
				token = response.substring(response.indexOf("token")+8, response.indexOf('"',response.indexOf("token")+9));
				JOptionPane.showMessageDialog(null, "Authentification Successful", "Success", JOptionPane.PLAIN_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(null, "Check your credentials", "Error", JOptionPane.ERROR_MESSAGE);
				debug.println("Wrong credentials.", DebugViewer.LIGHT);
			}
			// Print output
			debug.println(response, DebugViewer.HEAVY);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}
	
	// Bypass Redirectors
	private LinkedList<String> requestRedirector(String link) {
		HttpRequest req = new HttpRequest(ALLDEBRID_API+"/link/redirector");
		
		HashMap<String,String> param = new HashMap<String,String>();
		param.put("agent", AGENT);
		param.put("token", token);
		param.put("link", link);
		
		String response = null;
		String advancement = null;
		LinkedList<String> links = new LinkedList<String>();
		String originalLink = "";
		try {
			response = req.doGetRequest(param);
			advancement = response;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Look at the part with links
		advancement = advancement.substring(response.indexOf("["));	
		
		// While there are other links
		while(advancement.indexOf('"') != -1) {
			// Gets Second quote
			int endIndex = advancement.indexOf('"', advancement.indexOf('"')+1);
			
			// Isolate link
			originalLink = advancement.substring(advancement.indexOf('"')+1, endIndex);
			
			// Replace every "\" with nothing 
			// Because URL is like "https:\/\/"
			originalLink = originalLink.replace("\\", "");
			
			links.add(originalLink);
			
			advancement = advancement.substring(endIndex+1);
		}
		
		return links;
	}
	
	// Debrid on link
	public String requestDebrid(String link) {
		HttpRequest req = new HttpRequest(ALLDEBRID_API+"/link/unlock");
		
		HashMap<String,String> param = new HashMap<String,String>();
		param.put("agent", AGENT);
		param.put("token", token);
		param.put("link", link);
		
		String response = null;
		String advancement = null;
		
		try {
			response = req.doGetRequest(param);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		advancement = response;
		
		advancement = advancement.substring(advancement.indexOf("link")+7);
		String debridLink = advancement.substring(0, advancement.indexOf('"'));
		
		// Replace every "\" with nothing 
		// Because URL is like "https:\/\/"
		debridLink = debridLink.replace("\\", "");
		
		return debridLink;
	}
	
	
	// Bypass director then debrid (basically requestRedirector() into requestDebrid()
	public List<String> debridAll (List<String> links) {
		if(token == null) {
			JOptionPane.showMessageDialog(null, "Enter your login credentials first.", "ERROR", JOptionPane.ERROR_MESSAGE);
			debug.println("No credentials entered.", DebugViewer.LIGHT);
			return null;
		}else {
			
			// All my links to debrid after bypassing
			LinkedList<String> linksToDebrid = new LinkedList<String>();
			LinkedList<String> debridedLinks = new LinkedList<String>();
			
			
			// Bypass directors
			for(String link : links) {
				// Fill my LinkedList
				linksToDebrid.addAll(requestRedirector(link));
			}
			
			
			// Get debrided link
			for(String str : linksToDebrid) {
				String debridedLink = requestDebrid(str);
				debridedLinks.add(debridedLink);
			}
			
			return debridedLinks;
		}
	}
	
	public String encodeLink(String link) throws IOException {
		
		String linkEncoded = "";
		
		String strs[] = link.split("/");
		
		String last = strs[strs.length-1];
		String lastEncoded = URLEncoder.encode(last, "UTF-8");
		strs[strs.length-1] = lastEncoded;
		
		for(String str : strs) {
			linkEncoded += str +"/";
		}
		linkEncoded = linkEncoded.substring(0, linkEncoded.length()-1);
		
		
		return linkEncoded;
	}
	
	
	
	class DebugListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			DialogDebug dLog = new DialogDebug();
			String credentials = dLog.showDialog();
			if(credentials.equals("admin")) {
				debug.setVisible(true);
			}
		}
	}
	
	
	class LoginListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			DialogLogin dLog = new DialogLogin();
			String[] credentials = dLog.showDialog();
			username = credentials[0];
			pass = credentials[1];
			
			getToken();
		}
		
	}
	
	class SearchListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// On execute la recherche dans un autre Thread pour pas bloquer l'EDT
			new Thread(new Runnable() {
				@Override
				public void run() {
					HttpRequest req = new HttpRequest(stringURL+"/index.php?do=search");
					try {
						String str = req.doPostRequest(buildSearchParam());
						fillWithButtons(decomposeSearchResult(str));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	class ButtonListener implements ActionListener{
		
		Map.Entry<String,String> entry;
		
		public ButtonListener(Map.Entry<String,String> entryp) {
			entry = entryp;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// On execute la recherche dans un autre Thread pour pas bloquer l'EDT
			new Thread(new Runnable() {
				@Override
				public void run() {
					HttpRequest req = new HttpRequest(entry.getValue());
					try {
						String response = req.doGetRequest(null);
						
						// Gets download link from page
						List<String> list = decomposeDownloadResult(response);
						
						
						List<String> toDownload;
						if(checkDebrid.isSelected()) {
							// Bypass redirectors then debrid
							toDownload = debridAll(list);
							debug.println("Using AllDebrid", DebugViewer.LIGHT);
						}else {
							toDownload = list;
							debug.println("Not Using AllDebrid", DebugViewer.LIGHT);
						}
						
						
						
						int choiceOutput = 0;
						
						TextViewer viewer = new TextViewer();
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						String allLink = "";
						
						
						if(radioBrowser.isSelected())
							choiceOutput = 1;
						if(radioClipboard.isSelected())
							choiceOutput = 2;
						if(radioPrint.isSelected())
							choiceOutput = 3;
						
						
						
						// Itere les liens. On voit si on encode ou sinon on rajoute directement a la liste
						for(String link : toDownload) {
							
							
							// Si pour navigateur on encoded directement
							if(choiceOutput == 1) {
								String linkEncoded = encodeLink(link);
								allLink += linkEncoded +"\n";
								
								debug.println("Encoded link is : "+linkEncoded, DebugViewer.LIGHT);
							}else {
								allLink += link + "\n";

								debug.println("Link is : "+link, DebugViewer.LIGHT);
							}
						}
						
						
						/* 1 = Browser
						 * 2 = Clipboard
						 * 3 = Print	
						 */
						
						switch(choiceOutput) {
						case 1:
							for(String str : allLink.split("\n")) {
								Desktop.getDesktop().browse(new URL(str).toURI());
							}
							break;
						case 2:
							clipboard.setContents(new StringSelection(allLink), null);
							JOptionPane.showMessageDialog(null, "Copied all links to clipboard","Copy finish", JOptionPane.INFORMATION_MESSAGE);
							break;
						case 3:
							viewer.setText(allLink);
							viewer.setVisible(true);
							break;
						}
						
						
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}).start();
		}
	}
} 