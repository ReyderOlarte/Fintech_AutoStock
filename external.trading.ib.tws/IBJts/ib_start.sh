#java -cp jts.jar:hsqldb.jar:jcommon-1.0.12.jar:jfreechart-1.0.9.jar:jhall.jar:other.jar:rss.jar -Xmx512M -XX:MaxPermSize=128M jclient.LoginFrame .

while : ; do
  java -cp IBController.jar:jts.jar:hsqldb.jar:jfreechart-1.0.9.jar:jhall.jar:jcommon-1.0.12.jar:other.jar:rss.jar -Xmx512M -XX:MaxPermSize=128M ibcontroller.IBController
done