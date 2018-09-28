# Geospark Emacs Ensime Sbt AWS

This project contains a Vanilla Hello Configuration for Running a Geospark Job on AWS Cluster. I personally like to work with Ensime and Emacs, so their settings are given along that.

In order to know more how to set this up

Getting Emacs, Ensime, SBT, Spark work on EMR server



Installing emacs 25 under RHEL6



wget https://ftp.gnu.org/gnu/emacs/emacs-26.1.tar.xz

./configure --prefix=/usr --localstatedir=/var --with-x-toolkit=no --with-jpeg=no --with-gnutls=no

make

sudo make install



mkdir .emacs.d && cd .emacs.d

You can get a vanilla init.el that gives scala auto complete from here.

wget https://raw.githubusercontent.com/jawwada/dotfiles/master/geosaprk-ensime/init.el



Install sbt on the server:

curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo

sudo yum install sbt

First create A HELLO project with sbt

sbt new sbt/scala-seed.g8

cd hello/project


nano plugins.sbt

addSbtPlugin("org.ensime" % "sbt-ensime" % "2.5.1")

nano ../build.sbt. (please take care that in the build.sbt you have the same version of the scala as of the spark-shell —version). In my case, I have put

scalaVersion := "2.11.8"
name := „Hello“
version := "1.0"
val sparkVersion = "2.3.0"
resolvers ++= Seq(

"apache-snapshots" at "http://repository.apache.org/snapshots/"
)

libraryDependencies ++= Seq(

  "org.apache.spark" %% "spark-core" % sparkVersion,

  "org.apache.spark" %% "spark-sql" % sparkVersion,

  "org.apache.spark" %% "spark-mllib" % sparkVersion,

  "org.apache.spark" %% "spark-streaming" % sparkVersion,

  "org.apache.spark" %% "spark-hive" % sparkVersion,

  "mysql" % "mysql-connector-java" % "5.1.6"

)

    
also run sbt in your hello directory and run command ensimeConfig. This will connect autocomplete. ensimeConfigProject also needs to be run

You can go to hello directory, and do sbt compile and run to see whether the run is working. Start ensime server in emacs with M-x ensime. You can open the main scala file in emacs and check the performance of ensime. Whether it is giving auto complete

Now connect it spark

try the command

sudo find / -name "spark-core*jar”, I get the following hit: /usr/lib/spark/jars/spark-core_2.11-2.3.1.jar

Within your hello directory, create a symbolic link to the Spark JAR directory, say by the command

ln -s /usr/lib/spark/jars lib

This is a very important sbt feature, it automatically picks up unmanaged dependencies from the lib folder

Edit Hello.Scala


package example

import org.apache.spark.sql.SparkSession

object Hello extends Greeting with App {

  println(greeting)

  val spark = SparkSession

      .builder

      .appName("demo")

      .getOrCreate()

  val sc = spark.sparkContext

  val data = Array(1, 2, 3, 4, 5, 6, 7)

  val distData = sc.parallelize(data)

  println("count = " + distData.count() )

  spark.stop()

  }

trait Greeting {

  lazy val greeting: String = "hello"

  }

Now in the hello directory: if I run the following:

spark-submit target/scala-2.11/hello_*.jar

This runs perfectly. Now I can submit jobs to spark on cluster, with a greater knowledge of scala.

Now to run Geospark. I need to search, download and copy the following jars to my spark jars directory

geospark-1.1.3.jar .
geospark-sql_2.3-1.1.3.jar .
geospark-viz-1.1.3.jar .

sudo mv *.jar /usr/lib/spark/jars/

nano checkin.csv

-88.331492,32.324142,hotel
-88.175933,32.360763,gas
-88.388954,32.357073,bar
-88.221102,32.35078,restaurant


now open your main class in Emacs and replace the code with the following code to see whether things are working: 

/**Code Starts here**/

import org.datasyslab.geospark.spatialOperator.RangeQuery
import org.datasyslab.geospark.spatialOperator.JoinQuery
import org.datasyslab.geospark.spatialRDD.RectangleRDD
import com.vividsolutions.jts.geom.Envelope
import org.datasyslab.geospark.spatialOperator.KNNQuery
import org.datasyslab.geospark.spatialRDD.PointRDD
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.Point

val pointRDDInputLocation = "/home/hadoop/checkin.csv"
val pointRDDOffset = 0 // The point long/lat starts from Column 0
val pointRDDSplitter = FileDataSplitter.CSV
val carryOtherAttributes = true // Carry Column 2 (hotel, gas, bar...)
var objectRDD = new PointRDD(sc, pointRDDInputLocation, pointRDDOffset, pointRDDSplitter, carryOtherAttributes)
queryEnvelope
objectRDD

Hopefully, you followed this through, this is what works for me, if this does not work for you, it is because I gave incomplete information not incorrect one. Comment so I may help. Enjoy!!
