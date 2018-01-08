package com.Utility.IgniteUtl;

import com.Utility.DownloadUtility.KMeansDistributedClustererExample;
//import com.Utility.DownloadUtility.KNNClassificationExample;
//import com.Utility.DownloadUtility.MLPGroupTrainerExample;
import com.Utility.DownloadUtility.ReadFile;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.ml.clustering.KMeansDistributedClusterer;
import org.apache.ignite.ml.knn.models.KNNModel;
import org.apache.ignite.ml.knn.models.KNNStrategy;
import org.apache.ignite.ml.math.Matrix;
import org.apache.ignite.ml.math.StorageConstants;
import org.apache.ignite.ml.math.Tracer;
import org.apache.ignite.ml.math.Vector;
import org.apache.ignite.ml.math.distances.DistanceMeasure;
import org.apache.ignite.ml.math.distances.EuclideanDistance;
import org.apache.ignite.ml.math.impls.matrix.DenseLocalOnHeapMatrix;
import org.apache.ignite.ml.math.impls.matrix.SparseDistributedMatrix;
import org.apache.ignite.ml.nn.LabeledVectorsCache;
import org.apache.ignite.ml.nn.MLPGroupUpdateTrainerCacheInput;
import org.apache.ignite.ml.nn.MultilayerPerceptron;
import org.apache.ignite.ml.nn.architecture.MLPArchitecture;
import org.apache.ignite.ml.nn.initializers.RandomInitializer;
import org.apache.ignite.ml.nn.trainers.distributed.MLPGroupUpdateTrainer;
import org.apache.ignite.ml.nn.updaters.RPropParameterUpdate;
import org.apache.ignite.ml.structures.LabeledDataset;
import org.apache.ignite.ml.structures.LabeledDatasetTestTrainPair;
import org.apache.ignite.ml.structures.LabeledVector;
import org.apache.ignite.ml.structures.preprocessing.LabeledDatasetLoader;
import org.apache.ignite.ml.structures.preprocessing.LabellingMachine;
import org.apache.ignite.thread.IgniteThread;
import org.apache.ignite.ml.math.functions.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by elrond wang
 * 2018/1/7
 */
public class IgniteMLUtil {
    /** Separator. */
    private static final String SEPARATOR = "\t";
    /** distanceMeasure you can set is manually*/
    private static final DistanceMeasure distance = new EuclideanDistance();


    public static void Kmeans(String path_datasets, int k, int maxIterations, String aim_path){
        System.out.println(">>> K-means distributed clusterer example started.");
        String fileName = path_datasets;
        int kind = k;
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setPeerClassLoadingEnabled(true);
        try (Ignite ignite = Ignition.start(igniteConfiguration)){
            System.out.println(">>> Ignite grid started.");

            IgniteThread igniteThread;
            igniteThread = new IgniteThread(ignite.configuration().getIgniteInstanceName(),
                    KMeansDistributedClustererExample.class.getSimpleName(), () -> {
                ReadFile df = null;
                try {
                    df = new ReadFile(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int rowNum = df.getRowNum();
                int colNum = df.getColNum();

                SparseDistributedMatrix points = new SparseDistributedMatrix(rowNum, colNum,
                        StorageConstants.ROW_STORAGE_MODE, StorageConstants.RANDOM_ACCESS_MODE);

                try {
                    df.write(points);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                KMeansDistributedClusterer clusterer = new KMeansDistributedClusterer(distance, 3, maxIterations, 1L);

                System.out.println("the number of Clusters: " + kind);

                Vector[] resCenters = clusterer.cluster(points, kind).centers();

                File out_file = new File(aim_path);
                try(FileOutputStream fileOutputStream = new FileOutputStream(out_file);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)){
                    for(int i=0; i<rowNum ; i++){
                        Vector tuple = points.getRow(i);
                        int clusterbelongindex = -1;
                        double mindistance = Double.MAX_VALUE;
                        for (int j = 0 ;j < resCenters.length ; j ++){
                            double distance = new EuclideanDistance().compute(tuple, resCenters[j]);
                            if (distance < mindistance){
                                mindistance = distance;
                                clusterbelongindex = j;
                            }
                        }
                        for (int j = 0;j< colNum ; j++){
                            bufferedWriter.write(tuple.get(j) + " ");
                        }
                        bufferedWriter.write(clusterbelongindex + "\r\n");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                System.out.println("Cluster centers:");

                System.out.println(resCenters.length);

                Arrays.asList(resCenters).forEach(Tracer::showAscii);

                points.destroy();

                System.out.println("\n>>> K-means distributed clusterer example completed.");

            });

            igniteThread.start();

            igniteThread.join();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** */
    private static boolean closeEnough(Vector v1, Vector v2) {
        return v1.minus(v2).kNorm(2) < 5E-1;
    }
}
