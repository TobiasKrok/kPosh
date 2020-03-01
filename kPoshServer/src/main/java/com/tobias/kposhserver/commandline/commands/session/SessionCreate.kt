package com.tobias.kposhserver.commandline.commands.session

import com.tobias.kposhserver.server.Agent
import picocli.CommandLine

@CommandLine.Command(
        name = "create",
        description = ["Create new remote session","USAGE: session create <ids>"]
)
class SessionCreate : Runnable{
    @CommandLine.ParentCommand
    val parent : Session? = null

    @CommandLine.Parameters(
            paramLabel = "id",
            description =  ["one or more agent ids"],
            split = ","
    )
    private var ids : Array<Int> = emptyArray()

    override fun run() {
        val agents : ArrayList<Agent> = ArrayList()
        for(id in ids) {
            val agent : Agent? = parent?.agents()?.get(id)
            if(agent == null) {
                println("Could not find agent: $id")
            } else {
                agents.add(agent)
            }
        }
        if(agents.size == 0) {
            println(CommandLine(this).usageMessage)
        } else {
            parent?.inSession(true,agents)
        }
    }
}