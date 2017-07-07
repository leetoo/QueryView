package com.shah.persistence.demo

object TestUtils {

  object Mock {

    import akka.actor.Props
    import akka.stream.ActorMaterializer
    import com.shah.persistence.query.model.QueryViewImpl

    import scala.concurrent.ExecutionContext

    object AccountView {

      import akka.actor.Actor

      trait InMemQuerySupport extends Actor {

        import akka.NotUsed
        import akka.persistence.query.{EventEnvelope, PersistenceQuery}
        import akka.stream.scaladsl.Source
        import akka.persistence.inmemory.query.scaladsl.InMemoryReadJournal

        def queryJournal(idToQuery: String, fromSequenceNr: Long = 0L,
                         toSequenceNr: Long = Long.MaxValue): Source[EventEnvelope, NotUsed] = {
          PersistenceQuery(context.system).
            readJournalFor[InMemoryReadJournal](InMemoryReadJournal.Identifier).
            eventsByPersistenceId(idToQuery, fromSequenceNr, toSequenceNr)
        }

        def queryJournalFrom(idToQuery: String, fromSequenceNr: Long = 0L): Source[EventEnvelope, NotUsed] =
          queryJournal(idToQuery, fromSequenceNr, Long.MaxValue)
      }

      class AccountViewMockImpl(val snapshotFrequency: Int)(implicit override val ec: ExecutionContext)
        extends AccountView with QueryViewImpl with InMemQuerySupport {
        val materializer = ActorMaterializer()
      }

      def props(snapshotFrequency: Int)(implicit ec: ExecutionContext) =
        Props(new AccountViewMockImpl(snapshotFrequency))

    }

  }

}