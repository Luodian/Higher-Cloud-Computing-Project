package sample.BackEnd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Stack;

public class SystemPreferences {
	private static String hostname;
	private static String inetIP;
	private static String CPUInfo;
	private static String MemInfo;
	private static String GPUInfo;
	private static String AverageAsset;
	private static String StartThreshold;
	
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
		hostname = ExecuteCommand ("cat /etc/hostname");
		return hostname;
	}
	
	public static String fetchInetIP () {
		String result = ExecuteCommand ("sh src/sample/BackEnd/Shell/InetIP.sh");
		result = result.trim ();
		int first_index = result.indexOf ("netmask");
		int second_index = result.indexOf ("netmask", first_index + 8);
		inetIP = "";
		Stack<Character> temp = new Stack<> ();
		temp.clear ();
		for (int i = second_index - 2; i >= 0 && !" ".equals (String.valueOf (result.charAt (i))); --i) {
			temp.push (result.charAt (i));
		}
		while (!temp.isEmpty ()) {
			inetIP += String.valueOf (temp.peek ());
			temp.pop ();
		}
		return inetIP;
	}
}
