package consul.v1.kv

import consul.v1.common.ConsulRequestBasics
import consul.v1.common.Types._
import consul.v1.session.SessionId
import play.api.http.{ContentTypeOf, Writeable}
import play.api.libs.ws.WSRequest

import scala.concurrent.{ExecutionContext, Future}
trait KvRequests {

  def get(key:String,recurse:Boolean=false,dc:Option[DatacenterId]=Option.empty): Future[List[KvValue]]
  //def getRaw(key:String): Future[Option[String]]
  def put[T](key: String, value: T, flags: Option[Int]=Option.empty, acquire: Option[SessionId]=Option.empty, release: Option[SessionId]=Option.empty,dc:Option[DatacenterId]=Option.empty)(implicit wrt: Writeable[T], ct: ContentTypeOf[T]):Future[Boolean]
  def delete(key:String,recurse:Boolean,dc:Option[DatacenterId]=Option.empty):Future[_]
}

object KvRequests {

  def apply()(implicit executionContext: ExecutionContext, rb: ConsulRequestBasics): KvRequests = new KvRequests{

    def get(key: String,recurse:Boolean,dc:Option[DatacenterId]) = rb.erased(
      rb.jsonRequestMaker(
        s"/kv/$key",
        httpFunc = recurseDcRequestHolder(recurse,dc).andThen( _.get() )
      )(_.validateOpt[List[KvValue]].map(_.getOrElse(List.empty)))
    )

    def put[T](key: String, value: T, flags: Option[Int], acquire: Option[SessionId], release: Option[SessionId], dc:Option[DatacenterId])(implicit wrt: Writeable[T], ct: ContentTypeOf[T]): Future[Boolean] = {
      //this could be wrong - could be a simple string that is returned and we have to check if "true" or "false"
      val params = Seq.concat(
        flags.map("flags" -> _.toString),
        acquire.map("acquire" -> _.value),
        release.map("release" -> _.value)
      )

      rb.jsonDcRequestMaker(
        s"/kv/$key",dc,
        httpFunc = _.withQueryString(params:_*).put(value)
      )(_.validate[Boolean].getOrElse(false))
    }

    def delete(key: String, recurse: Boolean, dc:Option[DatacenterId]): Future[Boolean] = {
      rb.responseStatusRequestMaker(s"/kv/$key",
       httpFunc = recurseDcRequestHolder(recurse,dc).andThen( _.delete() )
      )(_ => true)
    }

    private def recurseDcRequestHolder(recurse:Boolean,dc:Option[DatacenterId]) = {
      val params = dc.map("dc"->_.toString).toList ++ Option(recurse).collect{ case true => ("recurse"->"") }
      (_:WSRequest).withQueryString(params:_*)
    }

  }


}