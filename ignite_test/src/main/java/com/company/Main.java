package com.company;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

public class Main {
	public static void main (String[] args) {
		try {
			CacheConfiguration cacheCfg = new CacheConfiguration("myCache");
			cacheCfg.setCacheMode(CacheMode.PARTITIONED);
			IgniteConfiguration cfg = new IgniteConfiguration();
			cfg.setCacheConfiguration(cacheCfg); cfg.setPeerClassLoadingEnabled (true); Ignite ignite = Ignition.start(cfg); IgniteCluster igniteCluster = ignite.cluster(); System.out.println(igniteCluster.node().addresses());

//			IgniteCluster cluster = ignite.cluster ();
//
//			// Cluster group with remote nodes, i.e. other than this node.
//			ClusterGroup remoteGroup = cluster.forRemotes ();
//
//			IgniteCompute compute = ignite.compute (cluster.forRemotes ());
//			// Broadcast to all remote nodes and print the ID of the node
//			// on which this closure is executing.
//			compute.broadcast (() -> System.out.println ("Hello Node: " + ignite.cluster ().localNode ().id ()));
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}
}
