import scala.math.random
import org.apache.spark._
import org.datasyslab.geospark.spatialOperator.RangeQuery
import org.datasyslab.geospark.spatialOperator.JoinQuery
import org.datasyslab.geospark.spatialRDD.RectangleRDD
import com.vividsolutions.jts.geom.Envelope
import org.datasyslab.geospark.spatialOperator.KNNQuery
import org.datasyslab.geospark.spatialRDD.PointRDD
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.Point
import org.datasyslab.geospark.enums.{FileDataSplitter, GridType, IndexType}

object Hello {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Hello")
    val spark = new SparkContext(conf)
    val sc=spark
    val slices = if (args.length > 0) args(0).toInt else 2
    val n = math.min(100000L * slices, Int.MaxValue).toInt // avoid overflow
    val pointRDDInputLocation = "/home/hadoop/hello/checkin.csv"
    val pointRDDOffset = 0 // The point long/lat starts from Column 0
    val pointRDDSplitter = FileDataSplitter.CSV
    val carryOtherAttributes = true // Carry Column 2 (hotel, gas, bar...)
    var objectRDD = new PointRDD(sc, pointRDDInputLocation, pointRDDOffset, pointRDDSplitter, carryOtherAttributes)
    objectRDD


    val count = spark.parallelize(1 until n, slices).map { i =>
      val x = random * 2 - 1
      val y = random * 2 - 1
      if (x*x + y*y < 1) 1 else 0
    }.reduce(_ + _)
    println("Pi is roughly " + 4.0 * count / n)
    spark.stop()
  }
}
