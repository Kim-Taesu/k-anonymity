import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

object Schema {
  private val _taxonomy = StructType(Seq(
    StructField("quasi", StringType),
    StructField("level", StringType),
    StructField("min", StringType)
  ))

  private val _rawData = StructType(Seq(
    StructField("T_Link_ID", StringType),
    StructField("Day", IntegerType),
    StructField("Time", IntegerType),
    StructField("Weather", StringType),
    StructField("Dest", StringType),
    StructField("CntOn", StringType),
    StructField("CntOff", StringType),
    StructField("CntEmp", StringType)
  ))

  def rawData: StructType = _rawData

  def taxonomy: StructType = _taxonomy
}
