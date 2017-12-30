package sample.BackEnd;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemPreferences {
	private String hostname = null;
	private String inetIP = null;
	private String CPUInfo = null;
	private String MemInfo = null;
	private String GPUInfo = null;
	private String AverageAsset = null;
	private String StartThreshold = null;
	
	SystemPreferences () {
	
	}
	
	private static String ExecuteCommand (String command) {
		String output = "";
		Process p;
		try {
			p = Runtime.getRuntime ().exec (command);
			p.waitFor ();
			BufferedReader reader = new BufferedReader (new InputStreamReader (p.getInputStream ()));
			String line = "";
			while ((line = reader.readLine ()) != null) {
				output += line;
			}
		} catch (Exception e) {
			e.printStackTrace ();
		}
		return output;
	}
	
	
	public static String fetchHostName () {
		return ExecuteCommand ("cat /etc/hostname");
	}
	
	public String fetchInetIP () {
		return inetIP;
	}
}
