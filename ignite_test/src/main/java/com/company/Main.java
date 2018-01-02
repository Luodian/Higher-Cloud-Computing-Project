package com.company;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;

public class Main {
	public static void main (String[] args) {
		try (Ignite ignite = Ignition.start ("examples/config/example-ignite.xml")) {
			IgniteCluster cluster = ignite.cluster ();
			
			// Cluster group with remote nodes, i.e. other than this node.
			ClusterGroup remoteGroup = cluster.forRemotes ();
			
			IgniteCompute compute = ignite.compute (cluster.forRemotes ());
			// Broadcast to all remote nodes and print the ID of the node
			// on which this closure is executing.
			compute.broadcast (() -> System.out.println ("Hello Node: " + ignite.cluster ().localNode ().id ()));
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}
}
