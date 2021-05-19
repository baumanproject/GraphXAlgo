package org.mai.spark.graphx.rdd

import org.apache.spark.sql.SparkSession
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.graphx.lib.{PageRank}


object Main extends App{
  val spark = SparkSession
  .builder()
  .master("local[*]")
  .appName("graphTest")
  .getOrCreate()

  case class Props(posts: Int, eg: Double)

  val verticesRdd = spark.read
    .format("com.databricks.spark.csv")
    .option("sep",",")
    .option("header","true")
    .option("inferSchema", "true")
    .load("E:/spark-graphx/Neo4j/neo4j/import/InstagramUserStats.csv")
    .rdd
    .map(row => (row.getAs[Int](0).toLong,Props(row.getAs[Int](1),row.getAs[Double](4))))

  val edgesRdd = spark.read
    .format("com.databricks.spark.csv")
    .option("sep",",")
    .option("header","true")
    .option("inferSchema", "true")
    .load("E:/spark-graphx/Neo4j/neo4j/import/Network.csv")
    .rdd
    .map(row =>Edge(row.getAs[Int](1),row.getAs[Int](0), row.getAs[Double](2)))

  val graph: Graph[Props, Double] = Graph(verticesRdd, edgesRdd).cache()
  val algo = new BreadthFirstSearch {}
  val depth = 3
  val amount_of_top = 5

  val ranks = PageRank.runUntilConvergence(graph, tol = 0.0000001, resetProb = 0.8)

  val res = ranks.vertices.sortBy(-_._2).take(amount_of_top)
    .map(folowee => (folowee._1, folowee._2,algo.searchPathByDepth(graph, folowee._1,depth).vertices.filter(vertex=> vertex._2._2.asInstanceOf[(Double, List[Int])]._1 != Double.PositiveInfinity).count()-1)
    )

  res.foreach(folowee=> println(s"For node ${folowee._1} with pg score = ${folowee._2} bws with depth ${depth} = ${folowee._3}"))


}
