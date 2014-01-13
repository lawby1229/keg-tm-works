package edu.thu.keg.weka.cluster;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class SimpleKmeans {
	public static void KMeans(String fileName, int numOfCluster) {
		// 实例
		Instances insts = null;
		// 算法
		SimpleKMeans skm = new SimpleKMeans();
		File file = new File(fileName);
		ArffLoader loader = new ArffLoader();
		try {
			loader.setFile(file);

			insts = loader.getDataSet();
			// 第0个属性不要
			insts.deleteAttributeAt(0);
			// 聚类个数
			skm.setNumClusters(numOfCluster);
			// 保留实例有序
			skm.setPreserveInstancesOrder(true);
			skm.setMaxIterations(100);
			// 先聚类
			skm.buildClusterer(insts);
			// 聚类结果数量分布
			System.out.println(Arrays.toString(skm.getClusterSizes()));
			// 聚类结果下标数组
			System.out.println(Arrays.toString(skm.getAssignments()));
			System.out.println("kmeans算法:" + skm);
			// System.out.println("样本arff:" + insts);
			// 得到最终的凝聚点（类中心）arff
			Instances tempIns = skm.getClusterCentroids();
			System.out.println("聚类中心arff:" + tempIns);
			// 再分类

			for (int i = 0; i < insts.numInstances(); i++) {
				System.out.println(i + ":"
						+ skm.clusterInstance(insts.instance(i)));

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String arg[]) {
		SimpleKmeans.KMeans("mobile_function_train2k_test1k.arff", 10);
	}
}
