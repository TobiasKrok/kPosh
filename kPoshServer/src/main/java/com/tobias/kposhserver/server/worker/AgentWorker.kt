package com.tobias.kposhserver.server.worker

import com.tobias.kposhserver.server.Agent
import com.tobias.kposhserver.server.command.Command
import com.tobias.kposhserver.server.command.CommandType
import java.lang.IllegalArgumentException
import java.util.concurrent.BlockingQueue

class AgentWorker(private val queue: BlockingQueue<Command>) : Runnable {

    @Synchronized
    fun process(cmd: String, agent: Agent) {
        queue.put(parseCommand(cmd, agent))
    }

    @Synchronized
    fun process(cmd: Command) {
        queue.put(cmd)
    }

    override fun run() {
        while (!Thread.interrupted()) {
            handleCommand(queue.take())

        }
    }

    private fun handleCommand(command: Command) {
        when (command.commandType) {
            CommandType.RESPONSE_POWERSHELL -> println("${command.cmd}")
            CommandType.RESPONSE_AGENT -> println("${command.cmd}")
            CommandType.CALL_AGENT -> command.agent.write(command)
            else -> {
                println("Unknown command")
            }
        }
    }


    private fun parseCommand(cmd: String, agent: Agent): Command {

        var cmdType = CommandType.UNKNOWN
        var data = ""
        try {
            cmdType = CommandType.valueOf(cmd.substringAfterLast(":").trim())
            data = cmd.substring(cmd.indexOf("CMD:") + 4, cmd.indexOf("TYPE:") - 1)
        } catch (ex: IllegalArgumentException) {
            // TODO logging
            println("Error parsing command: $ex")
        }
        return Command(data, agent, cmdType)
    }
}