package com.tobias.kposhserver.server

import com.tobias.kposhserver.server.command.Command
import com.tobias.kposhserver.server.worker.AgentWorker
import java.net.ServerSocket
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue


class Server(private val port: Int) : Runnable {
    val agents : HashMap<Int, Agent> = HashMap();
    private val queue : BlockingQueue<Command> = LinkedBlockingQueue<Command>()
    val worker : AgentWorker = AgentWorker(queue)
    init {
        Thread(worker).start()
    }
    override fun run() {
        val socket = ServerSocket(port)
        socket.use {
            while(!Thread.interrupted()) {
                val agent : Agent = Agent(it.accept(),agents.size,worker)
                agents.put(agents.size,agent)
                agent.run()
            }

        }
    }

}