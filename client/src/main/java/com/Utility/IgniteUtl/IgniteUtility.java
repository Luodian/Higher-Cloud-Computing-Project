package com.Utility.IgniteUtl;

import com.Utility.DownloadUtility.SiteFileFetch;
import com.Utility.DownloadUtility.Utility;
import org.apache.ignite.*;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.transactions.Transaction;
import sample.Controller;
import sample.DownController;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static com.Utility.DownloadUtility.FileInfo.getFileSize;

public class IgniteUtility {

	static int receive_num = 0;
	// 根据接收到的Url和分段数，启动对应数目个结点。
	public static void multicast (Ignite ignite, Collection<UUID> sons_id, String url, int seg_num) {
		int cnt = 1;
		long fileLen = getFileSize (url);

		long[] startPos = new long[seg_num];
		long[] endPos = new long[seg_num];

		startPos = Utility.fileSplit (0, fileLen, seg_num);
		if (startPos == null) {
			Utility.log ("File invalided!");
		} else {
			for (int i = 0; i < endPos.length - 1; i++) {
				endPos[i] = startPos[i + 1];
			}
			endPos[endPos.length - 1] = fileLen;
		}
		for (UUID nodeID : sons_id) {
			assert startPos != null;
			broadcast (ignite, nodeID, url, startPos[cnt - 1], endPos[cnt - 1], cnt);
			cnt++;
		}
	}
	
	public static void broadcast (Ignite ignite, UUID son_id, String url, long start, long end, int seriesID) {
		try {
			// Messaging instance over given cluster group (in this case, remote nodes).
			IgniteMessaging rmtMsg = ignite.message (ignite.cluster ().forRemotes ().forNodeId (son_id));
			ArrayList<String> config = new ArrayList<> ();
			config.add (url);
			config.add (String.valueOf (start));
			config.add (String.valueOf (end));
			config.add (String.valueOf (seriesID));
			rmtMsg.sendOrdered ("DownloadTaskComing", config.toString (), 1);
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}
	
	public static void ConcateByteArray (ArrayList<byte[]> multi_file, String filename) {
		try {
			FileOutputStream dout = new FileOutputStream (filename);
			for (byte[] aMulti_file : multi_file) {
				dout.write(aMulti_file,0 ,aMulti_file.length);
			}
			dout.close ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}
	
//	public static Ignite startDownloadIgnite (Map<String,String> group_attr, String url, String filepath, String filename, String cachename, int node_num)
//    {
//		CacheConfiguration cacheCfg = new CacheConfiguration (cachename);
//		cacheCfg.setCacheMode (CacheMode.PARTITIONED);
//
//		IgniteConfiguration cfg = new IgniteConfiguration ();
//		cfg.setCacheConfiguration (cacheCfg);
//        cfg.setUserAttributes(group_attr);
//        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
////        discoverySpi.setNetworkTimeout(5000);
//        discoverySpi.setClientReconnectDisabled(false);
//        cfg.setDiscoverySpi(discoverySpi);
//		cfg.setPeerClassLoadingEnabled (true);
//
//		Ignite ignite = Ignition.start (cfg);
//
////		Collection<UUID> node_ids = new ArrayList<> ();
////
////		ClusterGroup cg = ignite.cluster ().forRemotes ();
////		Collection<ClusterNode> clusterNodes = cg.nodes ();
////		for (ClusterNode e : clusterNodes) {
////			node_ids.add (e.id ());
////		}
////
////		//		String url = "https://ws1.sinaimg.cn/large/006tNc79ly1fn4o49dqcaj30sg0sgmzo.jpg";
////
////		multicast (ignite, node_ids, url, node_num);
//
//        ArrayListSerializaion<byte[]> buffer = new ArrayListSerializaion<byte[]> ();
//
//		IgniteMessaging igniteMessaging = ignite.message (ignite.cluster ().forLocal ());
//
//		for (int i = 1; i <= node_num; ++i)
//		{
//			igniteMessaging.localListen (String.valueOf (i), (nodeID, msg) ->
//			{
//				System.out.println (msg);
//				//				System.out.println ("LLLLLLLLLLl");
//				if (msg.equals ("SUCCESS")) {
//					//					System.out.println ("LLLLLLLLLLl");
//					receive_num++;
//					if (receive_num == node_num) {
//
//						for (int j = 1; j <= node_num; ++j) {
//							Transaction tx = ignite.transactions ().txStart ();
//							IgniteCache<String, ArrayListSerializaion<byte[]>> tmp_cache = ignite.cache (cachename);
//							tx.commit ();
//							ArrayListSerializaion<byte[]> tmp_byte = tmp_cache.get (String.valueOf (j));
//							buffer.addAll (tmp_byte);
//						}
//						int count = 0;
//                        for (byte[] e:buffer) {
//                            count += e.length;
//                        }
//                        System.out.println(count);
//                        if (filepath.charAt (filepath.length () - 1) != '/') {
//							ConcateByteArray (buffer, filepath.concat ("/") + filename);
//						} else {
//							ConcateByteArray (buffer, filepath + filename);
//						}
//					}
//				}
//				return true;
//			});
//		}
//		return ignite;
//	}
public static Ignite startDownloadIgnite (Map<String,String> group_attrs,String cachename,String gridName) {

    CacheConfiguration cacheCfg = new CacheConfiguration (cachename);
    cacheCfg.setCacheMode (CacheMode.PARTITIONED);

    IgniteConfiguration cfg = new IgniteConfiguration ();
    cfg.setCacheConfiguration (cacheCfg);
    cfg.setUserAttributes (group_attrs);

    cfg.setPeerClassLoadingEnabled (true);

    cfg.setGridName(gridName);
    Ignite ignite = Ignition.start (cfg);

//		Collection<UUID> node_ids = new ArrayList<> ();
//
//		ClusterGroup cg = ignite.cluster ().forRemotes ();
//		Collection<ClusterNode> clusterNodes = cg.nodes ();
//		for (ClusterNode e : clusterNodes) {
//			node_ids.add (e.id ());
//		}

    //		String url = "https://ws1.sinaimg.cn/large/006tNc79ly1fn4o49dqcaj30sg0sgmzo.jpg

//    ";

    return ignite;
}

    public static void listenDownloading(Ignite ignite,String filepath, String filename, String cachename, int node_num)
    {
        ArrayListSerializaion<byte[]> buffer = new ArrayListSerializaion<byte[]> ();

        IgniteMessaging igniteMessaging = ignite.message (ignite.cluster ().forLocal ());

        for (int i = 1; i <= node_num; ++i) {
            igniteMessaging.localListen (String.valueOf (i), (nodeID, msg) ->
            {
                System.out.println (msg);
                //				System.out.println ("LLLLLLLLLLl");
                if (msg.equals ("SUCCESS")) {
                    //					System.out.println ("LLLLLLLLLLl");
                    receive_num++;
                    if (receive_num == node_num) {
                    	DownController.isFinishPutIntoCache = true;
                        for (int j = 1; j <= node_num; ++j) {
                            Transaction tx = ignite.transactions ().txStart ();
                            IgniteCache<String, ArrayListSerializaion<byte[]>> tmp_cache = ignite.cache (cachename);
                            tx.commit ();
                            ArrayListSerializaion<byte[]> tmp_byte = tmp_cache.get (String.valueOf (j));
                            buffer.addAll (tmp_byte);
                        }
                        int count = 0;
                        for (byte[] e:buffer) {
                            count += e.length;
                        }
                        System.out.println(count);
                        if (filepath.charAt (filepath.length () - 1) != '/') {
                            ConcateByteArray (buffer, filepath.concat ("/") + filename);
                        } else {
                            ConcateByteArray (buffer, filepath + filename);
                        }
						DownController.isFinishMerge = true;
                    }
                }
                return true;
            });
        }
    }
    //1. start
    //2. listen()
    //3. multicast()


    public static Ignite startDefaultIgnite (Map<String,String> group_attr,String cachename,String gridName)
    {
        CacheConfiguration cacheCfg = new CacheConfiguration (cachename);
        cacheCfg.setCacheMode (CacheMode.PARTITIONED);

        IgniteConfiguration cfg = new IgniteConfiguration ();
        cfg.setCacheConfiguration (cacheCfg);
        cfg.setUserAttributes(group_attr);
        cfg.setGridName(gridName);
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
//        discoverySpi.setNetworkTimeout(5000);
        discoverySpi.setClientReconnectDisabled(false);
        cfg.setDiscoverySpi(discoverySpi);
        cfg.setPeerClassLoadingEnabled (true);
		
		Ignite ignite = Ignition.start (cfg);
        IgniteTransactions transactions = ignite.transactions ();

		IgniteMessaging igniteMessaging = ignite.message (ignite.cluster ().forLocal());
		//监听的消息是接受方而不是发送方，监听的是接受这个动作而不是发送这个东作，所以其实这里填写local就可以了
		igniteMessaging.remoteListen ("DownloadTaskComing", (nodeID, msg) -> {
			Controller.receiveTask = true;
			msg = msg.toString ().substring (1, msg.toString ().length () - 1);
			String tmp[] = msg.toString ().split (", ");
			if (tmp.length != 4) {
				System.err.println ("WRONG DOWNLOAD TASK COMING MESSAGE");
				return true;
			}
			System.err.println (msg.toString ());
			String url = tmp[0];
			long start = Long.valueOf (tmp[1]);
			long end = Long.valueOf (tmp[2]);
			String seriesID = tmp[3];
			SiteFileFetch siteFileFetch = new SiteFileFetch (url, "./Downloads/", nodeID + "_" + seriesID, start, end, 5);
			siteFileFetch.start ();
			try {
				siteFileFetch.join ();
				Controller.isFinishHelp = true;
				Transaction tx = transactions.txStart ();
				IgniteCache<String, ArrayListSerializaion<byte[]>> cache = ignite.cache (cachename);

				File file = new File ("./Downloads/" + nodeID + "_" + seriesID);
                ArrayListSerializaion<byte[]> tmpArrays = new ArrayListSerializaion<byte[]>();
				try (FileInputStream fileInputStream = new FileInputStream (file);
				     BufferedInputStream bufferedInputStream = new BufferedInputStream (fileInputStream)
				) {
                    byte[] buffer = new byte[1024];
                    int readBytesAmount;
                    while ((readBytesAmount = bufferedInputStream.read(buffer, 0, 1024)) > 0 ) {
                        byte[] additem = new byte[readBytesAmount];
                        System.arraycopy(buffer, 0, additem, 0, readBytesAmount + start >= end ? (int)(end - start) : readBytesAmount);
                        buffer = new byte[1024];
                        tmpArrays.add(additem);
                    }
                    int count =0 ;
                    for (byte[] e:tmpArrays)
                    {
                        count += e.length;
                    }
                    System.out.println(count);
                    cache.put(seriesID, tmpArrays);
					tx.commit ();

					Controller.receiveTask = false;

					IgniteMessaging messaging = ignite.message (ignite.cluster ().forNodeId (nodeID));
					messaging.send (seriesID, "SUCCESS");
					System.out.println ("Send Message!");

				} catch (IOException e) {
					e.printStackTrace ();
				}
			} catch (InterruptedException e) {
				e.printStackTrace ();
			}
			return true;
		});
		return ignite;
	}
}
