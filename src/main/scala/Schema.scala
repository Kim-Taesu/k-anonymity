import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

object Schema {
  private val _taxonomy = StructType(Seq(
    StructField("quasi", StringType),
    StructField("level", StringType),
    StructField("min", StringType)
  ))

  private val _rawData = StructType(Seq(
    StructField("age", IntegerType),
    StructField("sex", IntegerType),
    StructField("tmp", IntegerType),
    StructField("surgery", IntegerType),
    StructField("length", IntegerType),
    StructField("location", IntegerType),
    StructField("disease", StringType)
  ))

  private val _noiseData = StructType(Seq(
    StructField("age", StringType),
    StructField("sex", StringType),
    StructField("tmp", StringType),
    StructField("surgery", StringType),
    StructField("length", StringType),
    StructField("location", StringType)
  ))

  def rawData: StructType = _rawData

  def taxonomy: StructType = _taxonomy

  def noiseData: StructType = _noiseData
}
