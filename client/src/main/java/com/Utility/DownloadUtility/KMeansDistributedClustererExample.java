/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.Utility.DownloadUtility;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.Utility.IgniteUtl.IgniteMLUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import com.Utility.DownloadUtility.ExampleNodeStartup;
import org.apache.ignite.ml.math.matrix.SparseDistributedMatrixExample;
import org.apache.ignite.ml.clustering.KMeansDistributedClusterer;
import org.apache.ignite.ml.math.StorageConstants;
import org.apache.ignite.ml.math.Tracer;
import org.apache.ignite.ml.math.Vector;
import org.apache.ignite.ml.math.distances.EuclideanDistance;
import org.apache.ignite.ml.math.impls.matrix.SparseDistributedMatrix;
import org.apache.ignite.thread.IgniteThread;

public class KMeansDistributedClustererExample {

    public static void execute(String filePath) throws InterruptedException, IOException {
        // IMPL NOTE based on KMeansDistributedClustererTestSingleNode#testClusterizationOnDatasetWithObviousStructure
        IgniteMLUtil.Kmeans(filePath, 2, 500,"./ML/result.txt");
        Process process = Runtime.getRuntime().exec("python ./ML/draw_plot.py");
        while (process.isAlive());
    }
}
