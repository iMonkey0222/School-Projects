#### 中文版
# 学校项目
## 1.云计算项目：大数据处理
分别用**MapReduce, Flink, Spark**进行大数据处理。
* 项目一：用Hadoop MapReduce来分析提供好的来自flickr.com的照片信息数据，获得关于数据集的总结性信息如每个地区的照片数量、Top50的地区、在前50个地区中每个地区最流行的10个标签。
* 项目二：用Flink处理分析流式细胞的数据集从而研究对于流感的免疫反应，分析任务包括每个研究者的有效的测量值数量、用k-means集群细胞测量值、异常值去除及再次集群。
* 项目三： 用Spark处理分析基因表达数据集，研究病人的单个基因的活动表达。任务包括每个癌症类型有特定活跃基因的病人数量等。

#### 实现说明
* 编程语言：Java, Python
* 框架：Hadoop/YARN
* 数据存储：HDFS
* Demo所用集群：用Microsoft Azure创建集群运行以上3个项目。

## 2. 机器学习与数据挖掘项目
用3种机器学习方法实现Cifar10的图像分类: CNN,Random Forest,Adaboost。

* 项目一：用自定义的朴素贝叶斯实现已给数据的分类 （不允许使用外部library）
* 项目二：用3种方法实现Cifar10（视觉数据集）的分类
	* 方法一：Adaboost
	* 方法一：随机森林
	* 方法一：卷积神经网络

#### 实现说明
* 框架使用：Keras
* 所用library：scikit-learn, matplotlib,numpy 
* 编程语言：python3
* 编程文档：Jupyter Notebook

#### 英文版
# School-Projects
Projects Finished in Year 2 Semester 1 will be listed under the follwing directory:

## 1. Cloud Computing 
There are 3 main projects related to **Big Data Processing**:
* Project 1 -- MapReduce Hadoop Project. 
* Proejct 2 -- Processing data with platform Flink
* Project 3 -- Processing data with platform Spark

	* Programming Language: Python
	* *Hadoop HDFS* was used as filesystem to store large data sets.
	* *Hadoop YARN* was used as resource manager in above projects.
	* *Microsoft Azure* was also used to create cluster to run the above projects in Demo.

## 2. Machine Learning and Data Mining
* Project 1 -- Classifying the given dataset with self defined Navie Bayes Classfier (not allowed to use external library).
* Project 2 -- Using 3 methods to classify the Cifar10 (vision dataset).
	* Method 1: Adaboost
	* Method 2: Random Forest
	* Method 3: Convolutional neural network (CNN) 

		* Programming Language: Python
		* External Library: Keras, Scikit-learn, Matlab
		* Online Editor: Jupyter Notebook

