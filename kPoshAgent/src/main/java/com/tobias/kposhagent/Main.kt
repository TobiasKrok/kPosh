package com.tobias.kposhagent

import java.net.Socket

fun main(args : Array<String>) {
    val ip = "localhost"
    val port = 420
    val agent = Agent(ip,port)
    Thread(agent).start()
}