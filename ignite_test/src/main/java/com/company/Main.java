package com.company;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.compute.ComputeTaskFuture;

public class Main {
	public static void main (String[] args) {
		
		try (Ignite ignite = Ignition.start ("examples/config/example-cache.xml"))
		{
// Limit broadcast to remote nodes only and
// enable asynchronous mode.
			IgniteCompute compute = ignite.compute(ignite.cluster().forRemotes()).withAsync();
// Print out hello message on remote nodes in the cluster group.
			compute.broadcast(() -> System.out.println("Hello Node: " + ignite.cluster().localNode().id()));
			ComputeTaskFuture<?> fut = compute.future();
			fut.listen(f -> System.out.println("Finished sending broadcast job."));
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}
}
