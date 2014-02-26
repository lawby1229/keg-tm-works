package edu.thu.keg.weka.cluster;

/**
 * Kmeans 聚类现有手机
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class SimpleKmeans {
	public static ClusterDetail KMeans(String fileName, int numOfCluster) {
		ClusterDetail result = new ClusterDetail();
		// 实例
		Instances insts = null, oldInsts;
		// 算法
		SimpleKMeans skm = new SimpleKMeans();
		File file = new File(fileName);
		ArffLoader loader = new ArffLoader();
		try {
			loader.setFile(file);
			insts = loader.getDataSet();
			result.setNumOfCluster(numOfCluster);
			result.setLabel(new String[insts.numInstances()]);
			result.setClassIdentify(new int[insts.numInstances()]);
			for (int i = 0; i < insts.numInstances(); i++) {
				result.getLabel()[i] = insts.instance(i).toString(0);
			}

			// 第0个属性不要-------------------------------------------------------------

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

				result.getClassIdentify()[i] = skm.clusterInstance(insts
						.instance(i));
				// System.out.println(i + ":"
				// + skm.clusterInstance(insts.instance(i)));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static void saveReToFile(List<ClusterDetail> listRe,
			String fileProffix) {
		FileWriter fw = null;
		String fileName = fileProffix;
		try {

			for (int i = 0; i < listRe.get(0).getLabel().length; i++) {
				String line = "";
				if (i == 0) {
					line = "PHONE_VERSION";
					for (int j = 0; j < listRe.size(); j++) {
						fileName = fileName + "_" + "C"
								+ listRe.get(j).getNumOfCluster();
						line = line + "," + "CLASS"
								+ listRe.get(j).getNumOfCluster();
					}

					fw = new FileWriter(fileName + ".csv");
					fw.write(line + "\n");
					fw.flush();
				}
				line = listRe.get(0).getLabel()[i];// 把品牌信息添加到行中，第一个String[][]的第一列
				for (int j = 0; j < listRe.size(); j++) {

					line = line + "," + listRe.get(j).getClassIdentify()[i];
				}
				fw.write(line + "\n");
				fw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String arg[]) {
		ClusterDetail cd = null;
		String fileName="253手机配置(all)_经过过滤_noQ.arff";
		ArrayList listRe = new ArrayList();
		cd = SimpleKmeans.KMeans(fileName, 4);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 5);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 6);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 7);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 8);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 9);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 10);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 11);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 12);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 13);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 14);
		listRe.add(cd);
		cd = SimpleKmeans.KMeans(fileName, 15);
		listRe.add(cd);
		SimpleKmeans.saveReToFile(listRe, "CClustar_NoQ");
		System.out.print(cd.getLabel()[3]);
	}

}

class ClusterDetail {
	String[] label;
	int[] classIdentify;
	int numOfCluster;

	ClusterDetail() {

	}

	/**
	 * @return the label
	 */
	public String[] getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String[] label) {
		this.label = label;
	}

	/**
	 * @return the classIdentify
	 */
	public int[] getClassIdentify() {
		return classIdentify;
	}

	/**
	 * @param classIdentify
	 *            the classIdentify to set
	 */
	public void setClassIdentify(int[] classIdentify) {
		this.classIdentify = classIdentify;
	}

	/**
	 * @return the classesNum
	 */
	public int getNumOfCluster() {
		return numOfCluster;
	}

	/**
	 * @param classesNum
	 *            the classesNum to set
	 */
	public void setNumOfCluster(int numOfCluster) {
		this.numOfCluster = numOfCluster;
	}

}
