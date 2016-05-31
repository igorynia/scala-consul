package consul

import java.net.Inet4Address

import consul.v1.acl.AclRequests
import consul.v1.agent.AgentRequests
import consul.v1.catalog.CatalogRequests
import consul.v1.common.{ConsulRequestBasics, Types}
import consul.v1.event.EventRequests
import consul.v1.health.HealthRequests
import consul.v1.kv.KvRequests
import consul.v1.session.SessionRequests
import consul.v1.status.StatusRequests
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

trait ConsulApiV1{
  def acl:     AclRequests
  def agent:   AgentRequests
  def catalog: CatalogRequests
  def event:   EventRequests
  def health:  HealthRequests
  def kv:      KvRequests
  def session: SessionRequests
  def status:  StatusRequests

}

class Consul(ws: WSClient, baseUrl: String, token: Option[String])(implicit ec: ExecutionContext){

  def this(address: Inet4Address, port: Int, token: Option[String])(implicit ec: ExecutionContext) = {
    this(s"http://${address.getHostAddress}:$port", token)(ec)
  }

  require(!baseUrl.endsWith("/"), "Base url shouldn't end with slash")

  lazy val v1: ConsulApiV1 with Types = new ConsulApiV1 with Types{
    private lazy val basePath = s"$baseUrl/v1"
    private implicit def requestBasics = new ConsulRequestBasics(basePath, token)

    lazy val health:  HealthRequests  = HealthRequests()
    lazy val agent:   AgentRequests   = AgentRequests()
    lazy val catalog: CatalogRequests = CatalogRequests()
    lazy val kv:      KvRequests      = KvRequests()
    lazy val status:  StatusRequests  = StatusRequests()
    lazy val acl:     AclRequests     = AclRequests()
    lazy val session: SessionRequests = SessionRequests()
    lazy val event:   EventRequests   = EventRequests()
  }

}
