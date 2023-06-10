import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Server {
	
	private static ServerSocket server = null;
	private static Socket socket = null;
	
    private static PrintWriter out;
    private static BufferedReader in;
    
    private static BufferedImage bImg;
   
	private static Thread communicationThread = null;
	
	public static void main(String[] args) {
		
		JLabel statusLabel = new JLabel("Awaiting Client...");
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(540, 960));
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(statusLabel, BorderLayout.SOUTH);
		frame.add(panel);
		frame.setVisible(true);
		frame.pack();
		
		double interpolation = 0;
		final int TICKS_PER_SECOND = 25;
		final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
		final int MAX_FRAMESKIP = 5;
		
		try {
			connectSocket();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		try {
			connectSocket();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		/*
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getHostAddress());
			server = new ServerSocket(1234);//0);
			System.out.println("Hosted Port: " + server.getLocalSocketAddress());
			socket = server.accept();
			
			out = new PrintWriter(socket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
	        System.out.println("Awaiting Client Confirmation...");
	        String msg = in.readLine();
        	if(msg.equals("0")) {
        		System.out.println("Received Client Confirmation.\nSending Server Confirmation...");
        		out.println("0");
        	}
        	
			System.out.println("Connected!");
			statusLabel.setText("Connected!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		
        communicationThread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean canRun = true;
				
				while(canRun) {
		            try {
		            	if(server != null && socket != null && in != null && out != null) {
			            	String data = null;
			            	try {
			            		data = in.readLine();
			            	} catch (SocketException e) {
			            		if(reconnectSocket())
			            			statusLabel.setText("Connected!");
			            		else
			            			statusLabel.setText("Failed to Connect.");
			            	}
			            	
			            	if(data != null) {
				            	System.out.println(data);
				            	
				            	String[] parsedData = data.split(",");
				            	if(parsedData.length > 1) {
					            	double width = Double.parseDouble(parsedData[0]);
					            	double height = Double.parseDouble(parsedData[1]);
					            	
					            	double scaleW = (double)panel.getWidth()/width;
					            	double scaleH = (double)panel.getHeight()/height;
					            	double scale = Math.min(scaleW, scaleH);
					            	
					            	int penSize = (int)Double.parseDouble(parsedData[7]);
					            	Color penColor = new Color(
					            			Integer.parseInt(parsedData[8]));
					            	
					            	Graphics2D graphics = (Graphics2D)panel.getGraphics();
					            	/*
					            	graphics.setRenderingHints(new RenderingHints(
					                        RenderingHints.KEY_ANTIALIASING,
					                        RenderingHints.VALUE_ANTIALIAS_ON));
					            	*/
					            	graphics.setStroke(new BasicStroke((int)(penSize*((scale))), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
					            	graphics.setColor(penColor);
					            	graphics.drawLine(
					            			(int)(Integer.parseInt(parsedData[3]) * scale),
					            			(int)(Integer.parseInt(parsedData[4]) * scale),
					            			(int)(Integer.parseInt(parsedData[5]) * scale),
					            			(int)(Integer.parseInt(parsedData[6]) * scale));
				            	
				            		out.println("0");
				            	}
			            	}
		            	}
		            }
		            catch (IOException e) {
		            	e.printStackTrace();
					}
		        }
				
			}
		});
		panel.setBackground(Color.BLACK);
		
		communicationThread.start();
	}
	
	public static String getPublicIP() throws IOException {
		String publicip = null;
		URL whatismyip = new URL("https://icanhazip.com/");
		
		BufferedReader in = null;
        in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        publicip = in.readLine();
        if (in != null)
        	in.close();
        
        System.out.println("Public IP: " + publicip);
        obfuscateIP(publicip);
        
        return publicip;
	}
	
	public static String obfuscateIP(String ip) {
		//String ip = "24.47.180.250";
		ip = "9.9.9.9";
		String[] ipArray = ip.split("\\.");

		String alphabet = "123456789abcdefghijklmnopqrstuvwxyz";
		String[] hexa = new String[ipArray.length];        
        String[] binary = new String[ipArray.length];
        int[] encrypt = new int[] {1,-2,3,-4,5,-6,7,-8,9,-10};
        char[] leadingDefault = new char[] {alphabet.charAt(9), alphabet.charAt(10),alphabet.charAt(11), alphabet.charAt(12)};
        
	    for (int i = 0; i < ipArray.length; i++) {
	        int temp = Integer.parseInt(ipArray[i]);
	        if (temp >= 0 && temp <= 255) {
	        	//binary[i] = (Integer.toBinaryString(temp));
	            //System.out.print("Binary: " + binary[i]);
	            hexa[i] = (Integer.toHexString(temp));
	            //System.out.print(", Hex: " + hexa[i]);
	            String tempHexa = "";
	            if(temp <= 9)
	            	tempHexa = ""+i;
	            for(int j = 0; j < hexa[i].length(); j++) {
	            	int loc = hexa[i].charAt(j);
	            	//int alphabetIndex = alphabet.indexOf(loc)+9+encrypt[j];
	            	tempHexa += alphabet.charAt(alphabet.indexOf(loc)+9+encrypt[j]);
	            }
	            hexa[i] = tempHexa;
	            System.out.println(", New Hex: " + hexa[i]);
	        }
	        
	    }
	    return ip;
	}
	
	public static boolean connectSocket() throws IOException {
	
		String publicip = getPublicIP();
        
		InetAddress ip;
		
		ip = InetAddress.getLocalHost();
		server = new ServerSocket(1234);//0);
		System.out.println("Local IP: " + ip.getHostAddress());
		System.out.println("Port: " + server.getLocalPort());
		System.out.println("Awaiting Client Connection...");
		socket = server.accept();
		
		out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
        System.out.println("Awaiting Client Transmit Confirmation...");
        String msg = in.readLine();
        System.out.println("In: " + msg);
    	if(msg.equals("0")) {
    		System.out.println("Received Client Transmit Confirmation.\nSending Server Confirmation Receipt...");
    		String outMsg = "0";
    		System.out.println("Out: " + outMsg);
    		out.println(outMsg);
    	}
        	
		System.out.println("Connected!");
		return true;
	}
	
	public static boolean reconnectSocket() throws IOException {
		
		String publicip = getPublicIP();
		
		socket.close();
		socket = null;
		server.close();
		server = null;
		in.close();
		in = null;
		out.println("0");
		out.close();
		out = null;
		
		InetAddress ip;
		ip = InetAddress.getLocalHost();
		System.out.println("Current IP address : " + ip.getHostAddress());
		server = new ServerSocket(1234);//0);
		System.out.println("Hosted Port: " + server.getLocalSocketAddress());
		socket = server.accept();
		
		out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
        System.out.println("Awaiting Client Confirmation...");
        String msg = in.readLine();
    	if(msg.equals("0")) {
    		System.out.println("Received Client Confirmation.\nSending Server Confirmation...");
    		out.println("0");
    	}
        	
		System.out.println("Connected!");
		
		return true;
	}
	
}
