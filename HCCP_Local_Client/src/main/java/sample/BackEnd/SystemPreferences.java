package sample.BackEnd;

import java.io.*;
import java.util.Stack;

public class SystemPreferences {
	private static String hostname;
	private static String inetIP;
	private static String CPUInfo;
	private static String MemInfo;
	private static String GPUInfo;
	private static String AverageAsset;
	private static double StartThreshold;
	
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
		String result = ExecuteCommand ("sh src/main/java/sample/BackEnd/Shell/InetIP.sh");
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
		String result = ExecuteCommand ("sh src/main/java/sample/BackEnd/Shell/CPUInfo.sh");
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
		String result = ExecuteCommand ("sh src/main/java/sample/BackEnd/Shell/MEMInfo.sh");
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
		String result = ExecuteCommand ("sh src/main/java/sample/BackEnd/Shell/GPUInfo.sh");
		int first_comma_index = result.indexOf (":");
		result = result.substring (first_comma_index + 1);
		int second_comma_index = result.indexOf (":");
		result = result.substring (second_comma_index + 1);
		result = result.trim ();
		return result;
	}
	
	//调用之前先询问下密码
	private static void install_sys_bench (String passwd) {
		try {
			String command = "echo " + passwd + " | sudo -S ls";
			File file_stream = new File ("src/main/java/sample/BackEnd/Shell/TrySudo.sh");
			file_stream.createNewFile ();
			// creates a FileWriter Object
			FileWriter writer = new FileWriter (file_stream);
			// 向文件写入内容
			writer.write ("#!/usr/bin/env bash\n" + command);
			
			writer.flush ();
			writer.close ();
			
			String valid_root = ExecuteCommand ("sh src/main/java/sample/BackEnd/Shell/GetSudo.sh");
			
			String install_result = ExecuteCommand ("sh src/main/java/sample/BackEnd/Shell/Install_sys_bench.sh");
			
		} catch (IOException E) {
			E.printStackTrace ();
		}
	}
	
	public static int GetProcessorNum () {
		String processor_num = ExecuteCommand ("sh src/main/java/sample/BackEnd/Shell/GetProcessorNum.sh");
		return Integer.valueOf (processor_num.trim ());
	}
	
	// 这里可能专门需要一个线程来处理这个，因为需要耗费10s左右的时间
	public static Double AssesBenchMark (int thread_num, String password) {
		String which_sysbench = ExecuteCommand ("which python");
		if (which_sysbench.equals ("")) {
			install_sys_bench (password);
		}
		String result = ExecuteCommand ("sh src/main/java/sample/BackEnd/Shell/ComputeAssesment.sh");
		int first_comma_index = result.indexOf ("total time:");
		int first_s_index = result.indexOf ("s", first_comma_index);
		result = result.substring (first_comma_index + 11, first_s_index);
		result = result.trim ();
		Double time = Double.valueOf (result);
		String format_decimal = String.format ("%.2f", time);
		time = Double.valueOf (format_decimal);
		return 100 - time;
	}
	
	public static void setThreshold (double _value) {
		StartThreshold = _value;
	}
}
