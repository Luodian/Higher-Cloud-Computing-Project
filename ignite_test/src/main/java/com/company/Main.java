package com.company;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.compute.gridify.Gridify;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;

import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.Collection;

public class Main {
	public static void main (String[] args) {
		IgniteConfiguration cfg = new IgniteConfiguration();
		
		cfg.setPeerClassLoadingEnabled(true);
		
		TcpCommunicationSpi commSpi = new TcpCommunicationSpi();

// Override local port.
		commSpi.setLocalPort(4321);
		
		cfg.setCommunicationSpi (commSpi);

// Start Ignite node.
		Ignite ignite = Ignition.start(cfg);
//		Ignite ignite = Ignition.start("src/main/resources/gridgain/examples/config/example-ignite.xml");
		IgniteCluster cluster = ignite.cluster();

// Compute instance over remote nodes.
		IgniteCompute compute = ignite.compute(cluster.forRemotes());

// Print hello message on all remote nodes.
		compute.broadcast(() -> System.out.println("Hello node: " + cluster.localNode().id()));
//		try (Ignite ignite = Ignition.start ("src/main/resources/gridgain/examples/config/example-ignite.xml"))
//		{
//			Collection<IgniteCallable<Integer>> calls = new ArrayList<> ();
//
//			// Iterate through all the words in the sentence and create Callable jobs.
//			for (final String word : "Count characters using callable".split (" "))
//				calls.add (word::length);
//
//			// Execute collection of Callables on the grid.
//			Collection<Integer> res = ignite.compute ().call (calls);
//
//			// Add up all the results.
//			int sum = res.stream ().mapToInt (Integer::intValue).sum ();
//
//			System.out.println ("Total number of characters is '" + sum + "'.");
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace ();
//		}
	}
}
