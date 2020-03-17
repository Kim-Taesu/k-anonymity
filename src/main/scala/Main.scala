import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Row, SparkSession}

object Main {


  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .appName("kAnonymity")
      .master("local[*]")
      .config("spark.sql.shuffle.partitions", 16)
      .config("spark.sql.crossJoin.enabled", "true")
      .config("org.apache.spark.serializer.KryoSerializer", "true")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")


    val rawData = spark.read
      .schema(Schema.rawData)
      .csv(Config.rawDataPath)
      .na.drop()

    val quasiInfo = Seq(col("age"), col("sex"), col("tmp"), col("surgery"), col("length"), col("location"))

    val quasiIdentifier = rawData.select(quasiInfo: _*).cache()
    val sensiInfo = Seq(col("disease"))
    val sensiIdentifier = rawData.select(sensiInfo: _*).withColumn("id", monotonically_increasing_id)

    val kValue = Config.kValue


    val quasiMaxValue = spark.sparkContext.broadcast(Array(100, 2, 10, 10, 80, 20))
    // TODO : 분류 트리 경우의수 계산
    val taxonomy = spark.read
      .textFile(JavaConfig.TAXONOMY_CASES_PATH)
      .rdd.map(row => {
      val ints = row.split("").map(_.toInt)
      var result: Array[Int] = Array()
      for (i <- ints.indices) {
        if (ints(i).equals(0)) {
          result :+= -1
        } else {
          result :+= quasiMaxValue.value(i) / ints(i)
        }
      }
      result
    }).collect()

    // TODO : K 만족할 때까지 분류 트리 순회
    for (index <- taxonomy.indices) {
      val item = taxonomy(index)
      val range = spark.sparkContext.broadcast(item)
      val noiseData = spark.createDataFrame(quasiIdentifier.rdd.map(row => {
        addNoise(row, range)
      }), Schema.noiseData)

      val noiseKValue = noiseData.groupBy(quasiInfo: _*)
        .count().orderBy("count")
        .selectExpr("count").collect().map(_.getLong(0))
      if (kValue < noiseKValue(0)) {
        noiseData
          .withColumn("id", monotonically_increasing_id)
          .join(sensiIdentifier, "id")
          .drop("id")
          .show()
      }
    }
  }

  def addNoise(row: Row, range: Broadcast[Array[Int]]): Row = {
    var result = ""
    val value = range.value
    for (index <- 0 until row.size) {
      val realValue = row.getInt(index)
      val rangeValue = value(index)
      if (rangeValue == -1) {
        result = result.concat(row.getInt(index).toString)
      }
      else {
        val from: Int = realValue / rangeValue
        val fromRest: Int = realValue % rangeValue
        var noiseValue: String = null
        if (from != 0 && fromRest == 0)
          noiseValue = (from - 1) * rangeValue + "-" + from * rangeValue
        else {
          val to = from + 1
          noiseValue = from * rangeValue + "-" + to * rangeValue
        }
        result = result.concat(noiseValue)
      }
      if (index < row.size - 1) {
        result = result.concat(",")
      }
    }
    Row.fromSeq(result.split(","))
  }
}
