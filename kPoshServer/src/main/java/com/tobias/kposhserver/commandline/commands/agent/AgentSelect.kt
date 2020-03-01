package com.tobias.kposhserver.commandline.commands.agent

import picocli.CommandLine

@CommandLine.Command(
        name = "select",
        description = ["Usage: agent select [agent ids]", "EXAMPLE: agent select 1,2,3"])
class AgentSelect : Runnable {


    @CommandLine.Parameters(
            paramLabel = "[AGENTS]",
            description =  ["one or more agent ids"],
            split = ","
    )
    private lateinit var agents : Array<Int>
    @CommandLine.ParentCommand
    val parent : Agent? = null
    override fun run() {
        for(id in agents) {
            println(id)
        }
    }
}