name := "kAnonymity_spark"

version := "0.1"

scalaVersion := "2.11.12"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
val sparkVersion = "2.4.4"
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
