import java.awt.Desktop;
import java.net.URI;

public class Phyphox {
	
	private static Desktop desktop = Desktop.getDesktop();
	
	public static void start(String IPAddress) {
		String start = "http://" + IPAddress + "/control?cmd=start";
		execute(start);
	}
	
	public static void clear(String IPAddress) {
		String clear = "http://" + IPAddress + "/control?cmd=clear";
		execute(clear);
	}
	
	public static void save(String IPAddress) {
		String save = "http://" + IPAddress + "/export?format=0";
		execute(save);
	}
	
	public static void execute(String command) {
		try {
			  URI oURL = new URI(command);
			  desktop.browse(oURL);
			} catch (Exception e) {
			  System.out.println("Phyphox execute fail!");
			}
	}

}
