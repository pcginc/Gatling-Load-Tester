package MicroLoadTest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import scala.util.Random


class MicroLoadTest extends Simulation {

  val userCount = 1 // Change for number of users requried
  val id = 1 // can also use a feeder file with valid ids -- jsonFile("filefeederexample.json")
  val createPerson = jsonUrl("https://microservice-poc-202623.appspot.com/people") // Json feeder with valid user fields for creating new people
  val httpConf = http.baseURL ("https://microservice-poc-202623.appspot.com")
  val sessionHeaders = Map("Authorization" -> "Bearer ${accessToken}")

  // Defining a map of headers before the scenario allows you to reuse these in several requests
  //  val sessionHeaders = Map("Authorization" -> "Bearer ${authToken}", "Content-Type" -> "application/json")


  //  val scn =  scenario("User Login")
  //    .exec(http("Login")
  //    .post("/login")
  //    .body(StringBody("""{"username":"admin","password":"password"}"""))
  //    .check(jsonPath("$.accessToken").exists.saveAs("accessToken"))
  //    )
  //    .exec(http("Create Person")
  //      .post("/people")
  //      .body(StringBody("""{"username":"admin","password":"password"}"""))
  //      .check(jsonPath("$.accessToken").exists.saveAs("accessToken"))
  //    )


  val scn = scenario("BasicSimulation") // A scenario is a chain of requests and pauses
    .exec(http("List People")
    .get("/people")
    .check(status.is(200)))
    .pause(7) // Note that Gatling uses real time pauses
    .exec(http("UserId")
    .get(s"/people/$id"))
    .pause(2)
    .exec(http("Disabled People")
      .get("/people/disabled"))
    .pause(3)
    .exec(http("Enabled People")
      .get("/people/enabled"))
    .pause(200 milliseconds)
    .exec(http("Login")
      .post("/login")
      .body(StringBody("""{"username":"admin","password":"password"}"""))
      .check(jsonPath("$accessToken").exists.saveAs("accessToken"))
    )
    .exec(http("Create Person")
      .post("/people")
      .headers(sessionHeaders)
      .body(StringBody("""${createPerson}"""))
    )

  setUp(
    scn.inject(atOnceUsers(userCount))
  ).protocols(httpConf)




  //  setUp(
  //    scn.inject(rampUsers(userCount) over (10 minutes))
  //  ).protocols(httpConf)




}