package com.tobias.kposhserver.commandline.commands

import com.tobias.kposhserver.commandline.KPoshCli
import com.tobias.kposhserver.server.Agent
import picocli.CommandLine

@CommandLine.Command(
    name = "agent",
    description = ["Configure and configure agents"],
    subcommands = [AgentSelect::class, AgentList::class]
)
class Agent: Runnable {
    @CommandLine.ParentCommand
   private val parent: KPoshCli? = null

    override fun run() {
        parent?.out?.println(CommandLine(this).usageMessage)
    }

    fun agents() : HashMap<Int, Agent>? = parent?.agents()
}