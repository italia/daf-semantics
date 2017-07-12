package modules

import com.google.inject.ImplementedBy
import javax.inject.Singleton
import javax.inject.Inject
import play.inject.ApplicationLifecycle
import scala.concurrent.Future

@ImplementedBy(classOf[KBModuleBase])
trait KBModule

@Singleton
class KBModuleBase @Inject() (lifecycle: ApplicationLifecycle) extends KBModule {

  import scala.concurrent.ExecutionContext.Implicits.global

  //  TODO:
  //  lifecycle.addStopHook { () =>
  //    Future{
  //      null
  //    }
  //  }

}

// SEE: https://stackoverflow.com/questions/39102722/stop-hook-is-not-being-called-when-play-application-is-shutting-down