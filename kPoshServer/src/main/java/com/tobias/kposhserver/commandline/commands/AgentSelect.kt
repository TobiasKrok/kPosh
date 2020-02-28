package com.tobias.kposhserver.commandline.commands

import com.tobias.kposhserver.commandline.commands.Agent
import picocli.CommandLine

@CommandLine.Command(name = "select",description = ["Usage: agent select [agent ids]", "EXAMPLE: agent select 1,2,3"])
class AgentSelect : Runnable {


    @CommandLine.Parameters(
        paramLabel = "[AGENTS]",
        description =  ["one or more agent ids "]
    )
    private lateinit var agents : Array<String>
    @CommandLine.ParentCommand
    val parent : Agent? = null
    override fun run() {
        for(str in agents) {
            println(str)
        }
    }
}