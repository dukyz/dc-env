import actor.{crawler, diplomat}


/**
  * @todo the entry point of the entire application.List the entry points of the modules
  *
  * @author dukyz
  */
object Main extends App {
    crawler.run
    diplomat.run
}
