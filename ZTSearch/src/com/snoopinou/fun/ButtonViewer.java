package com.snoopinou.fun;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ButtonViewer extends JFrame{
	
	JPanel buttonPanel;
	JTextField search = new JTextField();
	JScrollPane scrollPane;
	
	JMenuBar menuBar = new JMenuBar();
	JMenu file = new JMenu("File");
	JMenuItem login = new JMenuItem("Login");
	
	String stringURL = "";
	
//	final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36";
	final String AGENT = "snoopinou";
	final String ALLDEBRID_API = "https://api.alldebrid.com";
	String token = null;
	
	boolean verbose = true;
	
	
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
		this.setJMenuBar(menuBar);
		login.addActionListener(new LoginListener());
		file.add(login);
		menuBar.add(file);
		
		// Buttons
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0,2));
		
		// Scroll Pane with buttons
		scrollPane = new JScrollPane(buttonPanel);
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	
	private Map<String, String> buildRequestParam() {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("do", "search");
		map.put("subaction", "search");
		map.put("story", search.getText());
		
		return map;
	}
	
	public void fillWithButtons(Map<String,String> map) {
		if(map.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No Result found", "Error", JOptionPane.ERROR_MESSAGE);
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
	
	
	// Decompose notre recherche et isole les liens
	public Map<String,String> decomposeSearchResult(String response) {
		
		HashMap<String,String> map = new HashMap<String,String>();
		String url = null;
		String carac = "";
		
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
				System.out.println(response);
			}
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
	
	
	public void downloadAll(List<String> links) {
		
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
					HttpRequest req = new HttpRequest(stringURL);
					try {
						String str = req.doPostRequest(buildRequestParam());
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
						
						
						List<String> list = decomposeDownloadResult(response);
						
						
						List<String> toDownload = debridAll(list);
						
						for(String link : toDownload) {
							if(verbose) {
								System.out.println("Link is : "+link);
							}
							
//							Desktop.getDesktop().browse(new URI(link));
							
							String strs[] = link.split("/");
							
							String last = strs[strs.length-1];
							String lastEncoded = URLEncoder.encode(last, "UTF-8");
							strs[strs.length-1] = lastEncoded;
							
							String linkEncoded = "";
							for(String str : strs) {
								linkEncoded += str +"/";
							}
							linkEncoded = linkEncoded.substring(0, linkEncoded.length()-1);
							
							if(verbose) {
								System.out.println("Encoded link is : "+linkEncoded);
							}
							
							Desktop.getDesktop().browse(new URL(linkEncoded).toURI());
							
//							Desktop.getDesktop().browse(new URL(link).toURI());
							
//							Desktop.getDesktop().browse(new URI(URLEncoder.encode(link, "UTF-8")));
						}
						
						
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}).start();
		}
	}
} 
