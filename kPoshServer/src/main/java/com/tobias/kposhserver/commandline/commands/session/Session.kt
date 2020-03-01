package com.tobias.kposhserver.commandline.commands.session

import com.tobias.kposhserver.commandline.KPoshCli
import com.tobias.kposhserver.server.Agent
import picocli.CommandLine

@CommandLine.Command(
        name = "session",
        aliases = ["ses"],
        description = ["Manage remote session"],
        subcommands = [SessionCreate::class]

)
 class Session : Runnable {
    @CommandLine.ParentCommand
    private val parent: KPoshCli? = null

    override fun run() {
        parent?.out?.println(CommandLine(this).usageMessage)
    }
    fun agents() : HashMap<Int, Agent>? = parent?.agents()

    fun inSession(inSession : Boolean, agents: List<Agent>) {
        parent?.inSession(inSession,agents)
    }

}
