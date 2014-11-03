package br.ufu.facom.lsi.prefrec.clusterer.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import br.ufu.facom.lsi.prefrec.clusterer.distance.CosineDistance;

public class App {

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		File[] files = getFiles("./files2/");
		
		CosineDistance cd = new CosineDistance();
		EuclideanDistance ed = new EuclideanDistance();
		DBSCANClusterer dbscan = new DBSCANClusterer(2, 1, cd);
		List<Cluster<DoublePoint>> cluster = dbscan.cluster(getGPS(files));

		for (Cluster<DoublePoint> c : cluster) {
			System.out.println(c);
			for(DoublePoint dp : c.getPoints()){
				System.out.println(dp.toString());
			}
			System.out.println();
		}
	}

	private static File[] getFiles(String args) {
		return new File(args).listFiles();
	}

	private static List<DoublePoint> getGPS(File[] files)
			throws FileNotFoundException, IOException {

		List<DoublePoint> points = new ArrayList<DoublePoint>();
		
		double[] d1 = {5,4,2,1};
		double[] d2 = {1,2,5,4};
		double[] d3 = {4,5,1,2};
		double[] d4 = {2,1,4,5};
		
		points.add(new DoublePoint(d1));
		points.add(new DoublePoint(d2));
		points.add(new DoublePoint(d3));
		points.add(new DoublePoint(d4));
		
		return points;
	}
}