package com.tobias.kposhserver.commandline.commands

import picocli.CommandLine

@CommandLine.Command(
    name = "list",
    headerHeading = "@|bold,underline Usage|@:%n%n",
    description = ["Lists all connected agents"]
)
class AgentList : Runnable{
    @CommandLine.ParentCommand
    val parent : Agent? = null
    override fun run() {
        println("Agents")
        println("-------------------------")
        for((key, value) in parent?.agents().orEmpty()) {
            println("Agent $key")
        }
    }
}