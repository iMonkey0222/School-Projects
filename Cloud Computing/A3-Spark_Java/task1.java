package A3;

import java.util.ArrayList;
import java.util.Comparator;
import java.io.Serializable;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;
import scala.Tuple3;

public class task1{

	public static void main(String[] args) {
		String inputDataPath = args[0];
		String outputDataPath = args[1];
		SparkConf conf = new SparkConf();

		conf.setAppName("Task1");
		JavaSparkContext sc = new JavaSparkContext(conf);

		// read GEO.txt and PatientData without header
		JavaRDD<String> patientData = sc.textFile(inputDataPath+"PatientMetaData.txt").filter(line->{
			return !line.contains("id");
		});
		JavaRDD<String> geoData = sc.textFile(inputDataPath+"GEO.txt").filter(line->{
			return !line.contains("patientid");
		});


		// patientid, geneid, expression_value
		// Use filter to filter the geodata that gene42>1,250,000
		//JavaPairRDD<String, Tuple2<Integer, Float>> geo42Data = geoData.mapToPair(s->{
		JavaPairRDD<String, Float> geo42Data = geoData.mapToPair(s->{
			String[] values = s.split(",");
			String patientID = values[0];
			int geneNo = Integer.parseInt(values[1]);
			Float expressionValue= Float.parseFloat(values[2]);
			// String s = Float.toString(25.0f);
			return new Tuple2<String, Tuple2<Integer, Float>>(patientID, new Tuple2<Integer, Float>(geneNo, expressionValue));
		}).filter(t->{
			int geneNo = t._2._1;
			Float expressionValue = t._2._2;
			return (geneNo == 42) && (expressionValue > 1250000);
		}).mapToPair(t->{
			return new Tuple2<String, Float>(t._1, t._2._2); // patientid, expression_value
		});

//		System.out.println("Geodata\n"+geo42Data.take(30));


		// s: id, age, gender, postcode, diseases, drug_response
		// flatMapToPair is used because one patient can have multiple dieases
		// id, disease1; id, disease2;...
		JavaPairRDD<String, String> patientWithCancer= patientData.flatMapToPair(s->{
			String[] values = s.split(",");
			String patientID = values[0];
			
			ArrayList<Tuple2<String, String>> results = new ArrayList<Tuple2<String, String>>();
			if (values.length >=6) {
				String[] diseases = values[4].split(" "); // split by space
				for (String disease: diseases) {
					results.add(new Tuple2<String, String>(patientID, disease));
				}
			}
			return results.iterator();
		}).filter(t->{
			// filter records with disease: breast-cancer, prostate-cancer, pancreatic-cancer, leukemia, or lymphoma
			String disease = t._2;
			return disease.contains("breast-cancer")
				|| disease.contains("prostate-cancer") 
				|| disease.contains("pancreatic-cancer")
				|| disease.contains("leukemia")
				|| disease.contains("lymphoma");
		});

		//patientDiease.take(30).foreach(line->println(line));

//		System.out.print("print test \n");
//		System.out.println(patientWithCancer.take(30));

		// input: (patientID,Cancertype) (patientID, expression_value>1250000)
		// 1. Join: -> (patientID, (Cancertype, expression_value))
		// 2. MapToPair: -> (Cancertype, expression_value)
		// 3. ReduceByKey:-> (cnacertype, patient-with-hene42-count)
		JavaPairRDD<String, Integer> cancerPatientCount = patientWithCancer.join(geo42Data).mapToPair(t->{
			String cancer = t._2._1;
			//String patientID = t._1;
			return new Tuple2<String, Integer>(cancer, 1); // (Cancertype,1)
		}).reduceByKey((v1, v2)-> v1 + v2);

		// Do secondary sort: 
		// 1. SortBy expression_value 
		// 2. for same occurance cancertype, sort alphabetically
		JavaPairRDD<Tuple2<String, Integer>, Integer> sortedRdd = cancerPatientCount.mapToPair(t->{
			return new Tuple2<Tuple2<String, Integer>, Integer>(new Tuple2<String, Integer>(t._1, t._2), t._2);
		}).sortByKey(new TupleComparator(), true); // Secondary sort: sort by key alphabetically (true)
		
		JavaPairRDD<String, Integer> sortedRddToPairs = sortedRdd.mapToPair(t->{
			return new Tuple2<String, Integer>(t._1._1, t._1._2);
		});
		

		// Define the output format as string
		JavaRDD<String> sortedCancerCount = sortedRddToPairs.map(t->{			
			return new String(t._1 + "\t" + t._2);
		});
		System.out.println("Final results\n"+sortedCancerCount.take(10));

		//sortedRddToPairs.saveAsTextFile(outputDataPath+"task1.notstring");

		sortedCancerCount.saveAsTextFile(outputDataPath+"task1.sorted.cancter.patientCount-large");
		sc.close();

	}

	// UDF-> Secondary sort: 
	// Sort the tuple via value first (INteger: expression_value), 
	public static final class TupleComparator implements Comparator<Tuple2<String,Integer>>, Serializable {
		public int compare(Tuple2<String,Integer> tuple1, Tuple2<String,Integer> tuple2) {
			if (tuple1._2.compareTo(tuple2._2) == 0) {
				return tuple1._1.compareTo(tuple2._1);
			}
			return -tuple1._2.compareTo(tuple2._2);
		}
	}	

}

/**
 * spark-submit  \
   --class A3.task1 \
   --master yarn \
   --num-executors 2 \
   sparkML.jar \
   hdfs://soit-hdp-pro-1.ucc.usyd.edu.au:8020/share/genedata/large/ \
   hdfs://soit-hdp-pro-1.ucc.usyd.edu.au:8020/user/xwan6774/A3-Spark/
 */

/*
.filter(s->{
			// filter records with disease: breast-cancer, prostate-cancer, pancreatic-cancer, leukemia, or lymphoma
			//String[] cancers = ["breast-cancer", "prostate-cancer", "pancreatic-cancer", "leukemia", "lymphoma"   ];
			return s.contains("breast-cancer") 
				|| s.contains("prostate-cancer") 
				|| s.contains("pancreatic-cancer")
				|| s.contains("leukemia")
				|| s.contains("lymphoma");
		})
 */

		// JavaPairRDD<Integer, String> sorted = cancerPatientCount.mapToPair(t->t.swap()).sortByKey(false,3);
		// JavaPairRDD<String, Integer> sortedResult = sorted.mapToPair(t-> t.swap());
		// System.out.println("sort results\n"+sorted.take(10));
		// System.out.println("Final results\n"+sortedResult.take(10));


