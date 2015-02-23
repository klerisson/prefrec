package br.ufu.facom.lsi.prefrec.cluster.apache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.random.RandomGenerator;

public class KMeansClusterer<T extends Clusterable> extends KMeansPlusPlusClusterer<T> {
    
    /** Strategies to use for choose centroid. */
    public static enum CentroidStrategy {
    	
    	AVERAGE,
    	
    	MAJORITY
    }

    private CentroidStrategy strategy;
	
	public KMeansClusterer(int k) {
		super(k);
	}

	public KMeansClusterer(final int k, final int maxIterations) {
		super(k, maxIterations);
	}

	public KMeansClusterer(final int k, final int maxIterations,
			final DistanceMeasure measure) {
		super(k, maxIterations, measure);
	}

	public KMeansClusterer(final int k, final int maxIterations,
			final DistanceMeasure measure, final RandomGenerator random) {
		super(k, maxIterations, measure, random);
	}

	public KMeansClusterer(final int k, final int maxIterations,
			final DistanceMeasure measure, final RandomGenerator random,
			final EmptyClusterStrategy emptyStrategy, CentroidStrategy centroidStrategy) {
		super(k, maxIterations, measure, random, emptyStrategy);
		this.strategy = centroidStrategy;
	}
	
	@Override
	protected List<CentroidCluster<T>> chooseInitialCenters(final Collection<T> points) {
		
		// Convert to list for indexed access. Make it unmodifiable, since removal of items
        // would screw up the logic of this method.
        final List<T> pointList = Collections.unmodifiableList(new ArrayList<T> (points));

        // The number of points in the list.
        final int numPoints = pointList.size() - 1;

        // The resulting list of initial centers.
        final List<CentroidCluster<T>> resultSet = new ArrayList<CentroidCluster<T>>();

        int chosen;
        Random r = new Random(19700621);
        
        
        for(int i = 0; i < k; i++) {
        	
        	//chosen = (int) Math.round(Math.random() * numPoints);
        	chosen = (int) Math.round(r.nextInt(numPoints));
       
        	final T p = pointList.get(chosen);

            resultSet.add(new CentroidCluster<T> (p));
        }
        
        return resultSet;
	}
	
    /**
     * Computes the centroid for a set of points.
     *
     * @param points the set of points
     * @param dimension the point dimension
     * @return the computed centroid for the set of points
     */
	@Override
	protected Clusterable centroidOf(final Collection<T> points, final int dimension) {
        
    	final double[] centroid = new double[dimension];
        int nonEmptyCounter[] = new int[dimension];
        List<Map<Double,Integer>> histoList = null;
        
        if (strategy == CentroidStrategy.MAJORITY) {
        	histoList = new LinkedList<Map<Double,Integer>>();
        	for (int i = 0; i < centroid.length; i++) {
        		histoList.add(new LinkedHashMap<Double,Integer>());
        	}
        }
        
        for (final T p : points) {
            final double[] point = p.getPoint();
            for (int i = 0; i < centroid.length; i++) {
                if(point[i] != -1) {
                	centroid[i] += point[i];
                	nonEmptyCounter[i]++;
                	if (strategy == CentroidStrategy.MAJORITY) {
                		Map<Double, Integer> histo = histoList.get(i);
                		
                		if(histo.containsKey(point[i])) {
                			histo.put(point[i], histo.get(point[i])+1);
                		} else {
                			histo.put(point[i], 1);
                		}
                	}
                }
            }
        }
        
        for (int i = 0; i < centroid.length; i++) {
        	switch(strategy){
		        case AVERAGE:
		        	centroid[i] = nonEmptyCounter[i] > (points.size() / 2) ? centroid[i] / nonEmptyCounter[i] : -1;
		        	break;
		        case MAJORITY:
		        	centroid[i] = nonEmptyCounter[i] > (points.size() / 2) ? this.getMostFrequent(histoList.get(i)) : centroid[i] / nonEmptyCounter[i] ;
		        	break;
		        default:
		        	centroid[i] = nonEmptyCounter[i] != 0 ? centroid[i] / nonEmptyCounter[i] : -1;
		        	break;
	        }
        }
        return new DoublePoint(centroid);
    }
    

    private Double getMostFrequent(Map<Double,Integer> histogram){
    	Double keyOfMostFrequent = -1.0;
    	Integer freqOfMostFrequent = 0;  	
    	
    	for(Map.Entry<Double, Integer> entry : histogram.entrySet()) {
    		if(entry.getValue() > freqOfMostFrequent) {
    			keyOfMostFrequent = entry.getKey();
    			freqOfMostFrequent = entry.getValue();
    		}
    	}
    	return keyOfMostFrequent;
    }

    public CentroidStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(CentroidStrategy strategy) {
		this.strategy = strategy;
	}
}