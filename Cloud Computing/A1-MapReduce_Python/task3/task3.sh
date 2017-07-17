#!/bin/bash

if [ $# -lt 1 ]; then
    echo "Invalid number of parameters!"
    echo "Usage: ./task3.sh [allphotofiles] or [n01] or [n02] or [n07]"
    exit 1
fi

hdfs dfs -rm -r -f Task3

# Join tag with top50's place url
hadoop jar /usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.7.2.jar \
-D stream.num.map.output.key.fields=2 \
-D map.output.key.field.separator=# \
-D mapreduce.partition.keypartitioner.options=-k1,1 \
-D mapreduce.job.maps=10 \
-D mapreduce.job.reduces=13 \
-D mapreduce.job.name="Associate top50's url with tag" \
-mapper s1_reduceside_join_mapper.py -file s1_reduceside_join_mapper.py \
-reducer s1_reduceside_join_reducer.py -file s1_reduceside_join_reducer.py \
-input "Task2/Task2_Top50/part"* \
-input "Task1/place-photo-joinFile/part"* \
-output "Task3/tmpfile/s1-url-tag-joinFile" \
-partitioner org.apache.hadoop.mapred.lib.KeyFieldBasedPartitioner \


#  increase the map heap size
#  -D mapreduce.map.java.opts=-Xmx2014m \
hadoop jar /usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.7.2.jar \
-D stream.num.map.output.key.fields=1 \
-D mapreduce.partition.keypartitioner.options=-k1,1 \
-D mapreduce.job.maps=15 \
-D mapreduce.job.reduces=12 \
-D mapreduce.job.name='Tag count 1' \
-mapper s2_tag_mapper.py -file s2_tag_mapper.py \
-reducer s2_tag_reducer.py -file s2_tag_reducer.py \
-input "Task3/tmpfile/s1-url-tag-joinFile/part"* \
-output "Task3/tmpfile/s2-reducer1" \
-partitioner org.apache.hadoop.mapred.lib.KeyFieldBasedPartitioner \

# Sort top10 tags frequency for each place url
# Set 4 GB for Map task Containers, and 8 GB for Reduce tasks Containers.
# and increase the map and reduce heap size
hadoop jar /usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.7.2.jar \
-D stream.num.map.output.key.fields=1 \
-D mapreduce.map.memory.mb=4096 \
-D mapreduce.reduce.memory.mb=8192 \
-D mapreduce.map.java.opts=-Xmx3072m \
-D mapreduce.reduce.java.opts=-Xmx6144m \
-D mapreduce.job.maps=20 \
-D mapreduce.job.reduces=10 \
-D mapreduce.job.name='Tag count 2' \
-mapper s2_2mapper.py -file s2_2mapper.py \
-reducer s2_tag_sort_top_10_reducer.py -file s2_tag_sort_top_10_reducer.py \
-input "Task3/tmpfile/s2-reducer1/part"* \
-output "Task3/tmpfile/s2-top10-tag-frequency" \


# Copy task2's result to local
hdfs dfs -copyToLocal Task2/Task2_Top50/part-00000

hadoop jar /usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.7.2.jar \
-D stream.num.map.output.key.fields=1 \
-D mapreduce.job.maps=5 \
-D mapreduce.job.reduces=1 \
-D mapreduce.job.name='task3 join photocount with result' \
-files part-00000 \
-mapper s3_mapper.py -file s3_mapper.py \
-reducer s3_url_count_tag_join.py -file s3_url_count_tag_join.py \
-input "Task3/tmpfile/s2-top10-tag-frequency/part"* \
-output "Task3/"$1"top50_place_with_top10_tag_frequency" \

#hdfs dfs -rm -r -f Task3/tmpfile

#Remove the local file
rm -r -f part-00000

