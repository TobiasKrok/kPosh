package com.tobias.kposhagent

fun main(args : Array<String>) {
    val ip = "localhost"
    val port = 420
    val agent = Agent(ip,port)
    Thread(agent).start()

}