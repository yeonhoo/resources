package example

import akka.actor.{ActorSystem, Props}
import akka.persistence.PersistentActor
import akka.persistence.journal.Tagged
import com.typesafe.config.ConfigFactory

case class Command(i: Int)
case class Event(i: Int)

class MyPersistentActor extends PersistentActor {
  var sum: Int = 0

  override val persistenceId: String = "myPid"

  override def receiveRecover: Receive = {
    case Event(i) ⇒
      println(s"receiveRecover  : Recovering an event = Event($i)")
      sum += i
      println(s"receiveRecover  : current state = $sum")
    case Tagged(Event(i), tags) ⇒
      println(s"receiveRecover  : Recovering from Tagged(Event($i), $tags)")
      sum += i
      println(s"receiveRecover  : current state = $sum")

  }

  override def receiveCommand: Receive = {
    case Command(i) ⇒
      println(s"receiveCommand  : Received Command($i)")
      persist(Event(i)) { event ⇒
        println(s"persist callback: Event = Event(${event.i}) persisted")
        sum += i
        println(s"persist callback: current state = $sum")
      }
    case "kaboom" ⇒
      throw new Exception("exploded!")
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val system = ActorSystem("exampleSystem", config)

    try {
      val props = Props(new MyPersistentActor)
      val p1 = system.actorOf(props, "p1")
      p1 ! Command(1)
      p1 ! Command(2)
      p1 ! Command(3)

      // akka-persistence-cassandra plugin has internal buffering,
      // so if a persistent actor throws an exception and restart
      // before the buffer is flushed to write everything into cassandra,
      // there could be sequence number inconsistency ...
      //
      // There seems to be a way to avoid that issue by configuration,
      // but Thread.sleep was easier for this example.
      Thread.sleep(3000)
      p1 ! "kaboom"
      p1 ! Command(4)
      p1 ! Command(5)
      Thread.sleep(3000)
    } finally {
      system.terminate()
    }
  }
}