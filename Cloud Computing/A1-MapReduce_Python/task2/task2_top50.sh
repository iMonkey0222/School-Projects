#!/bin/bash

if [ $# -lt 0 ]; then
    echo "Invalid number of parameters!"
    echo "Usage: ./place_photo_count_driver.sh"
    exit 1
fi

hdfs dfs -rm -r -f Task2

hadoop jar /usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.7.2.jar \
-D mapreduce.job.maps=3 \
-D mapreduce.job.reduces=1 \
-D mapreduce.job.name='Top50 Photo Count' \
-file task2_count_sort_mapper.py \
-mapper task2_count_sort_mapper.py \
-file task2_count_sort_reducer.py \
-reducer task2_count_sort_reducer.py \
-input "Task1/Task1_Result_Locality_Photo+Count"/part* \
-output "Task2/Task2_Top50" \
