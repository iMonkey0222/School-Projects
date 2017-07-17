package A3;

import java.util.*;  
import java.util.ArrayList;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;
import scala.Tuple3;

public class task2{

	public static void main(String[] args) {
		String inputDataPath = args[0];
		String outputDataPath = args[1];
		SparkConf conf = new SparkConf();

		conf.setAppName("Task2");
		JavaSparkContext sc = new JavaSparkContext(conf);

		// read GEO.txt and PatientData without header
		JavaRDD<String> patientData = sc.textFile(inputDataPath+"PatientMetaData.txt").filter(line->{
			return !line.contains("id");
		});
		JavaRDD<String> geoData = sc.textFile(inputDataPath+"GEO.txt").filter(line->{
			return !line.contains("patientid");
		});


		// patientid, geneid, expression_value
		// Use filter to filter the geodata that gene >1,250,000
		JavaPairRDD<String, Integer> geo_Data = geoData.mapToPair(s->{
			String[] values = s.split(",");
			String patientID = values[0];
			int geneNo = Integer.parseInt(values[1]);
			Float expressionValue= Float.parseFloat(values[2]);
			return new Tuple2<String, Tuple2<Integer, Float>>(patientID, new Tuple2<Integer, Float>(geneNo, expressionValue));
		}).filter(t->{
			int geneNo = t._2._1;
			Float expressionValue = t._2._2;
			return expressionValue > 1250000;
		}).mapToPair(t->{
			return new Tuple2<String, Integer>(t._1, t._2._1); // patientid, geneid
		});



		// s: id, age, gender, postcode, diseases, drug_response
		// flatMapToPair is used because one patient can have multiple dieases
		// id, disease1; id, disease2;...
		JavaPairRDD<String, String> patientWithCancer = patientData.flatMapToPair(s->{
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


		// (patientID,Cancertype) (patientID, geneid with expression_value>1250000)
		// Join: -> (patientID, (Cancertype, geneid))
		// MapToPair: -> (patientID, geneid)
		JavaPairRDD<String, Integer> cancerPatientID = patientWithCancer.join(geo_Data).mapToPair(t->{
			String patientID = t._1;
			Integer geneid = t._2._2;
			return new Tuple2<String, Integer>(patientID, geneid);
		});

		//JavaPairRDD<String, Integer>cancerPatientCount = cancerPatientID.reduceByKey()
		System.out.println("==============TEST 1============");
		// System.out.println("Join results\n"+cancerPatientID.take(50));


		// set the initial support threshold with 30% minimal support
		long num_patientWithCancer = cancerPatientID.sortByKey().keys().distinct().count();
		double minSupport = 0.3;
		long threshold = (long)(num_patientWithCancer*minSupport);

		// System.out.println(num_patientWithCancer);
		// System.out.println(threshold);



		// 1. Computer frequent set with size 1
		// List<Tuple2<ItemSet, Integer>> c1 = cancerPatientID.mapToPair(t->{
		JavaPairRDD<ItemSet, Integer> c1 = cancerPatientID.mapToPair(t->{
			return new Tuple2<ItemSet, Integer>(new ItemSet(t._2), 1);
		}).reduceByKey((v1,v2)->v1+v2).filter(t-> t._2 >= threshold);

		//System.out.println(c1.collect());
		

		// 2. Compute all Frequent Set
		// int frequent = Integer.parseInt(args[2]);	// Get the max itemset_size as iteration
		int frequent = 2;	// set the max itemset_size as iteration number
		System.out.print("-----!The maximum itemset size: "+frequent);


		Set<Integer> tmpEleSet = new HashSet<Integer>();
		JavaPairRDD<ItemSet, Integer> finalUnion = c1;
		JavaPairRDD<String, ItemSet> patientGenesset = null;

		//while(true){
		for (int i = 2; i<=frequent; i++) {
			System.out.println("\nIteration:"+i);
			
			final int f = i;
			Set<Integer> eleSet = transferEleSet(c1.collect()); 	// Original elements set: extract keys (items) in c1
			if (i == 2) {
				tmpEleSet = eleSet;
			}
			Set<Integer> nextEleSet = tmpEleSet;
			System.out.println("Genes Set: \n"+nextEleSet +"\n");

			if (nextEleSet.isEmpty()) {
				System.out.println("Next ele set is EMPTY!\n");
				break;
			}
			
			//1. Original: PatientiD \t GeneID
			JavaPairRDD<String, Integer> filteredPatientGeneID= cancerPatientID.filter(t->{
				return nextEleSet.contains(t._2); 	// filter the patientIDGeneID that is contained in itemset
			});

			if (i==2) {
				System.out.print("\1.Item Set size = 2: Convert original to: PatientiD \t GeneSet\n");
				// 1.2. Convert original to: PatientiD \t GeneSet
				patientGenesset = filteredPatientGeneID.mapToPair(t->{
					ArrayList<Integer> elements = new ArrayList<Integer>(Arrays.asList(t._2));
					return new Tuple2<String, ItemSet>(t._1, new ItemSet(elements));
				});
				//System.out.println("Patient + ItemSet Genes: \n" + patientGenesset.take(50) +"\n");
			}else{System.out.print("Use previous patientID geneID");}
			

			System.out.print("\n2. Start Do self Join!\n");
			// 2. Do self Join: generate new (PatientID GeneID)
			// Join: (patient10,({9}，14)）, (patient10，({9}，21)) 
			// MapToPair: (patient10，({9，14})）,(patient10，({9，21})) 
			JavaPairRDD<String, ItemSet> newPatientGeneID = patientGenesset.join(filteredPatientGeneID).mapToPair(t->{
				ArrayList<Integer> geneArr = new ArrayList<Integer>(t._2._1.items);
				Integer gene = t._2._2;	// new gene
				if (!geneArr.contains(gene)) { // If gene not exist in the set, then add it to set
					geneArr.add(gene); 
				}else{
				// else If gene is already in the set, do nothing
				}
				Collections.sort(geneArr);
				// return new Tuple2<String, ItemSet>(t._1,new ItemSet( new ArrayList<Integer>(t._2._1.items)));
				return new Tuple2<String, ItemSet>(t._1, new ItemSet(geneArr));
			}).filter(t->{ // filter the array that the length is exactly = frequent value
				ArrayList<Integer> geneArr = new ArrayList<Integer>(t._2.items);
				return geneArr.size() == f; //!!!!!!!!!remeber to modify!!: i+1
			}).distinct();
			//.sortByKey();
			//System.out.println("New PatientID + Genes Set: \n" + newPatientGeneID.take(400)+ "\n");

			// 3. Generate new (GeneSet Count)
			// MapToPair: GeneSet \t 1
			// ReduceByKey: GeneSet \t Count
			// Filter: filter the tuple which support value > threshold
			// List<Tuple2<ItemSet, Integer>> newGeneSetCount = newPatientGeneID.mapToPair(t->{
			JavaPairRDD<ItemSet, Integer> newGeneSetCount = newPatientGeneID.mapToPair(t->{
				return new Tuple2<ItemSet, Integer>(t._2,1);
			}).reduceByKey((v1,v2)->v1+v2).filter(t->t._2 >= threshold);

			// Union the result
			finalUnion = finalUnion.union(newGeneSetCount);			

			// 1. Update the  temporary element set used for filtering
			tmpEleSet = transferEleSet(newGeneSetCount.collect());
			System.out.print("temporary Eleset: \n" + tmpEleSet);
			// 2. Update the  patientGenesset set used for join
			patientGenesset = newPatientGeneID;

		}

		// 1. Reverse the key value --> Support Count \t ItemSet
		// 2. SortByKey in desending order
		// 3. GroupByKey to aggregate
		JavaPairRDD<Integer, ItemSet> supportValue_Genes = finalUnion.mapToPair(t->{
			ItemSet geneSet = t._1;
			int count = t._2;
			return new Tuple2<Integer, ItemSet>(count, geneSet);
		}).sortByKey(false);

		// Format the result as support \t item1 \t item2 \t itemn
		JavaRDD<String> results = supportValue_Genes.map(t->{
			StringBuilder sb = new StringBuilder();
			String delima = "\t";

			int support = t._1;
			sb.append(support).append(delima);
			ArrayList<Integer> items = t._2.items;
			for (Integer item : items) {
				sb.append(item).append(delima);
			}
			return sb.toString();
		});

		//supportValue_Genes.saveAsTextFile(outputDataPath+"task2.small");
		supportValue_Genes.saveAsTextFile(outputDataPath+"task2/small");
		sc.close();

	}

	private static Set<Integer> transferEleSet(List<Tuple2<ItemSet, Integer>> tuples){
		Set<Integer> eleSet = new HashSet<Integer>();
		for (Tuple2<ItemSet, Integer> tuple : tuples) {
			ArrayList<Integer> elements = tuple._1().items;
			for (Integer ele: elements) {
				eleSet.add(ele);
			}
		}
		return eleSet;
	}

}



/**
 * spark-submit  \
   --class A3.task2 \
   --master yarn \
   --num-executors 35 \
   sparkML.jar \
   hdfs://soit-hdp-pro-1.ucc.usyd.edu.au:8020/share/genedata/small/ \
   hdfs://soit-hdp-pro-1.ucc.usyd.edu.au:8020/user/xwan6774/A3-Spark/
 */