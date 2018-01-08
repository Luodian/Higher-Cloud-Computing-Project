package sample;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class SayHelloUtil {
	
	// 开启用于发送消息的ignite
	
	public static Ignite startIgnite_send(Map<String, String> group_attrs)
	{
	
		IgniteConfiguration cfg = new IgniteConfiguration ();
		cfg.setPeerClassLoadingEnabled (true);
		// 设置群组
		cfg.setUserAttributes(group_attrs);
		//配置断点自动重连
		TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
		
		discoverySpi.setNetworkTimeout (5000);
		
		discoverySpi.setClientReconnectDisabled(true);
		
		cfg.setDiscoverySpi(discoverySpi);
		
		return Ignition.start(cfg);
	}
	
	public static void send_message(Ignite ignite , String Username, String group_key,String group_value)
	{
		IgniteMessaging rmtMsg = ignite.message (ignite.cluster ().forAttribute (group_key,group_value));
		
		rmtMsg.sendOrdered ("HelloMessageComing", "Hello, here is: " + Username, 1);
	}
	
	// 开启用于监控的ignite
	
	public static Ignite startIgnite_default(Map<String, String> group_attrs,String group_key,String group_value)
	{
		IgniteConfiguration cfg = new IgniteConfiguration ();
		
		cfg.setPeerClassLoadingEnabled (true);
		
		cfg.setUserAttributes (group_attrs);
		
		//配置断点自动重连
		TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
		
		discoverySpi.setNetworkTimeout (5000);
		
		discoverySpi.setClientReconnectDisabled(true);
		
		cfg.setDiscoverySpi(discoverySpi);
		
		Ignite ignite = Ignition.start (cfg);
		
		IgniteMessaging igniteMessaging = ignite.message (ignite.cluster ().forAttribute (group_key,group_value));
		//监听的消息是接受方而不是发送方，监听的是接受这个动作而不是发送这个东作，所以其实这里填写local就可以了
		igniteMessaging.localListen ("HelloMessageComing", (nodeID, msg) ->
		{
			try
			{
				System.out.println (msg);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
			return true;
		});
		return ignite;
	}
}
