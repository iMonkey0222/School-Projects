package A3;

import java.util.*;  
import java.util.ArrayList;

import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.*;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;
import scala.Tuple3;

public class task3{
	public static void main(String[] args) {
		String inputDataPath = args[0];
		String outputDataPath = args[1];
		SparkConf conf = new SparkConf();

		conf.setAppName("Task3");
		JavaSparkContext sc = new JavaSparkContext(conf);

		JavaRDD<String> task2_data = sc.textFile(inputDataPath+"task2.test.1");

		JavaPairRDD<Integer, ArrayList<String>> all = task2_data.mapToPair(s->{
			String[] values = s.split("\t");
			int support = Integer.parseInt(values[0]);
			ArrayList<String> items = new ArrayList<String>();
			for(int i=1; i<=values.length(); i++){
				items.add(values[i]);
			}
			return new Tuple2<Integer, ArrayList<String>>(support, items);
		});

		JavaPairRDD<Integer, ArrayList<String>> oneEle = all.filter(t->{
			return t._2.size()=1;
		});

		JavaPairRDD<Integer, ArrayList<String>> twoEle = all.filter(t->{
			return t._2.size()=2;
		});

		JavaPairRDD<Integer, ArrayList<String>> threeEle = all.filter(t->{
			return t._2.size()=3;
		});
		
		List<Tuple2<Integer, ArrayList<String>>> twoEle_list = twoEle.collect();
		List<Tuple2<Integer, ArrayList<String>>> threeEle_list = threeEle.collect();

		JavaPairRDD<Integer, Tuple2<ArrayList<String>, ArrayList<String>>> two_three = twoEle.flatMap(t->{
			ArrayList<String> list1 = t._2;
			for(Tuple2<Integer, ArrayList<String>> three : threeEle_list){
				int support = three._1;
				for (ArrayList<String> list2: three) {
					if(list1.contanis(list2)){
						return new Tuple2<Integer, Tuple2<ArrayList<String>, ArrayList<String>>>(support/t._1, new Tuple2<ArrayList<String>, ArrayList<String>>(list2, list1));
					}
				}

			}
		});

		JavaPairRDD<Integer, Tuple2<ArrayList<String>, ArrayList<String>>> one_three = oneEle.flatMap(t->{
			ArrayList<String> list1 = t._2;
			for(Tuple2<Integer, ArrayList<String>> three : threeEle_list){
				int support = three._1;
				for (ArrayList<String> list2: three) {
					if(list1.contanis(list2)){
						return new Tuple2<Integer, Tuple2<ArrayList<String>, ArrayList<String>>>(support/t._1, new Tuple2<ArrayList<String>, ArrayList<String>>(list2, list1));
					}
				}

			}
		});

		JavaPairRDD<Integer, Tuple2<ArrayList<String>, ArrayList<String>>> one_two = twoEle.flatMap(t->{
			ArrayList<String> list1 = t._2;
			for(Tuple2<Integer, ArrayList<String>> three : threeEle_list){
				int support = three._1;
				for (ArrayList<String> list2: three) {
					if(list1.contanis(list2)){
						return new Tuple2<Integer, Tuple2<ArrayList<String>, ArrayList<String>>>(support/t._1, new Tuple2<ArrayList<String>, ArrayList<String>>(list2, list1));
					}
				}

			}
		});

		JavaPairRDD<Integer, Tuple2<ArrayList<String>, ArrayList<String>>> finalset = two_three.union(one_three).union(one_two);
		
	}
}


/**
 * spark-submit  \
   --class A3.task3 \
   --master yarn \
   --num-executors 3 \
   sparkML.jar \
   hdfs://soit-hdp-pro-1.ucc.usyd.edu.au:8020/user/hzen4403/A3-Spark/ \
   hdfs://soit-hdp-pro-1.ucc.usyd.edu.au:8020/user/hzen4403/A3-Spark-3/
 */