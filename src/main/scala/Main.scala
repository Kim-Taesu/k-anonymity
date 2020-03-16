import org.apache.spark.sql.SparkSession

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


    // TODO: data read (with schema)
    val rawData = spark.read
      .schema(Schema.rawData)
      .csv(Config.rawDataPath)
      .na.drop()

    // TODO : 준식별자 선택
    val quasiIdentifier = Seq("some1", "some2")
    // TODO : 민감정보 선택
    val sensiIdentifier = Seq("some1")
    // TODO : 분류트리 설정
    val taxonomy: Seq[String] = Seq("a")
    // TODO : K Value 설정
    val kValue = Config.kValue
    // TODO : K 만족할 때까지 분류 트리 순회

    for (i <- taxonomy.indices) {
      val str = taxonomy(i)

    }
  }
}
