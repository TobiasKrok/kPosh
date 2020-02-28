package com.tobias.kposhserver.server

import com.tobias.kposhserver.server.command.Command
import com.tobias.kposhserver.server.worker.AgentWorker
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class Agent(socket: Socket, val id: Int, private val worker : AgentWorker) {
    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
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

     fun read() : String {
        return reader.readLine()
    }
    public fun write(command: Command) {
        writer.write(command.toString())
        writer.newLine()
        writer.flush()
    }


}