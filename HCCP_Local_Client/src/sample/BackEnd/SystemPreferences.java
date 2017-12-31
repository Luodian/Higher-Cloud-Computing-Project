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
	
	public static String fetchCPUInfo () {
		String result = ExecuteCommand ("sh src/sample/BackEnd/Shell/CPUInfo.sh");
		result = result.trim ();
		int first_comma_index = 0;
		int second_comma_index = 0;
		int third_comma_index = 0;
		first_comma_index = result.indexOf (":");
		second_comma_index = result.indexOf (":", first_comma_index + 1);
		third_comma_index = result.indexOf (":", second_comma_index + 1);
		String cpuinfo = result.substring (second_comma_index + 1, third_comma_index);
		cpuinfo = cpuinfo.trim ();
		cpuinfo = cpuinfo.substring (0, cpuinfo.length () - 5);
		return cpuinfo;
	}
	
	public static String fetchMemInfo () {
		String result = ExecuteCommand ("sh src/sample/BackEnd/Shell/MEMInfo.sh");
		result = result.trim ();
		int first_comma_index = result.indexOf (":");
		result = result.substring (first_comma_index + 1);
		result = result.trim ();
		String MemTotal = "";
		for (int i = 0; i < result.length () && !String.valueOf (result.charAt (i)).equals (" "); ++i) {
			MemTotal += String.valueOf (result.charAt (i));
		}
		String mem_value = null;
		if (MemTotal.length () == 7) {
			mem_value = String.valueOf (result.charAt (0)) + " GB";
		} else if (MemTotal.length () == 8) {
			mem_value = result.substring (0, 1) + " GB";
		} else if (MemTotal.length () == 6) {
			mem_value = result.substring (0, 2) + " MB";
		} else {
			mem_value = "Exception";
		}
		return mem_value;
	}
	
	public static String fetchGPUInfo () {
		String result = ExecuteCommand ("sh src/sample/BackEnd/Shell/GPUInfo.sh");
		int first_comma_index = result.indexOf (":");
		result = result.substring (first_comma_index + 1);
		int second_comma_index = result.indexOf (":");
		result = result.substring (second_comma_index + 1);
		result = result.trim ();
		return result;
	}
}
