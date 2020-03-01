package com.tobias.kposhserver

import com.tobias.kposhserver.commandline.KPoshCli
import com.tobias.kposhserver.server.Server

fun main(args: Array<String>) {
    val server : Server = Server(420)
    Thread(server).start()
    val worker = server.worker
    val cli = KPoshCli(server,worker)
    cli.startCli()
    }
