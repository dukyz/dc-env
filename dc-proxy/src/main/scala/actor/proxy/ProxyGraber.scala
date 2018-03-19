package actor.proxy

import akka.actor.Timers
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer, SnapshotSelectionCriteria}
import common.setting.ShardingDefault
import common.tool.ActorUtil

import scala.concurrent.duration._

/**
  * @todo grab new proxy from the vendor
  * @author dukyz
  */
object ProxyGraber {
    /**
      * Trigger object to grab new url action
      */
    case object GRAB
}

class ProxyGraber extends Timers with PersistentActor with ActorUtil  {
    import ProxyGraber._

    override def persistenceId: String = self.path.toString
    
    /**
      * Key of the timer
      */
    private case object TIMER_OF_PROXYGRABER
    
    /**
      * The interval of snapshoting
      */
    private val snapshotInterval = classConfig.getInt("snapshotInterval")
    
    /**
      * The position of the proxy archive.
      * This will be recovered in the receiveRecover.
      */
    private var order_id:Int = 0
    
    override def preStart = {
        timers.startPeriodicTimer(TIMER_OF_PROXYGRABER,GRAB,classConfig.getInt("timerPeriodic") seconds)
    }
    
    override def receiveRecover: Receive = {
        case RecoveryCompleted => log.info("recovered order_id is :"+order_id)
        case SnapshotOffer(_,snapshot:Int) => this.order_id = snapshot
        case o_id:Int => this.order_id = o_id
    }
    
    override def receiveCommand = {
        case GRAB => grabNewProxy
        case _ =>
    }
    
    def grabNewProxy = {
        withUrl(classConfig.getString("url")){
            buff => buff.getLines().foreach(proxy => {
                //increase order_id
                this.order_id += 1
                //persist order_id and handle subsequence after persistence
                persist(event = this.order_id)( persisted_order_id => {
                    //send proxy extracted to the saver actor
                    actorRegistration.findStuff[ProxySaver].get !
                        ShardingDefault.EntityEnvelope(persisted_order_id.toString,ProxySaver.SaveNew(persisted_order_id,proxy))
                    //clean the persisted history when reach the snapshotInterval.
                    if (persisted_order_id % snapshotInterval == 0) {
                        saveSnapshot(snapshot = persisted_order_id)
                        deleteMessages(toSequenceNr = lastSequenceNr)
                        deleteSnapshots(
                            SnapshotSelectionCriteria(
                                maxSequenceNr = lastSequenceNr - snapshotInterval - 1
                            )
                        )
                    }
                })
            })
        }
    }
}