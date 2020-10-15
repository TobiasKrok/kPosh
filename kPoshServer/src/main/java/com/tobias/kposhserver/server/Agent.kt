package com.tobias.kposhserver.server

import com.tobias.kposhserver.server.command.Command
import com.tobias.kposhserver.server.worker.AgentWorker
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class Agent(private val socket: Socket, val id: Int, private val worker : AgentWorker) {
    private var connected = AtomicBoolean()

     fun run()  {
         connected.set(true)
         val errorHandler = CoroutineExceptionHandler { _, ex ->
             println("Agent $id error: $ex")
             connected.set(false)

         }
         // Create reference of this agent to pass to coroutine
         val agent = this
         GlobalScope.launch(errorHandler){
             while (connected.get()) {
                 worker.process(read(),agent)
             }
         }
    }

     private fun read() : String = socket.getInputStream().bufferedReader().use(BufferedReader::readText)

    fun write(command: Command) {
        socket.getOutputStream().bufferedWriter().use{out -> out.write(command.cmd)}
    }


}